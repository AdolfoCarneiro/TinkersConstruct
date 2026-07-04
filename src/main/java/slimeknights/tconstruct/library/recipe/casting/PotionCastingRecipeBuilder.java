package slimeknights.tconstruct.library.recipe.casting;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.mantle.recipe.helper.TypeAwareRecipeSerializer;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;

/**
 * Builder for a potion bottle filling recipe. Takes a fluid and optional cast to create an item that copies the fluid NBT
 */
@SuppressWarnings({"WeakerAccess", "unused", "UnusedReturnValue"})
public class PotionCastingRecipeBuilder extends AbstractRecipeBuilder<PotionCastingRecipeBuilder> {
  private final Item result;
  @Nullable
  private final ModifierId modifier;
  private final TypeAwareRecipeSerializer<? extends PotionCastingRecipe> recipeSerializer;
  /** Constructs the concrete recipe instance for the modifier casting variants, null when {@link #modifier} is null */
  @Nullable
  private final ModifierCastingFactory modifierFactory;
  private Ingredient bottle = Ingredient.EMPTY;
  private FluidIngredient fluid = FluidIngredient.EMPTY;
  @Setter @Accessors(chain = true)
  private int coolingTime = 5;

  /**
   * Sets the recipe cooling time directly.
   * @param time  Cooling time in ticks
   */
  public PotionCastingRecipeBuilder setCoolingTime(int time) {
    this.coolingTime = time;
    return this;
  }

  private PotionCastingRecipeBuilder(Item result, @Nullable ModifierId modifier, TypeAwareRecipeSerializer<? extends PotionCastingRecipe> recipeSerializer, @Nullable ModifierCastingFactory modifierFactory) {
    this.result = result;
    this.modifier = modifier;
    this.recipeSerializer = recipeSerializer;
    this.modifierFactory = modifierFactory;
  }

  /* Bottle filling */

  /** Creates a new casting recipe for a bottle */
  public static PotionCastingRecipeBuilder castingRecipe(ItemLike result, TypeAwareRecipeSerializer<PotionCastingRecipe> serializer) {
    return new PotionCastingRecipeBuilder(result.asItem(), null, serializer, null);
  }

  /**
   * Creates a new casting basin recipe
   * @param result  Recipe result
   * @return  Builder instance
   */
  public static PotionCastingRecipeBuilder basinRecipe(ItemLike result) {
    return castingRecipe(result, TinkerSmeltery.basinPotionRecipeSerializer.get());
  }

  /**
   * Creates a new casting table recipe
   * @param result  Recipe result
   * @return  Builder instance
   */
  public static PotionCastingRecipeBuilder tableRecipe(ItemLike result) {
    return castingRecipe(result, TinkerSmeltery.tablePotionRecipeSerializer.get());
  }


  /* Modifier casting */

  /** Creates a new casting recipe for a bottle */
  public static PotionCastingRecipeBuilder tippingRecipe(ModifierId modifier, TypeAwareRecipeSerializer<? extends PotionCastingRecipe> serializer, ModifierCastingFactory factory) {
    return new PotionCastingRecipeBuilder(Items.AIR, modifier, serializer, factory);
  }

  /**
   * Creates a new tool potion casting basin recipe
   * @param modifier  Modifier required to cast
   * @return  Builder instance
   */
  public static PotionCastingRecipeBuilder basinTipping(ModifierId modifier) {
    return tippingRecipe(modifier, TinkerSmeltery.basinTippingRecipeSerializer.get(), TippingCastingRecipe::new);
  }

  /**
   * Creates a new tool potion casting table recipe
   * @param modifier  Recipe result
   * @return  Builder instance
   */
  public static PotionCastingRecipeBuilder tableTipping(ModifierId modifier) {
    return tippingRecipe(modifier, TinkerSmeltery.tableTippingRecipeSerializer.get(), TippingCastingRecipe::new);
  }

  /**
   * Creates a new tool potion casting basin recipe
   * @param modifier  Modifier required to cast
   * @return  Builder instance
   */
  public static PotionCastingRecipeBuilder basinClearing(ModifierId modifier) {
    return tippingRecipe(modifier, TinkerSmeltery.basinTipClearingRecipeSerializer.get(), TipClearingCastingRecipe::new);
  }

  /**
   * Creates a new tool potion casting table recipe
   * @param modifier  Recipe result
   * @return  Builder instance
   */
  public static PotionCastingRecipeBuilder tableClearing(ModifierId modifier) {
    return tippingRecipe(modifier, TinkerSmeltery.tableTipClearingRecipeSerializer.get(), TipClearingCastingRecipe::new);
  }


  /* Fluids */

  /**
   * Sets the fluid for this recipe
   * @param tagIn   Tag<Fluid> instance
   * @param amount  amount of fluid
   * @return  Builder instance
   */
  public PotionCastingRecipeBuilder setFluid(TagKey<Fluid> tagIn, int amount) {
    return this.setFluid(FluidIngredient.of(tagIn, amount));
  }

  /**
   * Sets the fluid ingredient
   * @param fluid  Fluid ingredient instance
   * @return  Builder instance
   */
  public PotionCastingRecipeBuilder setFluid(FluidIngredient fluid) {
    this.fluid = fluid;
    return this;
  }


  /* Cast */

  /**
   * Sets the cast from a tag, bottles are always consumed
   * @param tagIn     Cast tag
   * @return  Builder instance
   */
  public PotionCastingRecipeBuilder setBottle(TagKey<Item> tagIn) {
    return this.setBottle(Ingredient.of(tagIn));
  }

  /**
   * Sets the bottle from an item, bottles are always consumed
   * @param itemIn    Cast item
   * @return  Builder instance
   */
  public PotionCastingRecipeBuilder setBottle(ItemLike itemIn) {
    return this.setBottle(Ingredient.of(itemIn));
  }

  /**
   * Sets the bottle from an ingredient, bottles are always consumed
   * @param ingredient  Cast ingredient
   * @return  Builder instance
   */
  public PotionCastingRecipeBuilder setBottle(Ingredient ingredient) {
    this.bottle = ingredient;
    return this;
  }

  /**
   * Builds a recipe using the registry name as the recipe name
   * @param consumerIn  Recipe consumer
   */
  @Override
  public void save(RecipeOutput output) {
    this.save(output, BuiltInRegistries.ITEM.getKey(this.result));
  }

  @Override
  public void save(RecipeOutput output, ResourceLocation id) {
    if (this.fluid == FluidIngredient.EMPTY) {
      throw new IllegalStateException("Casting recipes require a fluid input");
    }
    if (this.coolingTime < 0) {
      throw new IllegalStateException("Cooling time is too low, must be at least 0");
    }
    net.minecraft.advancements.AdvancementHolder advancementHolder = this.buildOptionalAdvancement(output, id, "casting");
    if (modifier != null) {
      output.accept(id, modifierFactory.create(recipeSerializer, group, bottle, fluid, coolingTime, modifier), advancementHolder);
    } else {
      output.accept(id, new PotionCastingRecipe(recipeSerializer, group, bottle, fluid, result, coolingTime), advancementHolder);
    }
  }

  /**
   * Factory for constructing the concrete modifier casting recipe (tipping or tip clearing), so the built recipe's
   * Java type matches the {@link TypeAwareRecipeSerializer} it was constructed with. Each concrete recipe's own
   * {@link net.minecraft.world.item.crafting.RecipeSerializer} dispatches to a {@code RecordLoadable} keyed by the
   * recipe's actual class, so passing the wrong concrete type throws a {@link ClassCastException} during datagen.
   */
  @FunctionalInterface
  private interface ModifierCastingFactory {
    PotionCastingRecipe create(TypeAwareRecipeSerializer<?> serializer, String group, Ingredient tool, FluidIngredient fluid, int coolingTime, ModifierId modifier);
  }
}
