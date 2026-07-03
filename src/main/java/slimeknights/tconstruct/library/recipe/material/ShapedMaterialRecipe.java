package slimeknights.tconstruct.library.recipe.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
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
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.recipe.ingredient.MaterialValueIngredient;
import slimeknights.tconstruct.tables.TinkerTables;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Shaped recipe with a number of {@link MaterialValueIngredient} to set the material of the result.
 * @deprecated use {@link ShapedMaterialsRecipe}, which requires specifying the ingredients for each part.
 */
@Deprecated
public class ShapedMaterialRecipe extends ShapedRecipe {
  private MaterialValueIngredient material;
  private final List<MaterialVariantId> extraMaterials;
  /** Stored pattern for codec round-trip */
  private final ShapedRecipePattern ownPattern;
  /** Stored result for codec/network round-trip */
  private final ItemStack ownResult;

  public ShapedMaterialRecipe(String group, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack result, boolean showNotification, List<MaterialVariantId> extraMaterials) {
    super(group, category, pattern, result, showNotification);
    this.ownPattern = pattern;
    this.ownResult = result;
    this.extraMaterials = extraMaterials;
  }

  @Nullable
  public MaterialValueIngredient getMaterial() {
    if (material == null) {
      for (Ingredient ingredient : getIngredients()) {
        MaterialValueIngredient materialValue = ingredient.getCustomIngredient() instanceof MaterialValueIngredient mvi ? mvi : null;
        if (materialValue != null) {
          if (material == null) {
            material = materialValue;
          } else {
            material = material.merge(materialValue);
          }
        }
      }
      if (material == null) {
        TConstruct.LOG.error("No material ingredient found for a material shaped recipe, this indicates a broken recipe");
      }
    }
    return material;
  }

  @Nullable
  private MaterialVariantId findMaterial(CraftingInput inventory) {
    MaterialValueIngredient material = getMaterial();
    if (material == null) {
      return null;
    }
    MaterialVariantId firstMaterial = null;
    for (int i = 0; i < inventory.size(); i++) {
      ItemStack stack = inventory.getItem(i);
      if (!stack.isEmpty()) {
        MaterialVariantId matchedMaterial = material.getMaterialForStack(stack);
        if (matchedMaterial != null) {
          if (firstMaterial == null) {
            firstMaterial = matchedMaterial;
          } else if (!firstMaterial.matchesVariant(matchedMaterial)) {
            if (firstMaterial.getId().equals(matchedMaterial.getId())) {
              firstMaterial = firstMaterial.getId();
            } else {
              return null;
            }
          }
        }
      }
    }
    return firstMaterial;
  }

  @Override
  public boolean matches(CraftingInput inventory, Level level) {
    if (!super.matches(inventory, level)) {
      return false;
    }
    return findMaterial(inventory) != null;
  }

  public void setMaterial(ItemStack stack, MaterialVariantId material) {
    ShapedMaterialsRecipe.setMaterial(stack, material, extraMaterials);
  }

  @Override
  public ItemStack assemble(CraftingInput inventory, HolderLookup.Provider registryAccess) {
    ItemStack stack = super.assemble(inventory, registryAccess);
    MaterialVariantId material = findMaterial(inventory);
    if (material != null) {
      setMaterial(stack, material);
    }
    return stack;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerTables.shapedMaterialRecipeSerializer.get();
  }

  public static class Serializer implements LoggingRecipeSerializer<ShapedMaterialRecipe> {
    static final Loadable<List<MaterialVariantId>> EXTRA_MATERIALS = ShapedMaterialsRecipe.Serializer.EXTRA_MATERIALS;
    static final LoadableField<List<MaterialVariantId>, ShapedMaterialRecipe> MATERIAL_FIELD = EXTRA_MATERIALS.defaultField("extra_materials", List.of(), r -> r.extraMaterials);

    @Override
    public MapCodec<ShapedMaterialRecipe> codec() {
      return RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.STRING.optionalFieldOf("group", "").forGetter(ShapedMaterialRecipe::getGroup),
        CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(ShapedMaterialRecipe::category),
        ShapedRecipePattern.MAP_CODEC.forGetter(r -> r.ownPattern),
        ItemStack.STRICT_CODEC.fieldOf("result").forGetter(r -> r.ownResult),
        Codec.BOOL.optionalFieldOf("show_notification", true).forGetter(ShapedMaterialRecipe::showNotification),
        EXTRA_MATERIALS.codec().optionalFieldOf("extra_materials", List.of()).forGetter(r -> r.extraMaterials)
      ).apply(instance, ShapedMaterialRecipe::new));
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ShapedMaterialRecipe> streamCodec() {
      return StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);
    }

    private static void toNetwork(RegistryFriendlyByteBuf buf, ShapedMaterialRecipe recipe) {
      ShapedRecipePattern.STREAM_CODEC.encode(buf, recipe.ownPattern);
      ItemStack.STREAM_CODEC.encode(buf, recipe.ownResult);
      ByteBufCodecs.STRING_UTF8.encode(buf, recipe.getGroup());
      buf.writeEnum(recipe.category());
      buf.writeBoolean(recipe.showNotification());
      buf.writeVarInt(recipe.extraMaterials.size());
      for (MaterialVariantId id : recipe.extraMaterials) {
        ByteBufCodecs.STRING_UTF8.encode(buf, id.toString());
      }
    }

    private static ShapedMaterialRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
      ShapedRecipePattern pattern = ShapedRecipePattern.STREAM_CODEC.decode(buf);
      ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
      String group = ByteBufCodecs.STRING_UTF8.decode(buf);
      CraftingBookCategory category = buf.readEnum(CraftingBookCategory.class);
      boolean showNotification = buf.readBoolean();
      int count = buf.readVarInt();
      List<MaterialVariantId> extras = new ArrayList<>(count);
      for (int i = 0; i < count; i++) {
        extras.add(MaterialVariantId.tryParse(ByteBufCodecs.STRING_UTF8.decode(buf)));
      }
      return new ShapedMaterialRecipe(group, category, pattern, result, showNotification, extras);
    }
  }
}
