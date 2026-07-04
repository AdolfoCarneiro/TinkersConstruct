package slimeknights.tconstruct;

import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.common.data.AdvancementsProvider;
import slimeknights.tconstruct.common.data.ConfigurationDataProvider;
import slimeknights.tconstruct.common.data.DamageTypeProvider;
import slimeknights.tconstruct.common.data.RecipeProviderCollector;
import slimeknights.tconstruct.common.data.loot.GlobalLootModifiersProvider;
import slimeknights.tconstruct.common.data.loot.LootTableInjectionProvider;
import slimeknights.tconstruct.common.data.loot.TConstructLootTableProvider;
import slimeknights.tconstruct.common.data.tags.BiomeTagProvider;
import slimeknights.tconstruct.common.data.tags.BlockEntityTypeTagProvider;
import slimeknights.tconstruct.common.data.tags.BlockTagProvider;
import slimeknights.tconstruct.common.data.tags.DamageTypeTagProvider;
import slimeknights.tconstruct.common.data.tags.EnchantmentTagProvider;
import slimeknights.tconstruct.common.data.tags.EntityTypeTagProvider;
import slimeknights.tconstruct.common.data.tags.FluidTagProvider;
import slimeknights.tconstruct.common.data.tags.ItemTagProvider;
import slimeknights.tconstruct.common.data.tags.MenuTypeTagProvider;
import slimeknights.tconstruct.common.data.tags.PotionTagProvider;
import slimeknights.tconstruct.common.network.TinkerPayloadInit;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.ComputableDataKey;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.shared.TinkerAttributes;
import slimeknights.tconstruct.shared.TinkerClient;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerEffects;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.data.material.TrimMaterialProvider;
import slimeknights.tconstruct.world.TinkerStructures;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.data.WorldgenProvider;

import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * TConstruct
 *
 * Central mod object for Tinkers' Construct.
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
    modEventBus.addListener(TinkerPayloadInit::register);
    modEventBus.register(this);

    Config.init(modContainer);
    MaterialRegistry.init();

    // initialize modules, done this way rather than with annotations to give us control over the order
    // base
    modEventBus.register(new TinkerCommons());
    modEventBus.register(new TinkerMaterials());
    // TinkerEffects has no mod-bus @SubscribeEvent methods; it self-registers its game-bus listener internally.
    new TinkerEffects();
    modEventBus.register(new TinkerGadgets());
    modEventBus.register(new TinkerAttributes());
    // world
    modEventBus.register(new TinkerWorld());
    modEventBus.register(new TinkerStructures());
    // tools
    modEventBus.register(new TinkerTables());
    modEventBus.register(new TinkerModifiers());
    // TinkerToolParts has no @SubscribeEvent methods; NeoForge's EventBus.register(Object) throws
    // "has no @SubscribeEvent methods, but register was called anyway" for such classes (unlike Forge, which
    // registered a no-op silently), so just construct it to run its static field registration.
    new TinkerToolParts();
    modEventBus.register(new TinkerTools());
    // smeltery
    modEventBus.register(new TinkerSmeltery());
    modEventBus.register(new TinkerFluids());

    // init deferred registers
    TinkerModule.initRegisters();
    TinkerTags.init();

    // init client logic
    if (FMLEnvironment.dist == Dist.CLIENT) {
      TinkerClient.onConstruct();
    }

    LOG.info("TConstruct initialized (NeoForge 1.21.1).");
  }

  /**
   * Combines every module's recipe {@link DataProvider} (collected via {@link RecipeProviderCollector} instead of
   * being registered directly - see that class for why) into a single provider and registers it.
   * Runs at {@link EventPriority#LOWEST} so it fires after every module's own {@code gatherData} listener.
   */
  @SubscribeEvent(priority = EventPriority.LOWEST)
  void combineRecipeProviders(final GatherDataEvent event) {
    DataProvider combined = RecipeProviderCollector.combineAndClear();
    event.getGenerator().addProvider(true, combined);
  }

  /**
   * Registers every non-recipe datagen provider: datapack registries (damage types, worldgen, trim materials),
   * all tag providers, loot tables/loot modifiers/loot injection, advancements, and misc command configuration.
   * Ported from the 1.20.1 Forge {@code TConstruct#gatherData}; recipes are handled separately by
   * {@link #combineRecipeProviders(GatherDataEvent)} via {@link RecipeProviderCollector}.
   */
  @SubscribeEvent
  void gatherData(final GatherDataEvent event) {
    DataGenerator generator = event.getGenerator();
    PackOutput packOutput = generator.getPackOutput();
    ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
    boolean server = event.includeServer();

    // datapack registries: damage types, worldgen, trim materials.
    // Must go through event.createDatapackRegistryObjects(...) rather than constructing a
    // DatapackBuiltinEntriesProvider by hand and calling generator.addProvider() directly (as the 1.20.1 Forge
    // code did) - only this event helper patches our registry contents into event.getLookupProvider()'s backing
    // future. Skip it and every later provider only sees the *unpatched* vanilla lookup, which is missing even
    // vanilla's own datapack registries (e.g. minecraft:enchantment became one in 1.21), crashing loot table gen.
    RegistrySetBuilder registrySetBuilder = new RegistrySetBuilder();
    DamageTypeProvider.register(registrySetBuilder);
    WorldgenProvider.register(registrySetBuilder);
    TrimMaterialProvider.register(registrySetBuilder);
    event.createDatapackRegistryObjects(registrySetBuilder, Set.of(MOD_ID));
    CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

    // tags
    BlockTagProvider blockTags = new BlockTagProvider(packOutput, lookupProvider, existingFileHelper);
    generator.addProvider(server, blockTags);
    generator.addProvider(server, new ItemTagProvider(packOutput, lookupProvider, blockTags.contentsGetter(), existingFileHelper));
    generator.addProvider(server, new FluidTagProvider(packOutput, lookupProvider, existingFileHelper));
    generator.addProvider(server, new EntityTypeTagProvider(packOutput, lookupProvider, existingFileHelper));
    generator.addProvider(server, new BlockEntityTypeTagProvider(packOutput, lookupProvider, existingFileHelper));
    generator.addProvider(server, new BiomeTagProvider(packOutput, lookupProvider, existingFileHelper));
    generator.addProvider(server, new EnchantmentTagProvider(packOutput, lookupProvider, existingFileHelper));
    generator.addProvider(server, new MenuTypeTagProvider(packOutput, lookupProvider, existingFileHelper));
    generator.addProvider(server, new PotionTagProvider(packOutput, lookupProvider, existingFileHelper));
    generator.addProvider(server, new DamageTypeTagProvider(packOutput, lookupProvider, existingFileHelper));

    // other datagen
    generator.addProvider(server, new TConstructLootTableProvider(packOutput, lookupProvider));
    generator.addProvider(server, new AdvancementsProvider(packOutput, lookupProvider));
    generator.addProvider(server, new GlobalLootModifiersProvider(packOutput, lookupProvider));
    generator.addProvider(server, new LootTableInjectionProvider(packOutput));
    generator.addProvider(server, new ConfigurationDataProvider(packOutput));
  }

  public static ResourceLocation getResource(String name) {
    return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
  }

  /** Alias for {@link #getResource(String)}, for static import as {@code prefix("name")} */
  public static ResourceLocation prefix(String name) {
    return getResource(name);
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

  /** Makes a translation component for the given name with format arguments */
  public static MutableComponent makeTranslation(String base, String name, Object... args) {
    return Component.translatable(makeDescriptionId(base, name), args);
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
