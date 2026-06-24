package slimeknights.tconstruct;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * TConstruct
 *
 * Central mod object for Tinkers' Construct.
 * Phase 0 stub: minimal NeoForge entrypoint. All subsystems are quarantined
 * under src/_quarantine and restored incrementally in later phases.
 */
@Mod(TConstruct.MOD_ID)
public class TConstruct {
  public static final String MOD_ID = "tconstruct";
  public static final Logger LOG = LogManager.getLogger("TConstruct");

  /** Mod event bus, captured at construction time. Replaces the removed FMLJavaModLoadingContext.get().getModEventBus() static lookup. */
  private static IEventBus modEventBus;

  public TConstruct(IEventBus modEventBus, ModContainer modContainer) {
    TConstruct.modEventBus = modEventBus;
    LOG.info("TConstruct Phase 0 stub loaded (NeoForge 1.21.1). Subsystems quarantined.");
    // Later phases restore: TinkerModule registration, config, datagen, network, client events.
  }

  public static ResourceLocation getResource(String name) {
    return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
  }

  /** Gets the mod event bus, for registering DeferredRegister/RegisterCapabilitiesEvent/etc listeners */
  public static IEventBus getModEventBus() {
    return modEventBus;
  }

  /**
   * Validates that a class extending a Tinkers' base class lives in the slimeknights.tconstruct package,
   * since these base classes are internal and not a stable addon API.
   */
  public static void sealTinkersClass(Object self, String base, String solution) {
    String name = self.getClass().getName();
    if (!name.startsWith("slimeknights.tconstruct.")) {
      throw new IllegalStateException(base + " being extended from invalid package " + name + ". " + solution);
    }
  }
}
