package slimeknights.tconstruct.tools.network;

import lombok.RequiredArgsConstructor;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.shared.TinkerEffects;
import slimeknights.tconstruct.tools.logic.DoubleJumpHandler;
import slimeknights.tconstruct.tools.logic.InteractionHandler;

/**
 * Generic packet for various controls the client may send to the server
 */
@RequiredArgsConstructor
public enum TinkerControlPacket implements CustomPacketPayload {
  DOUBLE_JUMP,
  ANTIGRAVITY_JUMP,
  // helmet
  START_HELMET_INTERACT(TooltipKey.NORMAL),
  START_HELMET_INTERACT_SHIFT(TooltipKey.SHIFT),
  START_HELMET_INTERACT_CONTROL(TooltipKey.CONTROL),
  START_HELMET_INTERACT_ALT(TooltipKey.ALT),
  STOP_HELMET_INTERACT,
  // leggings
  START_LEGGINGS_INTERACT(TooltipKey.NORMAL),
  START_LEGGINGS_INTERACT_SHIFT(TooltipKey.SHIFT),
  START_LEGGINGS_INTERACT_CONTROL(TooltipKey.CONTROL),
  START_LEGGINGS_INTERACT_ALT(TooltipKey.ALT),
  STOP_LEGGINGS_INTERACT;

  public static final Type<TinkerControlPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TConstruct.MOD_ID, "tinker_control"));
  public static final StreamCodec<RegistryFriendlyByteBuf, TinkerControlPacket> STREAM_CODEC =
    ByteBufCodecs.<RegistryFriendlyByteBuf, TinkerControlPacket>idMapper(i -> TinkerControlPacket.values()[i], Enum::ordinal);

  private final TooltipKey modifier;

  TinkerControlPacket() {
    this(TooltipKey.UNKNOWN);
  }

  @Override
  public Type<? extends CustomPacketPayload> type() { return TYPE; }

  /** Gets the packet for helmet interaction */
  public static TinkerControlPacket getStartHelmetInteract(TooltipKey key) {
    return switch (key) {
      case SHIFT -> START_HELMET_INTERACT_SHIFT;
      case CONTROL -> START_HELMET_INTERACT_CONTROL;
      case ALT -> START_HELMET_INTERACT_ALT;
      default -> START_HELMET_INTERACT;
    };
  }

  /** Gets the packet for leggings interaction */
  public static TinkerControlPacket getStartLeggingsInteract(TooltipKey key) {
    return switch (key) {
      case SHIFT -> START_LEGGINGS_INTERACT_SHIFT;
      case CONTROL -> START_LEGGINGS_INTERACT_CONTROL;
      case ALT -> START_LEGGINGS_INTERACT_ALT;
      default -> START_LEGGINGS_INTERACT;
    };
  }

  public static void handleServer(TinkerControlPacket packet, IPayloadContext context) {
    context.enqueueWork(() -> {
      ServerPlayer player = (ServerPlayer) context.player();
      switch (packet) {
        case DOUBLE_JUMP -> DoubleJumpHandler.extraJump(player);
        case ANTIGRAVITY_JUMP -> TinkerEffects.antigravity.get().antigravityJump(player);
        case START_HELMET_INTERACT, START_HELMET_INTERACT_SHIFT, START_HELMET_INTERACT_CONTROL, START_HELMET_INTERACT_ALT
          -> InteractionHandler.startArmorInteract(player, EquipmentSlot.HEAD, packet.modifier);
        case STOP_HELMET_INTERACT -> InteractionHandler.stopArmorInteract(player, EquipmentSlot.HEAD);
        case START_LEGGINGS_INTERACT, START_LEGGINGS_INTERACT_SHIFT, START_LEGGINGS_INTERACT_CONTROL, START_LEGGINGS_INTERACT_ALT
          -> InteractionHandler.startArmorInteract(player, EquipmentSlot.LEGS, packet.modifier);
        case STOP_LEGGINGS_INTERACT -> InteractionHandler.stopArmorInteract(player, EquipmentSlot.LEGS);
      }
    });
  }
}
