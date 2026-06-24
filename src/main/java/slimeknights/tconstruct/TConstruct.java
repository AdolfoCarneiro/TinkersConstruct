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
@Mod(TConstruct.modID)
public class TConstruct {
  public static final String modID = "tconstruct";
  public static final Logger LOG = LogManager.getLogger("TConstruct");

  public TConstruct(IEventBus modEventBus, ModContainer modContainer) {
    LOG.info("TConstruct Phase 0 stub loaded (NeoForge 1.21.1). Subsystems quarantined.");
    // Later phases restore: TinkerModule registration, config, datagen, network, client events.
  }

  public static ResourceLocation getResource(String name) {
    return ResourceLocation.fromNamespaceAndPath(modID, name);
  }
}
