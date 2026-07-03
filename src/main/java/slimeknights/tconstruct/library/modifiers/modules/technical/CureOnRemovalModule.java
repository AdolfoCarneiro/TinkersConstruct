package slimeknights.tconstruct.library.modifiers.modules.technical;

import lombok.RequiredArgsConstructor;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.neoforge.common.EffectCures;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

/** Hook to cure effects using the worn item when its unequipped. Not enabled for composable simply because there is no benefit in JSON even if serialization is trivial. */
@RequiredArgsConstructor
public enum CureOnRemovalModule implements HookProvider, EquipmentChangeModifierHook {
  HELMET(EquipmentSlot.HEAD),
  CHESTPLATE(EquipmentSlot.CHEST),
  LEGGINGS(EquipmentSlot.LEGS),
  BOOT(EquipmentSlot.FEET);

  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<CureOnRemovalModule>defaultHooks(ModifierHooks.EQUIPMENT_CHANGE);

  private final EquipmentSlot slot;

  CureOnRemovalModule(EquipmentSlot slot) {
    this.slot = slot;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    if (context.getSlot() == slot) {
      IToolStackView replacement = context.getReplacementTool();
      if (replacement == null || replacement.getModifierLevel(modifier.getModifier()) == 0 || replacement.getItem() != tool.getItem()) {
        // cure effects using the helmet; ItemStack#curePotionEffects was replaced by the EffectCure system in 1.21.1, MILK is the closest match to the old "cures any curable effect" default
        context.getEntity().removeEffectsCuredBy(EffectCures.MILK);
      }
    }
  }
}
