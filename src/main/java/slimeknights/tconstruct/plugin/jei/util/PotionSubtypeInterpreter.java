package slimeknights.tconstruct.plugin.jei.util;

import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;

import javax.annotation.Nullable;
import java.util.List;

/** Common logic for subtype interpreter between the fluid and item form of our potion. Based on a JEI class with the same name */
public interface PotionSubtypeInterpreter<T> extends IIngredientSubtypeInterpreter<T> {
  @Nullable
  CompoundTag getTag(T ingredient);

  @Override
  default String apply(T ingredient, UidContext context) {
    CompoundTag tag = getTag(ingredient);
    if (tag == null) {
      return IIngredientSubtypeInterpreter.NONE;
    }
    Holder<Potion> potionHolder = Potions.EMPTY;
    if (tag.contains("Potion")) {
      potionHolder = BuiltInRegistries.POTION.getHolder(ResourceLocation.parse(tag.getString("Potion"))).orElse(Potions.EMPTY);
    }
    Potion potionType = potionHolder.value();
    String potionTypeString = potionType.getName("");
    StringBuilder stringBuilder = new StringBuilder(potionTypeString);
    List<MobEffectInstance> effects = potionType.getEffects();
    for (MobEffectInstance effect : effects) {
      stringBuilder.append(";").append(effect);
    }
    return stringBuilder.toString();
  }
}
