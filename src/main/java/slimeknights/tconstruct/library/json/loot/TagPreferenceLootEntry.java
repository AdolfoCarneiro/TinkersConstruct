package slimeknights.tconstruct.library.json.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import slimeknights.mantle.recipe.helper.TagPreference;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.shared.TinkerCommons;

import java.util.List;
import java.util.function.Consumer;

/** @deprecated use {@link slimeknights.mantle.loot.entry.TagPreferenceLootEntry} */
@Deprecated(forRemoval = true)
public class TagPreferenceLootEntry extends LootPoolSingletonContainer {
  /** Tag codec that warns on use, since this whole entry type is deprecated in favor of the mantle version */
  private static final Codec<TagKey<Item>> TAG_CODEC = TagKey.codec(Registries.ITEM).comapFlatMap(tag -> {
    TConstruct.LOG.warn("Using deprecated tag preference loot entry 'tconstruct:tag_preference', use 'mantle:tag_preference' instead");
    return DataResult.success(tag);
  }, tag -> tag);
  public static final MapCodec<TagPreferenceLootEntry> SERIALIZER = RecordCodecBuilder.mapCodec(instance ->
    singletonFields(instance).and(TAG_CODEC.fieldOf("tag").forGetter(entry -> entry.tag)).apply(instance, TagPreferenceLootEntry::new));
  private final TagKey<Item> tag;
  protected TagPreferenceLootEntry(int weight, int quality, List<LootItemCondition> conditions, List<LootItemFunction> functions, TagKey<Item> tag) {
    super(weight, quality, conditions, functions);
    this.tag = tag;
  }

  @SuppressWarnings("removal")
  @Override
  public LootPoolEntryType getType() {
    return TinkerCommons.lootTagPreference.get();
  }

  @Override
  protected void createItemStack(Consumer<ItemStack> consumer, LootContext context) {
    TagPreference.getPreference(tag).ifPresent(item -> consumer.accept(new ItemStack(item)));
  }

  /** @deprecated use {@link slimeknights.mantle.loot.entry.TagPreferenceLootEntry#tagPreference(TagKey)} */
  @Deprecated(forRemoval = true)
  public static LootPoolSingletonContainer.Builder<?> tagPreference(TagKey<Item> tag) {
    return slimeknights.mantle.loot.entry.TagPreferenceLootEntry.tagPreference(tag);
  }
}
