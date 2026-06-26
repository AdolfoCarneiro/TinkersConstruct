package slimeknights.tconstruct.library.recipe.ingredient;

import com.mojang.serialization.MapCodec;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.util.typed.TypedMap;
import slimeknights.tconstruct.library.json.predicate.material.MaterialPredicate;
import slimeknights.tconstruct.library.json.predicate.material.MaterialPredicateField;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipeCache;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Ingredient matching material items with the given value. Typically, matches ingots or blocks
 */
@Getter
@RequiredArgsConstructor
public class MaterialValueIngredient implements ICustomIngredient {
  private final IJsonPredicate<MaterialVariantId> material;
  private final float minValue;
  private final float maxValue;
  @Getter(AccessLevel.NONE)
  @Nullable
  private ItemStack[] items;

  private static final RecordLoadable<MaterialValueIngredient> LOADABLE = RecordLoadable.create(
    new MaterialPredicateField<MaterialValueIngredient>("material", MaterialValueIngredient::getMaterial),
    FloatLoadable.ANY.<MaterialValueIngredient>defaultField("min_value", 0f, MaterialValueIngredient::getMinValue),
    FloatLoadable.ANY.<MaterialValueIngredient>defaultField("max_value", Float.POSITIVE_INFINITY, MaterialValueIngredient::getMaxValue),
    MaterialValueIngredient::new
  );
  public static final MapCodec<MaterialValueIngredient> CODEC = LOADABLE.mapCodec(TypedMap.EMPTY);
  public static final StreamCodec<RegistryFriendlyByteBuf, MaterialValueIngredient> STREAM_CODEC = LOADABLE;

  /** Creates an ingredient matching a range of values */
  public static MaterialValueIngredient of(IJsonPredicate<MaterialVariantId> materials, float minValue, float maxValue) {
    return new MaterialValueIngredient(materials, minValue, maxValue);
  }

  /** Creates an ingredient matching an exact value */
  public static MaterialValueIngredient of(IJsonPredicate<MaterialVariantId> materials, float value) {
    return of(materials, value, value);
  }

  /** Checks the given material recipe against our filters */
  public boolean test(MaterialRecipe material) {
    float value = material.getValue() / (float) material.getNeeded();
    return minValue <= value && value <= maxValue && this.material.matches(material.getMaterial().getVariant());
  }

  @Override
  public boolean test(@Nullable ItemStack stack) {
    if (stack == null) {
      return false;
    }
    MaterialRecipe recipe = MaterialRecipeCache.findRecipe(stack);
    return recipe != MaterialRecipe.EMPTY && test(recipe);
  }

  @Override
  public Stream<ItemStack> getItems() {
    if (items == null) {
      items = MaterialRecipeCache.getAllRecipes().stream()
        .filter(this::test)
        .flatMap(material -> Arrays.stream(material.getIngredient().getItems()))
        .toArray(ItemStack[]::new);
    }
    return Arrays.stream(items);
  }

  @Override
  public boolean isSimple() {
    return true;
  }

  @Override
  public IngredientType<?> getType() {
    return TinkerRecipeTypes.MATERIAL_VALUE_INGREDIENT.get();
  }


  /* Helpers for ShapedMaterialRecipe */

  private boolean contains(MaterialValueIngredient other) {
    return this.minValue <= other.minValue && other.maxValue <= this.maxValue;
  }

  /** Creates an ingredient that matches anything either of the two ingredients matches */
  public MaterialValueIngredient merge(MaterialValueIngredient other) {
    if (this == other) return this;

    IJsonPredicate<MaterialVariantId> predicate = this.material;
    if (this.material.equals(other.material)) {
      if (this.contains(other)) {
        return this;
      }
      if (other.contains(this)) {
        return other;
      }
    } else {
      predicate = MaterialPredicate.or(this.material, other.material);
    }
    return new MaterialValueIngredient(predicate, Math.min(this.minValue, other.minValue), Math.max(this.maxValue, other.maxValue));
  }

  /** Gets the material matching this recipe */
  @Nullable
  public MaterialVariantId getMaterialForStack(ItemStack stack) {
    MaterialRecipe recipe = MaterialRecipeCache.findRecipe(stack);
    return recipe != MaterialRecipe.EMPTY && test(recipe) ? recipe.getMaterial().getVariant() : null;
  }
}
