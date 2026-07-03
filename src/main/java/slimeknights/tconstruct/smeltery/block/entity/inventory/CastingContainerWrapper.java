package slimeknights.tconstruct.smeltery.block.entity.inventory;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import slimeknights.tconstruct.library.recipe.casting.ICastingContainer;
import slimeknights.tconstruct.smeltery.block.entity.CastingBlockEntity;

import javax.annotation.Nullable;

/**
 * Provides read only access to the input of a casting table. Prevents extra data from leaking
 */
public class CastingContainerWrapper implements ICastingContainer {
  private final CastingBlockEntity tile;
  private FluidStack fluid;
  private boolean switchSlots = false;

  public CastingContainerWrapper(CastingBlockEntity tile) {
    this.tile = tile;
  }

  public void setFluid(FluidStack fluid) {
    this.fluid = fluid;
  }

  @Override
  public ItemStack getStack() {
    ItemStack stack = tile.getItem(switchSlots ? CastingBlockEntity.OUTPUT : CastingBlockEntity.INPUT);
    if (stack.is(tile.getEmptyCastTag())) {
      return ItemStack.EMPTY;
    }
    return stack;
  }

  @Override
  public Fluid getFluid() {
    return fluid.getFluid();
  }

  @Nullable
  @Override
  public CompoundTag getFluidTag() {
    return new CompoundTag(); // F2: FluidStack CompoundTag NBT removed (now DataComponentPatch); casting fluid-NBT chain needs redesign
  }

  /** Uses the input for input (default) */
  public void useInput() {
    switchSlots = false;
  }

  /** Uses the output for input (for multistep casting) */
  public void useOutput() {
    switchSlots = true;
  }
}
