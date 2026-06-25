package slimeknights.tconstruct.smeltery.block.entity.inventory;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import slimeknights.tconstruct.library.recipe.molding.IMoldingContainer;

/** Wrapper around an item handler for the sake of use as a molding inventory */
public class MoldingContainerWrapper implements IMoldingContainer {
  private final IItemHandler handler;
  private final int slot;

  private ItemStack pattern = ItemStack.EMPTY;

  public MoldingContainerWrapper(IItemHandler handler, int slot) {
    this.handler = handler;
    this.slot = slot;
  }

  @Override
  public ItemStack getPattern() {
    return pattern;
  }

  public void setPattern(ItemStack pattern) {
    this.pattern = pattern;
  }

  @Override
  public ItemStack getMaterial() {
    return handler.getStackInSlot(slot);
  }
}
