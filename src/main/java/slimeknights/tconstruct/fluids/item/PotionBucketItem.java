package slimeknights.tconstruct.fluids.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

/** Implements filling a bucket with an NBT fluid */
public class PotionBucketItem extends PotionItem {
  private final Supplier<? extends Fluid> supplier;
  public PotionBucketItem(Supplier<? extends Fluid> supplier, Properties builder) {
    super(builder);
    this.supplier = supplier;
  }

  public Fluid getFluid() {
    return supplier.get();
  }

  /** Gets the potion holder from legacy custom NBT tag on this bucket */
  private static Holder<Potion> getPotionFromTag(ItemStack stack) {
    CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    if (tag.contains("Potion")) {
      java.util.Optional<Holder.Reference<Potion>> holder = BuiltInRegistries.POTION.getHolder(ResourceLocation.parse(tag.getString("Potion")));
      if (holder.isPresent()) return holder.get();
    }
    return Potions.EMPTY;
  }

  @Override
  public String getDescriptionId(ItemStack stack) {
    String bucketKey = getPotionFromTag(stack).value().getName(getDescriptionId() + ".effect.");
    if (Util.canTranslate(bucketKey)) {
      return bucketKey;
    }
    return super.getDescriptionId();
  }

  @Override
  public Component getName(ItemStack stack) {
    Holder<Potion> potionHolder = getPotionFromTag(stack);
    Potion potion = potionHolder.value();
    String bucketKey = potion.getName(getDescriptionId() + ".effect.");
    if (Util.canTranslate(bucketKey)) {
      return Component.translatable(bucketKey);
    }
    // default to filling with the contents
    return Component.translatable(getDescriptionId() + ".contents", Component.translatable(potion.getName("item.minecraft.potion.effect.")));
  }

  @Override
  public ItemStack getDefaultInstance() {
    ItemStack stack = new ItemStack(this);
    stack.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.AWKWARD));
    return stack;
  }

  @Override
  public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity living) {
    Player player = living instanceof Player p ? p : null;
    if (player instanceof ServerPlayer serverPlayer) {
      CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
    }

    // effects are 2x duration
    if (!level.isClientSide) {
      Potion potion = getPotionFromTag(stack).value();
      for (MobEffectInstance effect : potion.getEffects()) {
        if (effect.getEffect().isInstantenous()) {
          effect.getEffect().applyInstantenousEffect(player, player, living, effect.getAmplifier(), 2.5D);
        } else {
          living.addEffect(new MobEffectInstance(effect.getEffect(), effect.getDuration() * 5 / 2, effect.getAmplifier()));
        }
      }
    }

    if (player != null) {
      player.awardStat(Stats.ITEM_USED.get(this));
      if (!player.getAbilities().instabuild) {
        stack.shrink(1);
      }
    }

    if (player == null || !player.getAbilities().instabuild) {
      if (stack.isEmpty()) {
        return new ItemStack(Items.BUCKET);
      }
      if (player != null) {
        player.getInventory().add(new ItemStack(Items.BUCKET));
      }
    }
    living.gameEvent(GameEvent.DRINK);
    return stack;
  }

  @Override
  public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
    PotionContents contents = new PotionContents(getPotionFromTag(pStack));
    contents.addPotionTooltip(pTooltip::add, 2.5f, 20f);
  }

  @Override
  public int getUseDuration(ItemStack pStack) {
    return 96; // 3x duration of potion bottles
  }

  /** Capability factory, registered for this item by {@link slimeknights.tconstruct.fluids.TinkerFluids#registerCapabilities} */
  public static PotionBucketWrapper createFluidHandler(ItemStack stack, @Nullable Void context) {
    return new PotionBucketWrapper(stack);
  }

  public static class PotionBucketWrapper extends FluidBucketWrapper {
    public PotionBucketWrapper(ItemStack container) {
      super(container);
    }

    @Nonnull
    @Override
    public FluidStack getFluid() {
      return new FluidStack(((PotionBucketItem)container.getItem()).getFluid(),
                            FluidType.BUCKET_VOLUME, container.getTag());
    }
  }
}
