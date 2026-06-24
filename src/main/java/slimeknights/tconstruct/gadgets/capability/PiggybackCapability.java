package slimeknights.tconstruct.gadgets.capability;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import slimeknights.tconstruct.TConstruct;

import java.util.function.Supplier;

/** Attachment logic */
public class PiggybackCapability {
  private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, TConstruct.MOD_ID);

  /** Does not serialize as the world saves the entities already, they just dismount on logout */
  public static final Supplier<AttachmentType<PiggybackHandler>> PIGGYBACK = ATTACHMENT_TYPES.register(
    "piggyback", () -> AttachmentType.<PiggybackHandler>builder(PiggybackCapability::createDefault).build());

  /** Creates the default handler for a given holder, only meaningful for {@link Player} */
  private static PiggybackHandler createDefault(IAttachmentHolder holder) {
    return new PiggybackHandler(holder instanceof Player player ? player : null);
  }

  private PiggybackCapability() {}

  /** Registers this attachment type */
  public static void register() {
    ATTACHMENT_TYPES.register(TConstruct.getModEventBus());
  }
}
