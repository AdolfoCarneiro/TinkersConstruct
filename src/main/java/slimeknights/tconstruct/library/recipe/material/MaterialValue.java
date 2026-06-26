package slimeknights.tconstruct.library.recipe.material;

import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;

/**
 * Constant material value used for tool parts
 */
public class MaterialValue implements IMaterialValue {
  private final MaterialVariant material;

  @Override
  public MaterialVariant getMaterial() {
    return material;
  }
  private final int value;

  public MaterialValue(MaterialVariant material, int value) {
    this.material = material;
    this.value = value;
  }

  public MaterialValue(MaterialVariantId material, int value) {
    this(MaterialVariant.of(material), value);
  }

  @Override
  public int getValue() {
    return value;
  }
}
