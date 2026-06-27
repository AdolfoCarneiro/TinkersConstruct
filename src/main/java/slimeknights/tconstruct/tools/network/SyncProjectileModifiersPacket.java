package slimeknights.tconstruct.tools.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import slimeknights.mantle.client.SafeClientAccess;
import slimeknights.mantle.data.loadable.Streamable;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.capability.EntityModifierCapability;
import slimeknights.tconstruct.library.tools.capability.PersistentDataCapability;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import java.util.Objects;

public record SyncProjectileModifiersPacket(int entityId, ModifierNBT modifiers, CompoundTag persistentData) implements CustomPacketPayload {
  public static final Type<SyncProjectileModifiersPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TConstruct.MOD_ID, "sync_projectile_modifiers"));
  public static final StreamCodec<RegistryFriendlyByteBuf, SyncProjectileModifiersPacket> STREAM_CODEC = StreamCodec.of((buf, p) -> p.encode(buf), SyncProjectileModifiersPacket::new);
  private static final Streamable<ModifierNBT> MODIFIER_LIST = ModifierEntry.LOADABLE.list(0).flatXmap(ModifierNBT::new, ModifierNBT::getModifiers);

  public SyncProjectileModifiersPacket(Entity entity) {
    this(entity.getId(), EntityModifierCapability.getOrEmpty(entity), PersistentDataCapability.getOrWarn(entity).getCopy());
  }

  public SyncProjectileModifiersPacket(RegistryFriendlyByteBuf buffer) {
    this(buffer.readVarInt(), MODIFIER_LIST.decode(buffer), Objects.requireNonNullElse(buffer.readNbt(), new CompoundTag()));
  }

  public void encode(RegistryFriendlyByteBuf buffer) {
    buffer.writeVarInt(entityId);
    MODIFIER_LIST.encode(buffer, modifiers);
    buffer.writeNbt(persistentData);
  }

  @Override
  public Type<? extends CustomPacketPayload> type() { return TYPE; }

  public static void handleClient(SyncProjectileModifiersPacket packet, IPayloadContext context) {
    context.enqueueWork(() -> HandleClient.handle(packet));
  }

  private static class HandleClient {
    private static void handle(SyncProjectileModifiersPacket packet) {
      Level level = SafeClientAccess.getLevel();
      if (level != null) {
        Entity entity = level.getEntity(packet.entityId());
        if (entity != null) {
          EntityModifierCapability.getCapability(entity).setModifiers(packet.modifiers());
          PersistentDataCapability.getOrWarn(entity).copyFrom(packet.persistentData());
        }
      }
    }
  }
}
