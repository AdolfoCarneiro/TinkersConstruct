package slimeknights.tconstruct.library.modifiers.modules.build;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonSyntaxException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.mantle.data.loadable.primitive.ResourceLocationLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.block.BlockPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.mantle.util.LogicHelper;
import slimeknights.mantle.util.typed.TypedMap;
import slimeknights.tconstruct.library.json.LevelingInt;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.ModifierManager;
import slimeknights.tconstruct.library.modifiers.hook.armor.ProtectionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.behavior.EnchantmentModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.mining.BlockHarvestModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.mining.HarvestEnchantmentsModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.util.LevelingIntModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModuleBuilder;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;
import java.util.Map;
import java.util.Set;

/** Modules that add enchantments to a tool. */
public interface EnchantmentModule extends ModifierModule, LevelingIntModule, ConditionalModule<IToolStackView> {
  /**
   * Loadable resolving a {@link Holder} of an enchantment from its registry name.
   * <p>
   * Enchantments are a fully data-driven (world/datapack) registry in 1.21, so unlike most loadables this cannot
   * resolve via a static registry lookup ({@link Loadables#ENCHANTMENT} does that and fails both ways: it cannot
   * find the registry to look up a key during read, nor to look up a key for an object during write). Storing the
   * {@link Holder} instead of the raw {@link Enchantment} lets us sidestep the problem entirely for writing
   * ({@link Holder#unwrapKey()} carries its own key with no registry access needed), and for reading pulls the
   * live registry access from {@link ModifierManager#REGISTRIES}, which {@link ModifierManager} threads into the
   * context when parsing real modifier JSON. This context is not (and does not need to be) populated during
   * datagen, since datagen only ever serializes (writes) built modules, never parses them back from JSON.
   */
  ResourceLocationLoadable<Holder<Enchantment>> ENCHANTMENT_HOLDER_LOADABLE = new ResourceLocationLoadable<>() {
    @Override
    public Holder<Enchantment> fromKey(ResourceLocation name, String key, TypedMap context) {
      HolderLookup.Provider registries = context.get(ModifierManager.REGISTRIES);
      if (registries == null) {
        throw new JsonSyntaxException("Unable to parse " + key + " as an enchantment, no registry access available in this context");
      }
      return registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(ResourceKey.create(Registries.ENCHANTMENT, name));
    }

    @Override
    public ResourceLocation getKey(Holder<Enchantment> holder) {
      return holder.unwrapKey().orElseThrow(() -> new RuntimeException("Enchantment holder " + holder + " has no registry key, cannot serialize")).location();
    }

    @Override
    public Holder<Enchantment> decode(RegistryFriendlyByteBuf buffer, TypedMap context) {
      // unlike JSON parsing, RegistryFriendlyByteBuf always carries live registry access, so no context plumbing is needed here
      ResourceLocation name = buffer.readResourceLocation();
      return buffer.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(ResourceKey.create(Registries.ENCHANTMENT, name));
    }

    @Override
    public void encode(RegistryFriendlyByteBuf buffer, Holder<Enchantment> value) {
      buffer.writeResourceLocation(getKey(value));
    }
  };

  /* Common fields */
  LoadableField<Holder<Enchantment>,EnchantmentModule> ENCHANTMENT = ENCHANTMENT_HOLDER_LOADABLE.requiredField("name", EnchantmentModule::enchantmentHolder);
  LoadableField<IJsonPredicate<BlockState>,EnchantmentModule> BLOCK = BlockPredicate.LOADER.defaultField("block", EnchantmentModule::block);
  LoadableField<IJsonPredicate<LivingEntity>,EnchantmentModule> HOLDER = LivingEntityPredicate.LOADER.defaultField("holder", EnchantmentModule::holder);

  /** Gets the enchantment holder for this module */
  Holder<Enchantment> enchantmentHolder();

  /** Convenience accessor for the raw enchantment instance backing {@link #enchantmentHolder()} */
  default Enchantment enchantment() {
    return enchantmentHolder().value();
  }

  /** Gets the block predicate, will be {@link BlockPredicate#ANY} for {@link Constant} */
  default IJsonPredicate<BlockState> block() {
    return BlockPredicate.ANY;
  }

  /** Gets the holder predicate, will be {@link LivingEntityPredicate#ANY} for {@link Constant} */
  default IJsonPredicate<LivingEntity> holder() {
    return LivingEntityPredicate.ANY;
  }

  /**
   * Creates a builder for a constant enchantment from a raw {@link Enchantment} instance.
   * @apiNote The resulting module can only be used programmatically (e.g. registering a static modifier); it cannot
   *          be serialized to JSON, as a raw enchantment carries no registry key. Prefer
   *          {@link #builder(ResourceKey, HolderLookup.Provider)} when the module needs to be datagen'd.
   */
  static Builder builder(Enchantment enchantment) {
    return new Builder(Holder.direct(enchantment));
  }

  /**
   * Creates a builder for a constant enchantment from a ResourceKey, resolving the real {@link Enchantment} holder
   * via the given registry access. Enchantments are data-driven in 1.21.1, so a {@link HolderLookup.Provider} is
   * required to look them up (no more {@code BuiltInRegistries.ENCHANTMENT}).
   */
  static Builder builder(ResourceKey<Enchantment> enchantment, HolderLookup.Provider registries) {
    return new Builder(registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(enchantment));
  }

  /**
   * Shared builder instance
   */
  @SuppressWarnings("unused") // API
  @Setter
  @Accessors(fluent = true)
  class Builder extends ModuleBuilder.Stack<Builder> {
    private final Holder<Enchantment> enchantment;

    private Builder(Holder<Enchantment> enchantment) {
      this.enchantment = enchantment;
    }
    private LevelingInt lootingLevel = LevelingInt.LEVEL;
    private IJsonPredicate<BlockState> block = BlockPredicate.ANY;
    private IJsonPredicate<LivingEntity> holder = LivingEntityPredicate.ANY;

    /** @deprecated use {@link #lootingLevel(LevelingInt)} */
    @Deprecated(forRemoval = true)
    public Builder level(int level) {
      return lootingLevel(LevelingInt.eachLevel(level));
    }

    public Builder lootingLevel(LevelingInt lootingLevel) {
      this.lootingLevel = lootingLevel;
      return this;
    }

    public Builder block(IJsonPredicate<BlockState> block) {
      this.block = block;
      return this;
    }

    public Builder holder(IJsonPredicate<LivingEntity> holder) {
      this.holder = holder;
      return this;
    }

    /** Builds a module for a constant enchantment */
    public Constant constant() {
      if (block != BlockPredicate.ANY || holder != LivingEntityPredicate.ANY) {
        throw new IllegalStateException("Cannot build a constant enchantment module with block or holder conditions");
      }
      return new Constant(enchantment, lootingLevel, condition);
    }

    /** Builds a module for a constant enchantment which ignores its protection value */
    public Protection protection() {
      if (block != BlockPredicate.ANY || holder != LivingEntityPredicate.ANY) {
        throw new IllegalStateException("Cannot build a constant enchantment module with block or holder conditions");
      }
      return new Protection(enchantment, lootingLevel, condition);
    }

    /**
     * Creates a new main hand harvest module
     * @param key  Key to use for checking conditions, needs to be unique. Recommend suffixing the modifier ID (using the modifier ID will conflict with incremental)
     * @return  Module instance
     */
    public MainHandHarvest mainHandHarvest(ResourceLocation key) {
      return new MainHandHarvest(enchantment, lootingLevel, condition, key, block, holder);
    }

    /**
     * Creates a new armor harvest module
     * @param slots  Slots to allow this to run
     * @return  Module instance
     */
    public ArmorHarvest armorHarvest(EquipmentSlot... slots) {
      if (slots.length == 0) {
        throw new IllegalArgumentException("Must have at least 1 slot");
      }
      // immutable set preserves insertion order
      Set<EquipmentSlot> set = ImmutableSet.copyOf(slots);
      if (set.contains(EquipmentSlot.MAINHAND)) {
        throw new IllegalArgumentException("Cannot create armor harvest for the main hand slot");
      }
      return new ArmorHarvest(enchantment, lootingLevel, condition, set, block, holder);
    }

    /** Creates a new armor harvest module with the default slots */
    public ArmorHarvest armorHarvest() {
      return armorHarvest(HarvestEnchantmentsModifierHook.APPLICABLE_SLOTS);
    }
  }

  /** Implementation of a simple constant enchantment for the current tool */
  class Constant implements EnchantmentModule, EnchantmentModifierHook {
    private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<Constant>defaultHooks(ModifierHooks.ENCHANTMENTS);
    public static final RecordLoadable<Constant> LOADER = RecordLoadable.create(ENCHANTMENT, LevelingIntModule.FIELD, ModifierCondition.TOOL_FIELD, Constant::new);
    private final Holder<Enchantment> enchantment;
    private final LevelingInt level;
    private final ModifierCondition<IToolStackView> condition;

    protected Constant(Holder<Enchantment> enchantment, LevelingInt level, ModifierCondition<IToolStackView> condition) {
      this.enchantment = enchantment;
      this.level = level;
      this.condition = condition;
    }

    @Override public Holder<Enchantment> enchantmentHolder() { return enchantment; }
    @Override public LevelingInt level() { return level; }
    @Override public ModifierCondition<IToolStackView> condition() { return condition; }

    /** @deprecated use {@link Builder#constant()} */
    @Deprecated(forRemoval = true)
    public Constant(Holder<Enchantment> enchantment, int level, ModifierCondition<IToolStackView> condition) {
      this(enchantment, LevelingInt.eachLevel(level), condition);
    }

    /** @deprecated use {@link Builder#constant()} */
    @Deprecated(forRemoval = true)
    public Constant(Holder<Enchantment> enchantment, int level) {
      this(enchantment, level, ModifierCondition.ANY_TOOL);
    }

    @Override
    public int updateEnchantmentLevel(IToolStackView tool, ModifierEntry modifier, Enchantment enchantment, int level) {
      if (enchantment == this.enchantment() && condition().matches(tool, modifier)) {
        level += getLevel(modifier);
      }
      return level;
    }

    @Override
    public void updateEnchantments(IToolStackView tool, ModifierEntry modifier, Map<Enchantment,Integer> map) {
      if (condition().matches(tool, modifier)) {
        EnchantmentModifierHook.addEnchantment(map, this.enchantment(), getLevel(modifier));
      }
    }

    @Override
    public List<ModuleHook<?>> getDefaultHooks() {
      return DEFAULT_HOOKS;
    }

    @Override
    public RecordLoadable<Constant> getLoader() {
      return LOADER;
    }
  }

  /** Constant enchantment which cancels out the protection value */
  class Protection extends Constant implements ProtectionModifierHook {
    private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<Protection>defaultHooks(ModifierHooks.ENCHANTMENTS, ModifierHooks.PROTECTION);
    public static final RecordLoadable<Constant> LOADER = RecordLoadable.create(ENCHANTMENT, LevelingIntModule.FIELD, ModifierCondition.TOOL_FIELD, Protection::new);
    protected Protection(Holder<Enchantment> enchantment, LevelingInt level, ModifierCondition<IToolStackView> condition) {
      super(enchantment, level, condition);
    }

    /** @deprecated use {@link Builder#protection()} */
    @Deprecated(forRemoval = true)
    public Protection(Holder<Enchantment> enchantment, int level, ModifierCondition<IToolStackView> condition) {
      super(enchantment, level, condition);
    }

    @Override
    public List<ModuleHook<?>> getDefaultHooks() {
      return DEFAULT_HOOKS;
    }

    @Override
    public RecordLoadable<Constant> getLoader() {
      return LOADER;
    }

    @Override
    public float getProtectionModifier(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float modifierValue) {
      if (condition().matches(tool, modifier)) {
        int subtractLevel = getLevel(modifier);
        Enchantment enchantment = enchantment();
        if (subtractLevel > 0 && enchantment.matchingSlot(slotType) && !source.is(DamageTypeTags.BYPASSES_ENCHANTMENTS)) {
          if (context.getLevel() instanceof ServerLevel serverLevel) {
            org.apache.commons.lang3.mutable.MutableFloat protection = new org.apache.commons.lang3.mutable.MutableFloat(0f);
            enchantment.modifyDamageProtection(serverLevel, subtractLevel, context.getEntity().getItemBySlot(slotType), context.getEntity(), source, protection);
            modifierValue -= protection.floatValue();
          }
        }
      }
      return modifierValue;
    }
  }

  /**
   * Enchantment module that can condition on the block mined or the entity mining.
   * Exists as {@link HarvestEnchantmentsModifierHook} does not currently run on the main hand. TODO 1.21: update it to run on mainhand.
   */
  record MainHandHarvest(Holder<Enchantment> enchantmentHolder, LevelingInt level, ModifierCondition<IToolStackView> condition, ResourceLocation conditionFlag, IJsonPredicate<BlockState> block, IJsonPredicate<LivingEntity> holder) implements EnchantmentModule, EnchantmentModifierHook, BlockHarvestModifierHook {
    private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<MainHandHarvest>defaultHooks(ModifierHooks.ENCHANTMENTS, ModifierHooks.BLOCK_HARVEST);
    public static final RecordLoadable<MainHandHarvest> LOADER = RecordLoadable.create(ENCHANTMENT, LevelingIntModule.FIELD, ModifierCondition.TOOL_FIELD, Loadables.RESOURCE_LOCATION.requiredField("condition_flag", MainHandHarvest::conditionFlag), BLOCK, HOLDER, MainHandHarvest::new);

    /** @apiNote use {@link Builder#mainHandHarvest(ResourceLocation)} */
    @Internal
    public MainHandHarvest {}

    /** @deprecated use {@link Builder#mainHandHarvest(ResourceLocation)} */
    @Deprecated(forRemoval = true)
    public MainHandHarvest(Holder<Enchantment> enchantment, int level, ModifierCondition<IToolStackView> condition, ResourceLocation conditionFlag, IJsonPredicate<BlockState> block, IJsonPredicate<LivingEntity> holder) {
      this(enchantment, LevelingInt.eachLevel(level), condition, conditionFlag, block, holder);
    }

    @Override
    public void startHarvest(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context) {
      if (condition.matches(tool, modifier) && block.matches(context.getState()) && holder.matches(context.getLiving())) {
        tool.getPersistentData().putBoolean(conditionFlag, true);
      }
      BlockHarvestModifierHook.super.startHarvest(tool, modifier, context);
    }

    @Override
    public void finishHarvest(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context, int harvested) {
      tool.getPersistentData().remove(conditionFlag);
    }

    @Override
    public int updateEnchantmentLevel(IToolStackView tool, ModifierEntry modifier, Enchantment enchantment, int level) {
      if (enchantment == this.enchantment() && tool.getPersistentData().getBoolean(conditionFlag)) {
        level += getLevel(modifier);
      }
      return level;
    }

    @Override
    public void updateEnchantments(IToolStackView tool, ModifierEntry modifier, Map<Enchantment,Integer> map) {
      if (tool.getPersistentData().getBoolean(conditionFlag)) {
        EnchantmentModifierHook.addEnchantment(map, this.enchantment(), getLevel(modifier));
      }
    }

    @Override
    public List<ModuleHook<?>> getDefaultHooks() {
      return DEFAULT_HOOKS;
    }

    @Override
    public RecordLoadable<MainHandHarvest> getLoader() {
      return LOADER;
    }
  }

  /**
   * Enchantment module that can condition on the block mined or the entity mining on armor. Requires the harvesting be done with a tinker tool.
   * TODO 1.21: rename to conditional harvest. The slot filter lets us avoid double applying to a constant enchantment harvest tool.
   */
  record ArmorHarvest(Holder<Enchantment> enchantmentHolder, LevelingInt level, ModifierCondition<IToolStackView> condition, Set<EquipmentSlot> slots, IJsonPredicate<BlockState> block, IJsonPredicate<LivingEntity> holder) implements EnchantmentModule, HarvestEnchantmentsModifierHook {
    private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<ArmorHarvest>defaultHooks(ModifierHooks.HARVEST_ENCHANTMENTS);
    public static final RecordLoadable<ArmorHarvest> LOADER = RecordLoadable.create(ENCHANTMENT, LevelingIntModule.FIELD, ModifierCondition.TOOL_FIELD, TinkerLoadables.EQUIPMENT_SLOT_SET.requiredField("slots", ArmorHarvest::slots), BLOCK, HOLDER, ArmorHarvest::new);

    /** @apiNote use {@link Builder#armorHarvest(EquipmentSlot...)} */
    @Internal
    public ArmorHarvest {}

    /** @deprecated use {@link Builder#armorHarvest(EquipmentSlot...)} */
    @Deprecated(forRemoval = true)
    public ArmorHarvest(Holder<Enchantment> enchantment, int level, ModifierCondition<IToolStackView> condition, Set<EquipmentSlot> slots, IJsonPredicate<BlockState> block, IJsonPredicate<LivingEntity> holder) {
      this(enchantment, LevelingInt.eachLevel(level), condition, slots, block, holder);
    }

    @Override
    public void updateHarvestEnchantments(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context, EquipmentContext equipment, EquipmentSlot slot, net.minecraft.world.item.enchantment.ItemEnchantments.Mutable map) {
      if (slots.contains(slot) && condition.matches(tool, modifier) && block.matches(context.getState()) && holder.matches(context.getLiving())) {
        EnchantmentModifierHook.addEnchantment(map, enchantmentHolder(), getLevel(modifier));
      }
    }

    @Override
    public List<ModuleHook<?>> getDefaultHooks() {
      return DEFAULT_HOOKS;
    }

    @Override
    public RecordLoadable<ArmorHarvest> getLoader() {
      return LOADER;
    }
  }
}
