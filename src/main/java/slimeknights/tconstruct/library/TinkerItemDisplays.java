package slimeknights.tconstruct.library;

import net.minecraft.world.item.ItemDisplayContext;

/** Custom transform types used for tinkers item rendering */
public class TinkerItemDisplays {
  private TinkerItemDisplays() {}

  /** Used by the melter and smeltery for display of items its melting */
  public static final ItemDisplayContext MELTER = ItemDisplayContext.NONE;
  /** Used by the part builder, crafting station, tinkers station, and tinker anvil */
  public static final ItemDisplayContext TABLE = ItemDisplayContext.NONE;
  /** Used by the casting table for item rendering */
  public static final ItemDisplayContext CASTING_TABLE = ItemDisplayContext.FIXED;
  /** Used by the casting basin for item rendering */
  public static final ItemDisplayContext CASTING_BASIN = ItemDisplayContext.NONE;
  /** Used by the fluid cannon for display of the item in front */
  public static final ItemDisplayContext FLUID_CANNON = ItemDisplayContext.FIXED;
  /** Used by throwing to allow adjusting the tool position */
  public static final ItemDisplayContext THROWN = ItemDisplayContext.FIXED;
}
