package slimeknights.tconstruct.library.recipe.modifiers.severing;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.ingredient.EntityIngredient;

import javax.annotation.Nullable;

/** Builder for entity melting recipes */
public class SeveringRecipeBuilder extends AbstractRecipeBuilder<SeveringRecipeBuilder> {
  private final EntityIngredient ingredient;
  private final ItemOutput output;
  private float baseChance = 0.05f;
  private float lootingBonus = 0.01f;
  @Nullable
  private ItemOutput childOutput = null;

  private SeveringRecipeBuilder(EntityIngredient ingredient, ItemOutput output) {
    this.ingredient = ingredient;
    this.output = output;
  }

  public static SeveringRecipeBuilder severing(EntityIngredient ingredient, ItemOutput output) {
    return new SeveringRecipeBuilder(ingredient, output);
  }

  /** Creates a new builder from an item */
  public static SeveringRecipeBuilder severing(EntityIngredient ingredient, ItemLike output) {
    return severing(ingredient, ItemOutput.fromItem(output));
  }

  public SeveringRecipeBuilder setBaseChance(float baseChance) { this.baseChance = baseChance; return this; }
  public SeveringRecipeBuilder setLootingBonus(float lootingBonus) { this.lootingBonus = lootingBonus; return this; }
  public SeveringRecipeBuilder setChildOutput(ItemOutput childOutput) { this.childOutput = childOutput; return this; }

  /** Doubles the drop chances for this rare mob */
  public SeveringRecipeBuilder rareMob() {
    baseChance = 0.1f;
    lootingBonus = 0.02f;
    return this;
  }

  public SeveringRecipeBuilder noChildOutput() {
    return setChildOutput(ItemOutput.EMPTY);
  }

  @Override
  public void save(RecipeOutput output) {
    save(output, BuiltInRegistries.ITEM.getKey(this.output.get().getItem()));
  }

  @Override
  public void save(RecipeOutput output, ResourceLocation id) {
    AdvancementHolder advancementId = this.buildOptionalAdvancement(output, id, "severing");
    if (childOutput != null) {
      output.accept(id, new AgeableSeveringRecipe(ingredient, this.output, childOutput, baseChance, lootingBonus), advancementId);
    } else {
      output.accept(id, new SeveringRecipe(ingredient, this.output, baseChance, lootingBonus), advancementId);
    }
  }
}
