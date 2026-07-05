package slimeknights.tconstruct.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import slimeknights.tconstruct.TConstruct;

public record InventorySlotSyncPacket(ItemStack itemStack, int slot, BlockPos pos) implements CustomPacketPayload {
  public static final Type<InventorySlotSyncPacket> TYPE =
    new Type<>(ResourceLocation.fromNamespaceAndPath(TConstruct.MOD_ID, "inventory_slot_sync"));
  public static final StreamCodec<RegistryFriendlyByteBuf, InventorySlotSyncPacket> STREAM_CODEC =
    StreamCodec.composite(
      ItemStack.OPTIONAL_STREAM_CODEC, InventorySlotSyncPacket::itemStack,
      ByteBufCodecs.SHORT.map(s -> (int) s, i -> (short) (int) i), InventorySlotSyncPacket::slot,
      BlockPos.STREAM_CODEC,  InventorySlotSyncPacket::pos,
      InventorySlotSyncPacket::new);

  @Override
  public Type<? extends CustomPacketPayload> type() { return TYPE; }

  public static void handleClient(InventorySlotSyncPacket packet, IPayloadContext context) {
    context.enqueueWork(() -> HandleClient.handle(packet));
  }

  private static class HandleClient {
    private static void handle(InventorySlotSyncPacket packet) {
      Level world = Minecraft.getInstance().level;
      if (world != null) {
        BlockEntity te = world.getBlockEntity(packet.pos);
        if (te != null) {
          IItemHandler cap = world.getCapability(Capabilities.ItemHandler.BLOCK, packet.pos, null);
          if (cap instanceof IItemHandlerModifiable modifiable) {
            modifiable.setStackInSlot(packet.slot, packet.itemStack);
            //noinspection ConstantConditions
            Minecraft.getInstance().levelRenderer.blockChanged(null, packet.pos, null, null, 0);
          }
        }
      }
    }
  }
}
