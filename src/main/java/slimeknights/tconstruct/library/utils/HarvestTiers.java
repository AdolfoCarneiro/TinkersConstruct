package slimeknights.tconstruct.library.utils;

import com.google.common.collect.Maps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import slimeknights.mantle.client.ResourceColorManager;
import slimeknights.mantle.data.listener.ISafeManagerReloadListener;
import slimeknights.tconstruct.TConstruct;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * Harvest level display names
 */
public class HarvestTiers {
  private HarvestTiers() {}

  /** Cache of name for each tier */
  private static final Map<Tier, Component> harvestLevelNames = Maps.newHashMap();
  /** Listener to clear name cache so we get new colors */
  public static final ISafeManagerReloadListener RELOAD_LISTENER = manager -> harvestLevelNames.clear();

  /**
   * Gets a ResourceLocation name for a tier. Uses the Tiers enum name for vanilla tiers,
   * falling back to "tconstruct:unknown" for unrecognized tiers.
   */
  @Nullable
  public static ResourceLocation getTierName(Tier tier) {
    if (tier instanceof Tiers vanilla) {
      return ResourceLocation.withDefaultNamespace(vanilla.name().toLowerCase());
    }
    return null;
  }

  /**
   * Looks up a tier by ResourceLocation name. Only supports vanilla Tiers enum.
   */
  @Nullable
  public static Tier byName(ResourceLocation id) {
    for (Tiers t : Tiers.values()) {
      if (ResourceLocation.withDefaultNamespace(t.name().toLowerCase()).equals(id)) {
        return t;
      }
    }
    return null;
  }

  /**
   * Vanilla tiers sorted by actual harvest strength (ascending). This is NOT the same as
   * {@link Tiers#ordinal()}: the enum declares WOOD, STONE, IRON, DIAMOND, GOLD, NETHERITE in
   * that order, but GOLD's real harvest level ties with WOOD (weakest), not "above DIAMOND".
   * <p>
   * 1.20.1 Forge computed this cross-mod via {@code TierSortingRegistry.getSortedTiers()}
   * (topological sort over "incorrect_for" tag containment, with mod-declared tie-breaks).
   * NeoForge 1.21.1 removed {@code TierSortingRegistry} outright with no cross-mod replacement
   * (verified: absent from neoforge-21.1.234-sources.jar; vanilla {@code Tier} only exposes
   * {@link Tier#getIncorrectBlocksForDrops()}, no ordering). Since this mod only ever resolves
   * vanilla tier ids through {@link #byName}, we hardcode the known-correct vanilla order here
   * rather than re-deriving it. Mirrors the equivalent table already reintroduced for datagen in
   * {@code BlockTagProvider#harvestLevel(Tiers)}.
   * <p>
   * Tiers not in this table (only possible if {@link #byName} is ever extended to resolve modded
   * tiers) are absent from the list; {@link #max}/{@link #min} already treat list-absent tiers as
   * weakest via {@code indexOf() == -1}, matching the original 1.20.1 fallback idiom.
   */
  private static final List<Tier> VANILLA_TIER_ORDER = List.of(Tiers.WOOD, Tiers.GOLD, Tiers.STONE, Tiers.IRON, Tiers.DIAMOND, Tiers.NETHERITE);

  /** Returns vanilla tiers sorted by level (ascending) */
  public static List<Tier> getSortedTiers() {
    return VANILLA_TIER_ORDER;
  }

  /** Makes a translation key for the given name */
  private static MutableComponent makeLevelKey(Tier tier) {
    ResourceLocation tierId = getTierName(tier);
    String key = Util.makeTranslationKey("harvest_tier", tierId);
    TextColor color = ResourceColorManager.getTextColor(key);
    return TConstruct.makeTranslation("stat", key).withStyle(style -> style.withColor(color));
  }

  /**
   * Gets the harvest level name for the given level number
   * @param tier  Tier
   * @return  Level name
   */
  public static Component getName(Tier tier) {
    return harvestLevelNames.computeIfAbsent(tier, n -> makeLevelKey(tier));
  }

  /** Gets the larger of two tiers */
  public static Tier max(Tier a, Tier b) {
    List<Tier> sorted = getSortedTiers();
    // note indexOf returns -1 if the tier is missing, so the larger of an unsorted tier and a sorted one is the sorted one
    if (sorted.indexOf(b) > sorted.indexOf(a)) {
      return b;
    }
    return a;
  }

  /** Gets the smaller of two tiers */
  public static Tier min(Tier a, Tier b) {
    List<Tier> sorted = getSortedTiers();
    // note indexOf returns -1 if the tier is missing, so the smaller of an unsorted tier and a sorted one is the unsorted one
    if (sorted.indexOf(b) < sorted.indexOf(a)) {
      return b;
    }
    return a;
  }

  /** Gets the smallest tier in the sorting registry */
  public static Tier minTier() {
    List<Tier> sortedTiers = getSortedTiers();
    if (sortedTiers.isEmpty()) {
      TConstruct.LOG.error("No sorted tiers exist, this should not happen");
      return Tiers.WOOD;
    }
    return sortedTiers.get(0);
  }
}
