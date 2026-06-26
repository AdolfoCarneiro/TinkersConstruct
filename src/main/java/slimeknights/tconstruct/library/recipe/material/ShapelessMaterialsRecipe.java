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
import net.minecraft.world.item.crafting.ShapelessRecipe;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.mantle.recipe.helper.LoggingRecipeSerializer;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.tables.TinkerTables;

import java.util.ArrayList;
import java.util.List;

/**
 * Shapeless recipe with a number of {@link slimeknights.tconstruct.library.recipe.ingredient.MaterialIngredient} and
 * {@link slimeknights.tconstruct.library.recipe.ingredient.MaterialValueIngredient} to set the materials of the result.
 */
public class ShapelessMaterialsRecipe extends ShapelessRecipe implements MaterialsCraftingTableRecipe {
  /** Number of parts to match */
  private final int partCount;

  @Override
  public int getPartCount() {
    return partCount;
  }
  /** List of additional materials to add beyond the parts */
  private final List<MaterialVariantId> extraMaterials;
  /** Stored result for codec/network round-trip */
  private final ItemStack ownResult;

  public ShapelessMaterialsRecipe(String group, CraftingBookCategory category, ItemStack result, NonNullList<Ingredient> ingredients, int partCount, List<MaterialVariantId> extraMaterials) {
    super(group, category, result, ingredients);
    this.ownResult = result;
    this.partCount = partCount;
    this.extraMaterials = extraMaterials;
  }

  @Override
  public List<Ingredient> getParts() {
    return getIngredients();
  }

  @Override
  public List<MaterialVariantId> getExtraMaterials() {
    return extraMaterials;
  }

  /** Sets the material for the given stack */
  @Override
  public void setMaterial(ItemStack stack, MaterialVariantId material) {
    ShapedMaterialsRecipe.setMaterial(stack, material, extraMaterials);
  }

  @Override
  public ItemStack assemble(CraftingInput inventory, HolderLookup.Provider registryAccess) {
    return ShapedMaterialsRecipe.assemble(super.assemble(inventory, registryAccess), inventory, getIngredients(), partCount, false, extraMaterials);
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerTables.shapelessMaterialsRecipeSerializer.get();
  }

  public static class Serializer implements LoggingRecipeSerializer<ShapelessMaterialsRecipe> {
    static final Loadable<List<MaterialVariantId>> EXTRA_MATERIALS = ShapedMaterialsRecipe.Serializer.EXTRA_MATERIALS;
    static final LoadableField<List<MaterialVariantId>, ShapelessMaterialsRecipe> MATERIAL_FIELD = EXTRA_MATERIALS.defaultField("extra_materials", List.of(), r -> r.extraMaterials);

    @Override
    public MapCodec<ShapelessMaterialsRecipe> codec() {
      return RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.STRING.optionalFieldOf("group", "").forGetter(ShapelessMaterialsRecipe::getGroup),
        CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(ShapelessMaterialsRecipe::category),
        ItemStack.STRICT_CODEC.fieldOf("result").forGetter(r -> r.ownResult),
        Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").forGetter(r -> new ArrayList<>(r.getIngredients())),
        Codec.intRange(1, 9).fieldOf("parts").forGetter(r -> r.partCount),
        EXTRA_MATERIALS.codec().optionalFieldOf("extra_materials", List.of()).forGetter(r -> r.extraMaterials)
      ).apply(instance, (group, category, result, ingredients, parts, extras) -> {
        NonNullList<Ingredient> nl = NonNullList.copyOf(ingredients);
        return new ShapelessMaterialsRecipe(group, category, result, nl, parts, extras);
      }));
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ShapelessMaterialsRecipe> streamCodec() {
      return StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);
    }

    private static void toNetwork(RegistryFriendlyByteBuf buf, ShapelessMaterialsRecipe recipe) {
      ItemStack.STREAM_CODEC.encode(buf, recipe.ownResult);
      ByteBufCodecs.STRING_UTF8.encode(buf, recipe.getGroup());
      buf.writeEnum(recipe.category());
      ByteBufCodecs.collection(ArrayList::new, Ingredient.CONTENTS_STREAM_CODEC).encode(buf, new ArrayList<>(recipe.getIngredients()));
      buf.writeByte(recipe.partCount);
      buf.writeVarInt(recipe.extraMaterials.size());
      for (MaterialVariantId id : recipe.extraMaterials) {
        ByteBufCodecs.STRING_UTF8.encode(buf, id.toString());
      }
    }

    private static ShapelessMaterialsRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
      ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
      String group = ByteBufCodecs.STRING_UTF8.decode(buf);
      CraftingBookCategory category = buf.readEnum(CraftingBookCategory.class);
      List<Ingredient> decoded = ByteBufCodecs.collection(ArrayList::new, Ingredient.CONTENTS_STREAM_CODEC).decode(buf);
      NonNullList<Ingredient> ingredients = NonNullList.copyOf(decoded);
      int partCount = buf.readByte();
      int count = buf.readVarInt();
      List<MaterialVariantId> extras = new ArrayList<>(count);
      for (int i = 0; i < count; i++) {
        extras.add(MaterialVariantId.tryParse(ByteBufCodecs.STRING_UTF8.decode(buf)));
      }
      return new ShapelessMaterialsRecipe(group, category, result, ingredients, partCount, extras);
    }
  }
}
