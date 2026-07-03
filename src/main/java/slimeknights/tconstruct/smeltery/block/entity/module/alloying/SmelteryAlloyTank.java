package slimeknights.tconstruct.smeltery.block.entity.module.alloying;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.tconstruct.library.recipe.alloying.IMutableAlloyTank;
import slimeknights.tconstruct.smeltery.block.entity.tank.SmelteryTank;

/**
 * Smeltery implementation of the alloy tank, basically just a wrapper around the smeltery tank
 */
public class SmelteryAlloyTank implements IMutableAlloyTank {
  /**
   * Handler parent
   */
  private final SmelteryTank handler;
  /** Current temperature. Provided as a getter and setter as there are a few contexts with different source for temperature */
  private int temperature = 0;

  public SmelteryAlloyTank(SmelteryTank handler) {
    this.handler = handler;
  }

  @Override
  public int getTemperature() {
    return temperature;
  }

  public void setTemperature(int temperature) {
    this.temperature = temperature;
  }

  @Override
  public int getTanks() {
    return handler.getTanks();
  }

  @Override
  public FluidStack getFluidInTank(int tank) {
    return handler.getFluidInTank(tank);
  }

  @Override
  public boolean canFit(FluidStack fluid, int removed) {
    // the fluid fits if the net gain in fluid fits in the empty space
    return (fluid.getAmount() - removed) <= handler.getRemainingSpace();
  }

  @Override
  public FluidStack drain(int tank, FluidStack fluidStack) {
    return handler.drain(fluidStack, FluidAction.EXECUTE);
  }

  @Override
  public int fill(FluidStack fluidStack) {
    return handler.fill(fluidStack, FluidAction.EXECUTE);
  }
}
