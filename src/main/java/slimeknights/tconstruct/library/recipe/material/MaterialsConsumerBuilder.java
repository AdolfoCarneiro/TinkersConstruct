package slimeknights.tconstruct.library.recipe.material;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.neoforged.neoforge.common.conditions.ICondition;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Special variant of a {@link RecipeOutput} wrapper for {@link ShapedMaterialsRecipe} and {@link ShapelessMaterialsRecipe} data generation.
 * <p>
 * Callers build the underlying recipe using the normal vanilla {@link net.minecraft.data.recipes.ShapedRecipeBuilder}/
 * {@link net.minecraft.data.recipes.ShapelessRecipeBuilder} and pass the {@link RecipeOutput} returned by {@link #build(RecipeOutput)}
 * into that builder's {@code save(...)}. This wrapper intercepts the {@link ShapedRecipe}/{@link ShapelessRecipe} the vanilla builder
 * produces, converts it into the equivalent materials recipe (resolving the "parts" pattern symbols back to their {@link Ingredient}s
 * via the recipe's own key), and forwards the converted recipe to the real output.
 */
public class MaterialsConsumerBuilder {
  private final String parts;
  private final int partCount;
  private final List<MaterialVariantId> materials = new ArrayList<>();

  private MaterialsConsumerBuilder(String parts, int partCount) {
    this.parts = parts;
    this.partCount = partCount;
  }

  /** Creates a new shaped recipe with the given pattern symbols as parts */
  public static MaterialsConsumerBuilder shaped(String parts) {
    if (parts.isEmpty()) {
      throw new IllegalArgumentException("Parts may not be empty");
    }
    return new MaterialsConsumerBuilder(parts, 0);
  }

  /** Creates a new shapeless recipe with the first {@code parts} ingredients as parts */
  public static MaterialsConsumerBuilder shapeless(int parts) {
    if (parts <= 0) {
      throw new IllegalArgumentException("Parts must be greater than 0");
    }
    return new MaterialsConsumerBuilder("", parts);
  }

  /** Adds a material to the builder */
  public MaterialsConsumerBuilder material(MaterialVariantId material) {
    materials.add(material);
    return this;
  }

  /** Wraps the given output, converting the shaped/shapeless recipe it receives into the materials variant */
  public RecipeOutput build(RecipeOutput output) {
    return new Wrapped(output, List.copyOf(materials), parts, partCount);
  }

  private record Wrapped(RecipeOutput parent, List<MaterialVariantId> materials, String parts, int partCount) implements RecipeOutput {
    @Override
    public Advancement.Builder advancement() {
      return parent.advancement();
    }

    @Override
    public void accept(ResourceLocation id, Recipe<?> recipe, @Nullable AdvancementHolder advancement, ICondition... conditions) {
      parent.accept(id, convert(recipe), advancement, conditions);
    }

    /** Converts the vanilla recipe produced by the wrapped builder into the corresponding materials recipe */
    private Recipe<?> convert(Recipe<?> recipe) {
      if (!parts.isEmpty() && recipe instanceof ShapedRecipe shaped) {
        return toShapedMaterials(shaped);
      }
      if (partCount > 0 && recipe instanceof ShapelessRecipe shapeless) {
        return toShapelessMaterials(shapeless);
      }
      throw new IllegalArgumentException("MaterialsConsumerBuilder cannot convert recipe of type " + recipe.getClass());
    }

    private ShapedMaterialsRecipe toShapedMaterials(ShapedRecipe shaped) {
      ShapedRecipePattern pattern = shaped.pattern;
      Map<Character, Ingredient> key = pattern.data
        .orElseThrow(() -> new IllegalStateException("Shaped recipe pattern is missing its key data, cannot resolve parts"))
        .key();
      List<Ingredient> partsList = new ArrayList<>(parts.length());
      for (int i = 0; i < parts.length(); i++) {
        char symbol = parts.charAt(i);
        Ingredient ingredient = key.get(symbol);
        if (ingredient == null) {
          throw new IllegalArgumentException("Parts references symbol '" + symbol + "' but it's not defined in the key");
        }
        partsList.add(ingredient);
      }
      return new ShapedMaterialsRecipe(shaped.getGroup(), shaped.category(), pattern, shaped.result, shaped.showNotification(), partsList, materials);
    }

    private ShapelessMaterialsRecipe toShapelessMaterials(ShapelessRecipe shapeless) {
      NonNullList<Ingredient> ingredients = shapeless.getIngredients();
      return new ShapelessMaterialsRecipe(shapeless.getGroup(), shapeless.category(), shapeless.result, ingredients, partCount, materials);
    }
  }
}
