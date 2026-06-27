package slimeknights.tconstruct.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import slimeknights.tconstruct.TConstruct;

/**
 * Packet to notify neighbors that a block changed, used when breaking blocks in weird contexts that vanilla suppresses updates in for some reason
 */
public class UpdateNeighborsPacket implements CustomPacketPayload {
  public static final Type<UpdateNeighborsPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TConstruct.MOD_ID, "update_neighbors"));
  public static final StreamCodec<RegistryFriendlyByteBuf, UpdateNeighborsPacket> STREAM_CODEC = StreamCodec.of((buf, p) -> p.encode(buf), UpdateNeighborsPacket::new);

  private final BlockState state;
  private final BlockPos pos;

  public UpdateNeighborsPacket(BlockState state, BlockPos pos) {
    this.state = state;
    this.pos = pos;
  }

  public UpdateNeighborsPacket(RegistryFriendlyByteBuf buffer) {
    this.state = Block.stateById(buffer.readVarInt());
    this.pos = buffer.readBlockPos();
  }

  public void encode(RegistryFriendlyByteBuf buffer) {
    buffer.writeVarInt(Block.getId(state));
    buffer.writeBlockPos(pos);
  }

  @Override
  public Type<? extends CustomPacketPayload> type() { return TYPE; }

  public static void handleClient(UpdateNeighborsPacket packet, IPayloadContext context) {
    context.enqueueWork(() -> HandleClient.handle(packet));
  }

  private static class HandleClient {
    private static void handle(UpdateNeighborsPacket packet) {
      Level level = Minecraft.getInstance().level;
      if (level != null) {
        packet.state.updateNeighbourShapes(level, packet.pos, Block.UPDATE_CLIENTS, 511);
        packet.state.updateIndirectNeighbourShapes(level, packet.pos, Block.UPDATE_CLIENTS, 511);
      }
    }
  }
}
