package slimeknights.tconstruct.tools.modifiers.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Reimplementation of vanilla's {@code ApplyBonusCount.Formula} hierarchy.
 * <p>
 * In 1.20.1 {@code ApplyBonusCount.Formula}/{@code BinomialWithBonusCount}/{@code OreDrops}/{@code UniformBonusCount}
 * were public, so {@link ChrysophiliteBonusFunction} and {@link ModifierBonusLootFunction} reused them directly.
 * As of 1.21.1 those classes are package-private in {@code net.minecraft.world.level.storage.loot.functions},
 * so this mirrors their JSON shape ("formula" id + optional "parameters" object) and math locally.
 */
public interface BonusCountFormula {
  /** Calculates the new stack count for the given enchantment/modifier level */
  int calculateNewCount(RandomSource random, int originalCount, int level);

  /** Gets the type of this formula, used for serialization */
  FormulaType getType();

  /** Registered formula types, keyed by their serialized ID */
  Map<ResourceLocation,FormulaType> FORMULAS = Stream.of(BinomialWithBonusCount.TYPE, OreDrops.TYPE, UniformBonusCount.TYPE)
    .collect(Collectors.toMap(FormulaType::id, Function.identity()));

  Codec<FormulaType> FORMULA_TYPE_CODEC = ResourceLocation.CODEC.comapFlatMap(
    id -> {
      FormulaType type = FORMULAS.get(id);
      return type != null ? DataResult.success(type) : DataResult.error(() -> "No formula type with id: '" + id + "'");
    },
    FormulaType::id);

  /** Codec for a formula, matching the "formula"/"parameters" JSON shape used in 1.20.1 */
  MapCodec<BonusCountFormula> CODEC = ExtraCodecs.dispatchOptionalValue(
    "formula", "parameters", FORMULA_TYPE_CODEC, BonusCountFormula::getType, FormulaType::codec);

  /** Pairing of a formula's ID and its parameter codec */
  record FormulaType(ResourceLocation id, Codec<? extends BonusCountFormula> codec) {}

  /** Applies a bonus based on a binomial distribution with {@code n = level + extraRounds} and {@code p = probability} */
  record BinomialWithBonusCount(int extraRounds, float probability) implements BonusCountFormula {
    public static final Codec<BinomialWithBonusCount> CODEC = RecordCodecBuilder.create(inst -> inst.group(
      Codec.INT.fieldOf("extra").forGetter(BinomialWithBonusCount::extraRounds),
      Codec.FLOAT.fieldOf("probability").forGetter(BinomialWithBonusCount::probability)
    ).apply(inst, BinomialWithBonusCount::new));
    public static final FormulaType TYPE = new FormulaType(ResourceLocation.withDefaultNamespace("binomial_with_bonus_count"), CODEC);

    @Override
    public int calculateNewCount(RandomSource random, int originalCount, int level) {
      for (int i = 0; i < level + extraRounds; i++) {
        if (random.nextFloat() < probability) {
          originalCount++;
        }
      }
      return originalCount;
    }

    @Override
    public FormulaType getType() {
      return TYPE;
    }
  }

  /** Applies a bonus count with the special formula vanilla uses for fortune ore drops */
  record OreDrops() implements BonusCountFormula {
    public static final Codec<OreDrops> CODEC = Codec.unit(OreDrops::new);
    public static final FormulaType TYPE = new FormulaType(ResourceLocation.withDefaultNamespace("ore_drops"), CODEC);

    @Override
    public int calculateNewCount(RandomSource random, int originalCount, int level) {
      if (level > 0) {
        int bonus = random.nextInt(level + 2) - 1;
        if (bonus < 0) {
          bonus = 0;
        }
        return originalCount * (bonus + 1);
      }
      return originalCount;
    }

    @Override
    public FormulaType getType() {
      return TYPE;
    }
  }

  /** Adds a bonus count based on the level scaled by a constant multiplier */
  record UniformBonusCount(int bonusMultiplier) implements BonusCountFormula {
    public static final Codec<UniformBonusCount> CODEC = RecordCodecBuilder.create(inst -> inst.group(
      Codec.INT.fieldOf("bonusMultiplier").forGetter(UniformBonusCount::bonusMultiplier)
    ).apply(inst, UniformBonusCount::new));
    public static final FormulaType TYPE = new FormulaType(ResourceLocation.withDefaultNamespace("uniform_bonus_count"), CODEC);

    @Override
    public int calculateNewCount(RandomSource random, int originalCount, int level) {
      return originalCount + random.nextInt(bonusMultiplier * level + 1);
    }

    @Override
    public FormulaType getType() {
      return TYPE;
    }
  }
}
