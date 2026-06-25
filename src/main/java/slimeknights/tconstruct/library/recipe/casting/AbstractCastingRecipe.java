package slimeknights.tconstruct.library.recipe.casting;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import slimeknights.mantle.data.loadable.common.IngredientLoadable;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.mantle.data.loadable.primitive.BooleanLoadable;

import javax.annotation.Nonnull;
// ResourceLocation import removed: id no longer lives in the recipe (it's in RecipeHolder)

/** Shared logic between item and material casting */
public abstract class AbstractCastingRecipe implements ICastingRecipe {
  /* Common fields */
  protected static final LoadableField<Ingredient,AbstractCastingRecipe> CAST_FIELD = IngredientLoadable.ALLOW_EMPTY.defaultField("cast", Ingredient.EMPTY, AbstractCastingRecipe::getCast);
  protected static final LoadableField<Boolean,AbstractCastingRecipe> CAST_CONSUMED_FIELD = BooleanLoadable.INSTANCE.defaultField("cast_consumed", false, false, AbstractCastingRecipe::isConsumed);
  protected static final LoadableField<Boolean,AbstractCastingRecipe> SWITCH_SLOTS_FIELD = BooleanLoadable.INSTANCE.defaultField("switch_slots", false, false, AbstractCastingRecipe::switchSlots);

  @Nonnull
  private final RecipeType<?> type;
  private final String group;
  /** 'cast' item for recipe (doesn't have to be an actual 'cast') */
  private final Ingredient cast;
  private final boolean consumed;
  private final boolean switchSlots;

  protected AbstractCastingRecipe(RecipeType<?> type, String group, Ingredient cast, boolean consumed, boolean switchSlots) {
    this.type = type;
    this.group = group;
    this.cast = cast;
    this.consumed = consumed;
    this.switchSlots = switchSlots;
  }

  @Nonnull
  public RecipeType<?> getType() {
    return type;
  }

  public String getGroup() {
    return group;
  }

  public Ingredient getCast() {
    return cast;
  }

  public boolean isConsumed() {
    return consumed;
  }

  public boolean switchSlots() {
    return switchSlots;
  }

  @Override
  public NonNullList<Ingredient> getIngredients() {
    return NonNullList.of(Ingredient.EMPTY, this.cast);
  }
}
