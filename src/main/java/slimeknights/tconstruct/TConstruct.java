package slimeknights.tconstruct;

import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.ComputableDataKey;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;

import java.util.Random;
import java.util.function.Supplier;

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
  public static final Random RANDOM = new Random();
  /** Registry provider covering vanilla built-in registries (fluids, items, etc), for FluidStack/ItemStack NBT save/parse calls in static contexts with no Level/RegistryAccess available */
  public static final HolderLookup.Provider STATIC_PROVIDER = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);

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

  public static <T> TinkerDataKey<T> createKey(String name) {
    return TinkerDataKey.of(getResource(name));
  }

  public static <T> ComputableDataKey<T> createKey(String name, Supplier<T> constructor) {
    return ComputableDataKey.of(getResource(name), constructor);
  }

  /** Makes a translation key for the given name, e.g. "block.tconstruct.name" */
  public static String makeDescriptionId(String base, String name) {
    return Util.makeDescriptionId(base, getResource(name));
  }

  /** Alias of {@link #makeDescriptionId(String, String)}, kept for call site compatibility */
  public static String makeTranslationKey(String base, String name) {
    return makeDescriptionId(base, name);
  }

  /** Makes a translation component for the given name */
  public static MutableComponent makeTranslation(String base, String name) {
    return Component.translatable(makeDescriptionId(base, name));
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
