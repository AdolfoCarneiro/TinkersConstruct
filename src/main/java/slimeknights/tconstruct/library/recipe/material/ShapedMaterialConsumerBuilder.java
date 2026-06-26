package slimeknights.tconstruct.library.recipe.material;

import lombok.NoArgsConstructor;
import net.minecraft.data.recipes.RecipeOutput;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;

import java.util.ArrayList;
import java.util.List;

/** Special variant for {@link ShapedMaterialRecipe} data generation.
 *  TODO: redesign for 1.21.1 — old FinishedRecipe JSON-injection approach is obsolete. */
@Deprecated
@NoArgsConstructor(staticName = "wrap")
public class ShapedMaterialConsumerBuilder {
  private final List<MaterialVariantId> materials = new ArrayList<>();

  /** Adds a material to the builder */
  public ShapedMaterialConsumerBuilder material(MaterialVariantId material) {
    materials.add(material);
    return this;
  }

  /** Returns a RecipeOutput wrapper. TODO: implement proper wrapping for 1.21.1. */
  public RecipeOutput build(RecipeOutput output) {
    return output;
  }
}
