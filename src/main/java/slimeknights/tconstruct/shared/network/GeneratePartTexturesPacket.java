package slimeknights.tconstruct.shared.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.shared.client.ClientGeneratePartTexturesCommand;

/** Packet to tell the client to generate tool textures */
public record GeneratePartTexturesPacket(Operation operation, String modId, String materialPath) implements CustomPacketPayload {
  public static final Type<GeneratePartTexturesPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TConstruct.MOD_ID, "generate_part_textures"));
  public static final StreamCodec<RegistryFriendlyByteBuf, GeneratePartTexturesPacket> STREAM_CODEC = StreamCodec.composite(
    ByteBufCodecs.<RegistryFriendlyByteBuf, Operation>idMapper(i -> Operation.values()[i], Enum::ordinal),
    GeneratePartTexturesPacket::operation,
    ByteBufCodecs.stringUtf8(Short.MAX_VALUE), GeneratePartTexturesPacket::modId,
    ByteBufCodecs.stringUtf8(Short.MAX_VALUE), GeneratePartTexturesPacket::materialPath,
    GeneratePartTexturesPacket::new
  );

  @Override
  public Type<? extends CustomPacketPayload> type() { return TYPE; }

  public static void handleClient(GeneratePartTexturesPacket packet, IPayloadContext context) {
    context.enqueueWork(() -> ClientGeneratePartTexturesCommand.generateTextures(packet.operation(), packet.modId(), packet.materialPath()));
  }

  public enum Operation { ALL, MISSING }
}
