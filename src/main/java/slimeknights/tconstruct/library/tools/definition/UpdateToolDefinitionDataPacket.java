package slimeknights.tconstruct.library.tools.definition;

import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import slimeknights.tconstruct.TConstruct;

import java.util.Map;
import java.util.Map.Entry;

/** Packet to sync tool definitions to the client */
@RequiredArgsConstructor
public class UpdateToolDefinitionDataPacket implements CustomPacketPayload {
  public static final Type<UpdateToolDefinitionDataPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TConstruct.MOD_ID, "update_tool_definition_data"));
  public static final StreamCodec<RegistryFriendlyByteBuf, UpdateToolDefinitionDataPacket> STREAM_CODEC = StreamCodec.of((buf, p) -> p.encode(buf), UpdateToolDefinitionDataPacket::new);

  @Getter(AccessLevel.PROTECTED)
  private final Map<ResourceLocation, ToolDefinitionData> dataMap;

  public UpdateToolDefinitionDataPacket(RegistryFriendlyByteBuf buffer) {
    int size = buffer.readVarInt();
    ImmutableMap.Builder<ResourceLocation, ToolDefinitionData> builder = ImmutableMap.builder();
    for (int i = 0; i < size; i++) {
      ResourceLocation name = buffer.readResourceLocation();
      try {
        ToolDefinitionData data = ToolDefinitionData.LOADABLE.decode(buffer, ToolDefinitionLoader.contextBuilder(name).build());
        builder.put(name, data);
      } catch (RuntimeException e) {
        TConstruct.LOG.error("Failed to decode Tool Definition for {}", name, e);
        throw e;
      }
    }
    dataMap = builder.build();
  }

  public void encode(RegistryFriendlyByteBuf buffer) {
    buffer.writeVarInt(dataMap.size());
    for (Entry<ResourceLocation, ToolDefinitionData> entry : dataMap.entrySet()) {
      ResourceLocation name = entry.getKey();
      buffer.writeResourceLocation(name);
      try {
        ToolDefinitionData.LOADABLE.encode(buffer, entry.getValue());
      } catch (RuntimeException e) {
        TConstruct.LOG.error("Failed to encode Tool Definition for {}", name, e);
        throw e;
      }
    }
  }

  @Override
  public Type<? extends CustomPacketPayload> type() { return TYPE; }

  public static void handleClient(UpdateToolDefinitionDataPacket packet, IPayloadContext context) {
    context.enqueueWork(() -> ToolDefinitionLoader.getInstance().updateDataFromServer(packet.dataMap));
  }
}
