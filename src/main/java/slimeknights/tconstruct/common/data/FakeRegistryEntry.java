package slimeknights.tconstruct.common.data;

import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import slimeknights.tconstruct.common.TinkerEffect;

import java.util.function.Supplier;

/**
 * Handles creating fake registry entries for datagen, for referencing IDs owned by other mods that may not be
 * loaded in the current environment (e.g. compat mod items/effects/blocks).
 * <p>
 * Datagen serializers for loot tables ({@code LootItem}), fluid effects ({@code Loadables.MOB_EFFECT}/{@code
 * Loadables.BLOCK}), etc. all resolve the JSON id from the actual registered object (either via a bound
 * {@code Holder} or via a reverse {@code Registry#getKey} lookup) rather than accepting a raw {@link ResourceLocation}.
 * If the requested id is not present in the target registry, this temporarily unfreezes the registry just long
 * enough to bind a throwaway instance under that id, so those lookups resolve to the real id instead of throwing
 * "unbound holder" / "registry does not contain object" errors at datagen time.
 */
public class FakeRegistryEntry {
  @SuppressWarnings("unchecked")
  private static <T> T getOrCreate(Registry<T> registry, ResourceLocation id, Supplier<T> constructor) {
    T existing = registry.getOptional(id).orElse(null);
    if (existing != null) {
      return existing;
    }
    // unfreeze so the constructor (which may bind an intrusive holder, e.g. Item/Block) and the registration below are both legal
    MappedRegistry<T> writable = (MappedRegistry<T>) registry;
    writable.unfreeze();
    T value = constructor.get();
    writable.register(ResourceKey.create(registry.key(), id), value, RegistrationInfo.BUILT_IN);
    return value;
  }

  /** Gets or creates a fake block with the given ID */
  public static Block block(ResourceLocation id) {
    return getOrCreate(BuiltInRegistries.BLOCK, id, () -> new Block(BlockBehaviour.Properties.of()));
  }

  /** Gets or creates a fake item with the given ID */
  public static Item item(ResourceLocation id) {
    return getOrCreate(BuiltInRegistries.ITEM, id, () -> new Item(new Item.Properties()));
  }

  /** Gets or creates a fake mob effect with the given ID */
  public static MobEffect effect(ResourceLocation id) {
    return getOrCreate(BuiltInRegistries.MOB_EFFECT, id, () -> new TinkerEffect(MobEffectCategory.NEUTRAL, false));
  }
}
