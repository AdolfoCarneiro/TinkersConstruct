package slimeknights.tconstruct.tables.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.tables.client.inventory.BaseTabbedScreen;

public record UpdateStationScreenPacket() implements CustomPacketPayload {
  public static final UpdateStationScreenPacket INSTANCE = new UpdateStationScreenPacket();
  public static final Type<UpdateStationScreenPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TConstruct.MOD_ID, "update_station_screen"));
  public static final StreamCodec<RegistryFriendlyByteBuf, UpdateStationScreenPacket> STREAM_CODEC = StreamCodec.of((buf, p) -> {}, buf -> INSTANCE);

  @Override
  public Type<? extends CustomPacketPayload> type() { return TYPE; }

  public static void handleClient(UpdateStationScreenPacket packet, IPayloadContext context) {
    context.enqueueWork(() -> HandleClient.handle());
  }

  /** Safely runs client side only code in a method only called on client */
  private static class HandleClient {
    private static void handle() {
      Screen screen = Minecraft.getInstance().screen;
      if (screen != null) {
        if (screen instanceof BaseTabbedScreen) {
          ((BaseTabbedScreen<?,?>) screen).updateDisplay();
        }
      }
    }
  }
}
