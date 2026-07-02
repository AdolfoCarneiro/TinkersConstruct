package slimeknights.tconstruct.plugin.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import slimeknights.mantle.client.SafeClientAccess;
import slimeknights.tconstruct.library.recipe.ingredient.MaterialValueIngredient;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipeCache;
import slimeknights.tconstruct.library.recipe.material.ShapedMaterialRecipe;
import slimeknights.tconstruct.plugin.jei.material.MaterialsCraftingExtension;
import slimeknights.tconstruct.plugin.jei.material.ShapedMaterialsExtension;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.stream.IntStream;

/**
 * Logic to show {@link ShapedMaterialRecipe} in JEI
 * @deprecated use {@link ShapedMaterialsExtension}
 */
@Deprecated
public class ShapedMaterialExtension implements ICraftingCategoryExtension<ShapedMaterialRecipe> {
  /** Singleton extension instance, JEI 16.0.0+ extensions receive the recipe per call instead of wrapping one */
  public static final ShapedMaterialExtension INSTANCE = new ShapedMaterialExtension();

  private final Map<ShapedMaterialRecipe, Display> cache = new WeakHashMap<>();

  private ShapedMaterialExtension() {}

  /** Computes, and caches, the display data for the given recipe */
  private Display getDisplay(ShapedMaterialRecipe recipe) {
    return cache.computeIfAbsent(recipe, r -> {
      MaterialValueIngredient materials = r.getMaterial();
      ItemStack plainResult = r.getResultItem(Objects.requireNonNull(SafeClientAccess.getRegistryAccess()));
      List<ItemStack> result;
      if (materials != null) {
        result = MaterialRecipeCache.getAllRecipes().stream().filter(materials::test).flatMap(mat -> {
          ItemStack stack = plainResult.copy();
          r.setMaterial(stack, mat.getMaterial().getVariant());
          // add one copy of the stack per item in the nested ingredient, so the lengths match up
          return IntStream.range(0, mat.getIngredient().getItems().length).mapToObj(i -> stack);
        }).toList();
      } else {
        result = List.of(plainResult);
      }
      List<Ingredient> inputs = r.getIngredients();
      int[] materialSlots = IntStream.range(0, inputs.size()).filter(i -> {
        Ingredient ingredient = inputs.get(i);
        return ingredient.isCustom() && ingredient.getCustomIngredient() instanceof MaterialValueIngredient;
      }).toArray();
      return new Display(plainResult, result, materialSlots);
    });
  }

  @Override
  public int getWidth(RecipeHolder<ShapedMaterialRecipe> recipeHolder) {
    return recipeHolder.value().getWidth();
  }

  @Override
  public int getHeight(RecipeHolder<ShapedMaterialRecipe> recipeHolder) {
    return recipeHolder.value().getHeight();
  }

  @Override
  public void setRecipe(RecipeHolder<ShapedMaterialRecipe> recipeHolder, IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focusGroup) {
    ShapedMaterialRecipe recipe = recipeHolder.value();
    Display display = getDisplay(recipe);
    MaterialsCraftingExtension.setRecipe(this, recipeHolder, builder, craftingGridHelper, recipe, display.result(), display.plainResult(), display.materialSlots());
  }

  /** Cached per-recipe display data */
  private record Display(ItemStack plainResult, List<ItemStack> result, int[] materialSlots) {}
}
