package slimeknights.tconstruct.library.tools.item.armor;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.mantle.registration.object.IdAwareObject;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/** Armor material wrapper that returns 0 for all stats, since we bypass all the usages via our tool system */
public class DummyArmorMaterial implements IdAwareObject {
  private final ResourceLocation id;
  private final Holder<ArmorMaterial> material;

  public DummyArmorMaterial(ResourceLocation id, SoundEvent equipSound) {
    this.id = id;
    Map<ArmorItem.Type, Integer> defense = new EnumMap<>(ArmorItem.Type.class);
    for (ArmorItem.Type type : ArmorItem.Type.values()) {
      defense.put(type, 0);
    }
    this.material = Registry.registerForHolder(
      BuiltInRegistries.ARMOR_MATERIAL,
      id,
      new ArmorMaterial(
        defense,
        0,
        BuiltInRegistries.SOUND_EVENT.wrapAsHolder(equipSound),
        () -> Ingredient.EMPTY,
        List.of(new ArmorMaterial.Layer(id)),
        0f,
        0f
      )
    );
  }

  @Override
  public ResourceLocation getId() {
    return id;
  }

  public Holder<ArmorMaterial> getMaterial() {
    return material;
  }
}
