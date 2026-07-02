package slimeknights.tconstruct.plugin.jei.util;

import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;

import java.util.Optional;

/** Common logic for subtype interpreter between the fluid and item form of our potion. Based on a JEI class with the same name */
public interface PotionSubtypeInterpreter<T> extends IIngredientSubtypeInterpreter<T> {
  /** Gets the custom data tag off the ingredient, empty if none is present */
  CompoundTag getTag(T ingredient);

  @Override
  default String apply(T ingredient, UidContext context) {
    CompoundTag tag = getTag(ingredient);
    if (!tag.contains("Potion")) {
      return IIngredientSubtypeInterpreter.NONE;
    }
    Optional<Holder<Potion>> potionHolder = BuiltInRegistries.POTION.getHolder(ResourceLocation.parse(tag.getString("Potion"))).map(h -> h);
    StringBuilder stringBuilder = new StringBuilder(Potion.getName(potionHolder, ""));
    potionHolder.ifPresent(holder -> {
      for (MobEffectInstance effect : holder.value().getEffects()) {
        stringBuilder.append(";").append(effect);
      }
    });
    return stringBuilder.toString();
  }
}
