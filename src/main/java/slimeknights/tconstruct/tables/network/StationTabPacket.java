package slimeknights.tconstruct.tables.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.network.TinkerNetwork;
import slimeknights.tconstruct.tables.block.ITabbedBlock;

public class StationTabPacket implements CustomPacketPayload {
  public static final Type<StationTabPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TConstruct.MOD_ID, "station_tab"));
  public static final StreamCodec<RegistryFriendlyByteBuf, StationTabPacket> STREAM_CODEC = StreamCodec.of((buf, p) -> p.encode(buf), StationTabPacket::new);

  private final BlockPos pos;

  public StationTabPacket(BlockPos pos) {
    this.pos = pos;
  }

  public StationTabPacket(RegistryFriendlyByteBuf buffer) {
    this.pos = buffer.readBlockPos();
  }

  public void encode(RegistryFriendlyByteBuf buffer) {
    buffer.writeBlockPos(pos);
  }

  @Override
  public Type<? extends CustomPacketPayload> type() { return TYPE; }

  public static void handleServer(StationTabPacket packet, IPayloadContext context) {
    context.enqueueWork(() -> {
      ServerPlayer sender = (ServerPlayer) context.player();
      ItemStack heldStack = sender.containerMenu.getCarried();
      if (!heldStack.isEmpty()) {
        // set it to empty, so it doesn't get dropped
        sender.containerMenu.setCarried(ItemStack.EMPTY);
      }

      Level world = sender.getCommandSenderWorld();
      if (!world.hasChunkAt(packet.pos)) {
        return;
      }
      BlockState state = world.getBlockState(packet.pos);
      if (state.getBlock() instanceof ITabbedBlock) {
        ((ITabbedBlock) state.getBlock()).openGui(sender, sender.getCommandSenderWorld(), packet.pos);
      } else {
        MenuProvider provider = state.getMenuProvider(sender.getCommandSenderWorld(), packet.pos);
        if (provider != null) {
          sender.openMenu(provider, packet.pos);
        }
      }

      if (!heldStack.isEmpty()) {
        sender.containerMenu.setCarried(heldStack);
        TinkerNetwork.sendVanillaPacket(sender, new ClientboundContainerSetSlotPacket(-1, -1, -1, heldStack));
      }
    });
  }
}
