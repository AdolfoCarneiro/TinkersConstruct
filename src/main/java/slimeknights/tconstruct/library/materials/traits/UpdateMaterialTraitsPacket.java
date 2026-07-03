package slimeknights.tconstruct.library.materials.traits;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialId;

import java.util.HashMap;
import java.util.Map;

@Getter
public class UpdateMaterialTraitsPacket implements CustomPacketPayload {
  public static final Type<UpdateMaterialTraitsPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TConstruct.MOD_ID, "update_material_traits"));
  public static final StreamCodec<RegistryFriendlyByteBuf, UpdateMaterialTraitsPacket> STREAM_CODEC = StreamCodec.of((buf, p) -> p.encode(buf), UpdateMaterialTraitsPacket::new);

  protected final Map<MaterialId,MaterialTraits> materialToTraits;

  public UpdateMaterialTraitsPacket(Map<MaterialId,MaterialTraits> materialToTraits) {
    this.materialToTraits = materialToTraits;
  }

  /** Gets the map of material ID to traits */
  public Map<MaterialId,MaterialTraits> getMaterialToTraits() {
    return materialToTraits;
  }

  public UpdateMaterialTraitsPacket(RegistryFriendlyByteBuf buffer) {
    int materialCount = buffer.readInt();
    materialToTraits = new HashMap<>(materialCount);
    for (int i = 0; i < materialCount; i++) {
      MaterialId id = new MaterialId(buffer.readResourceLocation());
      MaterialTraits traits = MaterialTraits.read(buffer);
      materialToTraits.put(id, traits);
    }
  }

  public void encode(RegistryFriendlyByteBuf buffer) {
    buffer.writeInt(materialToTraits.size());
    materialToTraits.forEach((materialId, traits) -> {
      buffer.writeResourceLocation(materialId);
      traits.write(buffer);
    });
  }

  @Override
  public Type<? extends CustomPacketPayload> type() { return TYPE; }

  public static void handleClient(UpdateMaterialTraitsPacket packet, IPayloadContext context) {
    context.enqueueWork(() -> MaterialRegistry.updateMaterialTraitsFromServer(packet));
  }
}
