package slimeknights.tconstruct.library.tools.capability;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import java.util.function.Supplier;

/**
 * Capability to store persistent NBT data on an entity. For players, this is automatically synced to the client on load, but not during gameplay.
 * Persists after death, will reassess if we need some data to not persist death
 */
public class PersistentDataCapability {
  private PersistentDataCapability() {}

  private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, TConstruct.MOD_ID);

  /** Reads/writes the raw NBT compound backing {@link ModDataNBT} */
  private static final IAttachmentSerializer<CompoundTag,ModDataNBT> SERIALIZER = new IAttachmentSerializer<>() {
    @Override
    public ModDataNBT read(IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider provider) {
      return ModDataNBT.readFromNBT(tag);
    }

    @Override
    public CompoundTag write(ModDataNBT attachment, HolderLookup.Provider provider) {
      return attachment.getCopy();
    }
  };

  /** Stream codec used to sync the data to its owning player */
  private static final StreamCodec<ByteBuf,ModDataNBT> STREAM_CODEC = ByteBufCodecs.COMPOUND_TAG.map(ModDataNBT::readFromNBT, ModDataNBT::getCopy);

  /**
   * Attachment type. Works on any entity, persisted to NBT (server side only), copied across death/end-return,
   * and synced only to its own owning player at specific lifecycle moments (respawn, dimension change, login) - never continuously.
   */
  public static final Supplier<AttachmentType<ModDataNBT>> ATTACHMENT = ATTACHMENT_TYPES.register("persistent_data", () ->
    AttachmentType.builder(holder -> new ModDataNBT())
                  .serialize(SERIALIZER)
                  .copyOnDeath()
                  .sync((holder, to) -> holder == to, STREAM_CODEC)
                  .build());

  /** Gets the data or warns if its missing */
  public static ModDataNBT getOrWarn(Entity entity) {
    return entity.getData(ATTACHMENT);
  }

  /** Registers this capability */
  public static void register() {
    ATTACHMENT_TYPES.register(TConstruct.getModEventBus());
    NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, PlayerEvent.PlayerRespawnEvent.class, PersistentDataCapability::playerRespawn);
    NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, PlayerEvent.PlayerChangedDimensionEvent.class, PersistentDataCapability::playerChangeDimension);
    NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, PlayerEvent.PlayerLoggedInEvent.class, PersistentDataCapability::playerLoggedIn);
  }

  /** sync caps when the player respawns/returns from the end */
  private static void playerRespawn(PlayerEvent.PlayerRespawnEvent event) {
    event.getEntity().syncData(ATTACHMENT);
  }

  /** sync caps when the player changes dimensions */
  private static void playerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
    event.getEntity().syncData(ATTACHMENT);
  }

  /** sync caps when the player logs in */
  private static void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
    event.getEntity().syncData(ATTACHMENT);
  }
}
