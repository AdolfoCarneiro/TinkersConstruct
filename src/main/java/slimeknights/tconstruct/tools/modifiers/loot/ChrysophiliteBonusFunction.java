package slimeknights.tconstruct.tools.modifiers.loot;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.modifiers.traits.skull.ChrysophiliteModifier;

import java.util.List;
import java.util.Set;

/** Loot modifier to boost drops based on teh chrysophilite amount */
public class ChrysophiliteBonusFunction extends LootItemConditionalFunction {
  public static final MapCodec<ChrysophiliteBonusFunction> SERIALIZER = RecordCodecBuilder.mapCodec(instance ->
    commonFields(instance).and(instance.group(
      FormulaType.CODEC.fieldOf("formula").forGetter(function -> function.formula),
      Codec.BOOL.optionalFieldOf("include_base", true).forGetter(function -> function.includeBase))
    ).apply(instance, ChrysophiliteBonusFunction::new));

  /** Formula to apply */
  private final FormulaType formula;
  /** If true, the includes the helmet in the level, if false level is just gold pieces */
  private final boolean includeBase;
  protected ChrysophiliteBonusFunction(List<LootItemCondition> conditions, FormulaType formula, boolean includeBase) {
    super(conditions);
    this.formula = formula;
    this.includeBase = includeBase;
  }

  /** Creates a generic builder */
  public static Builder<?> builder(FormulaType formula, boolean includeBase) {
    return simpleBuilder(conditions -> new ChrysophiliteBonusFunction(conditions, formula, includeBase));
  }

  /** Creates a builder for the binomial with bonus formula */
  public static Builder<?> binomialWithBonusCount(float probability, int extra, boolean includeBase) {
    return builder(FormulaType.ORE_DROPS, includeBase);
  }

  /** Creates a builder for the ore drops formula */
  public static Builder<?> oreDrops(boolean includeBase) {
    return builder(FormulaType.ORE_DROPS, includeBase);
  }

  /** Creates a builder for the uniform bonus count */
  public static Builder<?> uniformBonusCount(int bonusMultiplier, boolean includeBase) {
    return builder(FormulaType.ORE_DROPS, includeBase);
  }

  @Override
  protected ItemStack run(ItemStack stack, LootContext context) {
    int level = ChrysophiliteModifier.getTotalGold(context.getParamOrNull(LootContextParams.THIS_ENTITY));
    if (!includeBase) {
      level--;
    }
    if (level > 0) {
      stack.setCount(formula.calculate(context, stack.getCount(), level));
    }
    return stack;
  }

  @Override
  public Set<LootContextParam<?>> getReferencedContextParams() {
    return ImmutableSet.of(LootContextParams.THIS_ENTITY);
  }

  @Override
  public LootItemFunctionType getType() {
    return TinkerModifiers.chrysophiliteBonusFunction.get();
  }

  private enum FormulaType {
    ORE_DROPS;

    private static final Codec<FormulaType> CODEC = Codec.STRING.xmap(FormulaType::fromName, FormulaType::serializedName);

    private static FormulaType fromName(String name) {
      return ORE_DROPS;
    }

    private String serializedName() {
      return "minecraft:ore_drops";
    }

    private int calculate(LootContext context, int originalCount, int enchantmentLevel) {
      if (enchantmentLevel > 0) {
        int bonus = context.getRandom().nextInt(enchantmentLevel + 2) - 1;
        if (bonus < 0) {
          bonus = 0;
        }
        return originalCount * (bonus + 1);
      }
      return originalCount;
    }
  }
}
