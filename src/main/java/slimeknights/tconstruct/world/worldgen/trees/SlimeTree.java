package slimeknights.tconstruct.world.worldgen.trees;

import net.minecraft.world.level.block.grower.TreeGrower;
import slimeknights.tconstruct.world.TinkerStructures;
import slimeknights.tconstruct.world.block.FoliageType;

import java.util.Optional;

/** Provides TreeGrower instances for each slime sapling foliage type. */
public class SlimeTree {
  private static final TreeGrower EARTH = new TreeGrower("tconstruct:earth_slime_tree", Optional.empty(), Optional.of(TinkerStructures.earthSlimeTree), Optional.empty());
  private static final TreeGrower SKY   = new TreeGrower("tconstruct:sky_slime_tree",   Optional.empty(), Optional.of(TinkerStructures.skySlimeTree),   Optional.empty());
  // 85% chance of tall ender tree (secondary), 15% of normal (primary)
  private static final TreeGrower ENDER = new TreeGrower("tconstruct:ender_slime_tree", 0.85f, Optional.empty(), Optional.empty(), Optional.of(TinkerStructures.enderSlimeTree), Optional.of(TinkerStructures.enderSlimeTreeTall), Optional.empty(), Optional.empty());
  private static final TreeGrower BLOOD = new TreeGrower("tconstruct:blood_slime_fungus", Optional.empty(), Optional.of(TinkerStructures.bloodSlimeFungus), Optional.empty());
  private static final TreeGrower ICHOR = new TreeGrower("tconstruct:ichor_slime_fungus", Optional.empty(), Optional.of(TinkerStructures.ichorSlimeFungus), Optional.empty());

  public static TreeGrower forType(FoliageType type) {
    return switch (type) {
      case EARTH -> EARTH;
      case SKY   -> SKY;
      case ENDER -> ENDER;
      case BLOOD -> BLOOD;
      case ICHOR -> ICHOR;
    };
  }
}
