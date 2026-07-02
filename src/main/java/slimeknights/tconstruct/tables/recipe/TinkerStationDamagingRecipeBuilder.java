package slimeknights.tconstruct.tables.recipe;

import net.minecraft.core.registries.BuiltInRegistries;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;



/** Builder for tinker station damaging recipes */
public class TinkerStationDamagingRecipeBuilder extends AbstractRecipeBuilder<TinkerStationDamagingRecipeBuilder> {

  private final Ingredient ingredient;
  private final int damageAmount;

  private TinkerStationDamagingRecipeBuilder(Ingredient ingredient, int damageAmount) {
    this.ingredient = ingredient;
    this.damageAmount = damageAmount;
  }

  public static TinkerStationDamagingRecipeBuilder damage(Ingredient ingredient, int damageAmount) {
    return new TinkerStationDamagingRecipeBuilder(ingredient, damageAmount);
  }

  @Override
  public void save(RecipeOutput consumer) {
    ItemStack[] stacks = ingredient.getItems();
    if (stacks.length == 0) {
      throw new IllegalStateException("Empty ingredient not allowed");
    }
    save(consumer, BuiltInRegistries.ITEM.getKey(stacks[0].getItem()));
  }

  @Override
  public void save(RecipeOutput consumer, ResourceLocation id) {
    if (ingredient == Ingredient.EMPTY) {
      throw new IllegalStateException("Empty ingredient not allowed");
    }
    AdvancementHolder advancementId = buildOptionalAdvancement(consumer, id, "tinker_station");
    consumer.accept(id, new TinkerStationDamagingRecipe(ingredient, damageAmount), advancementId);
  }
}
