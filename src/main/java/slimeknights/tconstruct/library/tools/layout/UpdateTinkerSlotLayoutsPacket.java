package slimeknights.tconstruct.library.tools.layout;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import slimeknights.tconstruct.TConstruct;

import java.util.Collection;

/**
 * Packet to update the slot layouts for the tinker station
 */
public class UpdateTinkerSlotLayoutsPacket implements CustomPacketPayload {
  public static final Type<UpdateTinkerSlotLayoutsPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TConstruct.MOD_ID, "update_tinker_slot_layouts"));
  public static final StreamCodec<RegistryFriendlyByteBuf, UpdateTinkerSlotLayoutsPacket> STREAM_CODEC = StreamCodec.of((buf, p) -> p.encode(buf), UpdateTinkerSlotLayoutsPacket::new);

  @VisibleForTesting
  final Collection<StationSlotLayout> layouts;

  public UpdateTinkerSlotLayoutsPacket(Collection<StationSlotLayout> layouts) {
    this.layouts = layouts;
  }

  public UpdateTinkerSlotLayoutsPacket(RegistryFriendlyByteBuf buffer) {
    ImmutableList.Builder<StationSlotLayout> builder = ImmutableList.builder();
    int max = buffer.readVarInt();
    for (int i = 0; i < max; i++) {
      builder.add(StationSlotLayout.read(buffer));
    }
    layouts = builder.build();
  }

  public void encode(RegistryFriendlyByteBuf buffer) {
    buffer.writeVarInt(layouts.size());
    for (StationSlotLayout layout : layouts) {
      layout.write(buffer);
    }
  }

  @Override
  public Type<? extends CustomPacketPayload> type() { return TYPE; }

  public static void handleClient(UpdateTinkerSlotLayoutsPacket packet, IPayloadContext context) {
    context.enqueueWork(() -> StationSlotLayoutLoader.getInstance().setSlots(packet.layouts));
  }
}
