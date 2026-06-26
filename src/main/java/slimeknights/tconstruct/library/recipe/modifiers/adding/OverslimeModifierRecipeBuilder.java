package slimeknights.tconstruct.library.recipe.modifiers.adding;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.tconstruct.common.TinkerTags;


/**
 * Builder for overslime recipes
 */
public class OverslimeModifierRecipeBuilder extends AbstractRecipeBuilder<OverslimeModifierRecipeBuilder> {
  private Ingredient tools = Ingredient.of(TinkerTags.Items.DURABILITY);
  private final Ingredient ingredient;
  private final int restoreAmount;

  private OverslimeModifierRecipeBuilder(Ingredient ingredient, int restoreAmount) {
    this.ingredient = ingredient;
    this.restoreAmount = restoreAmount;
  }

  public static OverslimeModifierRecipeBuilder modifier(Ingredient ingredient, int restoreAmount) {
    return new OverslimeModifierRecipeBuilder(ingredient, restoreAmount);
  }

  /** Creates a new builder for the given item */
  public static OverslimeModifierRecipeBuilder modifier(ItemLike item, int restoreAmount) {
    return modifier(Ingredient.of(item), restoreAmount);
  }

  public OverslimeModifierRecipeBuilder setTools(Ingredient tools) {
    this.tools = tools;
    return this;
  }

  @Override
  public void save(RecipeOutput output) {
    ItemStack[] stacks = ingredient.getItems();
    if (stacks.length == 0) {
      throw new IllegalStateException("Empty ingredient not allowed");
    }
    save(output, BuiltInRegistries.ITEM.getKey(stacks[0].getItem()));
  }

  @Override
  public void save(RecipeOutput output, ResourceLocation id) {
    if (ingredient == Ingredient.EMPTY) {
      throw new IllegalStateException("Empty ingredient not allowed");
    }
    AdvancementHolder advancementId = buildOptionalAdvancement(output, id, "modifiers");
    output.accept(id, new OverslimeModifierRecipe(tools, ingredient, restoreAmount), advancementId);
  }

  /** Creates a crafting table overslime repair recipe */
  public OverslimeModifierRecipeBuilder saveCrafting(RecipeOutput output, ResourceLocation id) {
    if (ingredient == Ingredient.EMPTY) {
      throw new IllegalStateException("Empty ingredient not allowed");
    }
    AdvancementHolder advancementId = buildOptionalAdvancement(output, id, "modifiers");
    output.accept(id, new OverslimeCraftingTableRecipe(tools, ingredient, restoreAmount), advancementId);
    return this;
  }
}
