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
import java.util.Arrays;
import java.util.Comparator;
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

  /** Returns vanilla tiers sorted by level (ascending) */
  public static List<Tier> getSortedTiers() {
    return Arrays.stream(Tiers.values())
      .sorted(Comparator.comparingInt(Tiers::getLevel))
      .collect(java.util.stream.Collectors.toList());
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
