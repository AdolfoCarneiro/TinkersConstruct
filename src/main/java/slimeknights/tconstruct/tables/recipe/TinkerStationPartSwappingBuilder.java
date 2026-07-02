package slimeknights.tconstruct.tables.recipe;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.tconstruct.library.recipe.tinkerstation.building.ToolMaterialSwappingRecipe;

import java.util.ArrayList;
import java.util.List;


/** Builder for {@link TinkerStationPartSwapping} and {@link ToolMaterialSwappingRecipe} */
public class TinkerStationPartSwappingBuilder extends AbstractRecipeBuilder<TinkerStationPartSwappingBuilder> {
  private final Ingredient tools;
  private boolean fromTool = false;
  private int maxStackSize = 16;
  /** Additional requirements beyond the "part" */
  private final List<SizedIngredient> extraRequirements = new ArrayList<>();

  private TinkerStationPartSwappingBuilder(Ingredient tools) {
    this.tools = tools;
  }

  public static TinkerStationPartSwappingBuilder tools(Ingredient tools) {
    return new TinkerStationPartSwappingBuilder(tools);
  }

  /** Sets the swapping to be from a tool instead of from a part */
  public TinkerStationPartSwappingBuilder fromTool() {
    this.fromTool = true;
    return this;
  }

  /** Sets the max stack size for the swapping recipe */
  public TinkerStationPartSwappingBuilder maxStackSize(int maxStackSize) {
    this.maxStackSize = maxStackSize;
    return this;
  }

  /** Adds an extra ingredient requirement */
  public TinkerStationPartSwappingBuilder addExtraRequirement(SizedIngredient ingredient) {
    extraRequirements.add(ingredient);
    return this;
  }

  /** Adds an extra ingredient requirement */
  public TinkerStationPartSwappingBuilder addExtraRequirement(Ingredient ingredient) {
    return addExtraRequirement(SizedIngredient.of(ingredient));
  }

  /** Adds an extra ingredient requirement */
  public TinkerStationPartSwappingBuilder addExtraRequirement(ItemLike... items) {
    return addExtraRequirement(SizedIngredient.fromItems(items));
  }

  @Override
  public void save(RecipeOutput consumer) {
    save(consumer, Loadables.ITEM.getKey(tools.getItems()[0].getItem()));
  }

  @Override
  public void save(RecipeOutput consumer, ResourceLocation id) {
    if (fromTool) {
      consumer.accept(id, new ToolMaterialSwappingRecipe(tools, maxStackSize, extraRequirements), null);
    } else {
      consumer.accept(id, new TinkerStationPartSwapping(tools, maxStackSize, extraRequirements), null);
    }
  }
}
