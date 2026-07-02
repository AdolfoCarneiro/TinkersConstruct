package slimeknights.tconstruct.library.recipe.casting.material;

import lombok.experimental.Accessors;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.mantle.recipe.helper.TypeAwareRecipeSerializer;
import slimeknights.tconstruct.library.json.predicate.material.MaterialPredicate;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;


/** Builder for {@link PartSwapCastingRecipe} */
@Accessors(chain = true)
public class PartSwapCastingRecipeBuilder extends AbstractRecipeBuilder<PartSwapCastingRecipeBuilder> {
  private final Ingredient tools;
  private final int itemCost;
  private final TypeAwareRecipeSerializer<PartSwapCastingRecipe> recipeSerializer;
  private int index = 0;
  private IJsonPredicate<MaterialVariantId> allowedMaterials = MaterialPredicate.ANY;

  private PartSwapCastingRecipeBuilder(Ingredient tools, int itemCost, TypeAwareRecipeSerializer<PartSwapCastingRecipe> recipeSerializer) {
    this.tools = tools;
    this.itemCost = itemCost;
    this.recipeSerializer = recipeSerializer;
  }

  private static PartSwapCastingRecipeBuilder castingRecipe(Ingredient tools, int itemCost, TypeAwareRecipeSerializer<PartSwapCastingRecipe> recipeSerializer) {
    return new PartSwapCastingRecipeBuilder(tools, itemCost, recipeSerializer);
  }

  /** Sets the index of the part to swap */
  public PartSwapCastingRecipeBuilder index(int index) {
    this.index = index;
    return this;
  }

  /** Sets the predicate for materials allowed to be swapped in */
  public PartSwapCastingRecipeBuilder setAllowedMaterials(IJsonPredicate<MaterialVariantId> allowedMaterials) {
    this.allowedMaterials = allowedMaterials;
    return this;
  }

  /**
   * Creates a new part swapping recipe
   * @param tools     List of tools
   * @param itemCost  Amount needed to cast to swap
   * @return  Builder instance
   */
  public static PartSwapCastingRecipeBuilder basinRecipe(Ingredient tools, int itemCost) {
    return castingRecipe(tools, itemCost, TinkerSmeltery.basinPartSwappingSerializer.get());
  }

  /**
   * Creates a new part swapping recipe
   * @param itemCost  Amount needed to cast to swap
   * @return  Builder instance
   */
  public static PartSwapCastingRecipeBuilder tableRecipe(Ingredient tools, int itemCost) {
    return castingRecipe(tools, itemCost, TinkerSmeltery.tablePartSwappingSerializer.get());
  }

  @SuppressWarnings("deprecation")
  @Override
  public void save(RecipeOutput output) {
    save(output, BuiltInRegistries.ITEM.getKey(tools.getItems()[0].getItem()));
  }

  @Override
  public void save(RecipeOutput output, ResourceLocation id) {
    output.accept(id, new PartSwapCastingRecipe(recipeSerializer, group, tools, itemCost, index, allowedMaterials), this.buildOptionalAdvancement(output, id, "materials"));
  }
}
