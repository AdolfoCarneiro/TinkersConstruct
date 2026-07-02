package slimeknights.tconstruct.library.recipe.partbuilder;

import lombok.experimental.Accessors;
import net.minecraft.core.registries.BuiltInRegistries;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;



/**
 * Builder for a material item part crafting recipe
 */
@Accessors(chain = true)
public class PartRecipeBuilder extends AbstractRecipeBuilder<PartRecipeBuilder> {
  private final IMaterialItem output;
  private final int outputAmount;
  private int cost = 1;
  private ResourceLocation pattern = null;
  private Ingredient patternItem = IPartBuilderRecipe.DEFAULT_PATTERNS;
  private boolean allowUncraftable = false;

  private PartRecipeBuilder(IMaterialItem output, int outputAmount) {
    this.output = output;
    this.outputAmount = outputAmount;
  }

  /**
   * Creates a new part recipe with a custom output amount
   * @param output       Output item
   * @param outputAmount Number of items output
   * @return  Builder instance
   */
  public static PartRecipeBuilder partRecipe(IMaterialItem output, int outputAmount) {
    return new PartRecipeBuilder(output, outputAmount);
  }

  /**
   * Creates a new part recipe that outputs a single item
   * @param output  Output item
   * @return  Builder instance
   */
  public static PartRecipeBuilder partRecipe(IMaterialItem output) {
    return partRecipe(output, 1);
  }

  /** Sets the pattern item cost of the recipe */
  public PartRecipeBuilder setCost(int cost) {
    this.cost = cost;
    return this;
  }

  /** Sets the pattern used for datagen purposes */
  public PartRecipeBuilder setPattern(ResourceLocation pattern) {
    this.pattern = pattern;
    return this;
  }

  /** Sets the ingredient used for the pattern item */
  public PartRecipeBuilder setPatternItem(Ingredient patternItem) {
    this.patternItem = patternItem;
    return this;
  }

  /** Sets whether the recipe should be craftable without a pattern */
  public PartRecipeBuilder setAllowUncraftable(boolean allowUncraftable) {
    this.allowUncraftable = allowUncraftable;
    return this;
  }

  @Override
  public void save(RecipeOutput consumerIn) {
    this.save(consumerIn, BuiltInRegistries.ITEM.getKey(this.output.asItem()));
  }

  @Override
  public void save(RecipeOutput consumerIn, ResourceLocation id) {
    if (this.outputAmount <= 0) {
      throw new IllegalStateException("recipe " + id + " must output at least 1");
    }
    if (this.cost <= 0) {
      throw new IllegalStateException("recipe " + id + " has no cost associated with it");
    }
    if (this.pattern == null) {
      throw new IllegalStateException("recipe " + id + " has no pattern associated with it");
    }
    AdvancementHolder advancementId = buildOptionalAdvancement(consumerIn, id, "parts");
    consumerIn.accept(id, new PartRecipe(group, new Pattern(pattern), patternItem, cost, allowUncraftable, output, outputAmount), advancementId);
  }
}
