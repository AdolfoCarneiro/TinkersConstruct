package slimeknights.tconstruct.tools.modifiers.loot;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.tools.TinkerModifiers;

import java.util.List;
import java.util.Set;

/** Boosts drop rates based on modifier level */
public class ModifierBonusLootFunction extends LootItemConditionalFunction {
  public static final MapCodec<ModifierBonusLootFunction> SERIALIZER = RecordCodecBuilder.mapCodec(instance ->
    commonFields(instance).and(instance.group(
      ResourceLocation.CODEC.xmap(ModifierId::new, id -> id).fieldOf("modifier").forGetter(function -> function.modifier),
      FormulaType.CODEC.fieldOf("formula").forGetter(function -> function.formula),
      Codec.BOOL.optionalFieldOf("include_base", true).forGetter(function -> function.includeBase))
    ).apply(instance, ModifierBonusLootFunction::new));
  /** Modifier ID to use for multiplier bonus */
  private final ModifierId modifier;
  /** Formula to apply */
  private final FormulaType formula;
  /** If true, considers level 1 as bonus, if false considers level 1 as no bonus */
  private final boolean includeBase;

  protected ModifierBonusLootFunction(List<LootItemCondition> conditions, ModifierId modifier, FormulaType formula, boolean includeBase) {
    super(conditions);
    this.modifier = modifier;
    this.formula = formula;
    this.includeBase = includeBase;
  }

  /** Creates a generic builder */
  public static Builder<?> builder(ModifierId modifier, FormulaType formula, boolean includeBase) {
    return simpleBuilder(conditions -> new ModifierBonusLootFunction(conditions, modifier, formula, includeBase));
  }

  /** Creates a builder for the binomial with bonus formula */
  public static Builder<?> binomialWithBonusCount(ModifierId modifier, float probability, int extra, boolean includeBase) {
    return builder(modifier, FormulaType.ORE_DROPS, includeBase);
  }

  /** Creates a builder for the ore drops formula */
  public static Builder<?> oreDrops(ModifierId modifier, boolean includeBase) {
    return builder(modifier, FormulaType.ORE_DROPS, includeBase);
  }

  /** Creates a builder for the uniform bonus count */
  public static Builder<?> uniformBonusCount(ModifierId modifier, int bonusMultiplier, boolean includeBase) {
    return builder(modifier, FormulaType.ORE_DROPS, includeBase);
  }

  @Override
  public LootItemFunctionType getType() {
    return TinkerModifiers.modifierBonusFunction.get();
  }

  @Override
  public Set<LootContextParam<?>> getReferencedContextParams() {
    return ImmutableSet.of(LootContextParams.TOOL);
  }

  @Override
  protected ItemStack run(ItemStack stack, LootContext context) {
    int level = ModifierUtil.getModifierLevel(context.getParam(LootContextParams.TOOL), modifier);
    if (!includeBase) {
      level--;
    }
    if (level > 0) {
      stack.setCount(formula.calculate(context, stack.getCount(), level));
    }
    return stack;
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
