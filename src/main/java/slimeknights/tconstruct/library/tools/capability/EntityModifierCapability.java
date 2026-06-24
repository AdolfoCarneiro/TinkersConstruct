package slimeknights.tconstruct.library.tools.capability;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import java.util.function.Supplier;

/** Capability to allow an entity to store modifiers, used on projectiles fired from modifiable items */
public class EntityModifierCapability {
  private EntityModifierCapability() {}

  private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, TConstruct.MOD_ID);

  /** Reads/writes the backing {@link ModifierNBT} list */
  private static final IAttachmentSerializer<ListTag,ModifierNBT> SERIALIZER = new IAttachmentSerializer<>() {
    @Override
    public ModifierNBT read(IAttachmentHolder holder, ListTag tag, HolderLookup.Provider provider) {
      return ModifierNBT.readFromNBT(tag);
    }

    @Override
    public ListTag write(ModifierNBT attachment, HolderLookup.Provider provider) {
      return attachment.serializeToNBT();
    }
  };

  /** Attachment type. Works on any entity, defaults to {@link ModifierNBT#EMPTY}, persisted to NBT */
  public static final Supplier<AttachmentType<ModifierNBT>> ATTACHMENT = ATTACHMENT_TYPES.register(
    "modifiers", () -> AttachmentType.builder(holder -> ModifierNBT.EMPTY).serialize(SERIALIZER).build());

  /** Gets the data or an empty instance if missing */
  public static ModifierNBT getOrEmpty(Entity entity) {
    return entity.getData(ATTACHMENT);
  }

  /** Gets the capability for the entity or an empty instance if missing */
  public static EntityModifiers getCapability(Entity entity) {
    return new EntityModifiers() {
      @Override
      public ModifierNBT getModifiers() {
        return entity.getData(ATTACHMENT);
      }

      @Override
      public void setModifiers(ModifierNBT nbt) {
        entity.setData(ATTACHMENT, nbt);
      }
    };
  }

  /** Registers this capability with relevant busses */
  public static void register() {
    ATTACHMENT_TYPES.register(TConstruct.getModEventBus());
  }

  /** Interface for callers to use */
  public interface EntityModifiers {
    /** Gets the stored modifiers */
    ModifierNBT getModifiers();

    /** Sets the stored modifiers */
    void setModifiers(ModifierNBT nbt);

    /** Adds additional modifiers to the stored modifiers */
    default void addModifiers(ModifierNBT nbt) {
      ModifierNBT existing = getModifiers();
      if (existing.isEmpty()) {
        setModifiers(nbt);
      } else {
        setModifiers(ModifierNBT.builder().add(existing).add(nbt).build());
      }
    }
  }
}
