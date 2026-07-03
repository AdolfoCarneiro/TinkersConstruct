package slimeknights.tconstruct.tools.recipe.severing;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.tconstruct.library.recipe.modifiers.severing.SeveringRecipe;

import java.util.Objects;
import java.util.function.BiFunction;

/** Builder for severing recipes that have only the base chance and looting bonus as fields */
public class SpecialSeveringRecipeBuilder extends AbstractRecipeBuilder<SpecialSeveringRecipeBuilder> {
  private final BiFunction<Float,Float,? extends SeveringRecipe> factory;
  private float baseChance = 0.05f;
  private float lootingBonus = 0.01f;

  private SpecialSeveringRecipeBuilder(BiFunction<Float,Float,? extends SeveringRecipe> factory) {
    this.factory = factory;
  }

  /** Creates a new builder for the given recipe factory. */
  public static SpecialSeveringRecipeBuilder serializer(BiFunction<Float,Float,? extends SeveringRecipe> factory) {
    return new SpecialSeveringRecipeBuilder(factory);
  }

  /** Sets the base chance of this recipe */
  public SpecialSeveringRecipeBuilder setBaseChance(float baseChance) {
    this.baseChance = baseChance;
    return this;
  }

  /** Sets the looting bonus of this recipe */
  public SpecialSeveringRecipeBuilder setLootingBonus(float lootingBonus) {
    this.lootingBonus = lootingBonus;
    return this;
  }

  /** Doubles the drop chances for this rare mob */
  public SpecialSeveringRecipeBuilder rareMob() {
    baseChance = 0.1f;
    lootingBonus = 0.02f;
    return this;
  }

  @Override
  public void save(RecipeOutput consumer) {
    save(consumer, Objects.requireNonNull(BuiltInRegistries.RECIPE_SERIALIZER.getKey(factory.apply(baseChance, lootingBonus).getSerializer())));
  }

  @Override
  public void save(RecipeOutput consumer, ResourceLocation id) {
    AdvancementHolder advancementId = buildOptionalAdvancement(consumer, id, "severing");
    consumer.accept(id, factory.apply(baseChance, lootingBonus), advancementId);
  }
}
