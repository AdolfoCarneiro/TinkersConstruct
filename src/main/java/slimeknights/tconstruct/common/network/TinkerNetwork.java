package slimeknights.tconstruct.common.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;

/** Static send-helper facade over NeoForge PacketDistributor. Registration is in TinkerPayloadInit. */
public class TinkerNetwork {
  private TinkerNetwork() {}

  public static void sendTo(CustomPacketPayload payload, ServerPlayer player) {
    PacketDistributor.sendToPlayer(player, payload);
  }

  public static void sendToServer(CustomPacketPayload payload) {
    PacketDistributor.sendToServer(payload);
  }

  public static void sendToClientsAround(CustomPacketPayload payload, @Nullable LevelAccessor world, BlockPos position) {
    if (world instanceof ServerLevel server) {
      PacketDistributor.sendToPlayersNear(server, null, position.getX(), position.getY(), position.getZ(), 64, payload);
    }
  }

  public static void sendVanillaPacket(Entity player, Packet<?> packet) {
    if (player instanceof ServerPlayer serverPlayer) {
      serverPlayer.connection.send(packet);
    }
  }

  /** Reversed-arg overload used by some callers */
  public static void sendVanillaPacket(Packet<?> packet, Entity player) {
    sendVanillaPacket(player, packet);
  }

  public static void sendToTrackingAndSelf(CustomPacketPayload payload, Entity entity) {
    PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, payload);
  }

  public static void sendToTracking(CustomPacketPayload payload, Entity entity) {
    PacketDistributor.sendToPlayersTrackingEntity(entity, payload);
  }

  public static void sendToPlayerList(@Nullable ServerPlayer targetedPlayer, PlayerList playerList, CustomPacketPayload payload) {
    if (targetedPlayer != null) {
      PacketDistributor.sendToPlayer(targetedPlayer, payload);
    } else {
      for (ServerPlayer player : playerList.getPlayers()) {
        PacketDistributor.sendToPlayer(player, payload);
      }
    }
  }
}
