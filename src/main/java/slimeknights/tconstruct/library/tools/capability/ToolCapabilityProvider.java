package slimeknights.tconstruct.library.tools.capability;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import slimeknights.tconstruct.library.tools.capability.fluid.ToolFluidCapability;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;

import java.util.List;

/** Registers the standard tool capabilities (fluid, energy, inventory, block item provider) for all modifiable tool items */
public final class ToolCapabilityProvider {
  private ToolCapabilityProvider() {}

  /** Registers all capabilities provided by tools, plus the default block item provider for {@link BlockItem}s */
  public static void register(RegisterCapabilitiesEvent event) {
    List<Item> toolItems = BuiltInRegistries.ITEM.stream().filter(item -> item instanceof IModifiableDisplay).toList();
    if (!toolItems.isEmpty()) {
      ItemLike[] items = toolItems.toArray(new ItemLike[0]);
      event.registerItem(Capabilities.FluidHandler.ITEM, ToolFluidCapability::createIfPresent, items);
      event.registerItem(Capabilities.EnergyStorage.ITEM, ToolEnergyCapability::createIfPresent, items);
      event.registerItem(Capabilities.ItemHandler.ITEM, ToolInventoryCapability::createIfPresent, items);
      event.registerItem(BlockItemProviderCapability.CAPABILITY, BlockItemProviderModifierHook::createIfPresent, items);
    }

    List<Item> blockItems = BuiltInRegistries.ITEM.stream().filter(item -> item instanceof BlockItem).toList();
    if (!blockItems.isEmpty()) {
      event.registerItem(BlockItemProviderCapability.CAPABILITY, BlockItemProviderCapability::createIfBlockItem, blockItems.toArray(new ItemLike[0]));
    }
  }
}
