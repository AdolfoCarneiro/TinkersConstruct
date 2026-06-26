package slimeknights.tconstruct.library.recipe.ingredient;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.util.RegistryHelper;
import slimeknights.mantle.util.typed.TypedMap;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;

import javax.annotation.Nullable;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Item ingredient matching items with a block form in the given tag */
public class BlockTagIngredient implements ICustomIngredient {
  private final TagKey<Block> tag;
  @Nullable
  private Set<Item> matchingItems;

  public BlockTagIngredient(TagKey<Block> tag) {
    this.tag = tag;
  }

  public static final MapCodec<BlockTagIngredient> CODEC =
    Loadables.BLOCK_TAG.codec().fieldOf("tag").xmap(t -> new BlockTagIngredient(t), i -> i.tag);
  public static final StreamCodec<RegistryFriendlyByteBuf, BlockTagIngredient> STREAM_CODEC = StreamCodec.of(
    (buf, ing) -> Loadables.BLOCK_TAG.encode(buf, ing.tag),
    buf -> new BlockTagIngredient(Loadables.BLOCK_TAG.decode(buf, TypedMap.EMPTY))
  );

  @Override
  public boolean test(@Nullable ItemStack stack) {
    return stack != null && getMatchingItems().contains(stack.getItem());
  }

  @Override
  public boolean isSimple() {
    return true;
  }

  private Set<Item> getMatchingItems() {
    if (matchingItems == null) {
      matchingItems = RegistryHelper.getTagValueStream(BuiltInRegistries.BLOCK, tag)
                                    .map(Block::asItem)
                                    .filter(item -> item != Items.AIR)
                                    .collect(Collectors.toCollection(LinkedHashSet::new));
    }
    return matchingItems;
  }

  @Override
  public Stream<ItemStack> getItems() {
    return getMatchingItems().stream().map(ItemStack::new);
  }

  @Override
  public IngredientType<?> getType() {
    return TinkerRecipeTypes.BLOCK_TAG_INGREDIENT.get();
  }
}
