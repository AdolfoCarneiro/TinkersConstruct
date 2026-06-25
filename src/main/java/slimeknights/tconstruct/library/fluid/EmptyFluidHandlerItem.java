package slimeknights.tconstruct.library.fluid;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.EmptyFluidHandler;

/** Empty fluid handler item instance, usable like {@link EmptyFluidHandler#INSTANCE} */
public class EmptyFluidHandlerItem extends EmptyFluidHandler implements IFluidHandlerItem {
  public static final EmptyFluidHandlerItem INSTANCE = new EmptyFluidHandlerItem(ItemStack.EMPTY);

  /** Container reference */
  private final ItemStack container;

  public EmptyFluidHandlerItem(ItemStack container) {
    this.container = container;
  }

  @Override
  public ItemStack getContainer() {
    return container;
  }
}
