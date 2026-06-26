package slimeknights.tconstruct.library.recipe.ingredient;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.IngredientType;
import slimeknights.mantle.data.loadable.common.IngredientLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.util.typed.TypedMap;
import slimeknights.tconstruct.library.json.predicate.material.MaterialPredicate;
import slimeknights.tconstruct.library.json.predicate.material.MaterialPredicateField;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipeCache;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Extension of the vanilla ingredient to display materials on items and support matching by materials
 */
public class MaterialIngredient extends NestedIngredient {
  private final IJsonPredicate<MaterialVariantId> material;
  @Nullable
  private ItemStack[] materialStacks;

  protected MaterialIngredient(Ingredient nested, IJsonPredicate<MaterialVariantId> material) {
    super(nested);
    this.material = material;
  }

  /** @deprecated use {@link #MaterialIngredient(Ingredient, IJsonPredicate)} */
  @Deprecated(forRemoval = true)
  protected MaterialIngredient(Ingredient nested, MaterialVariantId material, @Nullable TagKey<IMaterial> tag) {
    this(nested, makePredicate(material, tag));
  }

  private static IJsonPredicate<MaterialVariantId> makePredicate(MaterialVariantId material, @Nullable TagKey<IMaterial> tag) {
    IJsonPredicate<MaterialVariantId> predicate = material.equals(IMaterial.UNKNOWN.getIdentifier()) ? MaterialPredicate.ANY : MaterialPredicate.variant(material);
    if (tag != null) {
      IJsonPredicate<MaterialVariantId> tagPredicate = MaterialPredicate.tag(tag);
      if (predicate == MaterialPredicate.ANY) {
        predicate = tagPredicate;
      } else {
        predicate = MaterialPredicate.and(predicate, tagPredicate);
      }
    }
    return predicate;
  }

  private static final RecordLoadable<MaterialIngredient> LOADABLE = RecordLoadable.create(
    IngredientLoadable.DISALLOW_EMPTY.<MaterialIngredient>requiredField("match", i -> i.nested),
    new MaterialPredicateField<MaterialIngredient>("material", i -> i.material),
    MaterialIngredient::new
  );
  public static final MapCodec<MaterialIngredient> CODEC = LOADABLE.mapCodec(TypedMap.EMPTY);
  public static final StreamCodec<RegistryFriendlyByteBuf, MaterialIngredient> STREAM_CODEC = LOADABLE;


  /* Static constructors */

  public static MaterialIngredient of(Ingredient ingredient, IJsonPredicate<MaterialVariantId> material) {
    return new MaterialIngredient(ingredient, material);
  }

  public static MaterialIngredient of(ItemLike item, IJsonPredicate<MaterialVariantId> material) {
    return of(Ingredient.of(item), material);
  }

  public static MaterialIngredient of(Ingredient ingredient) {
    return new MaterialIngredient(ingredient, MaterialPredicate.ANY);
  }

  public static MaterialIngredient of(Ingredient ingredient, MaterialVariantId material) {
    return of(ingredient, MaterialPredicate.variant(material));
  }

  public static MaterialIngredient of(Ingredient ingredient, TagKey<IMaterial> tag) {
    return of(ingredient, MaterialPredicate.tag(tag));
  }

  public static MaterialIngredient of(ItemLike item, MaterialVariantId material) {
    return of(Ingredient.of(item), material);
  }

  public static MaterialIngredient of(ItemLike item, TagKey<IMaterial> tag) {
    return of(Ingredient.of(item), tag);
  }

  public static MaterialIngredient of(ItemLike item) {
    return of(Ingredient.of(item));
  }

  public static MaterialIngredient of(TagKey<Item> tag, MaterialVariantId material) {
    return of(Ingredient.of(tag), material);
  }

  public static MaterialIngredient of(TagKey<Item> tag) {
    return of(Ingredient.of(tag));
  }


  /* Ingredient logic */

  @Override
  public boolean test(@Nullable ItemStack stack) {
    if (stack == null || stack.isEmpty() || !super.test(stack)) {
      return false;
    }
    if (material != MaterialPredicate.ANY) {
      return material.matches(IMaterialItem.getMaterialFromStack(stack));
    }
    return true;
  }

  @Override
  public Stream<ItemStack> getItems() {
    if (materialStacks == null) {
      if (!MaterialRegistry.isFullyLoaded()) {
        return Arrays.stream(nested.getItems());
      }
      materialStacks = Arrays.stream(nested.getItems())
        .flatMap(stack -> MaterialRecipeCache.getAllVariants().stream()
          .filter(material::matches)
          .map(mat -> IMaterialItem.withMaterial(stack, mat))
          .filter(s -> !s.isEmpty()))
        .distinct()
        .toArray(ItemStack[]::new);
    }
    return Arrays.stream(materialStacks);
  }

  @Override
  public boolean isSimple() {
    return material == MaterialPredicate.ANY;
  }

  @Override
  public IngredientType<?> getType() {
    return TinkerRecipeTypes.MATERIAL_INGREDIENT.get();
  }

}
