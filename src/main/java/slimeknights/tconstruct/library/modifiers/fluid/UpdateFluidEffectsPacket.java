package slimeknights.tconstruct.library.modifiers.fluid;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.tconstruct.TConstruct;

import java.util.ArrayList;
import java.util.List;

/** Packet to sync fluid predicates to the client */
@Internal
public record UpdateFluidEffectsPacket(List<FluidEffects.Entry> fluids) implements CustomPacketPayload {
  public static final Type<UpdateFluidEffectsPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TConstruct.MOD_ID, "update_fluid_effects"));
  public static final StreamCodec<RegistryFriendlyByteBuf, UpdateFluidEffectsPacket> STREAM_CODEC = StreamCodec.of((buf, p) -> p.encode(buf), UpdateFluidEffectsPacket::decode);

  /** Clientside constructor, reading from the buffer */
  public static UpdateFluidEffectsPacket decode(RegistryFriendlyByteBuf buffer) {
    int size = buffer.readVarInt();
    List<FluidEffects.Entry> entries = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      ResourceLocation key = buffer.readResourceLocation();
      try {
        FluidEffects effects = FluidEffects.LOADABLE.decode(buffer, FluidEffectManager.contextBuilder(key).build());
        entries.add(new FluidEffects.Entry(key, effects));
      } catch (RuntimeException e) {
        // put exception in the log with a bit more info
        TConstruct.LOG.error("Failed to decode fluid effects with ID {}", key, e);
        throw e;
      }
    }
    return new UpdateFluidEffectsPacket(List.copyOf(entries));
  }

  public void encode(RegistryFriendlyByteBuf buffer) {
    buffer.writeVarInt(fluids.size());
    for (FluidEffects.Entry entry : fluids) {
      ResourceLocation key = entry.name();
      buffer.writeResourceLocation(key);
      try {
        FluidEffects.LOADABLE.encode(buffer, entry.effects());
      } catch (RuntimeException e) {
        TConstruct.LOG.error("Failed to encode fluid effects with ID {}", key, e);
        throw e;
      }
    }
  }

  @Override
  public Type<? extends CustomPacketPayload> type() { return TYPE; }

  public static void handleClient(UpdateFluidEffectsPacket packet, IPayloadContext context) {
    context.enqueueWork(() -> FluidEffectManager.INSTANCE.updateFromServer(packet.fluids()));
  }
}
