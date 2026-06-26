package slimeknights.tconstruct.library.recipe.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.Level;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.mantle.recipe.helper.LoggingRecipeSerializer;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;
import slimeknights.tconstruct.tables.TinkerTables;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Shaped recipe with a number of {@link slimeknights.tconstruct.library.recipe.ingredient.MaterialIngredient} and
 * {@link slimeknights.tconstruct.library.recipe.ingredient.MaterialValueIngredient} to set the materials of the result.
 */
public class ShapedMaterialsRecipe extends ShapedRecipe implements MaterialsCraftingTableRecipe {
  /** List of tool parts to search for in the final recipe */
  private final List<Ingredient> parts;
  /**
   * If true, a part may show up multiple times in the inputs, and all copies should match.
   * If false, only the first instance of a part is checked for each input, allowing a tool with the same part multiple times.
   */
  private final boolean checkRepeats;
  /** List of additional materials to add beyond the parts */
  private final List<MaterialVariantId> extraMaterials;
  /** Stored pattern for codec round-trip */
  private final ShapedRecipePattern ownPattern;
  /** Stored result for codec/network round-trip */
  private final ItemStack ownResult;

  public ShapedMaterialsRecipe(String group, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack result, boolean showNotification, List<Ingredient> parts, List<MaterialVariantId> extraMaterials) {
    super(group, category, pattern, result, showNotification);
    this.ownPattern = pattern;
    this.ownResult = result;
    this.parts = parts;
    this.checkRepeats = parts.stream().unordered().distinct().count() == parts.size();
    this.extraMaterials = extraMaterials;
  }

  @Override
  public List<Ingredient> getParts() {
    return parts;
  }

  @Override
  public List<MaterialVariantId> getExtraMaterials() {
    return extraMaterials;
  }

  @Override
  public int getPartCount() {
    return parts.size();
  }

  /**
   * Finds materials for each of the parts
   * @return Array of all matched materials, or null if no match found.
   */
  @Nullable
  static MaterialVariantId[] findMaterials(CraftingInput inventory, List<Ingredient> parts, int partCount, boolean checkRepeats) {
    MaterialVariantId[] materials = new MaterialVariantId[partCount];
    for (int i = 0; i < inventory.size(); i++) {
      ItemStack stack = inventory.getItem(i);
      if (!stack.isEmpty()) {
        for (int p = 0; p < partCount; p++) {
          MaterialVariantId current = materials[p];
          if ((current == null || checkRepeats) && parts.get(p).test(stack)) {
            MaterialVariantId matched;
            if (stack.getItem() instanceof IMaterialItem materialItem) {
              matched = materialItem.getMaterial(stack);
            } else {
              matched = MaterialRecipeCache.findRecipe(stack).getMaterial().getVariant();
            }
            if (current == null) {
              materials[p] = matched;
              break;
            } else if (!current.matchesVariant(matched)) {
              if (current.getId().equals(matched.getId())) {
                materials[p] = current.getId();
                break;
              } else {
                return null;
              }
            }
          }
        }
      }
    }
    for (int p = 0; p < partCount; p++) {
      if (materials[p] == null) {
        return null;
      }
    }
    return materials;
  }

  @Override
  public boolean matches(CraftingInput inventory, Level level) {
    if (!super.matches(inventory, level)) {
      return false;
    }
    return findMaterials(inventory, parts, parts.size(), checkRepeats) != null;
  }

  /** Common logic to set materials on the result stack */
  public static void setMaterial(ItemStack stack, MaterialVariantId material, List<MaterialVariantId> extraMaterials) {
    if (extraMaterials.isEmpty() && stack.getItem() instanceof IMaterialItem materialItem) {
      materialItem.setMaterial(stack, material);
    } else {
      MaterialNBT.Builder builder = MaterialNBT.builder();
      builder.add(material);
      for (MaterialVariantId extraMaterial : extraMaterials) {
        builder.add(extraMaterial);
      }
      ToolStack.from(stack).setMaterials(builder.build());
    }
  }

  /** Sets the material for the given stack */
  @Override
  public void setMaterial(ItemStack stack, MaterialVariantId material) {
    setMaterial(stack, material, extraMaterials);
  }

  /** Assembles the item with material information */
  static ItemStack assemble(ItemStack stack, CraftingInput inventory, List<Ingredient> parts, int partCount, boolean checkRepeats, List<MaterialVariantId> extraMaterials) {
    MaterialVariantId[] materials = findMaterials(inventory, parts, partCount, checkRepeats);
    if (materials != null) {
      if (materials.length == 1 && extraMaterials.isEmpty() && stack.getItem() instanceof IMaterialItem materialItem) {
        return materialItem.setMaterial(stack, materials[0]);
      }
      MaterialNBT.Builder builder = MaterialNBT.builder();
      for (MaterialVariantId material : materials) {
        builder.add(material);
      }
      builder.add(extraMaterials);
      ToolStack.from(stack).setMaterials(builder.build());
    }
    return stack;
  }

  @Override
  public ItemStack assemble(CraftingInput inventory, HolderLookup.Provider registryAccess) {
    return assemble(super.assemble(inventory, registryAccess), inventory, parts, parts.size(), checkRepeats, extraMaterials);
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerTables.shapedMaterialsRecipeSerializer.get();
  }

  public static class Serializer implements LoggingRecipeSerializer<ShapedMaterialsRecipe> {
    static final Loadable<List<MaterialVariantId>> EXTRA_MATERIALS = MaterialVariantId.LOADABLE.list(0);
    static final LoadableField<List<MaterialVariantId>, ShapedMaterialsRecipe> MATERIAL_FIELD = EXTRA_MATERIALS.defaultField("extra_materials", List.of(), r -> r.extraMaterials);

    @Override
    public MapCodec<ShapedMaterialsRecipe> codec() {
      return RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.STRING.optionalFieldOf("group", "").forGetter(ShapedMaterialsRecipe::getGroup),
        CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(ShapedMaterialsRecipe::category),
        ShapedRecipePattern.MAP_CODEC.forGetter(r -> r.ownPattern),
        ItemStack.STRICT_CODEC.fieldOf("result").forGetter(r -> r.ownResult),
        Codec.BOOL.optionalFieldOf("show_notification", true).forGetter(ShapedMaterialsRecipe::showNotification),
        Ingredient.CODEC_NONEMPTY.listOf().fieldOf("parts").forGetter(r -> r.parts),
        EXTRA_MATERIALS.codec().optionalFieldOf("extra_materials", List.of()).forGetter(r -> r.extraMaterials)
      ).apply(instance, (group, category, pattern, result, showNotification, parts, extras) ->
        new ShapedMaterialsRecipe(group, category, pattern, result, showNotification, List.copyOf(parts), extras)
      ));
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ShapedMaterialsRecipe> streamCodec() {
      return StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);
    }

    private static void toNetwork(RegistryFriendlyByteBuf buf, ShapedMaterialsRecipe recipe) {
      ShapedRecipePattern.STREAM_CODEC.encode(buf, recipe.ownPattern);
      ItemStack.STREAM_CODEC.encode(buf, recipe.ownResult);
      ByteBufCodecs.STRING_UTF8.encode(buf, recipe.getGroup());
      buf.writeEnum(recipe.category());
      buf.writeBoolean(recipe.showNotification());
      ByteBufCodecs.collection(ArrayList::new, Ingredient.CONTENTS_STREAM_CODEC).encode(buf, new ArrayList<>(recipe.parts));
      buf.writeVarInt(recipe.extraMaterials.size());
      for (MaterialVariantId id : recipe.extraMaterials) {
        ByteBufCodecs.STRING_UTF8.encode(buf, id.toString());
      }
    }

    private static ShapedMaterialsRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
      ShapedRecipePattern pattern = ShapedRecipePattern.STREAM_CODEC.decode(buf);
      ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
      String group = ByteBufCodecs.STRING_UTF8.decode(buf);
      CraftingBookCategory category = buf.readEnum(CraftingBookCategory.class);
      boolean showNotification = buf.readBoolean();
      List<Ingredient> parts = ByteBufCodecs.collection(ArrayList::new, Ingredient.CONTENTS_STREAM_CODEC).decode(buf);
      int count = buf.readVarInt();
      List<MaterialVariantId> extras = new ArrayList<>(count);
      for (int i = 0; i < count; i++) {
        extras.add(MaterialVariantId.tryParse(ByteBufCodecs.STRING_UTF8.decode(buf)));
      }
      return new ShapedMaterialsRecipe(group, category, pattern, result, showNotification, parts, extras);
    }
  }
}
