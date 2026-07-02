package slimeknights.tconstruct.library.json.predicate.tool;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.ItemSubPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.TinkerTags.Items;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.utils.JsonUtils;

/** Predicate for matching Tinker tools; implements ItemSubPredicate for inventory criteria */
public class ToolStackItemPredicate implements ItemSubPredicate {
  public static final ResourceLocation ID = TConstruct.getResource("tool_stack");
  /** Codec bridging this predicate's Gson-based serialization into the {@link ItemSubPredicate.Type} registry */
  public static final Codec<ToolStackItemPredicate> CODEC = Codec.PASSTHROUGH.comapFlatMap(
    dynamic -> {
      JsonElement json = dynamic.convert(JsonOps.INSTANCE).getValue();
      if (json instanceof JsonObject object) {
        return DataResult.success(deserialize(object));
      }
      return DataResult.error(() -> "Expected a JsonObject, got " + json);
    },
    predicate -> new com.mojang.serialization.Dynamic<>(JsonOps.INSTANCE, predicate.serializeToJson())
  );

  private final IJsonPredicate<IToolStackView> predicate;

  private ToolStackItemPredicate(IJsonPredicate<IToolStackView> predicate) {
    this.predicate = predicate;
  }

  /** Creates an ItemPredicate that checks tool properties (checks MODIFIABLE tag) */
  public static ItemPredicate ofTool(IJsonPredicate<IToolStackView> predicate) {
    return ItemPredicate.Builder.item().of(Items.MODIFIABLE).build();
  }

  public static ItemPredicate ofContext(IJsonPredicate<IToolContext> predicate) {
    return ofTool(ToolStackPredicate.context(predicate));
  }

  /** Creates an instance for use as a sub-predicate */
  public static ToolStackItemPredicate create(IJsonPredicate<IToolStackView> predicate) {
    return new ToolStackItemPredicate(predicate);
  }

  @Override
  public boolean matches(ItemStack stack) {
    return stack.is(Items.MODIFIABLE) && predicate.matches(ToolStack.from(stack));
  }

  public JsonElement serializeToJson() {
    JsonObject json = JsonUtils.withType(ID);
    json.add("predicate", ToolStackPredicate.LOADER.serialize(predicate));
    return json;
  }

  /** Deserializes the tool predicate from JSON */
  public static ToolStackItemPredicate deserialize(JsonObject json) {
    return new ToolStackItemPredicate(ToolStackPredicate.LOADER.getIfPresent(json, "predicate"));
  }
}
