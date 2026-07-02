package slimeknights.tconstruct.plugin.jei.material;

import com.google.common.collect.Streams;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.client.SafeClientAccess;
import slimeknights.mantle.plugin.jei.MantleJEIConstants;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipeCache;
import slimeknights.tconstruct.library.recipe.material.MaterialsCraftingTableRecipe;
import slimeknights.tconstruct.library.recipe.material.ShapelessMaterialsRecipe;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.stream.Stream;

/**
 * Common logic for {@link ShapedMaterialsExtension} and shapeless material crafting recipes.
 * JEI 16.0.0+ extensions are singletons that receive the recipe on every call, so the per-recipe
 * display data computed in the old per-instance constructor is now lazily cached here instead.
 */
public class MaterialsCraftingExtension<T extends CraftingRecipe & MaterialsCraftingTableRecipe> implements ICraftingCategoryExtension<T> {
  private static final MaterialsCraftingExtension<ShapelessMaterialsRecipe> SHAPELESS = new MaterialsCraftingExtension<>();

  private final Map<T, Display> cache = new WeakHashMap<>();

  protected MaterialsCraftingExtension() {}

  /** {@return the singleton extension instance for shapeless material crafting recipes} */
  public static MaterialsCraftingExtension<ShapelessMaterialsRecipe> shapeless() {
    return SHAPELESS;
  }

  @Override
  public boolean isHandled(RecipeHolder<T> recipeHolder) {
    T recipe = recipeHolder.value();
    List<Ingredient> parts = recipe.getIngredients();
    for (int i = 0; i < recipe.getPartCount(); i++) {
      if (parts.get(i).getItems().length == 0) {
        return false;
      }
    }
    return true;
  }

  /** Gets the material slots for the given recipe */
  protected int[] getMaterialSlots(T recipe, Ingredient firstPart) {
    return new int[] {0};
  }

  /** Computes, and caches, the display data for the given recipe */
  private Display getDisplay(T recipe) {
    return cache.computeIfAbsent(recipe, r -> {
      ItemStack plainResult = r.getResultItem(Objects.requireNonNull(SafeClientAccess.getRegistryAccess()));
      List<ItemStack> result;
      @Nullable int[] materialSlots;
      // if we have just the one part, set the output to match its material
      if (r.getPartCount() == 1) {
        Ingredient firstPart = r.getParts().get(0);
        result = Arrays.stream(firstPart.getItems()).map(variant -> {
          ItemStack stack = plainResult.copy();
          if (variant.getItem() instanceof IMaterialItem materialItem) {
            r.setMaterial(stack, materialItem.getMaterial(variant));
          } else {
            r.setMaterial(stack, MaterialRecipeCache.findRecipe(variant).getMaterial().getVariant());
          }
          return stack;
        }).toList();
        materialSlots = getMaterialSlots(r, firstPart);
        // otherwise, use a display material. allow display tool part if it has just 1 material
      } else if (r.getExtraMaterials().isEmpty() && plainResult.getItem() instanceof IMaterialItem materialItem) {
        result = List.of(materialItem.setMaterialForced(plainResult, ToolBuildHandler.getRenderMaterial(0)));
        materialSlots = null;
      } else {
        // display tool
        result = List.of(IModifiableDisplay.getDisplayStack(plainResult));
        materialSlots = null;
      }
      return new Display(plainResult, result, materialSlots);
    });
  }

  /** Sets the recipe in the builder, shared with the legacy {@code ShapedMaterialExtension} */
  public static <R extends CraftingRecipe> void setRecipe(ICraftingCategoryExtension<R> self, RecipeHolder<R> recipeHolder, IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, R recipe, List<ItemStack> result, ItemStack plainResult, @Nullable int[] materialSlots) {
    builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT).addItemStack(plainResult);

    // apply ingredient stacks
    List<List<ItemStack>> inputStacks = recipe.getIngredients().stream().map(ingredient -> List.of(ingredient.getItems())).toList();
    // shapeless needs its width and height set, but we also want to recover those sizes, so calculate it locally
    int width = self.getWidth(recipeHolder);
    int height = self.getHeight(recipeHolder);
    if (width <= 0 || height <= 0) {
      width = height = getShapelessSize(inputStacks.size());
      builder.setShapeless();
    }
    List<IRecipeSlotBuilder> inputs = craftingGridHelper.createAndSetInputs(builder, VanillaTypes.ITEM_STACK, inputStacks, width, height);
    IRecipeSlotBuilder output = craftingGridHelper.createAndSetOutputs(builder, result);
    if (inputs.size() != 9) {
      Mantle.logger.error("Failed to create focus link for {} as the layout {} is not 3x3", recipeHolder.id(), builder.getClass().getName());
    } else if (materialSlots != null) {
      // apply focus links
      int finalWidth = width;
      int finalHeight = height;
      builder.createFocusLink(Streams.concat(
        Stream.of(output),
        Arrays.stream(materialSlots).mapToObj(i -> inputs.get(MantleJEIConstants.getCraftingIndex(i, finalWidth, finalHeight)))
      ).toArray(IRecipeSlotBuilder[]::new));
    }
  }

  @Override
  public void setRecipe(RecipeHolder<T> recipeHolder, IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
    T recipe = recipeHolder.value();
    Display display = getDisplay(recipe);
    setRecipe(this, recipeHolder, builder, craftingGridHelper, recipe, display.result(), display.plainResult(), display.materialSlots());
  }

  /** Gets the width and height of the grid for a shapeless recipe. */
  private static int getShapelessSize(int total) {
    if (total > 4) {
      return 3;
    } else if (total > 1) {
      return 2;
    } else {
      return 1;
    }
  }

  /** Cached per-recipe display data */
  private record Display(ItemStack plainResult, List<ItemStack> result, @Nullable int[] materialSlots) {}
}
