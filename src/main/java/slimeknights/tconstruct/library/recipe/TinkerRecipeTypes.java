package slimeknights.tconstruct.library.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.json.ConfigEnabledCondition;
import slimeknights.tconstruct.library.recipe.alloying.AlloyRecipe;
import slimeknights.tconstruct.library.recipe.casting.ICastingRecipe;
import slimeknights.tconstruct.library.recipe.entitymelting.EntityMeltingRecipe;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuel;
import slimeknights.tconstruct.library.recipe.ingredient.BlockTagIngredient;
import slimeknights.tconstruct.library.recipe.ingredient.MaterialIngredient;
import slimeknights.tconstruct.library.recipe.ingredient.MaterialValueIngredient;
import slimeknights.tconstruct.library.recipe.ingredient.NoContainerIngredient;
import slimeknights.tconstruct.library.recipe.ingredient.ToolHookIngredient;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.recipe.melting.IMeltingRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.severing.SeveringRecipe;
import slimeknights.tconstruct.library.recipe.molding.MoldingRecipe;
import slimeknights.tconstruct.library.recipe.partbuilder.IPartBuilderRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.recipe.worktable.IModifierWorktableRecipe;

/**
 * Class containing all of Tinkers Construct recipe types
 */
public class TinkerRecipeTypes {
  /** Deferred instance */
  private static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, TConstruct.MOD_ID);
  private static final DeferredRegister<IngredientType<?>> INGREDIENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.INGREDIENT_TYPES, TConstruct.MOD_ID);
  private static final DeferredRegister<MapCodec<? extends ICondition>> CONDITION_CODECS = DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, TConstruct.MOD_ID);

  static {
    CONDITION_CODECS.register(ConfigEnabledCondition.ID.getPath(), () -> ConfigEnabledCondition.CODEC);
  }

  public static final DeferredHolder<RecipeType<?>, RecipeType<IPartBuilderRecipe>> PART_BUILDER = register("part_builder");
  public static final DeferredHolder<RecipeType<?>, RecipeType<MaterialRecipe>> MATERIAL = register("material");
  public static final DeferredHolder<RecipeType<?>, RecipeType<ITinkerStationRecipe>> TINKER_STATION = register("tinker_station");
  public static final DeferredHolder<RecipeType<?>, RecipeType<IModifierWorktableRecipe>> MODIFIER_WORKTABLE = register("modifier_worktable");

  // casting
  public static final DeferredHolder<RecipeType<?>, RecipeType<ICastingRecipe>> CASTING_BASIN = register("casting_basin");
  public static final DeferredHolder<RecipeType<?>, RecipeType<ICastingRecipe>> CASTING_TABLE = register("casting_table");
  public static final DeferredHolder<RecipeType<?>, RecipeType<MoldingRecipe>> MOLDING_TABLE = register("molding_table");
  public static final DeferredHolder<RecipeType<?>, RecipeType<MoldingRecipe>> MOLDING_BASIN = register("molding_basin");

  // smeltery
  public static final DeferredHolder<RecipeType<?>, RecipeType<IMeltingRecipe>> MELTING = register("melting");
  public static final DeferredHolder<RecipeType<?>, RecipeType<EntityMeltingRecipe>> ENTITY_MELTING = register("entity_melting");
  public static final DeferredHolder<RecipeType<?>, RecipeType<MeltingFuel>> FUEL = register("fuel");
  public static final DeferredHolder<RecipeType<?>, RecipeType<AlloyRecipe>> ALLOYING = register("alloying");

  // modifiers
  public static final DeferredHolder<RecipeType<?>, RecipeType<SeveringRecipe>> SEVERING = register("severing");

  /** Internal recipe type for recipes that are not pulled by any specific crafting block */
  public static final DeferredHolder<RecipeType<?>, RecipeType<Recipe<?>>> DATA = register("data");

  // ingredient types
  public static final DeferredHolder<IngredientType<?>, IngredientType<BlockTagIngredient>> BLOCK_TAG_INGREDIENT =
    INGREDIENT_TYPES.register("block_tag", () -> new IngredientType<>(BlockTagIngredient.CODEC, BlockTagIngredient.STREAM_CODEC));
  public static final DeferredHolder<IngredientType<?>, IngredientType<NoContainerIngredient>> NO_CONTAINER_INGREDIENT =
    INGREDIENT_TYPES.register("no_container", () -> new IngredientType<>(NoContainerIngredient.CODEC, NoContainerIngredient.STREAM_CODEC));
  public static final DeferredHolder<IngredientType<?>, IngredientType<MaterialIngredient>> MATERIAL_INGREDIENT =
    INGREDIENT_TYPES.register("material", () -> new IngredientType<>(MaterialIngredient.CODEC, MaterialIngredient.STREAM_CODEC));
  public static final DeferredHolder<IngredientType<?>, IngredientType<MaterialValueIngredient>> MATERIAL_VALUE_INGREDIENT =
    INGREDIENT_TYPES.register("material_value", () -> new IngredientType<>(MaterialValueIngredient.CODEC, MaterialValueIngredient.STREAM_CODEC));
  public static final DeferredHolder<IngredientType<?>, IngredientType<ToolHookIngredient>> TOOL_HOOK_INGREDIENT =
    INGREDIENT_TYPES.register("tool_hook", () -> new IngredientType<>(ToolHookIngredient.CODEC, ToolHookIngredient.STREAM_CODEC));

  /** Initializes the deferred register */
  public static void init(IEventBus bus) {
    TYPES.register(bus);
    INGREDIENT_TYPES.register(bus);
    CONDITION_CODECS.register(bus);
  }

  /**
   * Registers a new recipe type, prefixing with the mod ID
   * @param name  Recipe type name
   * @param <T>   Recipe type
   * @return  Registered recipe type
   */
  static <T extends Recipe<?>> DeferredHolder<RecipeType<?>, RecipeType<T>> register(String name) {
    return TYPES.register(name, () -> new RecipeType<>() {
      @Override
      public String toString() {
        return TConstruct.MOD_ID + ":" + name;
      }
    });
  }
}
