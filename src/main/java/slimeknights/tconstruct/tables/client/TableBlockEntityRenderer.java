package slimeknights.tconstruct.tables.client;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.AABB;
import slimeknights.mantle.client.render.InventoryBlockEntityRenderer;
import slimeknights.tconstruct.shared.block.entity.TableBlockEntity;
import slimeknights.tconstruct.tables.block.entity.table.RetexturedTableBlockEntity;

/**
 * Extension of Mantle's {@link InventoryBlockEntityRenderer} that restores the taller render bounding box tables
 * need so tools/items displayed on top of them are not culled early. In 1.21.1 {@code getRenderBoundingBox} moved
 * from {@code BlockEntity} to {@code BlockEntityRenderer}, so this can no longer live on the block entity alone.
 */
public class TableBlockEntityRenderer extends InventoryBlockEntityRenderer<TableBlockEntity> {
  public TableBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    super(context);
  }

  @Override
  public AABB getRenderBoundingBox(TableBlockEntity blockEntity) {
    if (blockEntity instanceof RetexturedTableBlockEntity retextured) {
      return retextured.getRenderBoundingBox();
    }
    return super.getRenderBoundingBox(blockEntity);
  }
}
