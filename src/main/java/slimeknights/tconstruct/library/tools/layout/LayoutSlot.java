package slimeknights.tconstruct.library.tools.layout;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;

import javax.annotation.Nullable;
import java.util.Objects;

/** A single slot in a slot layout */
public class LayoutSlot {
  public static final LayoutSlot EMPTY = new LayoutSlot(null, "", -1, -1, null);

  /** Icon to display when the slot is empty */
  @Nullable
  private final Pattern icon;
  /** Name to display in the sidebar for the slot's "needs" */
  @Nullable
  private final String translation_key;
  private final int x;
  private final int y;
  /** Filter to only allow certain items in the slot under this layout */
  @Nullable @VisibleForTesting
  private final Ingredient filter;

  public LayoutSlot(@Nullable Pattern icon, @Nullable String translation_key, int x, int y, @Nullable Ingredient filter) {
    this.icon = icon;
    this.translation_key = translation_key;
    this.x = x;
    this.y = y;
    this.filter = filter;
  }

  @Nullable
  public Pattern getIcon() {
    return icon;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  @Nullable
  protected Ingredient getFilter() {
    return filter;
  }

  /** If true, this is an empty slot */
  public boolean isEmpty() {
    return getTranslationKey().isEmpty();
  }

  public boolean isHidden() {
    return x == -1 && y == -1;
  }

  /** Gets the translation key of this slot */
  public String getTranslationKey() {
    return Objects.requireNonNullElse(translation_key, "");
  }

  /** Checks if the given stack is valid for this slot */
  public boolean isValid(ItemStack stack) {
    return !stack.isEmpty() && (filter == null || filter.test(stack));
  }


  /* Buffers */

  /** Reads a slot from the packet buffer */
  public static LayoutSlot read(RegistryFriendlyByteBuf buffer) {
    Pattern pattern = null;
    if (buffer.readBoolean()) {
      pattern = new Pattern(buffer.readResourceLocation());
    }
    String name = buffer.readUtf(Short.MAX_VALUE);
    int x = buffer.readVarInt();
    int y = buffer.readVarInt();
    Ingredient ingredient = null;
    if (buffer.readBoolean()) {
      ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
    }
    return new LayoutSlot(pattern, name, x, y, ingredient);
  }

  /** Writes a slot to the packet buffer */
  public void write(RegistryFriendlyByteBuf buffer) {
    if (icon != null) {
      buffer.writeBoolean(true);
      buffer.writeResourceLocation(icon);
    } else {
      buffer.writeBoolean(false);
    }
    buffer.writeUtf(getTranslationKey());
    buffer.writeVarInt(x);
    buffer.writeVarInt(y);
    if (filter != null) {
      buffer.writeBoolean(true);
      Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, filter);
    } else {
      buffer.writeBoolean(false);
    }
  }
}
