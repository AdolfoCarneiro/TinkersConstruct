package slimeknights.tconstruct.library.recipe.casting.material;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;

import javax.annotation.Nullable;

import static slimeknights.tconstruct.library.recipe.melting.IMeltingRecipe.getTemperature;

/**
 * Builder to make parts and composites castable
 */
@Accessors(chain = true)
public class MaterialFluidRecipeBuilder extends AbstractRecipeBuilder<MaterialFluidRecipeBuilder> {
  /** Output material ID */
  private final MaterialVariantId outputId;
  /** Fluid used for casting */
  @Setter
  private FluidIngredient fluid = FluidIngredient.EMPTY;
  /** Temperature for cooling time calculations */
  @Setter
  private int temperature = -1;
  /** Material base for composite */
  @Setter @Nullable
  private MaterialVariantId inputId;

  private MaterialFluidRecipeBuilder(MaterialVariantId outputId) {
    this.outputId = outputId;
  }

  /** Creates a new builder for the given output material */
  public static MaterialFluidRecipeBuilder material(MaterialVariantId outputId) {
    return new MaterialFluidRecipeBuilder(outputId);
  }

  /** Sets the fluid for this recipe */
  public MaterialFluidRecipeBuilder setFluid(FluidIngredient fluid) {
    this.fluid = fluid;
    return this;
  }

  /** Sets the cooling temperature for this recipe */
  public MaterialFluidRecipeBuilder setTemperature(int temperature) {
    this.temperature = temperature;
    return this;
  }

  /** Sets the input material base for this composite */
  public MaterialFluidRecipeBuilder setInputId(@Nullable MaterialVariantId inputId) {
    this.inputId = inputId;
    return this;
  }

  /**
   * Sets the fluid for this recipe, and cooling time if unset.
   * @param fluidStack  Fluid input
   * @return  Builder instance
   */
  public MaterialFluidRecipeBuilder setFluidAndTemp(FluidStack fluidStack) {
    this.fluid = FluidIngredient.of(fluidStack);
    if (this.temperature == -1) {
      this.temperature = getTemperature(fluidStack);
    }
    return this;
  }

  /**
   * Sets the fluid for this recipe, and cooling time
   * @param tagIn   Tag<Fluid> instance
   * @param amount  Fluid amount
   */
  public MaterialFluidRecipeBuilder setFluid(TagKey<Fluid> tagIn, int amount) {
    setFluid(FluidIngredient.of(tagIn, amount));
    return this;
  }

  @Override
  public void save(RecipeOutput output) {
    save(output, outputId.getId());
  }

  @Override
  public void save(RecipeOutput output, ResourceLocation id) {
    if (this.fluid == FluidIngredient.EMPTY) {
      throw new IllegalStateException("Material fluid recipes require a fluid input");
    }
    if (this.temperature < 0) {
      throw new IllegalStateException("Temperature is too low, must be at least 0");
    }
    net.minecraft.advancements.AdvancementHolder advancementHolder = this.buildOptionalAdvancement(output, id, "materials");
    output.accept(id, new MaterialFluidRecipe(fluid, temperature, inputId, outputId), advancementHolder);
  }
}
