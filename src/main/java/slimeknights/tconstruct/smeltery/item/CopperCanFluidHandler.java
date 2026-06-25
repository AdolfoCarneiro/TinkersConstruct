package slimeknights.tconstruct.smeltery.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import slimeknights.tconstruct.library.recipe.FluidValues;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/** Capability handler instance for the copper can item */
public class CopperCanFluidHandler implements IFluidHandlerItem {
  private final ItemStack container;

  public CopperCanFluidHandler(ItemStack container) {
    this.container = container;
  }

  @Override
  public ItemStack getContainer() {
    return container;
  }

  /** Capability provider, registered for the copper can item by {@link slimeknights.tconstruct.smeltery.TinkerSmeltery} */
  @Nullable
  public static IFluidHandlerItem createIfPresent(ItemStack stack, @Nullable Void context) {
    return new CopperCanFluidHandler(stack);
  }

  /* Tank properties */

  @Override
  public int getTanks() {
    return 1;
  }

  @Override
  public boolean isFluidValid(int tank, FluidStack stack) {
    return true;
  }

  /** Gets the stack size sensitive capacity of the container */
  private int getCapacity() {
    // scale up by the stack size to prevent dupes with people trying to fill a stack of containers
    return FluidValues.INGOT * container.getCount();
  }

  @Override
  public int getTankCapacity(int tank) {
    return getCapacity();
  }

  /** Gets the contained fluid */
  private Fluid getFluid() {
    return CopperCanItem.getFluid(container);
  }

  /** Gets the contained fluid */
  @Nullable
  private CompoundTag getFluidTag() {
    return CopperCanItem.getFluidTag(container);
  }

  /** Builds a fluid stack of the given fluid/amount, tagged with this container's stored NBT if present */
  private FluidStack stackWithTag(Fluid fluid, int amount) {
    FluidStack stack = new FluidStack(fluid, amount);
    CompoundTag tag = getFluidTag();
    if (tag != null) {
      stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }
    return stack;
  }

  @Nonnull
  @Override
  public FluidStack getFluidInTank(int tank) {
    Fluid fluid = getFluid();
    if (fluid == Fluids.EMPTY) {
      return FluidStack.EMPTY;
    }
    return stackWithTag(getFluid(), getCapacity());
  }


  /* Interaction */

  @Override
  public int fill(FluidStack resource, FluidAction action) {
    // must not be filled, must have enough
    int capacity = getCapacity();
    if (getFluid() != Fluids.EMPTY || resource.getAmount() < capacity) {
      return 0;
    }
    // update fluid and return
    if (action.execute()) {
      // this is not size sensitive so no need to shrink resource for stack size
      CopperCanItem.setFluid(container, resource);
    }
    return capacity;
  }

  @Nonnull
  @Override
  public FluidStack drain(FluidStack resource, FluidAction action) {
    // must be draining at least an ingot
    int capacity = getCapacity();
    if (resource.isEmpty() || resource.getAmount() < capacity) {
      return FluidStack.EMPTY;
    }
    // must have a fluid, must match what they are draining
    Fluid fluid = getFluid();
    if (fluid == Fluids.EMPTY || fluid != resource.getFluid()) {
      return FluidStack.EMPTY;
    }
    // make sure NBT matches the requested NBT
    FluidStack output = stackWithTag(fluid, capacity);
    if (!FluidStack.areFluidStackTagsEqual(resource, output)) {
      return FluidStack.EMPTY;
    }
    // output 1 ingot times stack size
    if (action.execute()) {
      CopperCanItem.setFluid(container, FluidStack.EMPTY);
    }
    return output;
  }

  @Nonnull
  @Override
  public FluidStack drain(int maxDrain, FluidAction action) {
    // must be draining at least an ingot
    int capacity = getCapacity();
    if (maxDrain < capacity) {
      return FluidStack.EMPTY;
    }
    // must have a fluid
    Fluid fluid = getFluid();
    if (fluid == Fluids.EMPTY) {
      return FluidStack.EMPTY;
    }
    // output 1 ingot
    FluidStack output = stackWithTag(fluid, capacity);
    if (action.execute()) {
      CopperCanItem.setFluid(container, FluidStack.EMPTY);
    }
    return output;
  }
}
