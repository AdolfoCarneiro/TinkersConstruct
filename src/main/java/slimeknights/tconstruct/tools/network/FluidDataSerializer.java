package slimeknights.tconstruct.tools.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.neoforged.neoforge.fluids.FluidStack;

/** Serializer for fluid stack data in entities */
public class FluidDataSerializer implements EntityDataSerializer<FluidStack> {
  @Override
  public void write(RegistryFriendlyByteBuf buffer, FluidStack stack) {
    buffer.writeFluidStack(stack);
  }

  @Override
  public FluidStack read(RegistryFriendlyByteBuf buffer) {
    return buffer.readFluidStack();
  }

  @Override
  public FluidStack copy(FluidStack stack) {
    return stack.copy();
  }
}
