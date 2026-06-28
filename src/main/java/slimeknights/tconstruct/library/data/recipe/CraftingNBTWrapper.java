package slimeknights.tconstruct.library.data.recipe;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.nbt.CompoundTag;

/** Helper to add NBT to vanilla recipes. Stubbed for 1.21.1 — needs DataComponents redesign. */
public class CraftingNBTWrapper {
  private CraftingNBTWrapper() {}

  /** Creates a wrapped consumer, adding the given NBT. NBT injection not implemented for 1.21.1. */
  public static RecipeOutput wrap(RecipeOutput base, CompoundTag nbt) {
    return base;
  }
}
