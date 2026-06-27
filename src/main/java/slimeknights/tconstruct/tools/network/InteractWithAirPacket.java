package slimeknights.tconstruct.tools.network;

import lombok.RequiredArgsConstructor;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.tools.logic.InteractionHandler;

/** Packet sent client to server when an empty hand interaction */
@RequiredArgsConstructor
public enum InteractWithAirPacket implements CustomPacketPayload {
  /** Right click with an empty main hand and a chestplate */
  MAINHAND(InteractionHand.MAIN_HAND),
  /** Right click with an empty off hand and a chestplate */
  OFFHAND(InteractionHand.OFF_HAND),
  /** Left click with a supported tool */
  LEFT_CLICK(InteractionHand.MAIN_HAND);

  public static final Type<InteractWithAirPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TConstruct.MOD_ID, "interact_with_air"));
  public static final StreamCodec<RegistryFriendlyByteBuf, InteractWithAirPacket> STREAM_CODEC =
    ByteBufCodecs.<RegistryFriendlyByteBuf, InteractWithAirPacket>idMapper(i -> InteractWithAirPacket.values()[i], Enum::ordinal);

  private final InteractionHand hand;

  @Override
  public Type<? extends CustomPacketPayload> type() { return TYPE; }

  /** Gets the packet for the given hand */
  public static InteractWithAirPacket fromChestplate(InteractionHand hand) {
    return hand == InteractionHand.OFF_HAND ? OFFHAND : MAINHAND;
  }

  public static void handleServer(InteractWithAirPacket packet, IPayloadContext context) {
    context.enqueueWork(() -> {
      ServerPlayer player = (ServerPlayer) context.player();
      if (!player.isSpectator()) {
        if (packet == LEFT_CLICK) {
          ItemStack held = player.getItemInHand(packet.hand);
          if (held.is(TinkerTags.Items.INTERACTABLE_LEFT)) {
            InteractionResult result = InteractionHandler.onLeftClickInteraction(player, held, packet.hand);
            if (result.shouldSwing()) {
              player.swing(packet.hand, true);
            }
          }
        } else {
          ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
          if (chestplate.is(TinkerTags.Items.INTERACTABLE_ARMOR) && player.getItemInHand(packet.hand).isEmpty()) {
            InteractionResult result = InteractionHandler.onChestplateUse(player, chestplate, packet.hand);
            if (result.shouldSwing()) {
              player.swing(packet.hand, true);
            }
          }
        }
      }
    });
  }
}
