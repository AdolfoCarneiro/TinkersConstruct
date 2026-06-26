package slimeknights.tconstruct.library.recipe.material;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;

/**
 * Builder for a recipe to determine the material from an input
 */
@RequiredArgsConstructor(staticName = "materialRecipe")
@Accessors(chain = true)
public class MaterialRecipeBuilder extends AbstractRecipeBuilder<MaterialRecipeBuilder> {
  private final MaterialVariantId material;
  private Ingredient ingredient = Ingredient.EMPTY;
  @Setter
  private int value = 1;
  @Setter
  private int needed = 1;
  @Setter
  private ItemOutput leftover = ItemOutput.EMPTY;

  public MaterialRecipeBuilder setIngredient(TagKey<Item> tag) {
    return this.setIngredient(Ingredient.of(tag));
  }

  public MaterialRecipeBuilder setIngredient(ItemLike item) {
    return this.setIngredient(Ingredient.of(item));
  }

  public MaterialRecipeBuilder setIngredient(Ingredient ingredient) {
    this.ingredient = ingredient;
    return this;
  }

  @Override
  public void save(RecipeOutput output) {
    this.save(output, material.getId());
  }

  @Override
  public void save(RecipeOutput output, ResourceLocation id) {
    if (this.material == null) {
      throw new IllegalStateException("recipe " + id + " has no material associated with it");
    }
    if (this.ingredient == Ingredient.EMPTY) {
      throw new IllegalStateException("recipe " + id + " must have ingredient set");
    }
    if (this.value <= 0) {
      throw new IllegalStateException("recipe " + id + " has no value associated with it");
    }
    if (this.needed <= 0) {
      throw new IllegalStateException("recipe " + id + " has no needed associated with it");
    }
    AdvancementHolder advancementHolder = this.buildOptionalAdvancement(output, id, "materials");
    MaterialRecipe recipe = new MaterialRecipe(group, ingredient, value, needed, material, leftover);
    output.accept(id, recipe, advancementHolder);
  }
}
