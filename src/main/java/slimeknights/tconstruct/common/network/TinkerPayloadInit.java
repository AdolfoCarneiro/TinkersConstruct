package slimeknights.tconstruct.common.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import slimeknights.tconstruct.library.materials.definition.UpdateMaterialsPacket;
import slimeknights.tconstruct.library.materials.stats.UpdateMaterialStatsPacket;
import slimeknights.tconstruct.library.materials.traits.UpdateMaterialTraitsPacket;
import slimeknights.tconstruct.library.modifiers.UpdateModifiersPacket;
import slimeknights.tconstruct.library.modifiers.fluid.UpdateFluidEffectsPacket;
import slimeknights.tconstruct.library.tools.definition.UpdateToolDefinitionDataPacket;
import slimeknights.tconstruct.library.tools.layout.UpdateTinkerSlotLayoutsPacket;
import slimeknights.tconstruct.shared.network.GeneratePartTexturesPacket;
import slimeknights.tconstruct.smeltery.network.ChannelFlowPacket;
import slimeknights.tconstruct.smeltery.network.FaucetActivationPacket;
import slimeknights.tconstruct.smeltery.network.FluidUpdatePacket;
import slimeknights.tconstruct.smeltery.network.SmelteryFluidClickedPacket;
import slimeknights.tconstruct.smeltery.network.SmelteryTankUpdatePacket;
import slimeknights.tconstruct.smeltery.network.StructureErrorPositionPacket;
import slimeknights.tconstruct.smeltery.network.StructureUpdatePacket;
import slimeknights.tconstruct.tables.network.StationTabPacket;
import slimeknights.tconstruct.tables.network.TinkerStationRenamePacket;
import slimeknights.tconstruct.tables.network.TinkerStationSelectionPacket;
import slimeknights.tconstruct.tables.network.UpdateCraftingRecipePacket;
import slimeknights.tconstruct.tables.network.UpdateStationScreenPacket;
import slimeknights.tconstruct.tables.network.UpdateTinkerStationRecipePacket;
import slimeknights.tconstruct.tools.network.EntityMovementChangePacket;
import slimeknights.tconstruct.tools.network.InteractWithAirPacket;
import slimeknights.tconstruct.tools.network.PushBlockRowPacket;
import slimeknights.tconstruct.tools.network.SyncProjectileModifiersPacket;
import slimeknights.tconstruct.tools.network.TinkerControlPacket;
import slimeknights.tconstruct.tools.network.ToolContainerFluidUpdatePacket;

public final class TinkerPayloadInit {
  private TinkerPayloadInit() {}

  public static void register(RegisterPayloadHandlersEvent event) {
    PayloadRegistrar r = event.registrar("3");

    // common / shared
    r.playToClient(InventorySlotSyncPacket.TYPE,        InventorySlotSyncPacket.STREAM_CODEC,        InventorySlotSyncPacket::handleClient);
    r.playToClient(UpdateNeighborsPacket.TYPE,          UpdateNeighborsPacket.STREAM_CODEC,          UpdateNeighborsPacket::handleClient);
    r.playToClient(GeneratePartTexturesPacket.TYPE,     GeneratePartTexturesPacket.STREAM_CODEC,     GeneratePartTexturesPacket::handleClient);
    r.playToClient(SyncPersistentDataPacket.TYPE,       SyncPersistentDataPacket.STREAM_CODEC,       SyncPersistentDataPacket::handleClient);

    // gadgets
    r.playToClient(EntityMovementChangePacket.TYPE,     EntityMovementChangePacket.STREAM_CODEC,     EntityMovementChangePacket::handleClient);

    // tables
    r.playToServer(StationTabPacket.TYPE,                  StationTabPacket.STREAM_CODEC,                  StationTabPacket::handleServer);
    r.playToServer(TinkerStationRenamePacket.TYPE,         TinkerStationRenamePacket.STREAM_CODEC,         TinkerStationRenamePacket::handleServer);
    r.playToServer(TinkerStationSelectionPacket.TYPE,      TinkerStationSelectionPacket.STREAM_CODEC,      TinkerStationSelectionPacket::handleServer);
    r.playToClient(UpdateCraftingRecipePacket.TYPE,        UpdateCraftingRecipePacket.STREAM_CODEC,        UpdateCraftingRecipePacket::handleClient);
    r.playToClient(UpdateTinkerSlotLayoutsPacket.TYPE,     UpdateTinkerSlotLayoutsPacket.STREAM_CODEC,     UpdateTinkerSlotLayoutsPacket::handleClient);
    r.playToClient(UpdateStationScreenPacket.TYPE,         UpdateStationScreenPacket.STREAM_CODEC,         UpdateStationScreenPacket::handleClient);
    r.playToClient(UpdateTinkerStationRecipePacket.TYPE,   UpdateTinkerStationRecipePacket.STREAM_CODEC,   UpdateTinkerStationRecipePacket::handleClient);

    // tools / materials
    r.playToClient(UpdateMaterialsPacket.TYPE,             UpdateMaterialsPacket.STREAM_CODEC,             UpdateMaterialsPacket::handleClient);
    r.playToClient(UpdateMaterialStatsPacket.TYPE,         UpdateMaterialStatsPacket.STREAM_CODEC,         UpdateMaterialStatsPacket::handleClient);
    r.playToClient(UpdateMaterialTraitsPacket.TYPE,        UpdateMaterialTraitsPacket.STREAM_CODEC,        UpdateMaterialTraitsPacket::handleClient);
    r.playToClient(UpdateToolDefinitionDataPacket.TYPE,    UpdateToolDefinitionDataPacket.STREAM_CODEC,    UpdateToolDefinitionDataPacket::handleClient);
    r.playToClient(ToolContainerFluidUpdatePacket.TYPE,    ToolContainerFluidUpdatePacket.STREAM_CODEC,    ToolContainerFluidUpdatePacket::handleClient);
    r.playToClient(SyncProjectileModifiersPacket.TYPE,     SyncProjectileModifiersPacket.STREAM_CODEC,     SyncProjectileModifiersPacket::handleClient);

    // modifiers
    r.playToServer(TinkerControlPacket.TYPE,               TinkerControlPacket.STREAM_CODEC,               TinkerControlPacket::handleServer);
    r.playToServer(InteractWithAirPacket.TYPE,             InteractWithAirPacket.STREAM_CODEC,             InteractWithAirPacket::handleServer);
    r.playToClient(UpdateModifiersPacket.TYPE,             UpdateModifiersPacket.STREAM_CODEC,             UpdateModifiersPacket::handleClient);
    r.playToClient(UpdateFluidEffectsPacket.TYPE,          UpdateFluidEffectsPacket.STREAM_CODEC,          UpdateFluidEffectsPacket::handleClient);
    r.playToClient(PushBlockRowPacket.TYPE,                PushBlockRowPacket.STREAM_CODEC,                PushBlockRowPacket::handleClient);

    // smeltery
    r.playToClient(FluidUpdatePacket.TYPE,                 FluidUpdatePacket.STREAM_CODEC,                 FluidUpdatePacket::handleClient);
    r.playToClient(FaucetActivationPacket.TYPE,            FaucetActivationPacket.STREAM_CODEC,            FaucetActivationPacket::handleClient);
    r.playToClient(ChannelFlowPacket.TYPE,                 ChannelFlowPacket.STREAM_CODEC,                 ChannelFlowPacket::handleClient);
    r.playToClient(SmelteryTankUpdatePacket.TYPE,          SmelteryTankUpdatePacket.STREAM_CODEC,          SmelteryTankUpdatePacket::handleClient);
    r.playToClient(StructureUpdatePacket.TYPE,             StructureUpdatePacket.STREAM_CODEC,             StructureUpdatePacket::handleClient);
    r.playToServer(SmelteryFluidClickedPacket.TYPE,        SmelteryFluidClickedPacket.STREAM_CODEC,        SmelteryFluidClickedPacket::handleServer);
    r.playToClient(StructureErrorPositionPacket.TYPE,      StructureErrorPositionPacket.STREAM_CODEC,      StructureErrorPositionPacket::handleClient);
  }
}
