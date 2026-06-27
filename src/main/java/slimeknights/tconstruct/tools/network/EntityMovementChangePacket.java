package slimeknights.tconstruct.tools.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import slimeknights.tconstruct.TConstruct;

public class EntityMovementChangePacket implements CustomPacketPayload {
  public static final Type<EntityMovementChangePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TConstruct.MOD_ID, "entity_movement_change"));
  public static final StreamCodec<RegistryFriendlyByteBuf, EntityMovementChangePacket> STREAM_CODEC = StreamCodec.composite(
    ByteBufCodecs.VAR_INT, p -> p.entityID,
    ByteBufCodecs.DOUBLE, p -> p.x,
    ByteBufCodecs.DOUBLE, p -> p.y,
    ByteBufCodecs.DOUBLE, p -> p.z,
    ByteBufCodecs.FLOAT, p -> p.yRot,
    ByteBufCodecs.FLOAT, p -> p.xRot,
    EntityMovementChangePacket::new
  );

  private final int entityID;
  private final double x;
  private final double y;
  private final double z;
  private final float yRot;
  private final float xRot;

  public EntityMovementChangePacket(Entity entity) {
    this.entityID = entity.getId();
    this.x = entity.getDeltaMovement().x;
    this.y = entity.getDeltaMovement().y;
    this.z = entity.getDeltaMovement().z;
    this.yRot = entity.getYRot();
    this.xRot = entity.getXRot();
  }

  public EntityMovementChangePacket(int entityID, double x, double y, double z, float yRot, float xRot) {
    this.entityID = entityID;
    this.x = x;
    this.y = y;
    this.z = z;
    this.yRot = yRot;
    this.xRot = xRot;
  }

  @Override
  public Type<? extends CustomPacketPayload> type() { return TYPE; }

  public static void handleClient(EntityMovementChangePacket packet, IPayloadContext context) {
    context.enqueueWork(() -> HandleClient.handle(packet));
  }

  /** Safely runs client side only code in a method only called on client */
  private static class HandleClient {
    private static void handle(EntityMovementChangePacket packet) {
      if (Minecraft.getInstance().level != null) {
        Entity entity = Minecraft.getInstance().level.getEntity(packet.entityID);
        if (entity != null) {
          entity.setDeltaMovement(packet.x, packet.y, packet.z);
          entity.setYRot(packet.yRot);
          entity.setXRot(packet.xRot);
        }
      }
    }
  }
}
