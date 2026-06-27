package slimeknights.tconstruct.tools.network;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.fluid.block.MoveBlocksFluidEffect;

/** Packet handling {@link MoveBlocksFluidEffect} syncing to the client */
public record PushBlockRowPacket(BlockPos pos, Direction direction, boolean push, int moving) implements CustomPacketPayload {
  public static final Type<PushBlockRowPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TConstruct.MOD_ID, "push_block_row"));
  public static final StreamCodec<RegistryFriendlyByteBuf, PushBlockRowPacket> STREAM_CODEC = StreamCodec.composite(
    BlockPos.STREAM_CODEC, PushBlockRowPacket::pos,
    ByteBufCodecs.idMapper(i -> Direction.values()[i], Direction::ordinal),
    PushBlockRowPacket::direction,
    ByteBufCodecs.BOOL, PushBlockRowPacket::push,
    ByteBufCodecs.VAR_INT, PushBlockRowPacket::moving,
    PushBlockRowPacket::new
  );

  @Override
  public Type<? extends CustomPacketPayload> type() { return TYPE; }

  /** Gets the facing value for this packet */
  private Direction facing() {
    return push ? direction : direction.getOpposite();
  }

  public static void handleClient(PushBlockRowPacket packet, IPayloadContext context) {
    context.enqueueWork(() -> HandleClient.handle(packet));
  }

  /** Accesses client only safely */
  private static class HandleClient {
    public static void handle(PushBlockRowPacket packet) {
      Level level = Minecraft.getInstance().level;
      if (level != null) {
        MoveBlocksFluidEffect.moveBlocks(level, packet.pos, level.getBlockState(packet.pos), packet.facing(), packet.direction, packet.moving);
      }
    }
  }
}
