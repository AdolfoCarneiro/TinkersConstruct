package slimeknights.tconstruct.library.recipe.casting.material;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.mantle.recipe.helper.TypeAwareRecipeSerializer;
import slimeknights.tconstruct.library.json.predicate.material.MaterialPredicate;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;

/** Builder for a composite part recipe, should exist for each part */
@Accessors(fluent = true)
public class CompositeCastingRecipeBuilder extends AbstractRecipeBuilder<CompositeCastingRecipeBuilder> {
  private final IMaterialItem result;
  private final int itemCost;
  @Nullable
  private MaterialStatsId castingStatConflict = null;
  private final TypeAwareRecipeSerializer<? extends CompositeCastingRecipe> serializer;
  private IJsonPredicate<MaterialVariantId> allowedMaterials = MaterialPredicate.ANY;

  private CompositeCastingRecipeBuilder(IMaterialItem result, int itemCost, TypeAwareRecipeSerializer<? extends CompositeCastingRecipe> serializer) {
    this.result = result;
    this.itemCost = itemCost;
    this.serializer = serializer;
  }

  public static CompositeCastingRecipeBuilder composite(IMaterialItem result, int itemCost, TypeAwareRecipeSerializer<? extends CompositeCastingRecipe> serializer) {
    return new CompositeCastingRecipeBuilder(result, itemCost, serializer);
  }

  public CompositeCastingRecipeBuilder castingStatConflict(@Nullable MaterialStatsId castingStatConflict) {
    this.castingStatConflict = castingStatConflict;
    return this;
  }

  public CompositeCastingRecipeBuilder allowedMaterials(IJsonPredicate<MaterialVariantId> allowedMaterials) {
    this.allowedMaterials = allowedMaterials;
    return this;
  }

  public static CompositeCastingRecipeBuilder basin(IMaterialItem result, int itemCost) {
    return composite(result, itemCost, TinkerSmeltery.basinCompositeSerializer.get());
  }

  public static CompositeCastingRecipeBuilder table(IMaterialItem result, int itemCost) {
    return composite(result, itemCost, TinkerSmeltery.tableCompositeSerializer.get());
  }

  @Override
  public void save(RecipeOutput output) {
    save(output, BuiltInRegistries.ITEM.getKey(result.asItem()));
  }

  @Override
  public void save(RecipeOutput output, ResourceLocation id) {
    net.minecraft.advancements.AdvancementHolder advancementHolder = this.buildOptionalAdvancement(output, id, "casting");
    output.accept(id, new CompositeCastingRecipe(serializer, group, itemCost, result, allowedMaterials, castingStatConflict), advancementHolder);
  }
}
