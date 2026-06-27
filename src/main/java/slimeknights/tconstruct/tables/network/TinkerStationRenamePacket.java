package slimeknights.tconstruct.tables.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.tables.block.entity.table.TinkerStationBlockEntity;
import slimeknights.tconstruct.tables.menu.TinkerStationContainerMenu;

/** Packet to send to the server to update the name in the UI */
public record TinkerStationRenamePacket(String name) implements CustomPacketPayload {
  public static final Type<TinkerStationRenamePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TConstruct.MOD_ID, "tinker_station_rename"));
  public static final StreamCodec<RegistryFriendlyByteBuf, TinkerStationRenamePacket> STREAM_CODEC = StreamCodec.composite(
    ByteBufCodecs.stringUtf8(Short.MAX_VALUE), TinkerStationRenamePacket::name,
    TinkerStationRenamePacket::new
  );

  @Override
  public Type<? extends CustomPacketPayload> type() { return TYPE; }

  public static void handleServer(TinkerStationRenamePacket packet, IPayloadContext context) {
    context.enqueueWork(() -> {
      ServerPlayer sender = (ServerPlayer) context.player();
      if (sender.containerMenu instanceof TinkerStationContainerMenu station) {
        TinkerStationBlockEntity tile = station.getTile();
        if (tile != null) {
          station.getTile().setItemName(packet.name());
        }
      }
    });
  }
}
