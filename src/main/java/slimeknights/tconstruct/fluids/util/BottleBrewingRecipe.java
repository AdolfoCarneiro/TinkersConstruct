package slimeknights.tconstruct.fluids.util;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.brewing.BrewingRecipe;

/**
 * Recipe for transforming a bottle using an explicit brewing ingredient.
 * In 1.21.1, PotionBrewing.Mix is package-private so we accept the ingredient directly.
 */
public class BottleBrewingRecipe extends BrewingRecipe {
  public BottleBrewingRecipe(Ingredient input, Ingredient brewingIngredient, ItemStack output) {
    super(input, brewingIngredient, output);
  }
}
