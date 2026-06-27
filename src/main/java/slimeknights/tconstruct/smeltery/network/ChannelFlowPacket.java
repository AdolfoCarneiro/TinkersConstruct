package slimeknights.tconstruct.smeltery.network;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import slimeknights.mantle.util.BlockEntityHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.smeltery.block.entity.ChannelBlockEntity;

/** Packet for when the flowing state changes on a channel side */
public record ChannelFlowPacket(BlockPos pos, Direction side, boolean flow) implements CustomPacketPayload {
  public static final Type<ChannelFlowPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TConstruct.MOD_ID, "channel_flow"));
  public static final StreamCodec<RegistryFriendlyByteBuf, ChannelFlowPacket> STREAM_CODEC = StreamCodec.composite(
    BlockPos.STREAM_CODEC, ChannelFlowPacket::pos,
    ByteBufCodecs.idMapper(i -> Direction.values()[i], Direction::ordinal),
    ChannelFlowPacket::side,
    ByteBufCodecs.BOOL, ChannelFlowPacket::flow,
    ChannelFlowPacket::new
  );

  @Override
  public Type<? extends CustomPacketPayload> type() { return TYPE; }

  public static void handleClient(ChannelFlowPacket packet, IPayloadContext context) {
    context.enqueueWork(() -> HandleClient.handle(packet));
  }

  private static class HandleClient {
    private static void handle(ChannelFlowPacket packet) {
      BlockEntityHelper.get(ChannelBlockEntity.class, Minecraft.getInstance().level, packet.pos()).ifPresent(te -> te.setFlow(packet.side(), packet.flow()));
    }
  }
}
