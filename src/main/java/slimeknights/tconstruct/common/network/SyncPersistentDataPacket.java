package slimeknights.tconstruct.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tools.capability.PersistentDataCapability;

/** Packet to sync player persistent data to the client */
public record SyncPersistentDataPacket(CompoundTag data) implements CustomPacketPayload {
  public static final Type<SyncPersistentDataPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TConstruct.MOD_ID, "sync_persistent_data"));
  public static final StreamCodec<RegistryFriendlyByteBuf, SyncPersistentDataPacket> STREAM_CODEC = StreamCodec.composite(
    ByteBufCodecs.COMPOUND_TAG, SyncPersistentDataPacket::data,
    SyncPersistentDataPacket::new
  );

  @Override
  public Type<? extends CustomPacketPayload> type() { return TYPE; }

  public static void handleClient(SyncPersistentDataPacket packet, IPayloadContext context) {
    context.enqueueWork(() -> HandleClient.handle(packet));
  }

  /** Handles client side only code safely */
  private static class HandleClient {
    private static void handle(SyncPersistentDataPacket packet) {
      Player player = Minecraft.getInstance().player;
      if (player != null) {
        player.getCapability(PersistentDataCapability.CAPABILITY).ifPresent(data -> data.copyFrom(packet.data()));
      }
    }
  }
}
