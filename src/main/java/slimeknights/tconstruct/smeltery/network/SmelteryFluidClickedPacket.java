package slimeknights.tconstruct.smeltery.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import slimeknights.mantle.inventory.BaseContainerMenu;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.smeltery.block.entity.tank.ISmelteryTankHandler;

/**
 * Packet sent when a fluid is clicked in the smeltery UI
 */
public record SmelteryFluidClickedPacket(int index) implements CustomPacketPayload {
  public static final Type<SmelteryFluidClickedPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TConstruct.MOD_ID, "smeltery_fluid_clicked"));
  public static final StreamCodec<RegistryFriendlyByteBuf, SmelteryFluidClickedPacket> STREAM_CODEC = StreamCodec.composite(
    ByteBufCodecs.VAR_INT, SmelteryFluidClickedPacket::index,
    SmelteryFluidClickedPacket::new
  );

  @Override
  public Type<? extends CustomPacketPayload> type() { return TYPE; }

  public static void handleServer(SmelteryFluidClickedPacket packet, IPayloadContext context) {
    context.enqueueWork(() -> {
      ServerPlayer sender = (ServerPlayer) context.player();
      if (!sender.isSpectator()) {
        AbstractContainerMenu container = sender.containerMenu;
        if (container instanceof BaseContainerMenu<?> base && base.getTile() instanceof ISmelteryTankHandler tank) {
          tank.getTank().moveFluidToBottom(packet.index());
        }
      }
    });
  }
}
