package slimeknights.tconstruct.tables.network;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import slimeknights.mantle.util.BlockEntityHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.tables.block.entity.table.CraftingStationBlockEntity;

/**
 * Packet to send the current crafting recipe to a player who opens the crafting station
 */
public class UpdateCraftingRecipePacket implements CustomPacketPayload {
  public static final Type<UpdateCraftingRecipePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TConstruct.MOD_ID, "update_crafting_recipe"));
  public static final StreamCodec<RegistryFriendlyByteBuf, UpdateCraftingRecipePacket> STREAM_CODEC = StreamCodec.of((buf, p) -> p.encode(buf), UpdateCraftingRecipePacket::new);

  private final BlockPos pos;
  private final ResourceLocation recipe;

  public UpdateCraftingRecipePacket(BlockPos pos, RecipeHolder<CraftingRecipe> recipe) {
    this.pos = pos;
    this.recipe = recipe.id();
  }

  public UpdateCraftingRecipePacket(RegistryFriendlyByteBuf buffer) {
    this.pos = buffer.readBlockPos();
    this.recipe = buffer.readResourceLocation();
  }

  public void encode(RegistryFriendlyByteBuf buffer) {
    buffer.writeBlockPos(pos);
    buffer.writeResourceLocation(recipe);
  }

  @Override
  public Type<? extends CustomPacketPayload> type() { return TYPE; }

  public static void handleClient(UpdateCraftingRecipePacket packet, IPayloadContext context) {
    context.enqueueWork(() -> HandleClient.handle(packet));
  }

  /** Safely runs client side only code in a method only called on client */
  private static class HandleClient {
    private static void handle(UpdateCraftingRecipePacket packet) {
      Level world = Minecraft.getInstance().level;
      if (world != null) {
        BlockEntityHelper.get(CraftingStationBlockEntity.class, world, packet.pos).ifPresent(te ->
          world.getRecipeManager().byKey(packet.recipe)
               .filter(recipe -> recipe.value() instanceof CraftingRecipe)
               .map(recipe -> (RecipeHolder<CraftingRecipe>)recipe)
               .ifPresent(te::updateRecipe));
      }
    }
  }
}
