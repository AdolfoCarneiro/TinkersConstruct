package slimeknights.tconstruct.library.recipe.tinkerstation.building;

import lombok.experimental.Accessors;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.part.IToolPart;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;


/** Builder for {@link FixedMaterialSwappingRecipe} and {@link PartSwappingOverrideRecipe}. */
@Accessors(fluent = true)
public class MaterialSwappingRecipeBuilder extends AbstractRecipeBuilder<MaterialSwappingRecipeBuilder> {
  /** Tools that support this recipe */
  private final Ingredient tools;
  private int maxStackSize = 16;
  /** List of indices swapped by this recipe */
  private final BitSet indices = new BitSet();
  /** Additional requirements beyond the "part" */
  private final List<SizedIngredient> extraRequirements = new ArrayList<>();

  /** Part to swap, used by part override */
  private IToolPart part = null;

  /** Ingredient for the input part, used by fixed */
  private SizedIngredient ingredient = SizedIngredient.EMPTY;
  /** Material to swap to, used by fixed */
  private MaterialVariantId material = IMaterial.UNKNOWN_ID;
  /** Repair value on swapping, used by fixed */
  private int repairValue = 0;

  private MaterialSwappingRecipeBuilder(Ingredient tools) {
    this.tools = tools;
  }

  /** Creates a builder for the given tools */
  public static MaterialSwappingRecipeBuilder tools(Ingredient tools) {
    return new MaterialSwappingRecipeBuilder(tools);
  }

  /** Sets the max stack size a tool may have to use this recipe */
  public MaterialSwappingRecipeBuilder maxStackSize(int maxStackSize) {
    this.maxStackSize = maxStackSize;
    return this;
  }

  /** Sets the part to swap, used by part override */
  public MaterialSwappingRecipeBuilder part(IToolPart part) {
    this.part = part;
    return this;
  }

  /** Sets the repair value on swapping, used by fixed */
  public MaterialSwappingRecipeBuilder repairValue(int repairValue) {
    this.repairValue = repairValue;
    return this;
  }

  /** Creates a builder for the given tool */
  public static MaterialSwappingRecipeBuilder tool(ItemLike tool) {
    return tools(Ingredient.of(tool));
  }

  /** Creates a builder for the given tool */
  public static MaterialSwappingRecipeBuilder tools(TagKey<Item> tag) {
    return tools(Ingredient.of(tag));
  }

  /** Adds the given index to the recipe */
  public MaterialSwappingRecipeBuilder index(int index) {
    indices.set(index);
    return this;
  }

  /** Sets the material for this builder */
  public MaterialSwappingRecipeBuilder material(MaterialVariantId material, SizedIngredient ingredient) {
    this.material = material;
    this.ingredient = ingredient;
    return this;
  }

  /** Sets the material for this builder */
  public MaterialSwappingRecipeBuilder material(MaterialVariantId material, ItemLike item) {
    return material(material, SizedIngredient.fromItems(item));
  }

  /** Adds an extra ingredient requirement */
  public MaterialSwappingRecipeBuilder addExtraRequirement(SizedIngredient ingredient) {
    extraRequirements.add(ingredient);
    return this;
  }

  /** Adds an extra ingredient requirement */
  public MaterialSwappingRecipeBuilder addExtraRequirement(Ingredient ingredient) {
    return addExtraRequirement(SizedIngredient.of(ingredient));
  }

  /** Adds an extra ingredient requirement */
  public MaterialSwappingRecipeBuilder addExtraRequirement(ItemLike... items) {
    return addExtraRequirement(SizedIngredient.fromItems(items));
  }

  @Override
  public void save(RecipeOutput consumer) {
    save(consumer, Loadables.ITEM.getKey(tools.getItems()[0].getItem()));
  }

  @Override
  public void save(RecipeOutput consumer, ResourceLocation id) {
    int[] indices = this.indices.stream().toArray();
    if (indices.length == 0) {
      throw new IllegalStateException("Must set index");
    }
    if (part != null) {
      if (ingredient != SizedIngredient.EMPTY) {
        throw new IllegalStateException("Cannot set both part and ingredient");
      }
      consumer.accept(id, new PartSwappingOverrideRecipe(tools, maxStackSize, part, indices, extraRequirements), null);
    } else {
      consumer.accept(id, new FixedMaterialSwappingRecipe(tools, maxStackSize, ingredient, material, indices, repairValue, extraRequirements), null);
    }
  }
}
