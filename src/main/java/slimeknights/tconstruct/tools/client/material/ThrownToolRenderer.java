package slimeknights.tconstruct.tools.client.material;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.library.TinkerItemDisplays;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.entity.ThrownTool;
import slimeknights.tconstruct.tools.entity.ToolProjectile;

import javax.annotation.Nullable;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Renderer for {@link ThrownTool}.
 * <p>
 * Historically the thrown pose came from a custom {@code tconstruct:thrown} display context baked into each tool model.
 * {@link net.minecraft.world.item.ItemDisplayContext} is a closed enum in 1.21.1 and the custom context is no longer
 * registered, so the vanilla {@link net.minecraft.client.renderer.block.model.ItemTransforms.Deserializer} silently drops
 * that block. The per-tool thrown transforms are reproduced here and applied manually before rendering, matching the exact
 * translate/rotate/scale order that vanilla {@link ItemTransform#apply(boolean, PoseStack)} used.
 */
public class ThrownToolRenderer<T extends AbstractArrow & ToolProjectile> extends EntityRenderer<T> {
  /** Lazily built lookup of thrown pose per tool item (built once, on first render) */
  @Nullable
  private static Map<Item, ItemTransform> thrownTransforms;

  protected final ItemRenderer itemRenderer;
  public ThrownToolRenderer(Context context) {
    super(context);
    this.itemRenderer = context.getItemRenderer();
  }

  /**
   * Builds an {@link ItemTransform} matching a model {@code display} entry. Translation is scaled by 1/16 to mirror the
   * vanilla {@link ItemTransform.Deserializer}, which converts model-space units to blocks before {@link ItemTransform#apply}.
   */
  private static ItemTransform transform(float rotX, float rotY, float rotZ, float transX, float transY, float transZ, float scale) {
    return new ItemTransform(
      new Vector3f(rotX, rotY, rotZ),
      new Vector3f(transX * 0.0625f, transY * 0.0625f, transZ * 0.0625f),
      new Vector3f(scale, scale, scale));
  }

  /** Adds a transform for the given item if it is present (guards conditionally registered items) */
  private static void put(Map<Item, ItemTransform> map, ItemObject<? extends Item> item, ItemTransform transform) {
    Item value = item.getOrNull();
    if (value != null) {
      map.put(value, transform);
    }
  }

  /** Gets the thrown pose lookup, building it on first use (after registration has run) */
  private static Map<Item, ItemTransform> getThrownTransforms() {
    Map<Item, ItemTransform> map = thrownTransforms;
    if (map == null) {
      map = new IdentityHashMap<>();
      // tconstruct:item/base/axe
      ItemTransform axe = transform(0, 180, 300, -1, 1, 0, 1);
      put(map, TinkerTools.handAxe, axe);
      put(map, TinkerTools.kama, axe);
      put(map, TinkerTools.mattock, axe);
      put(map, TinkerTools.pickadze, axe);
      put(map, TinkerTools.pickaxe, axe);
      put(map, TinkerTools.minotaurAxe, axe);
      // tconstruct:item/base/tall
      ItemTransform tall = transform(0, 180, 0, 5, -5, 0, 1);
      put(map, TinkerTools.cleaver, tall);
      put(map, TinkerTools.excavator, tall);
      put(map, TinkerTools.javelin, tall);
      put(map, TinkerTools.sledgeHammer, tall);
      // tconstruct:item/base/tall_axe
      put(map, TinkerTools.broadAxe, transform(0, 180, 315, 5, -5, 0, 1));
      // tconstruct:item/base/bow
      put(map, TinkerTools.longbow, transform(0, 180, 180, 4, -4, 0, 1));
      // tconstruct:item/base/crossbow
      put(map, TinkerTools.crossbow, transform(90, 135, 225, 1, -1, 0, 1));
      // per-tool display models
      put(map, TinkerTools.warPick, transform(0, 180, 290, 0, 0, 0, 1));
      put(map, TinkerTools.veinHammer, transform(0, 180, 315, 3, -3, 0, 1));
      put(map, TinkerTools.scythe, transform(0, 180, 290, 3, -3, 0, 1));
      put(map, TinkerTools.swasher, transform(0, 180, 270, 0, 0, 0, 1));
      put(map, TinkerTools.dagger, transform(0, 180, 0, -2, 2, 0, 1));
      // tconstruct:item/arrow
      put(map, TinkerTools.arrow, transform(0, 0, 90, 2, -2, 0, 1));
      thrownTransforms = map;
    }
    return map;
  }

  @Override
  public void render(T entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
    poseStack.pushPose();
    poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90));
    poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot()) + 225));
    poseStack.translate(0.2, -0.2, 0);
    ItemStack stack = entity.getDisplayTool();
    // apply the tool's thrown pose manually, replicating the removed tconstruct:thrown display context
    ItemTransform transform = getThrownTransforms().get(stack.getItem());
    if (transform != null) {
      transform.apply(false, poseStack);
    }
    this.itemRenderer.renderStatic(stack, TinkerItemDisplays.THROWN, packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, entity.level(), entity.getId());
    poseStack.popPose();
    super.render(entity, yaw, partialTicks, poseStack, buffer, packedLight);
  }

  @Override
  public ResourceLocation getTextureLocation(T entity) {
    return InventoryMenu.BLOCK_ATLAS;
  }
}
