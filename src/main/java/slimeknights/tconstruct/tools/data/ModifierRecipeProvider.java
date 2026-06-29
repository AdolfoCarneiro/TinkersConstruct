package slimeknights.tconstruct.tools.data;

import net.minecraft.data.PackOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.Tags.Fluids;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import net.neoforged.neoforge.common.crafting.DifferenceIngredient;
import net.neoforged.neoforge.common.crafting.IntersectionIngredient;
import net.neoforged.neoforge.fluids.FluidType;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.datagen.MantleTags;
import slimeknights.mantle.recipe.condition.TagFilledCondition;
import slimeknights.mantle.recipe.data.ItemNameIngredient;
import slimeknights.mantle.recipe.helper.ItemOutput;

import slimeknights.mantle.recipe.ingredient.EntityIngredient;
import slimeknights.mantle.recipe.ingredient.FluidContainerIngredient;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.mantle.registration.object.WoodBlockObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.data.BaseRecipeProvider;
import slimeknights.tconstruct.common.registration.GeodeItemObject.BudSize;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.gadgets.entity.FrameType;
import slimeknights.tconstruct.library.json.predicate.modifier.ModifierPredicate;
import slimeknights.tconstruct.library.json.predicate.modifier.SlotTypeModifierPredicate;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.util.LazyModifier;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.PotionCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.ingredient.MaterialIngredient;
import slimeknights.tconstruct.library.recipe.ingredient.NoContainerIngredient;
import slimeknights.tconstruct.library.recipe.ingredient.ToolHookIngredient;
import slimeknights.tconstruct.library.recipe.modifiers.adding.IncrementalModifierRecipeBuilder;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipeBuilder;
import slimeknights.tconstruct.library.recipe.modifiers.adding.MultilevelIncrementalModifierRecipeBuilder;
import slimeknights.tconstruct.library.recipe.modifiers.adding.MultilevelModifierRecipeBuilder;
import slimeknights.tconstruct.library.recipe.modifiers.adding.OverslimeModifierRecipeBuilder;
import slimeknights.tconstruct.library.recipe.modifiers.adding.SwappableModifierRecipe.VariantFormatter;
import slimeknights.tconstruct.library.recipe.modifiers.adding.SwappableModifierRecipeBuilder;
import slimeknights.tconstruct.library.recipe.modifiers.severing.SeveringRecipeBuilder;
import slimeknights.tconstruct.library.recipe.tinkerstation.repairing.ModifierRepairRecipeBuilder;
import slimeknights.tconstruct.library.recipe.worktable.ModifierSetWorktableRecipeBuilder;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.data.material.MaterialIds;
import slimeknights.tconstruct.tools.recipe.EnchantmentConvertingRecipeBuilder;
import slimeknights.tconstruct.tools.recipe.ModifierRemovalRecipeBuilder;
import slimeknights.tconstruct.tools.recipe.ModifierSortingRecipeBuilder;
import slimeknights.tconstruct.tools.recipe.ToggleInteractionWorktableRecipeBuilder;
import slimeknights.tconstruct.tools.recipe.severing.SpecialSeveringRecipeBuilder;
import slimeknights.tconstruct.world.TinkerHeadType;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.FoliageType;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;


import static slimeknights.mantle.Mantle.COMMON;
import static slimeknights.tconstruct.library.recipe.melting.IMeltingRecipe.getTemperature;

public class ModifierRecipeProvider extends BaseRecipeProvider {
  public ModifierRecipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
    super(packOutput, registries);
  }

  @Override
  protected void buildRecipes(RecipeOutput output) {
    addItemRecipes(output);
    addModifierRecipes(output);
    addTextureRecipes(output);
    addHeadRecipes(output);
  }

  private void addItemRecipes(RecipeOutput output) {
    String folder = "tools/modifiers/";

    // durability reinforcements, use obsidian
    ItemCastingRecipeBuilder.tableRecipe(TinkerModifiers.emeraldReinforcement)
                            .setFluidAndTime(TinkerFluids.moltenEmerald, FluidValues.GEM_SHARD)
                            .setCast(TinkerCommons.obsidianPane, true)
                            .save(output, prefix(TinkerModifiers.emeraldReinforcement, folder));
    ItemCastingRecipeBuilder.tableRecipe(TinkerModifiers.slimesteelReinforcement)
                            .setFluidAndTime(TinkerFluids.moltenSlimesteel, FluidValues.NUGGET * 3)
                            .setCast(TinkerCommons.obsidianPane, true)
                            .save(output, prefix(TinkerModifiers.slimesteelReinforcement, folder));
    // protection reinforcements, use patterns
    ItemCastingRecipeBuilder.tableRecipe(TinkerModifiers.ironReinforcement)
                            .setFluidAndTime(TinkerFluids.moltenIron, FluidValues.INGOT)
                            .setCast(TinkerTables.pattern, true)
                            .save(output, prefix(TinkerModifiers.ironReinforcement, folder));
    ItemCastingRecipeBuilder.tableRecipe(TinkerModifiers.searedReinforcement)
                            .setFluid(FluidIngredient.of(TinkerFluids.searedStone.ingredient(FluidValues.BRICK), TinkerFluids.scorchedStone.ingredient(FluidValues.BRICK)))
                            .setCoolingTime(getTemperature(TinkerFluids.searedStone), FluidValues.BRICK)
                            .setCast(TinkerTables.pattern, true)
                            .save(output, prefix(TinkerModifiers.searedReinforcement, folder));
    ItemCastingRecipeBuilder.tableRecipe(TinkerModifiers.goldReinforcement)
                            .setFluidAndTime(TinkerFluids.moltenGold, FluidValues.INGOT)
                            .setCast(TinkerTables.pattern, true)
                            .save(output, prefix(TinkerModifiers.goldReinforcement, folder));
    ItemCastingRecipeBuilder.tableRecipe(TinkerModifiers.obsidianReinforcement)
                            .setFluidAndTime(TinkerFluids.moltenObsidian, FluidValues.GLASS_BLOCK)
                            .setCast(TinkerTables.pattern, true)
                            .save(output, prefix(TinkerModifiers.obsidianReinforcement, folder));
    ItemCastingRecipeBuilder.tableRecipe(TinkerModifiers.cobaltReinforcement)
                            .setFluidAndTime(TinkerFluids.moltenCobalt, FluidValues.INGOT)
                            .setCast(TinkerTables.pattern, true)
                            .save(output, prefix(TinkerModifiers.cobaltReinforcement, folder));

    // jeweled apple
    ItemCastingRecipeBuilder.tableRecipe(TinkerCommons.jeweledApple)
                            .setFluidAndTime(TinkerFluids.moltenDiamond, FluidValues.GEM * 2)
                            .setCast(Items.APPLE, true)
                            .save(output, prefix(TinkerCommons.jeweledApple, folder));

    // silky cloth
    ItemCastingRecipeBuilder.tableRecipe(TinkerModifiers.silkyCloth)
                            .setCast(Items.COBWEB, true)
                            .setFluidAndTime(TinkerFluids.moltenRoseGold, FluidValues.INGOT)
                            .save(output, prefix(TinkerModifiers.silkyCloth, folder));

    // modifier repair
    // pig iron - from bacon, only in the tinker station
    ModifierRepairRecipeBuilder.repair(ModifierIds.tasty, Ingredient.of(TinkerCommons.bacon), 25)
                               .save(output, prefix(ModifierIds.tasty, folder));
  }

  @SuppressWarnings("removal")
  private void addModifierRecipes(RecipeOutput output) {
    // modifiers
    String upgradeFolder = "tools/modifiers/upgrade/";
    String abilityFolder = "tools/modifiers/ability/";
    String slotlessFolder = "tools/modifiers/slotless/";
    String defenseFolder = "tools/modifiers/defense/";
    String compatFolder = "tools/modifiers/compat/";
    String worktableFolder = "tools/modifiers/worktable/";
    // salvage
    String salvageFolder = "tools/modifiers/salvage/";
    String upgradeSalvage = salvageFolder + "upgrade/";
    String abilitySalvage = salvageFolder + "ability/";
    String defenseSalvage = salvageFolder + "defense/";
    String compatSalvage = salvageFolder + "compat/";

    /*
     * durability
     */
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.reinforced)
                         .setInput(TinkerModifiers.emeraldReinforcement, 1, 4)
                         .setMaxLevel(5) // max 75% resistant to damage
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(TinkerTags.Items.DURABILITY)
                         .saveSalvage(output, prefix(ModifierIds.reinforced, upgradeSalvage))
                         .save(output, prefix(ModifierIds.reinforced, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.overforced)
                                    .setInput(TinkerModifiers.slimesteelReinforcement, 1, 4)
                                    .setMaxLevel(5) // +250 capacity
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .setTools(TinkerTags.Items.DURABILITY)
                                    .saveSalvage(output, prefix(ModifierIds.overforced, upgradeSalvage))
                                    .save(output, prefix(ModifierIds.overforced, upgradeFolder));
    // gems are special, I'd like them to be useful on all types of tools
    ModifierRecipeBuilder.modifier(ModifierIds.emerald)
                         .setTools(TinkerTags.Items.DURABILITY)
                         .addInput(Tags.Items.GEMS_EMERALD)
                         .setMaxLevel(1)
                         .setSlots(SlotType.UPGRADE, 1)
                         .saveSalvage(output, prefix(ModifierIds.emerald, upgradeSalvage))
                         .save(output, prefix(ModifierIds.emerald, upgradeFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.diamond)
                         .setTools(TinkerTags.Items.DURABILITY)
                         .addInput(Tags.Items.GEMS_DIAMOND)
                         .setMaxLevel(1)
                         .setSlots(SlotType.UPGRADE, 1)
                         .saveSalvage(output, prefix(ModifierIds.diamond, upgradeSalvage))
                         .save(output, prefix(ModifierIds.diamond, upgradeFolder));
    Ingredient multiuse = DifferenceIngredient.of(Ingredient.of(TinkerTags.Items.MODIFIABLE), Ingredient.of(TinkerTags.Items.SINGLE_USE));
    ModifierRecipeBuilder.modifier(ModifierIds.worldbound)
      .setTools(multiuse)
      .addInput(TinkerTags.Items.INGOTS_NETHERITE_SCRAP)
      .setMaxLevel(1)
      .save(output, prefix(ModifierIds.worldbound, slotlessFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.soulbound)
      .setTools(multiuse)
      .addInput(Items.ECHO_SHARD)
      .setSlots(SlotType.UPGRADE, 1)
      .setMaxLevel(1)
      .saveSalvage(output, prefix(ModifierIds.soulbound, upgradeSalvage))
      .save(output, prefix(ModifierIds.soulbound, upgradeFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.soulbound)
      .setTools(TinkerTags.Items.SINGLE_USE)
      .addInput(Items.SCULK_VEIN)
      .setMaxLevel(1)
      .save(output, wrap(ModifierIds.soulbound, slotlessFolder, "_ammo"));
    ModifierRecipeBuilder.modifier(ModifierIds.netherite)
                         .setTools(TinkerTags.Items.DURABILITY)
                         .addInput(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE)
                         .addInput(Tags.Items.INGOTS_NETHERITE)
                         .setMaxLevel(1)
                         .setSlots(SlotType.UPGRADE, 1)
                         .saveSalvage(output, prefix(ModifierIds.netherite, upgradeSalvage))
                         .save(output, prefix(ModifierIds.netherite, upgradeFolder));

    // overslime
    Ingredient overslimeTools = Ingredient.of(TinkerTags.Items.DURABILITY);
    for (SlimeType type : SlimeType.values()) {
      int amount;
      switch (type) {
        // earth is common and easy to get
        case EARTH -> amount = 20;
        // sky is tinkers specialty
        case SKY -> amount = 50;
        // ichor is hard to farm
        case ICHOR -> amount = 100;
        // ender is late game, but easier to farm than ichor
        case ENDER -> amount = 80;
        // unhandled -> update
        default -> {
          continue;
        }
      }
      String name = type.getSerializedName();
      // ball and bottle - base amount
      OverslimeModifierRecipeBuilder.modifier(TinkerCommons.slimeball.get(type), amount)
        .setTools(overslimeTools)
        .save(output, location(slotlessFolder + "overslime/" + name + "_ball"));
      OverslimeModifierRecipeBuilder.modifier(TinkerFluids.slimeBottle.get(type), amount)
        .saveCrafting(output, location(slotlessFolder + "overslime/" + name + "_bottle_crafting_table"))
        .save(output, location(slotlessFolder + "overslime/" + name + "_bottle"));
      // congealed: 4x
      OverslimeModifierRecipeBuilder.modifier(TinkerWorld.congealedSlime.get(type), amount * 4)
        .setTools(overslimeTools)
        .save(output, location(slotlessFolder + "overslime/" + name + "_congealed"));
      // block: 9x
      OverslimeModifierRecipeBuilder.modifier(TinkerWorld.slime.get(type), amount * 9)
        .setTools(overslimeTools)
        .save(output, location(slotlessFolder + "overslime/" + name + "_block"));
    }

    /*
     * general effects
     */
    ModifierRecipeBuilder.modifier(ModifierIds.experienced)
                         .addInput(Items.EXPERIENCE_BOTTLE)
                         .addInput(Items.EXPERIENCE_BOTTLE)
                         .addInput(Items.EXPERIENCE_BOTTLE)
                         .setMaxLevel(5) // max +250%
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(ingredientFromTags(TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST, TinkerTags.Items.LAUNCHERS, TinkerTags.Items.LEGGINGS))
                         .saveSalvage(output, prefix(ModifierIds.experienced, upgradeSalvage))
                         .save(output, prefix(ModifierIds.experienced, upgradeFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.magnetic)
                         .addInput(Items.COMPASS)
                         .setMaxLevel(5)
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(ingredientFromTags(TinkerTags.Items.MELEE_WEAPON, TinkerTags.Items.HARVEST))
                         .save(output, prefix(ModifierIds.magnetic, upgradeFolder));
    // armor has a max level of 1 per piece, so 4 total
    ModifierRecipeBuilder.modifier(ModifierIds.magnetic)
                         .addInput(Items.COMPASS)
                         .setMaxLevel(1)
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(TinkerTags.Items.WORN_ARMOR) // TODO: reconsider for shields
                         .save(output, wrap(ModifierIds.magnetic, upgradeFolder, "_armor"));
    // salvage supports either
    ModifierRecipeBuilder.modifier(ModifierIds.magnetic)
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(ingredientFromTags(TinkerTags.Items.MELEE_WEAPON, TinkerTags.Items.HARVEST, TinkerTags.Items.WORN_ARMOR))
                         .saveSalvage(output, prefix(ModifierIds.magnetic, upgradeSalvage));
    ModifierRecipeBuilder.modifier(ModifierIds.shiny)
                         .addInput(Ingredient.of(Items.ENCHANTED_GOLDEN_APPLE, Items.NETHER_STAR))
                         .setMaxLevel(1)
                         .save(output, prefix(ModifierIds.shiny, slotlessFolder));
    Ingredient sighted = ingredientFromTags(TinkerTags.Items.HELD, TinkerTags.Items.ARMOR);
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.farsighted)
                                    .setTools(sighted)
                                    .setInput(Tags.Items.CROPS_CARROT, 1, 45)
                                    .save(output, prefix(ModifierIds.farsighted, slotlessFolder));
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.nearsighted)
                                    .setTools(sighted)
                                    .setInput(Items.INK_SAC, 1, 45)
                                    .save(output, prefix(ModifierIds.nearsighted, slotlessFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.offhanded)
                         .setTools(TinkerTags.Items.INTERACTABLE_CHARGE_MODIFIER)
                         .addInput(Items.LEATHER)
                         .addInput(Items.FIRE_CHARGE)
                         .addInput(SlimeType.ICHOR.getSlimeballTag())
                         .setMaxLevel(2)
                         .save(output, prefix(ModifierIds.offhanded, slotlessFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.blunted)
      .setTools(TinkerTags.Items.INTERACTABLE_LEFT)
      .addInput(Items.HONEYCOMB)
      .addInput(Items.FEATHER)
      .addInput(Items.HONEYCOMB)
      .setMaxLevel(1).checkTraitLevel()
      .save(output, prefix(ModifierIds.blunted, slotlessFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.smelting)
      .setTools(ingredientFromTags(TinkerTags.Items.INTERACTABLE, TinkerTags.Items.WORN_ARMOR))
      .addInput(Blocks.CAMPFIRE)
      .setSlots(SlotType.UPGRADE, 1)
      .setMaxLevel(4)
      .saveSalvage(output, prefix(ModifierIds.smelting, upgradeSalvage))
      .save(output, prefix(ModifierIds.smelting, upgradeFolder));

    /*
     * Speed
     */

    // haste can use redstone or blocks
    hasteRecipes(output, ModifierIds.haste, ingredientFromTags(TinkerTags.Items.HARVEST, TinkerTags.Items.CHESTPLATES), 5, upgradeFolder, upgradeSalvage);
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.blasting)
                                    .setTools(TinkerTags.Items.STONE_HARVEST)
                                    .setInput(Tags.Items.GUNPOWDERS, 1, 20)
                                    .setMaxLevel(5) // +50 mining speed at max, conditionally
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .saveSalvage(output, prefix(ModifierIds.blasting, upgradeSalvage))
                                    .save(output, prefix(ModifierIds.blasting, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.hydraulic)
                                    .setTools(TinkerTags.Items.HARVEST)
                                    .setInput(Tags.Items.GEMS_PRISMARINE, 1, 36)
                                    .setMaxLevel(5)
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .saveSalvage(output, prefix(ModifierIds.hydraulic, upgradeSalvage))
                                    .save(output, wrap(ModifierIds.hydraulic, upgradeFolder, "_from_shard"));
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.hydraulic)
                                    .setTools(TinkerTags.Items.HARVEST)
                                    .setInput(Blocks.PRISMARINE, 4, 36)
                                    .setLeftover(Items.PRISMARINE_SHARD)
                                    .setMaxLevel(5)
                                    .disallowCrystal()
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .save(output, wrap(ModifierIds.hydraulic, upgradeFolder, "_from_block"));
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.hydraulic)
                                    .setTools(TinkerTags.Items.HARVEST)
                                    .setInput(Blocks.PRISMARINE_BRICKS, 9, 36)
                                    .setLeftover(Items.PRISMARINE_SHARD)
                                    .setMaxLevel(5)
                                    .disallowCrystal()
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .save(output, wrap(ModifierIds.hydraulic, upgradeFolder, "_from_bricks"));
    IncrementalModifierRecipeBuilder lightspeed = IncrementalModifierRecipeBuilder.modifier(ModifierIds.lightspeed)
      .setInput(Tags.Items.DUSTS_GLOWSTONE, 1, 64)
      .setMaxLevel(5) // +45 mining speed at max, conditionally
      .setSlots(SlotType.UPGRADE, 1)
      .setTools(ingredientFromTags(TinkerTags.Items.HARVEST, TinkerTags.Items.BOOTS))
      .saveSalvage(output, prefix(ModifierIds.lightspeed, upgradeSalvage));
    lightspeed
      .setTools(TinkerTags.Items.HARVEST)
      .save(output, wrap(ModifierIds.lightspeed, upgradeFolder, "_harvest_from_dust"));
    lightspeed
      .setMaxLevel(3) // 27% running speed at max, conditionally
      .setTools(TinkerTags.Items.BOOTS)
      .save(output, wrap(ModifierIds.lightspeed, upgradeFolder, "_boots_from_dust"));
    lightspeed = IncrementalModifierRecipeBuilder.modifier(ModifierIds.lightspeed)
      .setInput(Blocks.GLOWSTONE, 4, 64)
      .setLeftover(Items.GLOWSTONE_DUST)
      .disallowCrystal()
      .setSlots(SlotType.UPGRADE, 1);
    lightspeed
      .setMaxLevel(5)
      .setTools(TinkerTags.Items.HARVEST)
      .save(output, wrap(ModifierIds.lightspeed, upgradeFolder, "_harvest_from_block"));
    lightspeed
      .setMaxLevel(3)
      .setTools(TinkerTags.Items.BOOTS)
      .save(output, wrap(ModifierIds.lightspeed, upgradeFolder, "_boots_from_block"));

    /*
     * weapon
     */
    ModifierRecipeBuilder.modifier(ModifierIds.knockback)
                         .addInput(Items.PISTON)
                         .addInput(TinkerWorld.slime.get(SlimeType.EARTH))
                         .setMaxLevel(3) // max +2.5 knockback points (knockback 5) (whatever that number means in vanilla)
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(ingredientFromTags(TinkerTags.Items.MELEE, TinkerTags.Items.CHESTPLATES))
                         .saveSalvage(output, prefix(ModifierIds.knockback, upgradeSalvage))
                         .save(output, prefix(ModifierIds.knockback, upgradeFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.padded)
                         .addInput(Items.LEATHER)
                         .addInput(ItemTags.WOOL)
                         .addInput(Items.LEATHER)
                         .setMaxLevel(3) // max 12.5% knockback, or 6.25% on the dagger
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(TinkerTags.Items.MELEE)
                         .saveSalvage(output, prefix(ModifierIds.padded, upgradeSalvage))
                         .save(output, prefix(ModifierIds.padded, upgradeFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.severing)
                         .addInput(TinkerTags.Items.WITHER_BONES)
                         .addInput(Items.LIGHTNING_ROD)
                         .addInput(TinkerTags.Items.WITHER_BONES)
                         .addInput(Items.TNT)
                         .setMaxLevel(3) // max +25% head drop chance, combine with +15% chance from luck
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(ingredientFromTags(TinkerTags.Items.MELEE, TinkerTags.Items.LAUNCHERS))
                         .saveSalvage(output, prefix(TinkerModifiers.severing, upgradeSalvage))
                         .save(output, prefix(TinkerModifiers.severing, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.fiery)
                                    .setTools(ingredientFromTags(TinkerTags.Items.MELEE, TinkerTags.Items.BOWS, TinkerTags.Items.FISHING_RODS, TinkerTags.Items.WORN_ARMOR, TinkerTags.Items.SHIELDS))
                                    .setInput(Items.BLAZE_POWDER, 1, 25)
                                    .setMaxLevel(5) // +25 seconds fire damage
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .saveSalvage(output, prefix(ModifierIds.fiery, upgradeSalvage))
                                    .save(output, prefix(ModifierIds.fiery, upgradeFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.necrotic)
                         .addInput(TinkerMaterials.necroticBone)
                         .addInput(TinkerWorld.congealedSlime.get(SlimeType.ICHOR))
                         .addInput(Items.GHAST_TEAR)
                         .setMaxLevel(5) // +50% chance of heal, combine with +40% from traits for +90% total
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(ingredientFromTags(TinkerTags.Items.MELEE, TinkerTags.Items.BOWS, TinkerTags.Items.FISHING_RODS))
                         .saveSalvage(output, prefix(ModifierIds.necrotic, upgradeSalvage))
                         .save(output, prefix(ModifierIds.necrotic, upgradeFolder));

    /*
     * damage boost
     */
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.pierce)
                                    .setTools(ingredientFromTags(TinkerTags.Items.MELEE, TinkerTags.Items.LAUNCHERS))
                                    .setInput(TinkerGadgets.punji, 1, 10)
                                    .setMaxLevel(3) // +3 pierce, +1.5 damage
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .saveSalvage(output, prefix(ModifierIds.pierce, upgradeSalvage))
                                    .save(output, prefix(ModifierIds.pierce, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.smite)
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInput(Items.GLISTERING_MELON_SLICE, 1, 5)
                                    .setMaxLevel(5) // +12.5 undead damage
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .saveSalvage(output, prefix(ModifierIds.smite, upgradeSalvage))
                                    .save(output, prefix(ModifierIds.smite, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.baneOfSssss)
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInput(Items.FERMENTED_SPIDER_EYE, 1, 15)
                                    .setMaxLevel(5) // +12.5 spider damage
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .saveSalvage(output, prefix(ModifierIds.baneOfSssss, upgradeSalvage))
                                    .save(output, prefix(ModifierIds.baneOfSssss, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.antiaquatic)
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInput(Items.PUFFERFISH, 1, 5)
                                    .setMaxLevel(5) // +12.5 fish damage
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .saveSalvage(output, prefix(ModifierIds.antiaquatic, upgradeSalvage))
                                    .save(output, prefix(ModifierIds.antiaquatic, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.cooling)
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInput(Items.PRISMARINE_CRYSTALS, 1, 25)
                                    .setMaxLevel(5) // +10 fire mob damage
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .saveSalvage(output, prefix(ModifierIds.cooling, upgradeSalvage))
                                    .save(output, prefix(ModifierIds.cooling, upgradeFolder));
    // killager uses both types of lapis
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.killager)
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInput(Tags.Items.GEMS_LAPIS, 1, 45)
                                    .setMaxLevel(5) // +12.5 illager damage
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .saveSalvage(output, prefix(ModifierIds.killager, upgradeSalvage))
                                    .save(output, wrap(ModifierIds.killager, upgradeFolder, "_from_dust"));
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.killager)
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInput(Tags.Items.STORAGE_BLOCKS_LAPIS, 9, 45)
                                    .setMaxLevel(5) // +12.5 illager damage
                                    .disallowCrystal()
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .save(output, wrap(ModifierIds.killager, upgradeFolder, "_from_block"));
    // sharpness can use shards or blocks
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.sharpness)
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInput(Tags.Items.GEMS_QUARTZ, 1, 36)
                                    .setMaxLevel(5) // +5 damage
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .saveSalvage(output, prefix(ModifierIds.sharpness, upgradeSalvage))
                                    .save(output, wrap(ModifierIds.sharpness, upgradeFolder, "_from_shard"));
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.sharpness)
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInput(Items.QUARTZ_BLOCK, 4, 36)
                                    .setLeftover(new ItemStack(Items.QUARTZ))
                                    .setMaxLevel(5)
                                    .disallowCrystal()
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .save(output, wrap(ModifierIds.sharpness, upgradeFolder, "_from_block"));
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.sweeping)
                                    .setTools(TinkerTags.Items.SWORD)
                                    .setInput(Blocks.CHAIN, 1, 5) // 5% per chain, costing 55 nuggets, or just above 6 ingots
                                    .setMaxLevel(3) // goes 25%, 50%, 75%
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .saveSalvage(output, prefix(ModifierIds.sweeping, upgradeSalvage))
                                    .save(output, prefix(ModifierIds.sweeping, upgradeFolder));
    // swiftstrike works on blocks too, we are nice
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.swiftstrike)
                                    .setTools(TinkerTags.Items.MELEE_WEAPON)
                                    .setInput(Items.AMETHYST_SHARD, 1, 72)
                                    .setMaxLevel(5)
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .saveSalvage(output, prefix(ModifierIds.swiftstrike, upgradeSalvage))
                                    .save(output, wrap(ModifierIds.swiftstrike, upgradeFolder, "_from_shard"));
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.swiftstrike)
                                    .setTools(TinkerTags.Items.MELEE_WEAPON)
                                    .setInput(Blocks.AMETHYST_BLOCK, 4, 72)
                                    .setLeftover(new ItemStack(Items.AMETHYST_SHARD))
                                    .setMaxLevel(5)
                                    .disallowCrystal()
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .save(output, wrap(ModifierIds.swiftstrike, upgradeFolder, "_from_block"));

    /*
     * ranged
     */
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.power)
      .setTools(TinkerTags.Items.RANGED_POWER)
      .setInput(TinkerWorld.ichorGeode.asItem(), 1, 72)
      .setSlots(SlotType.UPGRADE, 1)
      .setMaxLevel(5)
      .saveSalvage(output, prefix(ModifierIds.power, upgradeSalvage))
      .save(output, prefix(ModifierIds.power, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.quickCharge)
      .setTools(TinkerTags.Items.RANGED_QUICK_CHARGE)
      .setInput(Items.MAGMA_CREAM, 1, 5)
      .setMaxLevel(4)
      .setSlots(SlotType.UPGRADE, 1)
      .saveSalvage(output, prefix(ModifierIds.quickCharge, upgradeSalvage))
      .save(output, prefix(ModifierIds.quickCharge, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.trueshot)
                                    .setInput(Items.TARGET, 1, 10)
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .setMaxLevel(3)
                                    .setTools(TinkerTags.Items.RANGED)
                                    .saveSalvage(output, prefix(ModifierIds.trueshot, upgradeSalvage))
                                    .save(output, prefix(ModifierIds.trueshot, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.blindshot)
                                    .setInput(Items.DIRT, 1, 10)
                                    .setTools(TinkerTags.Items.RANGED)
                                    .save(output, prefix(ModifierIds.blindshot, slotlessFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.punch)
                         .addInput(Items.PISTON)
                         .addInput(TinkerWorld.slime.get(SlimeType.SKY))
                         .setMaxLevel(3) // vanilla caps at 2, we want to go a bit beyond that, but it becomes broken too high on fishing rods
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(TinkerTags.Items.LAUNCHERS)
                         .saveSalvage(output, prefix(ModifierIds.punch, upgradeSalvage))
                         .save(output, prefix(ModifierIds.punch, upgradeFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.arrowPierce)
                         .addInput(Items.POINTED_DRIPSTONE)
                         .addInput(Items.POINTED_DRIPSTONE)
                         .addInput(Items.POINTED_DRIPSTONE)
                         .setMaxLevel(4) // same max as vanilla
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(TinkerTags.Items.BOWS) // salvage for old recipe
                         .saveSalvage(output, prefix(ModifierIds.arrowPierce, upgradeSalvage))
                         .setTools(TinkerTags.Items.CROSSBOWS)
                         .save(output, prefix(ModifierIds.arrowPierce, upgradeFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.bounce)
      .addInput(Items.PISTON)
      .addInput(TinkerWorld.slime.get(SlimeType.ICHOR))
      .setMaxLevel(3) // 7 bounces is more than you will ever need
      .setSlots(SlotType.UPGRADE, 1)
      .setTools(TinkerTags.Items.RANGED_BOUNCE)
      .saveSalvage(output, prefix(ModifierIds.bounce, upgradeSalvage))
      .save(output, prefix(ModifierIds.bounce, upgradeFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.freezing)
                         .addInput(Items.POWDER_SNOW_BUCKET)
                         .setMaxLevel(3)
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(ingredientFromTags(TinkerTags.Items.MELEE, TinkerTags.Items.BOWS, TinkerTags.Items.FISHING_RODS, TinkerTags.Items.WORN_ARMOR, TinkerTags.Items.SHIELDS))
                         .saveSalvage(output, prefix(ModifierIds.freezing, upgradeSalvage))
                         .save(output, prefix(ModifierIds.freezing, upgradeFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.bulkQuiver)
                         .addInput(Items.LEATHER)
                         .addInput(TinkerWorld.skySlimeVine)
                         .addInput(Items.LEATHER)
                         .addInput(TinkerWorld.skySlimeVine)
                         .addInput(TinkerWorld.skySlimeVine)
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(TinkerTags.Items.BOWS)
                         .saveSalvage(output, prefix(ModifierIds.bulkQuiver, abilitySalvage))
                         .save(output, prefix(ModifierIds.bulkQuiver, abilityFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.trickQuiver)
                         .addInput(TinkerModifiers.silkyCloth)
                         .addInput(TinkerWorld.skySlimeVine)
                         .addInput(TinkerModifiers.silkyCloth)
                         .addInput(TinkerWorld.skySlimeVine)
                         .addInput(TinkerWorld.skySlimeVine)
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(TinkerTags.Items.BOWS)
                         .saveSalvage(output, prefix(ModifierIds.trickQuiver, abilitySalvage))
                         .setTools(IntersectionIngredient.of(Ingredient.of(TinkerTags.Items.BOWS), Ingredient.of(TinkerTags.Items.INTERACTABLE)))
                         .save(output, prefix(ModifierIds.trickQuiver, abilityFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.sliver)
      .addInput(TinkerModifiers.silkyCloth)
      .addInput(TinkerWorld.skySlimeVine)
      .addInput(TinkerModifiers.silkyCloth)
      .addInput(TinkerWorld.skySlimeVine)
      .addInput(TinkerWorld.skySlimeVine)
      .setSlots(SlotType.ABILITY, 1)
      .setTools(Ingredient.of(TinkerTags.Items.STAFFS))
      .saveSalvage(output, prefix(ModifierIds.sliver, abilitySalvage))
      .save(output, prefix(ModifierIds.sliver, abilityFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.ballista)
      .addInput(TinkerMaterials.hepatizon.getIngotTag())
      .addInput(Items.CHAIN)
      .addInput(TinkerMaterials.hepatizon.getIngotTag())
      .setSlots(SlotType.ABILITY, 1)
      .setMaxLevel(1).checkTraitLevel()
      .setTools(TinkerTags.Items.BALLISTAS)
      .saveSalvage(output, prefix(ModifierIds.ballista, abilitySalvage))
      .save(output, prefix(ModifierIds.ballista, abilityFolder));
    BiConsumer<ItemLike,String> crystalshotRecipe = (item, variant) ->
      SwappableModifierRecipeBuilder.modifier(ModifierIds.crystalshot, variant)
                                    .addInput(item)
                                    .addInput(Items.BLAZE_ROD)
                                    .addInput(item)
                                    .addInput(TinkerMaterials.manyullyn.getIngotTag())
                                    .addInput(TinkerMaterials.manyullyn.getIngotTag())
                                    .setTools(TinkerTags.Items.BOWS)
                                    .setSlots(SlotType.ABILITY, 1)
                                    .save(output, wrap(ModifierIds.crystalshot, abilityFolder, "_" + variant));
    crystalshotRecipe.accept(Items.AMETHYST_CLUSTER, "amethyst");
    crystalshotRecipe.accept(TinkerWorld.earthGeode.getBud(BudSize.CLUSTER), "earthslime");
    crystalshotRecipe.accept(TinkerWorld.skyGeode.getBud(BudSize.CLUSTER), "skyslime");
    crystalshotRecipe.accept(TinkerWorld.ichorGeode.getBud(BudSize.CLUSTER), "ichor");
    crystalshotRecipe.accept(TinkerWorld.enderGeode.getBud(BudSize.CLUSTER), "enderslime");
    crystalshotRecipe.accept(Items.NETHER_QUARTZ_ORE, "quartz");
    SwappableModifierRecipeBuilder.modifier(ModifierIds.crystalshot, "random")
                                  .addInput(Ingredient.of(TinkerWorld.earthGeode.getBud(BudSize.CLUSTER), TinkerWorld.skyGeode.getBud(BudSize.CLUSTER)))
                                  .addInput(Ingredient.of(Items.AMETHYST_CLUSTER, Items.NETHER_QUARTZ_ORE))
                                  .addInput(Ingredient.of(TinkerWorld.ichorGeode.getBud(BudSize.CLUSTER), TinkerWorld.enderGeode.getBud(BudSize.CLUSTER)))
                                  .addInput(TinkerMaterials.manyullyn.getIngotTag())
                                  .addInput(TinkerMaterials.manyullyn.getIngotTag())
                                  .setTools(TinkerTags.Items.BOWS)
                                  .setSlots(SlotType.ABILITY, 1)
                                  .allowCrystal() // random is the coolest, and happens to be the easiest to enable
                                  .save(output, wrap(ModifierIds.crystalshot, abilityFolder, "_random"));
    ModifierRecipeBuilder.modifier(ModifierIds.crystalshot)
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(TinkerTags.Items.BOWS)
                         .saveSalvage(output, prefix(ModifierIds.crystalshot, abilitySalvage));
    ModifierRecipeBuilder.modifier(ModifierIds.barebow)
      .setTools(TinkerTags.Items.BOWS)
      .addInput(Tags.Items.STRINGS)
      .addInput(Tags.Items.RODS_WOODEN)
      .addInput(Tags.Items.STRINGS)
      .setMaxLevel(1)
      .save(output, prefix(ModifierIds.barebow, slotlessFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.multishot)
                         .addInput(Items.PISTON)
                         .addInput(TinkerMaterials.steel.getIngotTag())
                         .addInput(Items.PISTON)
                         .addInput(SlimeType.ICHOR.getSlimeballTag())
                         .addInput(SlimeType.ICHOR.getSlimeballTag())
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(TinkerTags.Items.BOWS) // allow salvaging multishot from an older bow
                         .saveSalvage(output, prefix(ModifierIds.multishot, abilitySalvage))
                         .setTools(TinkerTags.Items.CROSSBOWS) // crossbow exclusive now
                         .save(output, prefix(ModifierIds.multishot, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.sinistral)
                         .addInput(TinkerMaterials.slimesteel.getIngotTag())
                         .addInput(Items.NAUTILUS_SHELL)
                         .addInput(TinkerMaterials.slimesteel.getIngotTag())
                         .setMaxLevel(1).checkTraitLevel()
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(IntersectionIngredient.of(Ingredient.of(TinkerTags.Items.CROSSBOWS), Ingredient.of(TinkerTags.Items.INTERACTABLE_LEFT))) // this is the same recipes as dual wielding, but crossbows do not interact on left
                         .saveSalvage(output, prefix(TinkerModifiers.sinistral, upgradeSalvage))
                         .save(output, prefix(TinkerModifiers.sinistral, upgradeFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.scope)
      .setTools(TinkerTags.Items.INTERACTABLE_CHARGE)
      .addInput(Items.SUGAR)
      .addInput(Items.SPYGLASS)
      .addInput(Items.SUGAR)
      .setSlots(SlotType.UPGRADE, 1)
      .setMaxLevel(1).checkTraitLevel()
      .saveSalvage(output, prefix(ModifierIds.scope, upgradeSalvage))
      .save(output, prefix(ModifierIds.scope, upgradeFolder));

    // fishing
    ModifierRecipeBuilder.modifier(ModifierIds.lure)
      .setTools(TinkerTags.Items.FISHING_RODS)
      .addInput(TinkerCommons.cheeseIngot)
      .addInput(TinkerCommons.cheeseIngot)
      .addInput(TinkerCommons.cheeseIngot)
      .setSlots(SlotType.UPGRADE, 1)
      .setMaxLevel(3)
      .saveSalvage(output, prefix(ModifierIds.lure, upgradeSalvage))
      .save(output, prefix(ModifierIds.lure, upgradeFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.grapple)
      .setTools(TinkerTags.Items.FISHING_RODS)
      .addInput(Items.CHAIN)
      .addInput(Items.CHAIN)
      .addInput(TinkerMaterials.slimesteel.getIngotTag())
      .setSlots(SlotType.ABILITY, 1)
      .setMaxLevel(1).checkTraitLevel()
      .saveSalvage(output, prefix(ModifierIds.grapple, abilitySalvage))
      .save(output, prefix(ModifierIds.grapple, abilityFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.drillAttack)
      // allow on anything that might get springing, flinging, or grapple
      .setTools(ingredientFromTags(TinkerTags.Items.INTERACTABLE_CHARGE, TinkerTags.Items.FISHING_RODS))
      .addInput(TinkerMaterials.blazingBone)
      .addInput(Items.POINTED_DRIPSTONE)
      .addInput(TinkerMaterials.blazingBone)
      .setSlots(SlotType.ABILITY, 1)
      .setMaxLevel(1).checkTraitLevel()
      .saveSalvage(output, prefix(ModifierIds.drillAttack, abilitySalvage))
      .save(output, prefix(ModifierIds.drillAttack, abilityFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.collecting)
      .setTools(TinkerTags.Items.FISHING_RODS)
      .addInput(Blocks.HOPPER)
      .setSlots(SlotType.UPGRADE, 1)
      .setMaxLevel(1).checkTraitLevel()
      .saveSalvage(output, prefix(ModifierIds.collecting, upgradeSalvage))
      .save(output, prefix(ModifierIds.collecting, upgradeFolder));

    // throwing
    Ingredient bowLimb = MaterialIngredient.of(TinkerToolParts.bowLimb.get()).toVanilla();
    ModifierRecipeBuilder.modifier(ModifierIds.throwing)
      .setTools(IntersectionIngredient.of(
        Ingredient.of(TinkerTags.Items.DURABILITY),
        Ingredient.of(TinkerTags.Items.INTERACTABLE_CHARGE),
        ingredientFromTags(TinkerTags.Items.MELEE_WEAPON, TinkerTags.Items.HARVEST)
      ))
      .addInput(bowLimb)
      .addInput(TinkerMaterials.cinderslime.getIngotTag())
      .addInput(MaterialIngredient.of(TinkerToolParts.bowGrip.get()))
      .setSlots(SlotType.ABILITY, 1)
      .setMaxLevel(1).checkTraitLevel()
      .saveSalvage(output, prefix(ModifierIds.throwing, abilitySalvage))
      .save(output, prefix(ModifierIds.throwing, abilityFolder));
    MultilevelModifierRecipeBuilder.modifier(ModifierIds.returning)
      .setTools(ingredientFromTags(TinkerTags.Items.MELEE_WEAPON, TinkerTags.Items.HARVEST))
      .addInput(Items.ENDER_PEARL)
      .addInput(Items.CLOCK)
      .addInput(Items.ENDER_PEARL)
      .addLevel(SlotType.ABILITY, 1, 1)
      .addLevelRange(SlotType.UPGRADE, 1, 2, 4)
      .checkTraitLevel()
      .saveSalvage(output, prefix(ModifierIds.returning, abilitySalvage))
      .save(output, prefix(ModifierIds.returning, abilityFolder));

    /*
     * armor
     */
    // protection
    // all held tools can receive defense slots, so give them something to use it for
    Ingredient protectableTools = ingredientFromTags(TinkerTags.Items.ARMOR, TinkerTags.Items.HELD);
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.meleeProtection)
                                    .setInput(TinkerModifiers.cobaltReinforcement, 1, 5)
                                    .setSlots(SlotType.DEFENSE, 1)
                                    .setTools(protectableTools)
                                    .saveSalvage(output, prefix(ModifierIds.meleeProtection, defenseSalvage))
                                    .save(output, prefix(ModifierIds.meleeProtection, defenseFolder));
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.projectileProtection)
                                    .setInput(TinkerModifiers.ironReinforcement, 1, 5)
                                    .setSlots(SlotType.DEFENSE, 1)
                                    .setTools(protectableTools)
                                    .saveSalvage(output, prefix(ModifierIds.projectileProtection, defenseSalvage))
                                    .save(output, prefix(ModifierIds.projectileProtection, defenseFolder));
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.blastProtection)
                                    .setInput(TinkerModifiers.obsidianReinforcement, 1, 5)
                                    .setSlots(SlotType.DEFENSE, 1)
                                    .setTools(protectableTools)
                                    .saveSalvage(output, prefix(ModifierIds.blastProtection, defenseSalvage))
                                    .save(output, prefix(ModifierIds.blastProtection, defenseFolder));
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.magicProtection)
                                    .setInput(TinkerModifiers.goldReinforcement, 1, 5)
                                    .setSlots(SlotType.DEFENSE, 1)
                                    .setTools(protectableTools)
                                    .saveSalvage(output, prefix(ModifierIds.magicProtection, defenseSalvage))
                                    .save(output, prefix(ModifierIds.magicProtection, defenseFolder));
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.fireProtection)
                                    .setInput(TinkerModifiers.searedReinforcement, 1, 5)
                                    .setSlots(SlotType.DEFENSE, 1)
                                    .setTools(protectableTools)
                                    .saveSalvage(output, prefix(ModifierIds.fireProtection, defenseSalvage))
                                    .save(output, prefix(ModifierIds.fireProtection, defenseFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.protection)
                         .addInput(TinkerModifiers.goldReinforcement)
                         .addInput(TinkerModifiers.searedReinforcement)
                         .addInput(TinkerModifiers.obsidianReinforcement)
                         .addInput(TinkerModifiers.ironReinforcement)
                         .addInput(TinkerModifiers.cobaltReinforcement)
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(TinkerTags.Items.ARMOR)
                         .setMaxLevel(1)
                         .saveSalvage(output, prefix(ModifierIds.protection, abilitySalvage))
                         .save(output, prefix(ModifierIds.protection, abilityFolder));
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.blockade)
      .setInput(TinkerCommons.obsidianPane, 1, 10)
      .setSlots(SlotType.UPGRADE, 1)
      .setTools(IntersectionIngredient.of(Ingredient.of(TinkerTags.Items.INTERACTABLE_CHARGE), Ingredient.of(TinkerTags.Items.DURABILITY)))
      .setMaxLevel(3)
      .saveSalvage(output, prefix(ModifierIds.blockade, upgradeSalvage))
      .save(output, prefix(ModifierIds.blockade, upgradeFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.boundless)
      .addInput(TinkerCommons.obsidianPane, 4)
      .addInput(Items.WRITABLE_BOOK)
      .addInput(TinkerCommons.obsidianPane, 4)
      .addInput(TinkerWorld.ichorGeode, 2)
      .addInput(TinkerWorld.ichorGeode, 2)
      .setSlots(SlotType.ABILITY, 1)
      .setTools(TinkerTags.Items.SHIELDS)
      .setMaxLevel(1)
      .saveSalvage(output, prefix(ModifierIds.boundless, abilitySalvage))
      .save(output, prefix(ModifierIds.boundless, abilityFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.knockbackResistance)
                         .setTools(TinkerTags.Items.ARMOR)
                         .addInput(SizedIngredient.fromItems(Blocks.ANVIL, Blocks.CHIPPED_ANVIL, Blocks.DAMAGED_ANVIL))
                         .setSlots(SlotType.DEFENSE, 1)
                         .setMaxLevel(1)
                         .saveSalvage(output, prefix(ModifierIds.knockbackResistance, defenseSalvage))
                         .save(output, prefix(ModifierIds.knockbackResistance, defenseFolder));
    //noinspection removal
    ModifierRecipeBuilder.modifier(TinkerModifiers.golden)
                         .addInput(Tags.Items.INGOTS_GOLD)
                         .addInput(Tags.Items.INGOTS_GOLD)
                         .addInput(Tags.Items.INGOTS_GOLD)
                         .setSlots(SlotType.DEFENSE, 1)
                         .setMaxLevel(1).checkTraitLevel()
                         .setTools(TinkerTags.Items.WORN_ARMOR) // allow salvage on all worn armor
                         .saveSalvage(output, prefix(TinkerModifiers.golden, defenseSalvage))
                         .setTools(TinkerTags.Items.GOLDEN_ARMOR)
                         .save(withCondition(output, new TagFilledCondition<>(TinkerTags.Items.GOLDEN_ARMOR)), prefix(TinkerModifiers.golden, defenseFolder));
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.turtleShell)
                                    .setInput(Items.TURTLE_SCUTE, 1, 5)
                                    .setSlots(SlotType.DEFENSE, 1)
                                    .setTools(TinkerTags.Items.ARMOR)
                                    .saveSalvage(output, prefix(ModifierIds.turtleShell, defenseSalvage))
                                    .save(output, prefix(ModifierIds.turtleShell, defenseFolder));
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.shulking)
                                    .setInput(Items.SHULKER_SHELL, 1, 3)
                                    .setSlots(SlotType.DEFENSE, 1)
                                    .setTools(TinkerTags.Items.ARMOR)
                                    .saveSalvage(output, prefix(ModifierIds.shulking, defenseSalvage))
                                    .save(output, prefix(ModifierIds.shulking, defenseFolder));
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.dragonborn)
                                    .setInput(TinkerModifiers.dragonScale, 1, 5)
                                    .setSlots(SlotType.DEFENSE, 1)
                                    .setTools(TinkerTags.Items.ARMOR)
                                    .saveSalvage(output, prefix(ModifierIds.dragonborn, defenseSalvage))
                                    .save(output, prefix(ModifierIds.dragonborn, defenseFolder));
    // 3 each for chest and legs, 2 each for boots and helmet, leads to 10 total
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.revitalizing)
                                    .setTools(ingredientFromTags(TinkerTags.Items.WORN_ARMOR)) // revitalizing would suck on an item you constantly change
                                    .setInput(TinkerCommons.jeweledApple, 1, 2)
                                    .setSlots(SlotType.DEFENSE, 1)
                                    .saveSalvage(output, prefix(ModifierIds.revitalizing, defenseSalvage))
                                    .save(output, prefix(ModifierIds.revitalizing, defenseFolder));

    // upgrade - counterattack
    Ingredient wornOrShield = ingredientFromTags(TinkerTags.Items.WORN_ARMOR, TinkerTags.Items.SHIELDS); // held armor may include things that cannot block
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.thorns)
                                    .setTools(wornOrShield)
                                    .setInput(Blocks.CACTUS, 1, 25)
                                    .setMaxLevel(3)
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .saveSalvage(output, prefix(ModifierIds.thorns, upgradeSalvage))
                                    .save(output, prefix(ModifierIds.thorns, upgradeFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.springy)
                         .setTools(wornOrShield)
                         .addInput(Items.PISTON)
                         .addInput(TinkerWorld.slime.get(SlimeType.ICHOR))
                         .setSlots(SlotType.UPGRADE, 1)
                         .setMaxLevel(3)
                         .saveSalvage(output, prefix(ModifierIds.springy, upgradeSalvage))
                         .save(output, prefix(ModifierIds.springy, upgradeFolder));
    // upgrade - helmet
    ModifierRecipeBuilder.modifier(ModifierIds.respiration)
                         .setTools(TinkerTags.Items.HELMETS)
                         .addInput(ItemTags.FISHES)
                         .addInput(Tags.Items.GLASS_BLOCKS_COLORLESS)
                         .addInput(ItemTags.FISHES)
                         .addInput(Items.KELP)
                         .addInput(Items.KELP)
                         .setMaxLevel(3)
                         .setSlots(SlotType.UPGRADE, 1)
                         .saveSalvage(output, prefix(ModifierIds.respiration, upgradeSalvage))
                         .save(output, prefix(ModifierIds.respiration, upgradeFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.itemFrame)
                         .setTools(TinkerTags.Items.HELMETS)
                         .addInput(Ingredient.of(Arrays.stream(FrameType.values())
                                                               .filter(type -> type != FrameType.CLEAR)
                                                               .map(type -> new ItemStack(TinkerGadgets.itemFrame.get(type)))))
                         .setSlots(SlotType.UPGRADE, 1)
                         .saveSalvage(output, prefix(TinkerModifiers.itemFrame, upgradeSalvage))
                         .save(output, prefix(TinkerModifiers.itemFrame, upgradeFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.minimap)
      .setTools(TinkerTags.Items.HELMETS)
      .addInput(Items.COMPASS)
      .addInput(Tags.Items.SLIMEBALLS)
      .addInput(Items.PAPER)
      .setSlots(SlotType.UPGRADE, 1)
      .saveSalvage(output, prefix(ModifierIds.minimap, upgradeSalvage))
      .save(output, prefix(ModifierIds.minimap, upgradeFolder));
    // upgrade - leggings
    hasteRecipes(output, ModifierIds.speedy, Ingredient.of(TinkerTags.Items.LEGGINGS), 3, upgradeFolder, upgradeSalvage);
    // leaping changes slot type on level 2
    MultilevelIncrementalModifierRecipeBuilder.modifier(ModifierIds.leaping)
      .setTools(TinkerTags.Items.LEGGINGS)
      .setInput(TinkerWorld.skyGeode, 1, 36)
      .addLevelRange(SlotType.UPGRADE, 1, 1, 1)
      .addLevelRange(SlotType.ABILITY, 1, 2, 2)
      .saveSalvage(output, prefix(ModifierIds.leaping, salvageFolder))
      .save(output, wrap(ModifierIds.leaping, upgradeFolder, "_from_crystal"));
    MultilevelIncrementalModifierRecipeBuilder.modifier(ModifierIds.leaping)
      .setTools(TinkerTags.Items.LEGGINGS)
      .setInput(TinkerWorld.skyGeode.getBlock(), 4, 36)
      .setLeftover(TinkerWorld.skyGeode)
      .addLevelRange(SlotType.UPGRADE, 1, 1, 1)
      .addLevelRange(SlotType.ABILITY, 1, 2, 2)
      .disallowCrystal()
      .save(output, wrap(ModifierIds.leaping, upgradeFolder, "_from_block"));
    ModifierRecipeBuilder.modifier(ModifierIds.stepUp)
                         .setTools(TinkerTags.Items.LEGGINGS)
                         .addInput(Items.LEATHER)
                         .addInput(Items.GOLDEN_CARROT)
                         .addInput(Items.LEATHER)
                         .addInput(Items.SCAFFOLDING)
                         .addInput(Items.SCAFFOLDING)
                         .setSlots(SlotType.UPGRADE, 1)
                         .setMaxLevel(2)
                         .saveSalvage(output, prefix(ModifierIds.stepUp, upgradeSalvage))
                         .save(output, prefix(ModifierIds.stepUp, upgradeFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.swiftSneak)
                         .setTools(TinkerTags.Items.LEGGINGS)
                         .addInput(Blocks.SCULK_SENSOR)
                         .setMaxLevel(5)
                         .setSlots(SlotType.UPGRADE, 1)
                         .saveSalvage(output, prefix(ModifierIds.swiftSneak, upgradeSalvage))
                         .save(output, prefix(ModifierIds.swiftSneak, upgradeFolder));

    // upgrade - boots
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.featherFalling)
                                    .setTools(TinkerTags.Items.BOOTS)
                                    .setInput(Items.FEATHER, 1, 25) // 1% per feather
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .setMaxLevel(2)
                                    .saveSalvage(output, prefix(ModifierIds.featherFalling, upgradeSalvage))
                                    .save(output, prefix(ModifierIds.featherFalling, upgradeFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.longFall)
      .setTools(TinkerTags.Items.BOOTS)
      .addInput(Items.PISTON)
      .addInput(Items.PHANTOM_MEMBRANE)
      .addInput(Items.PISTON)
      .addInput(TinkerWorld.slime.get(SlimeType.ICHOR))
      .addInput(TinkerWorld.slime.get(SlimeType.ICHOR))
      .setSlots(SlotType.UPGRADE, 1)
      .setMaxLevel(1).checkTraitLevel()
      .saveSalvage(output, prefix(ModifierIds.longFall, upgradeSalvage))
      .save(output, prefix(ModifierIds.longFall, upgradeFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.soulspeed)
                         .setTools(TinkerTags.Items.BOOTS)
                         .addInput(Items.MAGMA_BLOCK)
                         .addInput(Items.CRYING_OBSIDIAN)
                         .addInput(Items.MAGMA_BLOCK)
                         .setSlots(SlotType.UPGRADE, 1)
                         .setMaxLevel(3)
                         .saveSalvage(output, prefix(ModifierIds.soulspeed, upgradeSalvage))
                         .save(output, prefix(ModifierIds.soulspeed, upgradeFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.depthStrider)
                         .setTools(TinkerTags.Items.BOOTS)
                         .addInput(ItemTags.FISHES)
                         .addInput(Blocks.PRISMARINE_BRICKS)
                         .addInput(ItemTags.FISHES)
                         .setSlots(SlotType.UPGRADE, 1)
                         .setMaxLevel(3)
                         .saveSalvage(output, prefix(ModifierIds.depthStrider, upgradeSalvage))
                         .save(output, prefix(ModifierIds.depthStrider, upgradeFolder));

    // upgrade - all
    ModifierRecipeBuilder.modifier(ModifierIds.ricochet)
                         .setTools(wornOrShield)
                         .addInput(Items.PISTON)
                         .addInput(TinkerWorld.slime.get(SlimeType.SKY))
                         .setSlots(SlotType.UPGRADE, 1)
                         .setMaxLevel(2) // 2 per piece gives +160% total
                         .saveSalvage(output, prefix(ModifierIds.ricochet, upgradeSalvage))
                         .save(output, prefix(ModifierIds.ricochet, upgradeFolder));

    // armor ability
    // helmet
    ModifierRecipeBuilder.modifier(ModifierIds.zoom)
                         .setTools(ingredientFromTags(TinkerTags.Items.HELMETS, TinkerTags.Items.INTERACTABLE_CHARGE))
                         .addInput(Tags.Items.STRINGS)
                         .addInput(Items.SPYGLASS)
                         .addInput(Tags.Items.STRINGS)
                         .setSlots(SlotType.UPGRADE, 1)
                         .setMaxLevel(1).checkTraitLevel()
                         .saveSalvage(output, prefix(ModifierIds.zoom, upgradeSalvage))
                         .save(output, prefix(ModifierIds.zoom, upgradeFolder));
    Ingredient tanks = NoContainerIngredient.of(TinkerTags.Items.TANKS);
    ModifierRecipeBuilder.modifier(ModifierIds.slurping)
                         .addInput(Items.GLASS_BOTTLE)
                         .addInput(tanks)
                         .addInput(Items.GLASS_BOTTLE)
                         .addInput(Tags.Items.INGOTS_COPPER)
                         .addInput(Tags.Items.INGOTS_COPPER)
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(ingredientFromTags(TinkerTags.Items.HELMETS, TinkerTags.Items.INTERACTABLE_CHARGE))
                         .saveSalvage(output, prefix(ModifierIds.slurping, abilitySalvage))
                         .save(output, prefix(ModifierIds.slurping, abilityFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.aquaAffinity)
                         .addInput(Blocks.PRISMARINE_BRICKS)
                         .addInput(Items.HEART_OF_THE_SEA)
                         .addInput(Blocks.PRISMARINE_BRICKS)
                         .addInput(Blocks.DARK_PRISMARINE)
                         .addInput(Blocks.DARK_PRISMARINE)
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(TinkerTags.Items.HELMETS)
                         .setMaxLevel(1).checkTraitLevel()
                         .saveSalvage(output, prefix(ModifierIds.aquaAffinity, abilitySalvage))
                         .save(output, prefix(ModifierIds.aquaAffinity, abilityFolder));
    // chestplate
    ModifierRecipeBuilder.modifier(TinkerModifiers.ambidextrous)
                         .setTools(TinkerTags.Items.UNARMED)
                         .addInput(Items.LEATHER)
                         .addInput(Tags.Items.GEMS_DIAMOND)
                         .addInput(Items.LEATHER)
                         .addInput(Tags.Items.STRINGS)
                         .addInput(Tags.Items.STRINGS)
                         .setMaxLevel(1).checkTraitLevel()
                         .setSlots(SlotType.ABILITY, 1)
                         .saveSalvage(output, prefix(TinkerModifiers.ambidextrous, abilitySalvage))
                         .save(output, prefix(TinkerModifiers.ambidextrous, abilityFolder));
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.strength)
                                    .setTools(TinkerTags.Items.CHESTPLATES)
                                    .setInput(TinkerWorld.ichorGeode.asItem(), 1, 72)
                                    .setSlots(SlotType.ABILITY, 1)
                                    .setMaxLevel(2)
                                    .saveSalvage(output, prefix(ModifierIds.strength, abilitySalvage))
                                    .save(output, prefix(ModifierIds.strength, abilityFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.wings)
                         .setTools(TinkerTags.Items.CHESTPLATES)
                         .setMaxLevel(1).checkTraitLevel()
                         .addInput(Items.ELYTRA)
                         .setSlots(SlotType.ABILITY, 2)
                         .saveSalvage(output, prefix(ModifierIds.wings, abilitySalvage))
                         .save(output, prefix(ModifierIds.wings, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.sleeves)
      .setTools(TinkerTags.Items.CHESTPLATES)
      .addInput(TinkerModifiers.silkyCloth)
      .addInput(TinkerMaterials.cinderslime.getIngotTag())
      .addInput(TinkerModifiers.silkyCloth)
      .setSlots(SlotType.UPGRADE, 1)
      .setMaxLevel(3)
      .saveSalvage(output, prefix(TinkerModifiers.sleeves, upgradeSalvage))
      .save(output, prefix(TinkerModifiers.sleeves, upgradeFolder));

    // leggings
    ModifierRecipeBuilder.modifier(ModifierIds.pockets)
                         .setTools(TinkerTags.Items.LEGGINGS)
                         .addInput(Items.SHULKER_SHELL)
                         .addInput(Tags.Items.INGOTS_IRON)
                         .addInput(Items.SHULKER_SHELL)
                         .addInput(Items.LEATHER)
                         .addInput(Items.LEATHER)
                         .setSlots(SlotType.ABILITY, 1)
                         .setMaxLevel(2)
                         .saveSalvage(output, prefix(ModifierIds.pockets, abilitySalvage))
                         .save(output, prefix(ModifierIds.pockets, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.shieldStrap)
                         .addInput(TinkerWorld.skySlimeVine)
                         .addInput(TinkerMaterials.slimesteel.getIngotTag())
                         .addInput(TinkerWorld.skySlimeVine)
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(TinkerTags.Items.LEGGINGS)
                         .saveSalvage(output, prefix(TinkerModifiers.shieldStrap, upgradeSalvage))
                         .save(output, prefix(TinkerModifiers.shieldStrap, upgradeFolder));
    BiConsumer<Integer,TagKey<Item>> toolBeltRecipe = (level, ingot) -> {
      ModifierRecipeBuilder builder = ModifierRecipeBuilder
        .modifier(ModifierIds.toolBelt)
        .addInput(Items.LEATHER)
        .addInput(ingot)
        .addInput(Items.LEATHER)
        .setTools(TinkerTags.Items.LEGGINGS)
        .exactLevel(level)
        .useSalvageMax();
      if (level == 1) {
        builder.setSlots(SlotType.ABILITY, 1);
        builder.saveSalvage(output, prefix(ModifierIds.toolBelt, abilitySalvage));
      } else {
        builder.disallowCrystal(); // prevent cheesing cost by extracting level 1
      }
      builder.save(output, wrap(ModifierIds.toolBelt, abilityFolder, "_" + level));
    };
    toolBeltRecipe.accept(1, Tags.Items.INGOTS_IRON);
    toolBeltRecipe.accept(2, Tags.Items.INGOTS_GOLD);
    toolBeltRecipe.accept(3, TinkerMaterials.roseGold.getIngotTag());
    toolBeltRecipe.accept(4, TinkerMaterials.cobalt.getIngotTag());
    toolBeltRecipe.accept(5, TinkerMaterials.hepatizon.getIngotTag());
    toolBeltRecipe.accept(6, TinkerMaterials.manyullyn.getIngotTag());
    ModifierRecipeBuilder.modifier(ModifierIds.soulBelt)
                         .addInput(Items.LEATHER)
                         .addInput(Ingredient.of(Items.RECOVERY_COMPASS))
                         .addInput(Items.LEATHER)
                         .setSlots(SlotType.ABILITY, 1)
                         .setMaxLevel(1)
                         .setTools(TinkerTags.Items.LEGGINGS)
                         .saveSalvage(output, prefix(ModifierIds.soulBelt, abilitySalvage))
                         .save(output, prefix(ModifierIds.soulBelt, abilityFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.workbench)
                         .addInput(Items.LEATHER)
                         .addInput(Blocks.CRAFTING_TABLE)
                         .addInput(Items.LEATHER)
                         .setMaxLevel(1).checkTraitLevel()
                         .setTools(TinkerTags.Items.LEGGINGS)
                         .save(output, prefix(ModifierIds.workbench, upgradeFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.craftingTable)
                         .addInput(Items.LEATHER)
                         .addInput(TinkerTables.craftingStation)
                         .addInput(Items.LEATHER)
                         .setSlots(SlotType.ABILITY, 1)
                         .setMaxLevel(1).checkTraitLevel()
                         .setTools(TinkerTags.Items.LEGGINGS)
                         .saveSalvage(output, prefix(ModifierIds.craftingTable, abilitySalvage))
                         .save(output, prefix(ModifierIds.craftingTable, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.wetting)
                         .addInput(Tags.Items.DUSTS_REDSTONE)
                         .addInput(tanks)
                         .addInput(Tags.Items.DUSTS_REDSTONE)
                         .addInput(Tags.Items.INGOTS_COPPER)
                         .addInput(Tags.Items.INGOTS_COPPER)
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(ingredientFromTags(TinkerTags.Items.LEGGINGS, TinkerTags.Items.SHIELDS))
                         .saveSalvage(output, prefix(TinkerModifiers.wetting, abilitySalvage))
                         .save(output, prefix(TinkerModifiers.wetting, abilityFolder));
    // boots
    ModifierRecipeBuilder.modifier(ModifierIds.doubleJump)
                         .setTools(TinkerTags.Items.BOOTS)
                         .addInput(Items.PISTON)
                         .addInput(TinkerWorld.slime.get(SlimeType.SKY))
                         .addInput(Items.PISTON)
                         .addInput(Items.PHANTOM_MEMBRANE)
                         .addInput(Items.PHANTOM_MEMBRANE)
                         .setSlots(SlotType.ABILITY, 1)
                         .setMaxLevel(2)
                         .saveSalvage(output, prefix(ModifierIds.doubleJump, abilitySalvage))
                         .save(output, prefix(ModifierIds.doubleJump, abilityFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.bouncy)
                         .setTools(TinkerTags.Items.BOOTS)
                         .addInput(TinkerWorld.congealedSlime.get(SlimeType.SKY),   4)
                         .addInput(TinkerWorld.congealedSlime.get(SlimeType.ICHOR), 4)
                         .addInput(TinkerWorld.congealedSlime.get(SlimeType.SKY),   4)
                         .addInput(TinkerWorld.congealedSlime.get(SlimeType.EARTH), 4)
                         .addInput(TinkerWorld.congealedSlime.get(SlimeType.EARTH), 4)
                         .setSlots(SlotType.ABILITY, 1)
                         .setMaxLevel(1).checkTraitLevel()
                         .saveSalvage(output, prefix(ModifierIds.bouncy, abilitySalvage))
                         .save(output, prefix(ModifierIds.bouncy, abilityFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.frostWalker)
                         .setTools(TinkerTags.Items.BOOTS)
                         .addInput(Items.BLUE_ICE)
                         .addInput(TinkerWorld.heads.get(TinkerHeadType.STRAY))
                         .addInput(Items.BLUE_ICE)
                         .addInput(Items.BLUE_ICE)
                         .addInput(Items.BLUE_ICE)
                         .setSlots(SlotType.ABILITY, 1)
                         .setMaxLevel(1)
                         .saveSalvage(output, prefix(ModifierIds.frostWalker, abilitySalvage))
                         .save(output, prefix(ModifierIds.frostWalker, abilityFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.snowdrift)
                         .setTools(TinkerTags.Items.BOOTS)
                         .addInput(Items.SNOW_BLOCK)
                         .addInput(Items.CARVED_PUMPKIN)
                         .addInput(Items.SNOW_BLOCK)
                         .addInput(Items.SNOW_BLOCK)
                         .addInput(Items.SNOW_BLOCK)
                         .setSlots(SlotType.ABILITY, 1)
                         .setMaxLevel(1)
                         .saveSalvage(output, prefix(ModifierIds.snowdrift, abilitySalvage))
                         .save(output, prefix(ModifierIds.snowdrift, abilityFolder));

    // transform ingredients
    Ingredient bootsWithDuraibility = IntersectionIngredient.of(Ingredient.of(TinkerTags.Items.BOOTS), Ingredient.of(TinkerTags.Items.DURABILITY));
    ModifierRecipeBuilder.modifier(ModifierIds.flamewake)
                         .setTools(bootsWithDuraibility)
                         .addInput(Items.FLINT)
                         .addInput(TinkerTags.Items.INGOTS_NETHERITE_SCRAP)
                         .addInput(Items.FLINT)
                         .addInput(Items.FLINT)
                         .addInput(Items.FLINT)
                         .setMaxLevel(1)
                         .setSlots(SlotType.ABILITY, 1)
                         .saveSalvage(output, prefix(ModifierIds.flamewake, abilitySalvage))
                         .save(output, prefix(ModifierIds.flamewake, abilityFolder));

    /*
     * ability
     */
    ModifierRecipeBuilder.modifier(ModifierIds.gilded)
                         .setTools(TinkerTags.Items.BONUS_SLOTS)
                         .addInput(Blocks.GILDED_BLACKSTONE)
                         .setSlots(SlotType.ABILITY, 1)
                         .saveSalvage(output, prefix(ModifierIds.gilded, abilitySalvage))
                         .save(output, prefix(ModifierIds.gilded, abilityFolder));
    // luck is 3 recipes
    // level 1 always requires a slot
    Ingredient luckSupporting = ingredientFromTags(TinkerTags.Items.MELEE_WEAPON, TinkerTags.Items.HARVEST, TinkerTags.Items.LAUNCHERS);
    ModifierRecipeBuilder.modifier(ModifierIds.luck)
                         .setTools(luckSupporting)
                         .addInput(Tags.Items.INGOTS_COPPER)
                         .addInput(SizedIngredient.fromItems(Items.CORNFLOWER, Items.BLUE_ORCHID))
                         .addInput(Tags.Items.INGOTS_COPPER)
                         .addInput(Tags.Items.STORAGE_BLOCKS_LAPIS)
                         .addInput(Tags.Items.STORAGE_BLOCKS_LAPIS)
                         .setMaxLevel(1)
                         .setSlots(SlotType.ABILITY, 1)
                         .allowCrystal()
                         .save(output, wrap(ModifierIds.luck, abilityFolder, "_level_1"));
    ModifierRecipeBuilder.modifier(ModifierIds.luck)
                         .setTools(luckSupporting)
                         .addInput(Tags.Items.INGOTS_GOLD)
                         .addInput(Items.GOLDEN_CARROT)
                         .addInput(Tags.Items.INGOTS_GOLD)
                         .addInput(Tags.Items.ENDER_PEARLS)
                         .addInput(Tags.Items.ENDER_PEARLS)
                         .disallowCrystal() // would allow a cost cheese
                         .exactLevel(2)
                         .save(output, wrap(ModifierIds.luck, abilityFolder, "_level_2"));
    ModifierRecipeBuilder.modifier(ModifierIds.luck)
                         .setTools(luckSupporting)
                         .addInput(TinkerMaterials.roseGold.getIngotTag())
                         .addInput(Items.RABBIT_FOOT)
                         .addInput(TinkerMaterials.roseGold.getIngotTag())
                         .addInput(Tags.Items.GEMS_DIAMOND)
                         .addInput(Items.NAME_TAG)
                         .disallowCrystal() // would allow a cost cheese
                         .exactLevel(3)
                         .save(output, wrap(ModifierIds.luck, abilityFolder, "_level_3"));
    // pants have just one level
    ModifierRecipeBuilder.modifier(ModifierIds.luck)
                         .setTools(TinkerTags.Items.LEGGINGS)
                         .setMaxLevel(1)
                         .setSlots(SlotType.ABILITY, 1)
                         .disallowCrystal() // prevents cheesing cost using luck 1
                         .saveSalvage(output, wrap(ModifierIds.luck, abilitySalvage, "_pants"));
    ModifierRecipeBuilder.modifier(ModifierIds.luck)
                         .setTools(ingredientFromTags(TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST, TinkerTags.Items.LAUNCHERS))
                         .exactLevel(1)
                         .useSalvageMax()
                         .setSlots(SlotType.ABILITY, 1)
                         .saveSalvage(output, prefix(ModifierIds.luck, abilitySalvage));

    // silky: all the cloth
    ModifierRecipeBuilder.modifier(ModifierIds.silky)
                         .addInput(TinkerModifiers.silkyCloth)
                         .addInput(TinkerModifiers.silkyCloth)
                         .addInput(TinkerModifiers.silkyCloth)
                         .addInput(TinkerModifiers.silkyCloth)
                         .addInput(TinkerModifiers.silkyCloth)
                         .setMaxLevel(1).checkTraitLevel()
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(TinkerTags.Items.HARVEST)
                         .saveSalvage(output, prefix(ModifierIds.silky, abilitySalvage))
                         .save(output, prefix(ModifierIds.silky, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.exchanging)
                         .addInput(Items.STICKY_PISTON)
                         .addInput(TinkerMaterials.hepatizon.getIngotTag())
                         .addInput(Items.STICKY_PISTON)
                         .addInput(Tags.Items.ENDER_PEARLS)
                         .addInput(Tags.Items.ENDER_PEARLS)
                         .setMaxLevel(1).checkTraitLevel()
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(TinkerTags.Items.HARVEST)
                         .saveSalvage(output, prefix(TinkerModifiers.exchanging, abilitySalvage))
                         .save(output, prefix(TinkerModifiers.exchanging, abilityFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.autosmelt)
                         .addInput(Tags.Items.RAW_MATERIALS)
                         .addInput(Blocks.BLAST_FURNACE)
                         .addInput(Tags.Items.INGOTS)
                         .addInput(Tags.Items.STORAGE_BLOCKS_COAL)
                         .addInput(Tags.Items.STORAGE_BLOCKS_COAL)
                         .setMaxLevel(1)
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(ingredientFromTags(TinkerTags.Items.HARVEST, TinkerTags.Items.FISHING_RODS))
                         .saveSalvage(output, prefix(ModifierIds.autosmelt, abilitySalvage))
                         .save(output, prefix(ModifierIds.autosmelt, abilityFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.channeling)
      .addInput(Blocks.LIGHTNING_ROD)
      .addInput(Blocks.CREEPER_HEAD)
      .addInput(Blocks.LIGHTNING_ROD)
      .addInput(Blocks.LIGHTNING_ROD)
      .addInput(Blocks.LIGHTNING_ROD)
      .setMaxLevel(1).checkTraitLevel()
      .setSlots(SlotType.ABILITY, 1)
      .setTools(ingredientFromTags(TinkerTags.Items.MELEE_WEAPON, TinkerTags.Items.FISHING_RODS))
      .saveSalvage(output, prefix(ModifierIds.channeling, abilitySalvage))
      .save(output, prefix(ModifierIds.channeling, abilityFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.fins)
      .addInput(ItemTags.FISHES)
      .addInput(Blocks.PRISMARINE_BRICKS)
      .addInput(ItemTags.FISHES)
      .setMaxLevel(1).checkTraitLevel()
      .setSlots(SlotType.UPGRADE, 1)
      .setTools(TinkerTags.Items.MELEE_WEAPON)
      .saveSalvage(output, prefix(ModifierIds.fins, upgradeSalvage))
      .save(output, prefix(ModifierIds.fins, upgradeFolder));

    // fluid stuff
    ModifierRecipeBuilder.modifier(TinkerModifiers.melting)
                         .addInput(Items.BLAZE_ROD)
                         .addInput(Ingredient.of(TinkerSmeltery.searedMelter, TinkerSmeltery.smelteryController, TinkerSmeltery.foundryController))
                         .addInput(Items.BLAZE_ROD)
                         .addInput(Items.LAVA_BUCKET)
                         .addInput(Items.LAVA_BUCKET)
                         .setMaxLevel(1)
                         .checkTraitLevel()
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(ingredientFromTags(TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST))
                         .saveSalvage(output, prefix(TinkerModifiers.melting, abilitySalvage))
                         .save(output, prefix(TinkerModifiers.melting, abilityFolder));
    SizedIngredient faucets = SizedIngredient.fromItems(TinkerSmeltery.searedFaucet, TinkerSmeltery.scorchedFaucet); // no salvage as don't want conversion between seared and scorched
    ModifierRecipeBuilder.modifier(ModifierIds.bucketing)
                         .addInput(faucets)
                         .addInput(Items.BUCKET)
                         .addInput(faucets)
                         .addInput(TinkerMaterials.steel.getIngotTag())
                         .addInput(TinkerMaterials.steel.getIngotTag())
                         .setMaxLevel(1)
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(TinkerTags.Items.INTERACTABLE)
                         .saveSalvage(output, prefix(ModifierIds.bucketing, abilitySalvage))
                         .save(output, prefix(ModifierIds.bucketing, abilityFolder));
    SizedIngredient channels = SizedIngredient.fromItems(TinkerSmeltery.searedChannel, TinkerSmeltery.scorchedChannel);
    ModifierRecipeBuilder.modifier(ModifierIds.spilling)
                         .addInput(channels)
                         .addInput(tanks)
                         .addInput(channels)
                         .addInput(Tags.Items.INGOTS_COPPER)
                         .addInput(Tags.Items.INGOTS_COPPER)
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(TinkerTags.Items.MELEE)
                         .saveSalvage(output, prefix(ModifierIds.spilling, abilitySalvage))
                         .save(output, prefix(ModifierIds.spilling, abilityFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.splashing)
                         .addInput(MantleTags.Items.SPLASH_BOTTLE)
                         .addInput(tanks)
                         .addInput(MantleTags.Items.SPLASH_BOTTLE)
                         .addInput(Tags.Items.INGOTS_COPPER)
                         .addInput(Tags.Items.INGOTS_COPPER)
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(IntersectionIngredient.of(Ingredient.of(TinkerTags.Items.DURABILITY), Ingredient.of(TinkerTags.Items.INTERACTABLE)))
                         .saveSalvage(output, prefix(ModifierIds.splashing, abilitySalvage))
                         .save(output, prefix(ModifierIds.splashing, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.bursting)
                         .addInput(Blocks.CACTUS)
                         .addInput(tanks)
                         .addInput(Blocks.CACTUS)
                         .addInput(Tags.Items.INGOTS_COPPER)
                         .addInput(Tags.Items.INGOTS_COPPER)
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(ingredientFromTags(TinkerTags.Items.CHESTPLATES, TinkerTags.Items.SHIELDS))
                         .saveSalvage(output, prefix(TinkerModifiers.bursting, abilitySalvage))
                         .save(output, prefix(TinkerModifiers.bursting, abilityFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.spitting)
      .addInput(bowLimb)
      .addInput(TinkerSmeltery.searedFluidCannon)
      .addInput(bowLimb)
      .setSlots(SlotType.ABILITY, 1)
      .setMaxLevel(3).checkTraitLevel()
      // swasher gets spitting to get multishot, rest get to spit with their non-spit. No spitting with arrows
      .setTools(IntersectionIngredient.of(
        Ingredient.of(TinkerTags.Items.DURABILITY),
        Ingredient.of(TinkerTags.Items.INTERACTABLE_CHARGE_MODIFIER)
      ))
      .saveSalvage(output, prefix(ModifierIds.spitting, abilitySalvage))
      .save(output, prefix(ModifierIds.spitting, abilityFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.slimeball)
      .addInput(TinkerWorld.skySlimeVine)
      .addInput(TinkerMaterials.slimesteel.getIngotTag())
      .addInput(TinkerWorld.skySlimeVine)
      .addInput(TinkerTags.Items.SLIMY_LOGS)
      .addInput(TinkerTags.Items.SLIMY_LOGS)
      .setSlots(SlotType.ABILITY, 1)
      .setMaxLevel(1).checkTraitLevel()
      // staff exclusive ranged option, though notably melting pan is a staff
      .setTools(TinkerTags.Items.STAFFS)
      .saveSalvage(output, prefix(ModifierIds.slimeball, abilitySalvage))
      .save(output, prefix(ModifierIds.slimeball, abilityFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.tank)
                         .addInput(tanks)
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(ingredientFromTags(TinkerTags.Items.HELD, TinkerTags.Items.ARMOR))
                         .saveSalvage(output, prefix(ModifierIds.tank, upgradeSalvage))
                         .save(output, prefix(ModifierIds.tank, upgradeFolder));
    // expanders
    ModifierRecipeBuilder.modifier(ModifierIds.expanded)
                         .addInput(Items.PISTON)
                         .addInput(TinkerMaterials.amethystBronze.getIngotTag())
                         .addInput(Items.PISTON)
                         .addInput(SlimeType.ICHOR.getSlimeballTag())
                         .addInput(SlimeType.ICHOR.getSlimeballTag())
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(TinkerTags.Items.AOE)
                         .saveSalvage(output, prefix(ModifierIds.expanded, abilitySalvage))
                         .save(output, prefix(ModifierIds.expanded, abilityFolder));
    // reach expander
    ModifierRecipeBuilder.modifier(ModifierIds.reach)
                         .setTools(TinkerTags.Items.CHESTPLATES)
                         .addInput(Items.PISTON)
                         .addInput(TinkerMaterials.queensSlime.getIngotTag())
                         .addInput(Items.PISTON)
                         .addInput(SlimeType.ENDER.getSlimeballTag())
                         .addInput(SlimeType.ENDER.getSlimeballTag())
                         .setSlots(SlotType.ABILITY, 1)
                         .setMaxLevel(2)
                         .saveSalvage(output, prefix(ModifierIds.reach, abilitySalvage))
                         .save(output, prefix(ModifierIds.reach, abilityFolder));
    // block transformers
    Ingredient interactableWithDurability = IntersectionIngredient.of(Ingredient.of(TinkerTags.Items.DURABILITY), Ingredient.of(TinkerTags.Items.INTERACTABLE));
    Ingredient interactableBootsWithDurability = IntersectionIngredient.of(Ingredient.of(TinkerTags.Items.DURABILITY), ingredientFromTags(TinkerTags.Items.INTERACTABLE, TinkerTags.Items.BOOTS));
    SizedIngredient roundPlate = SizedIngredient.of(MaterialIngredient.of(TinkerToolParts.adzeHead.get()).toVanilla());
    SizedIngredient smallBlade = SizedIngredient.of(MaterialIngredient.of(TinkerToolParts.smallBlade.get()).toVanilla());
    SizedIngredient toolBinding = SizedIngredient.of(MaterialIngredient.of(TinkerToolParts.toolBinding.get()).toVanilla());
    ModifierRecipeBuilder.modifier(ModifierIds.pathing)
                         .setTools(interactableBootsWithDurability)
                         .addInput(roundPlate)
                         .addInput(TinkerMaterials.steel.getIngotTag())
                         .addInput(toolBinding)
                         .setMaxLevel(1).checkTraitLevel()
                         .setSlots(SlotType.ABILITY, 1)
                         .saveSalvage(output, prefix(ModifierIds.pathing, abilitySalvage))
                         .save(output, prefix(ModifierIds.pathing, abilityFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.stripping)
                         .setTools(interactableWithDurability)
                         .addInput(SizedIngredient.of(MaterialIngredient.of(TinkerToolParts.smallAxeHead.get()).toVanilla()))
                         .addInput(TinkerMaterials.steel.getIngotTag())
                         .addInput(toolBinding)
                         .setMaxLevel(1).checkTraitLevel()
                         .setSlots(SlotType.ABILITY, 1)
                         .saveSalvage(output, prefix(ModifierIds.stripping, abilitySalvage))
                         .save(output, prefix(ModifierIds.stripping, abilityFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.tilling)
                         .setTools(interactableBootsWithDurability)
                         .addInput(smallBlade)
                         .addInput(TinkerMaterials.steel.getIngotTag())
                         .addInput(toolBinding)
                         .setMaxLevel(1).checkTraitLevel()
                         .setSlots(SlotType.ABILITY, 1)
                         .saveSalvage(output, prefix(ModifierIds.tilling, abilitySalvage))
                         .save(output, prefix(ModifierIds.tilling, abilityFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.brushing)
      .setTools(IntersectionIngredient.of(Ingredient.of(TinkerTags.Items.DURABILITY), Ingredient.of(TinkerTags.Items.INTERACTABLE_RIGHT)))
      .addInput(Tags.Items.FEATHERS)
      .addInput(Tags.Items.INGOTS_COPPER)
      .setMaxLevel(1).checkTraitLevel()
      .setSlots(SlotType.ABILITY, 1)
      .saveSalvage(output, prefix(ModifierIds.brushing, abilitySalvage))
      .save(output, prefix(ModifierIds.brushing, abilityFolder));

    // glowing
    ModifierRecipeBuilder.modifier(ModifierIds.glowing)
                         .setTools(interactableBootsWithDurability)
                         .addInput(Items.GLOWSTONE)
                         .addInput(Items.DAYLIGHT_DETECTOR)
                         .addInput(Items.SHROOMLIGHT)
                         .setMaxLevel(1).checkTraitLevel()
                         .setSlots(SlotType.ABILITY, 1)
                         .saveSalvage(output, prefix(ModifierIds.glowing, abilitySalvage))
                         .save(output, prefix(ModifierIds.glowing, abilityFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.firestarter)
                         .setTools(interactableWithDurability)
                         .addInput(TinkerMaterials.steel.getIngotTag())
                         .addInput(Items.FLINT)
                         .setMaxLevel(1).checkTraitLevel()
                         .setSlots(SlotType.ABILITY, 1)
                         .saveSalvage(output, prefix(ModifierIds.firestarter, abilitySalvage))
                         .save(output, prefix(ModifierIds.firestarter, abilityFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.fireprimer)
                         .setTools(Ingredient.of(TinkerTools.flintAndBrick))
                         .addInput(TinkerMaterials.steel.getIngotTag())
                         .addInput(Items.FLINT)
                         .setMaxLevel(1)
                         .setSlots(SlotType.UPGRADE, 1)
                         .saveSalvage(output, prefix(ModifierIds.fireprimer, upgradeSalvage))
                         .save(output, prefix(ModifierIds.fireprimer, upgradeFolder));
    // slings
    Ingredient blockWhileCharging = IntersectionIngredient.of(Ingredient.of(TinkerTags.Items.DURABILITY), Ingredient.of(TinkerTags.Items.INTERACTABLE_CHARGE));
    ModifierRecipeBuilder.modifier(ModifierIds.flinging)
                         .setTools(blockWhileCharging)
                         .addInput(Blocks.VINE)
                         .addInput(TinkerWorld.earthGeode.asItem())
                         .addInput(Blocks.VINE)
                         .addInput(TinkerWorld.congealedSlime.get(SlimeType.EARTH))
                         .addInput(TinkerWorld.congealedSlime.get(SlimeType.EARTH))
                         .setMaxLevel(1).checkTraitLevel()
                         .setSlots(SlotType.ABILITY, 1)
                         .saveSalvage(output, prefix(ModifierIds.flinging, abilitySalvage))
                         .save(output, prefix(ModifierIds.flinging, abilityFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.springing)
                         .setTools(blockWhileCharging)
                         .addInput(TinkerWorld.skySlimeVine)
                         .addInput(TinkerWorld.skyGeode.asItem())
                         .addInput(TinkerWorld.skySlimeVine)
                         .addInput(TinkerWorld.congealedSlime.get(SlimeType.SKY))
                         .addInput(TinkerWorld.congealedSlime.get(SlimeType.SKY))
                         .setMaxLevel(1).checkTraitLevel()
                         .setSlots(SlotType.ABILITY, 1)
                         .saveSalvage(output, prefix(ModifierIds.springing, abilitySalvage))
                         .save(output, prefix(ModifierIds.springing, abilityFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.bonking)
                         .setTools(blockWhileCharging)
                         .addInput(Blocks.WEEPING_VINES)
                         .addInput(TinkerWorld.ichorGeode.asItem())
                         .addInput(Blocks.WEEPING_VINES)
                         .addInput(TinkerWorld.congealedSlime.get(SlimeType.ICHOR))
                         .addInput(TinkerWorld.congealedSlime.get(SlimeType.ICHOR))
                         .setMaxLevel(1).checkTraitLevel()
                         .setSlots(SlotType.ABILITY, 1)
                         .saveSalvage(output, prefix(ModifierIds.bonking, abilitySalvage))
                         .save(output, prefix(ModifierIds.bonking, abilityFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.warping)
                         .setTools(blockWhileCharging)
                         .addInput(TinkerWorld.enderSlimeVine)
                         .addInput(TinkerWorld.enderGeode.asItem())
                         .addInput(TinkerWorld.enderSlimeVine)
                         .addInput(TinkerWorld.congealedSlime.get(SlimeType.ENDER))
                         .addInput(TinkerWorld.congealedSlime.get(SlimeType.ENDER))
                         .setMaxLevel(1).checkTraitLevel()
                         .setSlots(SlotType.ABILITY, 1)
                         .saveSalvage(output, prefix(ModifierIds.warping, abilitySalvage))
                         .save(output, prefix(ModifierIds.warping, abilityFolder));

    // unbreakable
    ModifierRecipeBuilder.modifier(ModifierIds.unbreakable)
                         .setTools(TinkerTags.Items.DURABILITY)
                         .addInput(Items.SHULKER_SHELL)
                         .addInput(Items.DRAGON_BREATH)
                         .addInput(Items.SHULKER_SHELL)
                         .addInput(Tags.Items.INGOTS_NETHERITE)
                         .addInput(Tags.Items.INGOTS_NETHERITE)
                         .setMaxLevel(1).checkTraitLevel()
                         .setSlots(SlotType.ABILITY, 1)
                         .saveSalvage(output, prefix(ModifierIds.unbreakable, abilitySalvage))
                         .save(output, prefix(ModifierIds.unbreakable, abilityFolder));
    // weapon
    ModifierRecipeBuilder.modifier(TinkerModifiers.dualWielding)
                         .addInput(TinkerMaterials.slimesteel.getIngotTag())
                         .addInput(Items.NAUTILUS_SHELL)
                         .addInput(TinkerMaterials.slimesteel.getIngotTag())
                         .setMaxLevel(1).checkTraitLevel()
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(DifferenceIngredient.of(IntersectionIngredient.of(Ingredient.of(TinkerTags.Items.MELEE_WEAPON), Ingredient.of(TinkerTags.Items.INTERACTABLE_RIGHT)), Ingredient.of(TinkerTools.dagger)))
                         .saveSalvage(output, prefix(TinkerModifiers.dualWielding, abilitySalvage))
                         .save(output, prefix(TinkerModifiers.dualWielding, abilityFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.blocking)
                         .setTools(DifferenceIngredient.of(
                           IntersectionIngredient.of(Ingredient.of(TinkerTags.Items.INTERACTABLE_CHARGE), Ingredient.of(TinkerTags.Items.DURABILITY)),
                           ingredientFromTags(TinkerTags.Items.PARRY, TinkerTags.Items.SHIELDS)))
                         .addInput(ItemTags.PLANKS)
                         .addInput(TinkerMaterials.steel.getIngotTag())
                         .addInput(ItemTags.PLANKS)
                         .addInput(ItemTags.PLANKS)
                         .addInput(ItemTags.PLANKS)
                         .setMaxLevel(1).checkTraitLevel()
                         .setSlots(SlotType.ABILITY, 1)
                         .saveSalvage(output, prefix(ModifierIds.blocking, abilitySalvage))
                         .save(output, prefix(ModifierIds.blocking, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.parrying)
                         .setTools(TinkerTags.Items.PARRY)
                         .addInput(ItemTags.PLANKS)
                         .addInput(TinkerMaterials.steel.getIngotTag())
                         .addInput(ItemTags.PLANKS)
                         .setMaxLevel(1).checkTraitLevel()
                         .setSlots(SlotType.ABILITY, 1)
                         .saveSalvage(output, prefix(TinkerModifiers.parrying, abilitySalvage))
                         .save(output, prefix(TinkerModifiers.parrying, abilityFolder));
    MultilevelModifierRecipeBuilder.modifier(ModifierIds.reflecting)
      .setTools(TinkerTags.Items.SHIELDS)
      .addInput(TinkerWorld.congealedSlime.get(SlimeType.SKY),   4)
      .addInput(TinkerWorld.congealedSlime.get(SlimeType.ICHOR), 4)
      .addInput(TinkerWorld.congealedSlime.get(SlimeType.SKY),   4)
      .addInput(TinkerWorld.congealedSlime.get(SlimeType.EARTH), 4)
      .addInput(TinkerWorld.congealedSlime.get(SlimeType.EARTH), 4)
      .addLevel(SlotType.ABILITY, 1, 1)
      .addLevelRange(SlotType.UPGRADE, 1, 2, 3)
      .saveSalvage(output, prefix(ModifierIds.reflecting, abilitySalvage))
      .save(output, prefix(ModifierIds.reflecting, abilityFolder));

    /*
     * extra modifiers
     */
    ModifierRecipeBuilder.modifier(ModifierIds.writable)
                         .setTools(TinkerTags.Items.BONUS_SLOTS)
                         .addInput(Items.WRITABLE_BOOK)
                         .setMaxLevel(1)
                         .save(output, prefix(ModifierIds.writable, slotlessFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.harmonious)
                         .setTools(TinkerTags.Items.BONUS_SLOTS)
                         .addInput(ItemTags.CREEPER_DROP_MUSIC_DISCS)
                         .setMaxLevel(1)
                         .save(output, prefix(ModifierIds.harmonious, slotlessFolder));
    Ingredient bonusNoSkull = DifferenceIngredient.of(Ingredient.of(TinkerTags.Items.BONUS_SLOTS), Ingredient.of(TinkerTags.Items.SKULLS));
    SizedIngredient standardSkulls = SizedIngredient.of(DifferenceIngredient.of(Ingredient.of(ItemTags.SKULLS), Ingredient.of(Items.DRAGON_HEAD)));
    ModifierRecipeBuilder.modifier(ModifierIds.recapitated)
      .setTools(bonusNoSkull)
      .addInput(standardSkulls)
      .setMaxLevel(1)
      .save(output, prefix(ModifierIds.recapitated, slotlessFolder));
    Ingredient bonusSkulls = IntersectionIngredient.of(Ingredient.of(TinkerTags.Items.BONUS_SLOTS), Ingredient.of(TinkerTags.Items.SKULLS));
    ModifierRecipeBuilder.modifier(ModifierIds.recapitated)
      .setTools(bonusSkulls)
      .addInput(standardSkulls).addInput(Tags.Items.SLIMEBALLS)
      .setMaxLevel(1)
      .save(output, wrap(ModifierIds.recapitated, slotlessFolder, "_for_skull"));
    ModifierRecipeBuilder.modifier(ModifierIds.forecast)
                         .setTools(TinkerTags.Items.BONUS_SLOTS)
                         .addInput(ingredientFromTags(Tags.Items.ORES_DIAMOND, Tags.Items.ORES_EMERALD, TinkerTags.Items.ORES_COBALT))
                         .setMaxLevel(1)
                         .save(output, prefix(ModifierIds.forecast, slotlessFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.embossed)
      .setTools(TinkerTags.Items.BONUS_SLOTS)
      .addInput(TinkerTags.Items.BOSS_TROPHIES)
      .setMaxLevel(1)
      .save(withCondition(output, new TagFilledCondition<>(TinkerTags.Items.BOSS_TROPHIES)), prefix(ModifierIds.embossed, slotlessFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.draconic)
      .setTools(bonusNoSkull)
      .addInput(Items.DRAGON_HEAD)
      .setMaxLevel(1)
      .save(output, wrap(ModifierIds.draconic, slotlessFolder, "_from_head"));
    ModifierRecipeBuilder.modifier(ModifierIds.draconic)
      .setTools(bonusSkulls)
      .addInput(Items.DRAGON_HEAD).addInput(Tags.Items.SLIMEBALLS)
      .setMaxLevel(1)
      .save(output, wrap(ModifierIds.draconic, slotlessFolder, "_for_skull"));
    ModifierRecipeBuilder.modifier(ModifierIds.draconic)
                         .setTools(TinkerTags.Items.BONUS_SLOTS)
                         .addInput(Items.DRAGON_BREATH)
                         .addInput(TinkerModifiers.dragonScale)
                         .addInput(Items.DRAGON_BREATH)
                         .addInput(TinkerModifiers.dragonScale)
                         .addInput(TinkerModifiers.dragonScale)
                         .setMaxLevel(1)
                         .disallowCrystal()
                         .save(output, wrap(ModifierIds.draconic, slotlessFolder, "_from_scales"));
    // rebalanced
    SwappableModifierRecipeBuilder.modifier(ModifierIds.rebalanced, SlotType.UPGRADE.getName())
      .setTools(TinkerTags.Items.BONUS_SLOTS)
      .addInput(TinkerMaterials.roseGold.getNuggetTag())
      .addInput(Items.END_CRYSTAL)
      .addInput(TinkerMaterials.roseGold.getNuggetTag())
      .addInput(TinkerWorld.skyGeode.getBlock())
      .addInput(TinkerWorld.skyGeode.getBlock())
      .disallowCrystal()
      .save(output, wrap(ModifierIds.rebalanced, slotlessFolder, "_" + SlotType.UPGRADE.getName()));
    SwappableModifierRecipeBuilder.modifier(ModifierIds.rebalanced, SlotType.DEFENSE.getName())
      .setTools(IntersectionIngredient.of(ingredientFromTags(TinkerTags.Items.ARMOR, TinkerTags.Items.HELD), Ingredient.of(TinkerTags.Items.BONUS_SLOTS)))
      .addInput(TinkerMaterials.cobalt.getNuggetTag())
      .addInput(Items.END_CRYSTAL)
      .addInput(TinkerMaterials.cobalt.getNuggetTag())
      .addInput(TinkerWorld.earthGeode.getBlock())
      .addInput(TinkerWorld.earthGeode.getBlock())
      .disallowCrystal()
      .save(output, wrap(ModifierIds.rebalanced, slotlessFolder, "_" + SlotType.DEFENSE.getName()));
    SwappableModifierRecipeBuilder.modifier(ModifierIds.rebalanced, SlotType.ABILITY.getName())
      .setTools(TinkerTags.Items.BONUS_SLOTS)
      .addInput(TinkerMaterials.queensSlime.getNuggetTag())
      .addInput(Items.END_CRYSTAL)
      .addInput(TinkerMaterials.queensSlime.getNuggetTag())
      .addInput(TinkerWorld.ichorGeode.getBlock())
      .addInput(TinkerWorld.ichorGeode.getBlock())
      .disallowCrystal()
      .save(output, wrap(ModifierIds.rebalanced, slotlessFolder, "_" + SlotType.ABILITY.getName()));
    SwappableModifierRecipeBuilder.modifier(ModifierIds.rebalanced, "traits")
      .setTools(ToolHookIngredient.of(TinkerTags.Items.BONUS_SLOTS, ToolHooks.REBALANCED_TRAIT))
      .addInput(TinkerMaterials.manyullyn.getNuggetTag())
      .addInput(Items.END_CRYSTAL)
      .addInput(TinkerMaterials.manyullyn.getNuggetTag())
      .addInput(TinkerWorld.enderGeode.getBlock())
      .addInput(TinkerWorld.enderGeode.getBlock())
      .disallowCrystal()
      .save(output, wrap(ModifierIds.rebalanced, slotlessFolder, "_traits"));
    ModifierRecipeBuilder.modifier(ModifierIds.redirected)
      .setTools(ToolHookIngredient.of(TinkerTags.Items.AMMO, ToolHooks.REBALANCED_TRAIT))
      .addInput(Items.DRAGON_BREATH)
      .save(output, prefix(ModifierIds.redirected, slotlessFolder));

    // tipping arrows and shurikens
    PotionCastingRecipeBuilder.tableTipping(ModifierIds.tipped)
      .setBottle(TinkerTags.Items.AMMO)
      .setCoolingTime(20)
      .setFluid(TinkerFluids.potion.ingredient(FluidValues.BOTTLE / 5))
      .save(output, location(slotlessFolder + "ammo_tipping"));
    PotionCastingRecipeBuilder.tableClearing(ModifierIds.tipped)
      .setBottle(TinkerTags.Items.AMMO)
      .setCoolingTime(20)
      .setFluid(MantleTags.Fluids.WATER, FluidValues.BOTTLE / 5)
      .save(output, location(slotlessFolder + "ammo_tip_clearing"));
    PotionCastingRecipeBuilder.tableTipping(ModifierIds.tipped)
      .setBottle(TinkerTags.Items.FISHING_RODS)
      .setCoolingTime(20)
      .setFluid(TinkerFluids.potion.ingredient(FluidValues.BOTTLE))
      .save(output, location(slotlessFolder + "fishing_rod_tipping"));
    PotionCastingRecipeBuilder.tableClearing(ModifierIds.tipped)
      .setBottle(TinkerTags.Items.FISHING_RODS)
      .setCoolingTime(20)
      .setFluid(MantleTags.Fluids.WATER, FluidValues.BOTTLE)
      .save(output, location(slotlessFolder + "fishing_rod_tip_clearing"));

    // removal
    IJsonPredicate<ModifierId> removable = ModifierPredicate.tag(TinkerTags.Modifiers.REMOVE_MODIFIER_BLACKLIST).inverted();
    ModifierRemovalRecipeBuilder.removal()
      .addInput(Blocks.WET_SPONGE)
      .addLeftover(Blocks.SPONGE)
      .modifierPredicate(removable)
      .save(output, location(worktableFolder + "remove_modifier_sponge"));
    ModifierRemovalRecipeBuilder.removal()
      .addInput(CompoundIngredient.of(
        FluidContainerIngredient.fromFluid(TinkerFluids.venom).toVanilla(),
        FluidContainerIngredient.fromIngredient(TinkerFluids.venom.ingredient(FluidValues.BOTTLE), Ingredient.of(TinkerFluids.venomBottle)).toVanilla())
      )
      .modifierPredicate(removable)
      .save(output, location(worktableFolder + "remove_modifier_venom"));
    // modifier extracting: sponge + crystal
    IJsonPredicate<ModifierId> extractBlacklist = ModifierPredicate.tag(TinkerTags.Modifiers.EXTRACT_MODIFIER_BLACKLIST).inverted();
    for (boolean dagger : new boolean[]{false, true}) {
      String suffix = dagger ? "_dagger" : "";
      SizedIngredient tools = dagger ? SizedIngredient.fromItems(2, TinkerTools.dagger) : SizedIngredient.of(DifferenceIngredient.of(Ingredient.of(TinkerTags.Items.MODIFIABLE), Ingredient.of(TinkerTags.Items.UNSALVAGABLE)));
      ModifierRemovalRecipeBuilder.extract()
                                  .setTools(tools)
                                  .setName("slotless")
                                  .addInput(Items.AMETHYST_SHARD)
                                  .addInput(Items.WET_SPONGE)
                                  .addLeftover(Items.SPONGE)
                                  .modifierPredicate(ModifierPredicate.and(extractBlacklist, new SlotTypeModifierPredicate(null), ModifierPredicate.tag(TinkerTags.Modifiers.EXTRACT_SLOTLESS_BLACKLIST).inverted()))
                                  .save(output, location(worktableFolder + "extract/slotless" + suffix));
      ModifierRemovalRecipeBuilder.extract()
                                  .setTools(tools)
                                  .slotName(SlotType.UPGRADE)
                                  .addInput(TinkerWorld.skyGeode)
                                  .addInput(Items.WET_SPONGE)
                                  .addLeftover(Items.SPONGE)
                                  .modifierPredicate(ModifierPredicate.and(extractBlacklist, new SlotTypeModifierPredicate(SlotType.UPGRADE)))
                                  .save(output, location(worktableFolder + "extract/upgrade" + suffix));
      ModifierRemovalRecipeBuilder.extract()
                                  .setTools(tools)
                                  .slotName(SlotType.DEFENSE)
                                  .addInput(TinkerWorld.earthGeode)
                                  .addInput(Items.WET_SPONGE)
                                  .addLeftover(Items.SPONGE)
                                  .modifierPredicate(ModifierPredicate.and(extractBlacklist, new SlotTypeModifierPredicate(SlotType.DEFENSE)))
                                  .save(output, location(worktableFolder + "extract/defense" + suffix));
      ModifierRemovalRecipeBuilder.extract()
                                  .setTools(tools)
                                  .slotName(SlotType.ABILITY)
                                  .addInput(TinkerWorld.ichorGeode)
                                  .addInput(Items.WET_SPONGE)
                                  .addLeftover(Items.SPONGE)
                                  .modifierPredicate(ModifierPredicate.and(extractBlacklist, new SlotTypeModifierPredicate(SlotType.ABILITY)))
                                  .save(output, location(worktableFolder + "extract/ability" + suffix));
      ModifierRemovalRecipeBuilder.extract()
                                  .setTools(tools)
                                  .addInput(TinkerWorld.enderGeode)
                                  .addInput(Items.WET_SPONGE)
                                  .addLeftover(Items.SPONGE)
                                  .modifierPredicate(extractBlacklist)
                                  .save(output, location(worktableFolder + "extract/modifier" + suffix));

    }
    ModifierSortingRecipeBuilder.sorting()
                                .addInput(Items.COMPASS)
                                .save(output, location(worktableFolder + "modifier_sorting"));

    // invisible ink
    ResourceLocation hiddenModifiers = TConstruct.getResource("invisible_modifiers");
    IJsonPredicate<ModifierId> blacklist = ModifierPredicate.tag(TinkerTags.Modifiers.INVISIBLE_INK_BLACKLIST).inverted();
    ModifierSetWorktableRecipeBuilder.setAdding(hiddenModifiers)
                                     .modifierPredicate(blacklist)
                                     .addInput(FluidContainerIngredient.fromIngredient(TinkerFluids.skySlime.ingredient(FluidValues.BOTTLE), Ingredient.of(TinkerFluids.slimeBottle.get(SlimeType.SKY))))
                                     .save(output, location(worktableFolder + "invisible_ink_adding"));
    ModifierSetWorktableRecipeBuilder.setRemoving(hiddenModifiers)
                                     .modifierPredicate(blacklist)
                                     .addInput(FluidContainerIngredient.fromIngredient(FluidIngredient.of(Fluids.MILK, FluidType.BUCKET_VOLUME), Ingredient.of(Items.MILK_BUCKET)))
                                     .save(output, location(worktableFolder + "invisible_ink_removing"));

    // swapping hands
    ToggleInteractionWorktableRecipeBuilder.builder()
      .tools(Ingredient.of(TinkerTags.Items.INTERACTABLE_DUAL))
      .addInput(Items.LEVER)
      .save(output, location(worktableFolder + "toggle_interaction_modifier"));

    // conversion
    for (boolean matchBook : new boolean[]{false, true}) {
      String suffix = matchBook ? "_book" : "_tool";
      EnchantmentConvertingRecipeBuilder.converting("slotless", matchBook)
                                        .addInput(Items.AMETHYST_SHARD)
                                        .modifierPredicate(ModifierPredicate.and(new SlotTypeModifierPredicate(null),
                                                                                  ModifierPredicate.tag(TinkerTags.Modifiers.EXTRACT_SLOTLESS_BLACKLIST).inverted()))
                                        .save(output, location(worktableFolder + "enchantment_converting/slotless" + suffix));
      EnchantmentConvertingRecipeBuilder.converting("upgrades", matchBook)
                                        .addInput(TinkerWorld.skyGeode.asItem())
                                        .addInput(Tags.Items.GEMS_LAPIS, 3)
                                        .modifierPredicate(ModifierPredicate.and(new SlotTypeModifierPredicate(SlotType.UPGRADE),
                                          ModifierPredicate.tag(TinkerTags.Modifiers.EXTRACT_UPGRADE_BLACKLIST).inverted()))
                                        .save(output, location(worktableFolder + "enchantment_converting/upgrade" + suffix));
      EnchantmentConvertingRecipeBuilder.converting("defense", matchBook)
                                        .addInput(TinkerWorld.earthGeode.asItem())
                                        .addInput(Tags.Items.INGOTS_GOLD, 1)
                                        .modifierPredicate(new SlotTypeModifierPredicate(SlotType.DEFENSE))
                                        .save(output, location(worktableFolder + "enchantment_converting/defense" + suffix));
      EnchantmentConvertingRecipeBuilder.converting("abilities", matchBook)
                                        .addInput(TinkerWorld.ichorGeode.asItem())
                                        .addInput(Tags.Items.GEMS_DIAMOND)
                                        .modifierPredicate(new SlotTypeModifierPredicate(SlotType.ABILITY))
                                        .save(output, location(worktableFolder + "enchantment_converting/ability" + suffix));
      EnchantmentConvertingRecipeBuilder.converting("modifiers", matchBook)
                                        .addInput(TinkerWorld.enderGeode)
                                        .addInput(Items.DRAGON_BREATH, 5)
                                        .returnInput()
                                        .save(output, location(worktableFolder + "enchantment_converting/unenchant" + suffix));
    }

    // compatability
    String theOneProbe = "theoneprobe";
    ResourceLocation probe = ResourceLocation.fromNamespaceAndPath(theOneProbe, "probe");
    RecipeOutput topConsumer = withCondition(output, modLoaded(theOneProbe));
    ModifierRecipeBuilder.modifier(ModifierIds.theOneProbe)
                         .setTools(ingredientFromTags(TinkerTags.Items.HELMETS, TinkerTags.Items.HELD))
                         .addInput(ItemNameIngredient.from(probe))
                         .setSlots(SlotType.UPGRADE, 1)
                         .setMaxLevel(1).checkTraitLevel()
                         .saveSalvage(topConsumer, prefix(ModifierIds.theOneProbe, compatSalvage))
                         .save(topConsumer, prefix(ModifierIds.theOneProbe, compatFolder));
    RecipeOutput headlightConsumer = withCondition(output, modLoaded("headlight"));
    BiConsumer<Ingredient,String> headlight = (ingredient, light) -> {
      SwappableModifierRecipeBuilder builder = SwappableModifierRecipeBuilder.modifier(ModifierIds.headlight, light);
      builder.variantFormatter(VariantFormatter.PARAMETER)
             .setTools(TinkerTags.Items.HELMETS)
             .addInput(Items.LEATHER)
             .addInput(ingredient)
             .addInput(Items.LEATHER)
             .setSlots(SlotType.UPGRADE, 1)
             .disallowCrystal();
      if ("10".equals(light)) {
        builder.saveSalvage(headlightConsumer, prefix(ModifierIds.headlight, compatSalvage));
      } else {
        builder.disallowCrystal();
      }
      builder.save(headlightConsumer, wrap(ModifierIds.headlight, compatFolder, "_" + light));
    };
    headlight.accept(Ingredient.of(Blocks.LANTERN), "15");
    headlight.accept(Ingredient.of(Blocks.SOUL_LANTERN), "10");
    headlight.accept(Ingredient.of(ItemTags.CANDLES), "5");
  }

  private void addTextureRecipes(RecipeOutput output) {
    String folder = "tools/modifiers/slotless/";

    // slime staff
    // nether
    woodTexture(output, MaterialIds.crimson, Blocks.CRIMSON_PLANKS, folder);
    woodTexture(output, MaterialIds.warped, Blocks.WARPED_PLANKS, folder);
    // slimewood
    woodTexture(output, MaterialIds.greenheart, TinkerWorld.greenheart, folder);
    woodTexture(output, MaterialIds.skyroot, TinkerWorld.skyroot, folder);
    woodTexture(output, MaterialIds.bloodshroom, TinkerWorld.bloodshroom, folder);
    woodTexture(output, MaterialIds.enderbark, TinkerWorld.enderbark, folder);
    // special
    woodTexture(output, MaterialIds.blazewood, TinkerMaterials.blazewood, folder);
    woodTexture(output, MaterialIds.nahuatl, TinkerMaterials.nahuatl, folder);
    woodTexture(output, MaterialIds.bamboo, Blocks.BAMBOO, folder);
    woodTexture(output, MaterialIds.cactus, Blocks.CACTUS, folder);
    // compat
    TagKey<Item> treatedWood = getItemTag(COMMON, "treated_wood");
    SwappableModifierRecipeBuilder.modifier(TinkerModifiers.embellishment, MaterialIds.treatedWood.toString())
      .variantFormatter(VariantFormatter.MATERIAL)
      .setTools(TinkerTags.Items.EMBELLISHMENT_WOOD)
      .addInput(treatedWood).addInput(TinkerTables.pattern).addInput(treatedWood)
      .save(withCondition(output, new TagFilledCondition<>(treatedWood)), wrap(TinkerModifiers.embellishment, folder, "/wood/treated"));
    TagKey<Item> ironwood = getItemTag(COMMON, "ingots/ironwood");
    SwappableModifierRecipeBuilder.modifier(TinkerModifiers.embellishment, MaterialIds.ironwood.toString())
      .variantFormatter(VariantFormatter.MATERIAL)
      .setTools(TinkerTags.Items.EMBELLISHMENT_WOOD)
      .addInput(ironwood).addInput(TinkerTables.pattern).addInput(ironwood)
      .save(withCondition(output, new TagFilledCondition<>(ironwood)), wrap(TinkerModifiers.embellishment, folder, "/wood/ironwood"));

    // cosmetics //
    output.accept(location(folder + "dyeing"), new slimeknights.tconstruct.tools.recipe.ArmorDyeingRecipe(), null);
    output.accept(location(folder + "trim"), new slimeknights.tconstruct.tools.recipe.ArmorTrimRecipe(), null);
    output.accept(location(folder + "banner"), new slimeknights.tconstruct.tools.recipe.BannerModifierRecipe(), null);

    // slimesuit //
    // basic slime
    slimeTexture(output, MaterialIds.earthslime, SlimeType.EARTH, folder);
    slimeTexture(output, MaterialIds.skyslime,   SlimeType.SKY, folder);
    slimeTexture(output, MaterialIds.ichor,      SlimeType.ICHOR, folder);
    slimeTexture(output, MaterialIds.enderslime, SlimeType.ENDER, folder);
    // slimy planks
    slimyWoodTexture(output, MaterialIds.earthslime, TinkerWorld.greenheart,  FoliageType.EARTH, folder);
    slimyWoodTexture(output, MaterialIds.skyslime,   TinkerWorld.skyroot,     FoliageType.SKY,   folder);
    slimyWoodTexture(output, MaterialIds.blood,      TinkerWorld.bloodshroom, FoliageType.BLOOD, folder);
    slimyWoodTexture(output, MaterialIds.enderslime, TinkerWorld.enderbark,   FoliageType.ENDER, folder);
    // weird slime
    SwappableModifierRecipeBuilder.modifier(TinkerModifiers.embellishment, MaterialIds.clay.toString())
                                  .variantFormatter(VariantFormatter.MATERIAL)
                                  .setTools(TinkerTags.Items.EMBELLISHMENT_SLIME)
                                  .addInput(Blocks.CLAY).addInput(Items.CLAY_BALL).addInput(Blocks.CLAY)
                                  .save(output, wrap(TinkerModifiers.embellishment, folder, "/slime/clay"));
    SwappableModifierRecipeBuilder.modifier(TinkerModifiers.embellishment, MaterialIds.magma.toString())
                                  .variantFormatter(VariantFormatter.MATERIAL)
                                  .setTools(TinkerTags.Items.EMBELLISHMENT_SLIME)
                                  .addInput(Blocks.MAGMA_BLOCK).addInput(Items.MAGMA_CREAM).addInput(Blocks.MAGMA_BLOCK)
                                  .save(output, wrap(TinkerModifiers.embellishment, folder, "/slime/magma"));
    SwappableModifierRecipeBuilder.modifier(TinkerModifiers.embellishment, MaterialIds.honey.toString())
                                  .variantFormatter(VariantFormatter.MATERIAL)
                                  .setTools(TinkerTags.Items.EMBELLISHMENT_SLIME)
                                  .addInput(Blocks.HONEY_BLOCK).addInput(Items.HONEY_BOTTLE).addInput(Blocks.HONEY_BLOCK)
                                  .save(output, wrap(TinkerModifiers.embellishment, folder, "/slime/honey"));
  }

  private void addHeadRecipes(RecipeOutput output) {
    String folder = "tools/severing/";
    // first, beheading
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.ZOMBIE), Items.ZOMBIE_HEAD)
												 .save(output, location(folder + "zombie_head"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.SKELETON), Items.SKELETON_SKULL)
												 .save(output, location(folder + "skeleton_skull"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.WITHER_SKELETON), Items.WITHER_SKELETON_SKULL)
      .save(output, location(folder + "wither_skeleton_skull"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.WITHER), Items.WITHER_SKELETON_SKULL).rareMob()
      .save(output, location(folder + "wither_salvage"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.CREEPER), Items.CREEPER_HEAD)
												 .save(output, location(folder + "creeper_head"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.PIGLIN), Items.PIGLIN_HEAD)
                         .save(output, location(folder + "piglin_head"));
    SpecialSeveringRecipeBuilder.serializer(TinkerModifiers.playerBeheadingSerializer).rareMob().save(output, location(folder + "player_head"));
    SpecialSeveringRecipeBuilder.serializer(TinkerModifiers.snowGolemBeheadingSerializer).save(output, location(folder + "snow_golem_head"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.IRON_GOLEM), Blocks.CARVED_PUMPKIN)
                         .save(output, location(folder + "iron_golem_head"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.ENDER_DRAGON), Items.DRAGON_HEAD).rareMob()
                         .save(output, location(folder + "ender_dragon_head"));
    TinkerWorld.headItems.forEach((type, head) -> {
      if (type.isNatural()) {
        SeveringRecipeBuilder.severing(EntityIngredient.of(type.getType()), head)
          .save(output, location(folder + type.getSerializedName() + "_head"));
      }
    });

    // other body parts
    // hostile
    // beeyeing
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.SPIDER, EntityType.CAVE_SPIDER), Items.SPIDER_EYE)
                         .save(output, location(folder + "spider_eye"));
    // besilking
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.SPIDER, EntityType.CAVE_SPIDER), Items.COBWEB)
                         .save(output, location(folder + "cobweb"));
    // be-internal-combustion-device
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.CREEPER), Blocks.TNT)
                         .save(output, location(folder + "creeper_tnt"));
    // bemembraning?
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.PHANTOM), Items.PHANTOM_MEMBRANE)
                         .save(output, location(folder + "phantom_membrane"));
    // beshelling
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.SHULKER), Items.SHULKER_SHELL)
                         .save(output, location(folder + "shulker_shell"));
    // deboning
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.SKELETON, EntityType.SKELETON_HORSE, EntityType.STRAY), ItemOutput.fromItem(Items.BONE, 2))
                         .save(output, location(folder + "skeleton_bone"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.WITHER_SKELETON), ItemOutput.fromItem(TinkerMaterials.necroticBone, 2))
                         .save(output, location(folder + "wither_skeleton_bone"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.BLAZE), ItemOutput.fromItem(Items.BLAZE_ROD, 2))
                         .save(output, location(folder + "blaze_rod"));
    // desliming (you cut off a chunk of slime)
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.SLIME), Items.SLIME_BALL)
                         .save(output, location(folder + "earthslime_ball"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(TinkerWorld.skySlimeEntity.get()), TinkerCommons.slimeball.get(SlimeType.SKY))
                         .save(output, location(folder + "skyslime_ball"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(TinkerWorld.enderSlimeEntity.get()), TinkerCommons.slimeball.get(SlimeType.ENDER))
                         .save(output, location(folder + "enderslime_ball"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(TinkerWorld.terracubeEntity.get()), Items.CLAY_BALL)
                         .save(output, location(folder + "terracube_clay"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.MAGMA_CUBE), Items.MAGMA_CREAM)
                         .save(output, location(folder + "magma_cream"));
    // descaling? I don't know what to call those
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.GUARDIAN, EntityType.ELDER_GUARDIAN), ItemOutput.fromItem(Items.PRISMARINE_SHARD, 2))
                         .save(output, location(folder + "guardian_shard"));

    // passive
    // befeating
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.RABBIT), Items.RABBIT_FOOT)
                         .noChildOutput()
												 .save(output, location(folder + "rabbit_foot"));
    // befeathering
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.CHICKEN), ItemOutput.fromItem(Items.FEATHER, 2))
                         .noChildOutput()
                         .save(output, location(folder + "chicken_feather"));
    // beshrooming
    SpecialSeveringRecipeBuilder.serializer(TinkerModifiers.mooshroomDemushroomingSerializer).save(output, location(folder + "mooshroom_shroom"));
    // beshelling
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.TURTLE), Items.TURTLE_HELMET)
                         .setChildOutput(ItemOutput.fromItem(Items.TURTLE_SCUTE))
                         .save(output, location(folder + "turtle_shell"));
    // befleecing
    SpecialSeveringRecipeBuilder.serializer(TinkerModifiers.sheepShearing).save(output, location(folder + "sheep_wool"));
  }

  /** Adds recipes for a plate armor texture with a custom tag */
  private void woodTexture(RecipeOutput output, MaterialVariantId material, ItemLike planks, String folder) {
    SwappableModifierRecipeBuilder.modifier(TinkerModifiers.embellishment, material.toString())
                                  .variantFormatter(VariantFormatter.MATERIAL)
                                  .setTools(TinkerTags.Items.EMBELLISHMENT_WOOD)
                                  .addInput(planks).addInput(TinkerTables.pattern).addInput(planks)
                                  .save(output, wrap(TinkerModifiers.embellishment, folder, "/wood/" + material.getLocation('_').getPath()));
  }

  /** Adds recipes for a slime armor texture */
  private void slimeTexture(RecipeOutput output, MaterialId material, SlimeType slime, String folder) {
    ItemLike congealed = TinkerWorld.congealedSlime.get(slime);
    SwappableModifierRecipeBuilder.modifier(TinkerModifiers.embellishment, material.toString())
                                  .variantFormatter(VariantFormatter.MATERIAL)
                                  .setTools(TinkerTags.Items.EMBELLISHMENT_SLIME)
                                  .addInput(congealed).addInput(TinkerCommons.slimeball.get(slime)).addInput(congealed)
                                  .save(output, wrap(TinkerModifiers.embellishment, folder, "/slime/" + slime.getSerializedName()));
  }

  /** Adds recipes for a slime armor texture */
  private void slimyWoodTexture(RecipeOutput output, MaterialId material, WoodBlockObject wood, FoliageType foliage, String folder) {
    ItemLike planks = wood.get();
    SwappableModifierRecipeBuilder.modifier(TinkerModifiers.embellishment, material.toString())
                                  .variantFormatter(VariantFormatter.MATERIAL)
                                  .setTools(TinkerTags.Items.EMBELLISHMENT_SLIME)
                                  .addInput(planks).addInput(TinkerWorld.slimeSapling.get(foliage)).addInput(planks)
                                  .save(output, wrap(TinkerModifiers.embellishment, folder, "/slime/" + wood.getWoodType().name().split(":", 2)[1]));
  }

  /** Adds haste like recipes using redstone */
  public void hasteRecipes(RecipeOutput output, ModifierId modifier, Ingredient tools, int maxLevel, @Nullable String recipeFolder, @Nullable String salvageFolder) {
    IncrementalModifierRecipeBuilder builder = IncrementalModifierRecipeBuilder
      .modifier(modifier)
      .setTools(tools)
      .setInput(Tags.Items.DUSTS_REDSTONE, 1, 45)
      .setMaxLevel(maxLevel)
      .setSlots(SlotType.UPGRADE, 1);
    if (salvageFolder != null) {
      builder.saveSalvage(output, prefix(modifier, salvageFolder));
    }
    if (recipeFolder != null) {
      builder.save(output, wrap(modifier, recipeFolder, "_from_dust"));
      IncrementalModifierRecipeBuilder.modifier(modifier)
                                      .setTools(tools)
                                      .setInput(Tags.Items.STORAGE_BLOCKS_REDSTONE, 9, 45)
                                      .setLeftover(new ItemStack(Items.REDSTONE))
                                      .setMaxLevel(maxLevel)
                                      .setSlots(SlotType.UPGRADE, 1)
                                      .disallowCrystal() // avoid redundancy, though in this case the end result is the same
                                      .save(output, wrap(modifier, recipeFolder, "_from_block"));
    }
  }

  /** Prefixes the modifier ID with the given prefix */
  public ResourceLocation prefix(LazyModifier modifier, String prefix) {
    return prefix(modifier.getId(), prefix);
  }

  /** Prefixes the modifier ID with the given prefix and suffix */
  public ResourceLocation wrap(LazyModifier modifier, String prefix, String suffix) {
    return wrap(modifier.getId(), prefix, suffix);
  }

  /**
   * Creates a compound ingredient from multiple tags
   * @param tags  Tags to use
   * @return  Compound ingredient
   */
  @SafeVarargs
  private static Ingredient ingredientFromTags(TagKey<Item>... tags) {
    Ingredient[] tagIngredients = new Ingredient[tags.length];
    for (int i = 0; i < tags.length; i++) {
      tagIngredients[i] = Ingredient.of(tags[i]);
    }
    return CompoundIngredient.of(tagIngredients);
  }
}
