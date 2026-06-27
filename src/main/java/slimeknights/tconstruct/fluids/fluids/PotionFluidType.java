package slimeknights.tconstruct.fluids.fluids;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Holder;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import slimeknights.mantle.fluid.texture.ClientTextureFluidType;
import slimeknights.mantle.recipe.helper.FluidOutput;
import slimeknights.tconstruct.fluids.TinkerFluids;

import java.util.Objects;
import java.util.function.Consumer;

public class PotionFluidType extends FluidType {
  public PotionFluidType(Properties properties) {
    super(properties);
  }

  @Override
  public String getDescriptionId(FluidStack stack) {
    CompoundTag tag = stack.getTag();
    Holder<Potion> potion = Potions.EMPTY;
    if (tag != null && tag.contains("Potion")) {
      potion = BuiltInRegistries.POTION.getHolder(ResourceLocation.parse(tag.getString("Potion"))).orElse(Potions.EMPTY);
    }
    return potion.value().getName("item.minecraft.potion.effect.");
  }

  @Override
  public ItemStack getBucket(FluidStack fluidStack) {
    ItemStack itemStack = new ItemStack(fluidStack.getFluid().getBucket());
    itemStack.setTag(fluidStack.getTag());
    return itemStack;
  }

  @Override
  public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
    consumer.accept(new ClientTextureFluidType(this) {
      /**
       * Gets the color, based on {@link PotionContents#getColor()}
       * @param stack  Fluid stack instance
       * @return  Color for the fluid
       */
      @Override
      public int getTintColor(FluidStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("CustomPotionColor", Tag.TAG_ANY_NUMERIC)) {
          return tag.getInt("CustomPotionColor") | 0xFF000000;
        }
        if (tag == null || !tag.contains("Potion")) {
          return getTintColor();
        }
        Holder<Potion> potion = BuiltInRegistries.POTION.getHolder(ResourceLocation.parse(tag.getString("Potion"))).orElse(Potions.EMPTY);
        if (potion == Potions.EMPTY) {
          return getTintColor();
        }
        return PotionContents.getColor(potion) | 0xFF000000;
      }
    });
  }

  /** Creates the potion tag */
  private static CompoundTag potionTag(ResourceLocation location) {
    CompoundTag tag = new CompoundTag();
    tag.putString("Potion", location.toString());
    return tag;
  }

  /** Creates a fluid stack for the given potion */
  public static FluidStack potionFluid(ResourceKey<Potion> potion, int size) {
    CompoundTag tag = null;
    if (potion != Potions.EMPTY_ID) {
      tag = potionTag(potion.location());
    }
    FluidStack stack = new FluidStack(TinkerFluids.potion.get(), size);
    if (tag != null) {
      stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }
    return stack;
  }

  /** Creates a fluid stack for the given potion */
  @SuppressWarnings("deprecation")  // forge registries have nullable keys, like why would you want that?
  public static FluidStack potionFluid(Potion potion, int size) {
    CompoundTag tag = null;
    if (potion != Potions.EMPTY) {
      tag = potionTag(BuiltInRegistries.POTION.getKey(potion));
    }
    FluidStack stack = new FluidStack(TinkerFluids.potion.get(), size);
    if (tag != null) {
      stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }
    return stack;
  }

  /** Creates a fluid output for the given potion */
  @SuppressWarnings("deprecation")  // forge registries have nullable keys, like why would you want that?
  public static FluidOutput potionResult(Potion potion, int size) {
    CompoundTag tag = null;
    if (potion != Potions.EMPTY) {
      tag = potionTag(BuiltInRegistries.POTION.getKey(potion));
    }
    return FluidOutput.fromTag(Objects.requireNonNull(TinkerFluids.potion.getCommonTag()), size, tag);
  }

  /** Creates a potion bucket for the given potion */
  public static ItemStack potionBucket(ResourceKey<Potion> potion) {
    ItemStack stack = new ItemStack(TinkerFluids.potion);
    if (potion != Potions.EMPTY_ID) {
      stack.setTag(potionTag(potion.location()));
    }
    return stack;
  }

  /** Creates a potion bucket for the given potion */
  @SuppressWarnings("deprecation")  // forge registries have nullable keys, like why would you want that?
  public static ItemStack potionBucket(Potion potion) {
    ItemStack stack = new ItemStack(TinkerFluids.potion);
    if (potion != Potions.EMPTY) {
      stack.setTag(potionTag(BuiltInRegistries.POTION.getKey(potion)));
    }
    return stack;
  }
}
