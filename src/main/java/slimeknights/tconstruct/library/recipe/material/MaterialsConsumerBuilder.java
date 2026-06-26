package slimeknights.tconstruct.library.recipe.material;

import net.minecraft.data.recipes.RecipeOutput;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;

import java.util.ArrayList;
import java.util.List;

/** Special variant for {@link ShapedMaterialsRecipe} and {@link ShapelessMaterialsRecipe} data generation.
 *  TODO: redesign for 1.21.1 — old FinishedRecipe JSON-injection approach is obsolete. */
public class MaterialsConsumerBuilder {
  private final String parts;
  private final int partCount;
  private final List<MaterialVariantId> materials = new ArrayList<>();

  private MaterialsConsumerBuilder(String parts, int partCount) {
    this.parts = parts;
    this.partCount = partCount;
  }

  /** Creates a new shaped recipe with the given ingredients as parts */
  public static MaterialsConsumerBuilder shaped(String parts) {
    if (parts.isEmpty()) {
      throw new IllegalArgumentException("Parts may not be empty");
    }
    return new MaterialsConsumerBuilder(parts, 0);
  }

  /** Creates a new shapeless recipe with the first ingredients as parts */
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

  /** Returns a RecipeOutput wrapper. TODO: implement proper wrapping for 1.21.1. */
  public RecipeOutput build(RecipeOutput output) {
    return output;
  }
}
