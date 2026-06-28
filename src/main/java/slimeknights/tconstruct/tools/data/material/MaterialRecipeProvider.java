package slimeknights.tconstruct.tools.data.material;

import net.minecraft.data.PackOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;

import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import net.neoforged.neoforge.common.crafting.DifferenceIngredient;
import java.util.List;
import net.neoforged.neoforge.common.conditions.OrCondition;
import net.neoforged.neoforge.fluids.FluidType;
import slimeknights.mantle.datagen.MantleTags;
import slimeknights.mantle.recipe.condition.TagCombinationCondition;
import slimeknights.mantle.recipe.condition.TagFilledCondition;
import slimeknights.mantle.recipe.helper.FluidOutput;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.data.BaseRecipeProvider;
import slimeknights.tconstruct.common.json.ConfigEnabledCondition;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.data.recipe.IMaterialRecipeHelper;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialFluidRecipeBuilder;
import slimeknights.tconstruct.library.recipe.melting.MaterialMeltingRecipeBuilder;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.recipe.severing.SheepShearingRecipe;
import slimeknights.tconstruct.world.TinkerWorld;



import static slimeknights.mantle.Mantle.COMMON;

public class MaterialRecipeProvider extends BaseRecipeProvider implements IMaterialRecipeHelper {
  public MaterialRecipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
    super(packOutput, registries);
  }

  @Override
  protected void buildRecipes(RecipeOutput output) {
    addMaterialItems(output);
    addMaterialSmeltery(output);
  }

  private void addMaterialItems(RecipeOutput output) {
    String folder = "tools/materials/";
    // tier 1
    materialRecipe(output, MaterialIds.wood,   Ingredient.of(Tags.Items.RODS_WOODEN), 1, 2, folder + "wood/sticks");
    // planks
    materialRecipe(output, MaterialIds.crimson,  Ingredient.of(Items.CRIMSON_PLANKS),  1, 1, folder + "wood/planks/crimson");
    materialRecipe(output, MaterialIds.warped,   Ingredient.of(Items.WARPED_PLANKS),   1, 1, folder + "wood/planks/warped");
    materialRecipe(withCondition(output, TagCombinationCondition.difference(ItemTags.PLANKS, TinkerTags.Items.VARIANT_PLANKS)), MaterialIds.wood,
                   DifferenceIngredient.of(Ingredient.of(ItemTags.PLANKS), Ingredient.of(TinkerTags.Items.VARIANT_PLANKS)), 1, 1, folder + "wood/planks/default");
    // logs
    // standard wood, different recipes just swap the leftovers
    materialRecipe(output, MaterialIds.wood, Ingredient.of(ItemTags.OAK_LOGS),      4, 1, ItemOutput.fromItem(Blocks.OAK_PLANKS),      folder + "wood/logs/oak");
    materialRecipe(output, MaterialIds.wood, Ingredient.of(ItemTags.SPRUCE_LOGS),   4, 1, ItemOutput.fromItem(Blocks.SPRUCE_PLANKS),   folder + "wood/logs/spruce");
    materialRecipe(output, MaterialIds.wood, Ingredient.of(ItemTags.BIRCH_LOGS),    4, 1, ItemOutput.fromItem(Blocks.BIRCH_PLANKS),    folder + "wood/logs/birch");
    materialRecipe(output, MaterialIds.wood, Ingredient.of(ItemTags.JUNGLE_LOGS),   4, 1, ItemOutput.fromItem(Blocks.JUNGLE_PLANKS),   folder + "wood/logs/jungle");
    materialRecipe(output, MaterialIds.wood, Ingredient.of(ItemTags.DARK_OAK_LOGS), 4, 1, ItemOutput.fromItem(Blocks.DARK_OAK_PLANKS), folder + "wood/logs/dark_oak");
    materialRecipe(output, MaterialIds.wood, Ingredient.of(ItemTags.ACACIA_LOGS),   4, 1, ItemOutput.fromItem(Blocks.ACACIA_PLANKS),   folder + "wood/logs/acacia");
    materialRecipe(output, MaterialIds.wood, Ingredient.of(ItemTags.MANGROVE_LOGS), 4, 1, ItemOutput.fromItem(Blocks.MANGROVE_PLANKS), folder + "wood/logs/mangrove");
    materialRecipe(output, MaterialIds.wood, Ingredient.of(ItemTags.CHERRY_LOGS),   4, 1, ItemOutput.fromItem(Blocks.CHERRY_PLANKS),   folder + "wood/logs/cherry");
    // variant wood, swaps the variant as well
    materialRecipe(output, MaterialIds.crimson,  Ingredient.of(ItemTags.CRIMSON_STEMS), 4, 1, ItemOutput.fromItem(Blocks.CRIMSON_PLANKS),  folder + "wood/logs/crimson");
    materialRecipe(output, MaterialIds.warped,   Ingredient.of(ItemTags.WARPED_STEMS),  4, 1, ItemOutput.fromItem(Blocks.WARPED_PLANKS),   folder + "wood/logs/warped");
    materialRecipe(withCondition(output, TagCombinationCondition.difference(ItemTags.LOGS, TinkerTags.Items.VARIANT_LOGS)), MaterialIds.wood,
                   DifferenceIngredient.of(Ingredient.of(ItemTags.LOGS), Ingredient.of(TinkerTags.Items.VARIANT_LOGS)), 4, 1,
                   ItemOutput.fromItem(Items.STICK, 2), folder + "wood/logs/default");
    // bamboo
    materialRecipe(output, MaterialIds.bamboo, Ingredient.of(Items.BAMBOO),           1, 9, folder + "wood/bamboo/stick");
    materialRecipe(output, MaterialIds.bamboo, Ingredient.of(ItemTags.BAMBOO_BLOCKS), 1, 1, folder + "wood/bamboo/block");
    materialRecipe(output, MaterialIds.bamboo, Ingredient.of(Blocks.BAMBOO_PLANKS),   1, 2, folder + "wood/bamboo/planks");
    // stone
    materialRecipe(output, MaterialIds.stone,      Ingredient.of(TinkerTags.Items.STONE),      1, 1, folder + "rock/stone");
    materialRecipe(output, MaterialIds.andesite,   Ingredient.of(TinkerTags.Items.ANDESITE),   1, 1, folder + "rock/andesite");
    materialRecipe(output, MaterialIds.diorite,    Ingredient.of(TinkerTags.Items.DIORITE),    1, 1, folder + "rock/diorite");
    materialRecipe(output, MaterialIds.granite,    Ingredient.of(TinkerTags.Items.GRANITE),    1, 1, folder + "rock/granite");
    materialRecipe(output, MaterialIds.blackstone, Ingredient.of(TinkerTags.Items.BLACKSTONE), 1, 1, folder + "rock/blackstone");
    materialRecipe(output, MaterialIds.calcite,    Ingredient.of(Blocks.CALCITE),              1, 1, folder + "rock/calcite");
    materialRecipe(output, MaterialIds.flint,      Ingredient.of(Items.FLINT),                 1, 1, folder + "flint/flint");
    materialRecipe(output, MaterialIds.basalt,     Ingredient.of(TinkerTags.Items.BASALT),     1, 1, folder + "flint/basalt");
    materialRecipe(output, MaterialIds.deepslate,  Ingredient.of(TinkerTags.Items.DEEPSLATE),  1, 1, folder + "flint/deepslate");
    // copper - want to include oxidized and waxed
    ItemOutput copperIngot = ItemOutput.fromTag(Tags.Items.INGOTS_COPPER);
    materialRecipe(output, MaterialIds.copper, Ingredient.of(TinkerTags.Items.NUGGETS_COPPER), 1, 9, folder + "copper/nugget");
    materialRecipe(output, MaterialIds.copper, Ingredient.of(Tags.Items.INGOTS_COPPER),        1, 1, folder + "copper/ingot");
    materialRecipe(output, MaterialIds.copper, CompoundIngredient.of(Ingredient.of(Tags.Items.STORAGE_BLOCKS_COPPER), Ingredient.of(Blocks.WAXED_COPPER_BLOCK)), 9, 1, copperIngot, folder + "copper/block");
    materialRecipe(output, MaterialIds.oxidizedCopper, Ingredient.of(Blocks.EXPOSED_COPPER, Blocks.WEATHERED_COPPER, Blocks.OXIDIZED_COPPER, Blocks.WAXED_EXPOSED_COPPER, Blocks.WAXED_WEATHERED_COPPER, Blocks.WAXED_OXIDIZED_COPPER), 9, 1, copperIngot, folder + "copper/oxidized");
    // other tier 1
    materialRecipe(output, MaterialIds.bone,         Ingredient.of(TinkerTags.Items.BONES),    1, 1, folder + "bone");
    materialRecipe(output, MaterialIds.chorus,       Ingredient.of(Items.POPPED_CHORUS_FRUIT), 1, 1, folder + "chorus_popped");
    // tier 1 binding
    materialRecipe(output, MaterialIds.string,  Ingredient.of(Tags.Items.STRINGS),  1, 4, folder + "string");
    materialRecipe(output, MaterialIds.leather, Ingredient.of(Tags.Items.LEATHERS), 1, 1, folder + "leather");
    materialRecipe(output, MaterialIds.leather, Ingredient.of(Items.RABBIT_HIDE),  1, 2, folder + "rabbit_hide");
    materialRecipe(output, MaterialIds.vine,    Ingredient.of(Items.VINE),         1, 1, folder + "vine");
    materialRecipe(output, MaterialIds.cactus,  Ingredient.of(Blocks.CACTUS),      1, 1, folder + "cactus");
    materialRecipe(output, MaterialIds.feather, Ingredient.of(Items.FEATHER),      1, 1, folder + "feather");
    materialRecipe(output, MaterialIds.paper,   Ingredient.of(Items.PAPER),        1, 1, folder + "paper");
    materialRecipe(output, MaterialIds.leaves,  Ingredient.of(ItemTags.LEAVES),    1, 1, folder + "leaves");
    // tier 1 wool
    for (DyeColor color : DyeColor.values()) {
      String name = color.getName();
      materialRecipe(output, MaterialVariantId.create(MaterialIds.wool, name), Ingredient.of(SheepShearingRecipe.WOOL_BY_COLOR.get(color)), 1, 1, folder + "wool/" + name);
    }

    // tier 2
    metalMaterialRecipe(output, MaterialIds.iron, folder, "iron", false);
    metalMaterialRecipe(output, MaterialIds.gold, folder, "gold", false);
    materialRecipe(output, MaterialIds.searedStone,   Ingredient.of(TinkerSmeltery.searedBrick),       1, 1, folder + "seared_stone/brick");
    materialRecipe(output, MaterialIds.searedStone,   Ingredient.of(TinkerTags.Items.SEARED_BLOCKS),   4, 1, ItemOutput.fromItem(TinkerSmeltery.searedBrick), folder + "seared_stone/block");
    materialRecipe(output, MaterialIds.scorchedStone, Ingredient.of(TinkerSmeltery.scorchedBrick),     1, 1, folder + "scorched_stone/brick");
    materialRecipe(output, MaterialIds.scorchedStone, Ingredient.of(TinkerTags.Items.SCORCHED_BLOCKS), 4, 1, ItemOutput.fromItem(TinkerSmeltery.scorchedBrick), folder + "scorched_stone/block");
    materialRecipe(output, MaterialIds.venombone,     Ingredient.of(TinkerMaterials.venombone),        1, 1, folder + "venombone");
    metalMaterialRecipe(output, MaterialIds.roseGold, folder, "rose_gold", false);
    materialRecipe(output, MaterialIds.necroticBone, Ingredient.of(TinkerTags.Items.WITHER_BONES), 1, 1, folder + "necrotic_bone");
    materialRecipe(output, MaterialIds.endstone, Ingredient.of(Tags.Items.END_STONES), 1, 1, folder + "endstone");
    // ammo
    materialRecipe(output, MaterialIds.turtle,     Ingredient.of(Items.TURTLE_SCUTE),          1, 1, folder + "turtle_scute");
    materialRecipe(output, MaterialIds.earthslime, Ingredient.of(TinkerWorld.earthGeode),      1, 1, folder + "earthslime");
    materialRecipe(output, MaterialIds.skyslime,   Ingredient.of(TinkerWorld.skyGeode),        1, 1, folder + "skyslime");
    materialRecipe(output, MaterialIds.blaze,      Ingredient.of(Tags.Items.RODS_BLAZE),       1, 1, folder + "blaze");
    materialRecipe(output, MaterialIds.enderPearl, Ingredient.of(Tags.Items.ENDER_PEARLS),     1, 1, folder + "ender_pearl");
    materialRecipe(output, MaterialIds.amethyst,   Ingredient.of(Tags.Items.GEMS_AMETHYST),    1, 1, folder + "amethyst");
    materialRecipe(output, MaterialIds.prismarine, Ingredient.of(Tags.Items.DUSTS_PRISMARINE), 1, 1, folder + "prismarine");
    materialRecipe(output, MaterialIds.glass,      Ingredient.of(Tags.Items.GLASS),            4, 1, folder + "glass");
    materialRecipe(output, MaterialIds.glass,      Ingredient.of(Tags.Items.GLASS_PANES),      1, 1, folder + "glass_pane");

    materialRecipe(output, MaterialIds.skyslimeVine, Ingredient.of(TinkerWorld.skySlimeVine), 1, 1, folder + "skyslime_vine");
    materialRecipe(output, MaterialIds.weepingVine,  Ingredient.of(Items.WEEPING_VINES), 1, 1, folder + "weeping_vine");
    materialRecipe(output, MaterialIds.twistingVine, Ingredient.of(Items.TWISTING_VINES), 1, 1, folder + "twisting_vine");
    // slimewood
    materialRecipe(output, MaterialIds.greenheart,  Ingredient.of(TinkerWorld.greenheart),  1, 1, folder + "slimewood/greenheart_planks");
    materialRecipe(output, MaterialIds.skyroot,     Ingredient.of(TinkerWorld.skyroot),     1, 1, folder + "slimewood/skyroot_planks");
    materialRecipe(output, MaterialIds.bloodshroom, Ingredient.of(TinkerWorld.bloodshroom), 1, 1, folder + "slimewood/bloodshroom_planks");
    materialRecipe(output, MaterialIds.enderbark,   Ingredient.of(TinkerWorld.enderbark),   1, 1, folder + "slimewood/enderbark_planks");
    materialRecipe(output, MaterialIds.greenheart,  Ingredient.of(TinkerWorld.greenheart.getLogItemTag()),  4, 1, ItemOutput.fromItem(TinkerWorld.greenheart),  folder + "slimewood/greenheart_logs");
    materialRecipe(output, MaterialIds.skyroot,     Ingredient.of(TinkerWorld.skyroot.getLogItemTag()),     4, 1, ItemOutput.fromItem(TinkerWorld.skyroot),     folder + "slimewood/skyroot_logs");
    materialRecipe(output, MaterialIds.bloodshroom, Ingredient.of(TinkerWorld.bloodshroom.getLogItemTag()), 4, 1, ItemOutput.fromItem(TinkerWorld.bloodshroom), folder + "slimewood/bloodshroom_logs");
    materialRecipe(output, MaterialIds.enderbark,   Ingredient.of(TinkerWorld.enderbark.getLogItemTag()),   4, 1, ItemOutput.fromItem(TinkerWorld.enderbark),   folder + "slimewood/enderbark_logs");
    // slimeball
    for (SlimeType type : SlimeType.values()) {
      String name = type.getSerializedName();
      materialRecipe(output, MaterialVariantId.create(MaterialIds.slimeball, name), Ingredient.of(type.getSlimeballTag()), 1, 1, folder + "slimeball/" + name);
    }
    materialRecipe(output, MaterialIds.magma, Ingredient.of(Items.MAGMA_CREAM),1, 1, folder + "magma");
    materialRecipe(output, MaterialIds.clay, Ingredient.of(Items.CLAY_BALL),   1, 1, folder + "clay_ball");
    materialRecipe(output, MaterialIds.clay, Ingredient.of(Blocks.CLAY),       4, 1, folder + "clay_block");

    // tier 3
    metalMaterialRecipe(output, MaterialIds.slimesteel, folder, "slimesteel", false);
    materialRecipe(output, MaterialIds.nahuatl, Ingredient.of(TinkerMaterials.nahuatl), 1, 1, folder + "nahuatl");
    metalMaterialRecipe(output, MaterialIds.amethystBronze, folder, "amethyst_bronze", false);
    metalMaterialRecipe(output, MaterialIds.pigIron, folder, "pig_iron", false);
    materialRecipe(output, MaterialIds.obsidian, Ingredient.of(Items.OBSIDIAN),             4, 1, folder + "obsidian");
    materialRecipe(output, MaterialIds.obsidian, Ingredient.of(TinkerCommons.obsidianPane), 1, 1, folder + "obsidian_pane");
    // misc
    materialRecipe(output, MaterialIds.ice, Ingredient.of(Blocks.ICE),        1, 9, folder + "ice/unpacked");
    materialRecipe(output, MaterialIds.ice, Ingredient.of(Blocks.PACKED_ICE), 1, 1, folder + "ice/packed");
    materialRecipe(output, MaterialIds.ice, Ingredient.of(Blocks.BLUE_ICE),   9, 1, folder + "ice/blue");
    materialRecipe(output, MaterialIds.ichor, Ingredient.of(TinkerWorld.ichorGeode), 1, 1, folder + "ichor");
    materialRecipe(output, MaterialIds.quartz, Ingredient.of(Tags.Items.GEMS_QUARTZ),           1, 1, folder + "quartz/gem");
    materialRecipe(output, MaterialIds.quartz, Ingredient.of(Tags.Items.STORAGE_BLOCKS_QUARTZ), 4, 1, folder + "quartz/block");
    materialRecipe(output, MaterialIds.glowstone, Ingredient.of(Tags.Items.DUSTS_GLOWSTONE), 1, 4, folder + "glowstone/dust");
    materialRecipe(output, MaterialIds.glowstone, Ingredient.of(Blocks.GLOWSTONE), 1, 1, ItemOutput.fromItem(Items.GLOWSTONE_DUST),folder + "glowstone/block");
    materialRecipe(output, MaterialIds.magnetite, Ingredient.of(TinkerTags.Items.STEEL_SHARD), 1, 1, folder + "magnetite");
    materialRecipe(output, MaterialIds.kobold, Ingredient.of(TinkerTags.Items.COBALT_SHARD), 1, 1, folder + "kobold");
    materialRecipe(output, MaterialIds.gunpowder, Ingredient.of(Tags.Items.GUNPOWDER), 1, 4, folder + "gunpowder");
    materialRecipe(output, MaterialIds.redstone, Ingredient.of(Tags.Items.DUSTS_REDSTONE), 1, 4, folder + "redstone/dust");
    materialRecipe(output, MaterialIds.redstone, Ingredient.of(Tags.Items.STORAGE_BLOCKS_REDSTONE), 9, 4, ItemOutput.fromItem(Items.REDSTONE, 4), folder + "redstone/block");

    // tier 3 (nether)
    metalMaterialRecipe(output, MaterialIds.cobalt, folder, "cobalt", false);
    metalMaterialRecipe(output, MaterialIds.steel,  folder, "steel",  false);
    // tier 4
    metalMaterialRecipe(output, MaterialIds.cinderslime, folder, "cinderslime", false);
    metalMaterialRecipe(output, MaterialIds.queensSlime, folder, "queens_slime", false);
    metalMaterialRecipe(output, MaterialIds.manyullyn, folder, "manyullyn", false);
    metalMaterialRecipe(output, MaterialIds.hepatizon, folder, "hepatizon", false);
    metalMaterialRecipe(output, MaterialIds.knightmetal, folder, "knightmetal", false);
    metalMaterialRecipe(output, MaterialIds.knightslime, folder, "knightslime", false);
    materialRecipe(output, MaterialIds.blazewood, Ingredient.of(TinkerMaterials.blazewood), 1, 1, folder + "blazewood");
    materialRecipe(output, MaterialIds.blazingBone, Ingredient.of(TinkerMaterials.blazingBone), 1, 1, folder + "blazing_bone");
    //registerMetalMaterial(output, MaterialIds.soulsteel,   "soulsteel",    false);
    // debris has no storage block, just ingots and nuggets
    materialRecipe(output, MaterialIds.ancient, Ingredient.of(TinkerTags.Items.INGOTS_NETHERITE_SCRAP), 1, 1, folder + "ancient/ingot");
    materialRecipe(output, MaterialIds.ancient, Ingredient.of(TinkerTags.Items.NUGGETS_NETHERITE_SCRAP), 1, 9, folder + "ancient/nugget");
    materialRecipe(output, MaterialIds.dragonScale, Ingredient.of(TinkerModifiers.dragonScale), 1, 1, folder + "dragon_scale");
    materialRecipe(output, MaterialIds.shulker, Ingredient.of(Items.SHULKER_SHELL), 2, 1, folder + "shulker");
    materialRecipe(output, MaterialIds.endRod, Ingredient.of(Items.END_ROD), 1, 1, folder + "end_rod");
    materialRecipe(output, MaterialIds.knightly, Ingredient.of(TinkerTags.Items.KNIGHTMETAL_SHARD), 1, 1, folder + "knightly");

    // tier 5
    materialRecipe(output, MaterialIds.enderslimeVine, Ingredient.of(TinkerWorld.enderSlimeVine), 1, 1, folder + "enderslime_vine");

    // tier 2 (mod compat)
    metalMaterialRecipe(output, MaterialIds.osmium, folder, "osmium", true);
    metalMaterialRecipe(output, MaterialIds.ironwood, folder, "ironwood", true);
    metalMaterialRecipe(output, MaterialIds.silver, folder, "silver", true);
    metalMaterialRecipe(output, MaterialIds.lead, folder, "lead", true);
    metalMaterialRecipe(output, MaterialIds.aluminum, folder, "aluminum", true);
    materialRecipe(withCondition(output, tagCondition("treated_wood")),  MaterialIds.treatedWood, Ingredient.of(getItemTag(COMMON, "treated_wood")), 1, 1, folder + "treated_wood");
    // no whitestone, use repair kits
    // tier 3 (mod integration)
    metalMaterialRecipe(output, MaterialIds.bronze, folder, "bronze", true);
    metalMaterialRecipe(output, MaterialIds.constantan, folder, "constantan", true);
    metalMaterialRecipe(output, MaterialIds.invar, folder, "invar", true);
    metalMaterialRecipe(output, MaterialIds.pewter, folder, "pewter", true);
    materialRecipe(
      withCondition(output, new OrCondition(List.of(ConfigEnabledCondition.FORCE_INTEGRATION_MATERIALS, tagCondition("ingots/uranium")))),
      MaterialIds.necronium, Ingredient.of(TinkerMaterials.necroniumBone), 1, 1, folder + "necronium");
    metalMaterialRecipe(output, MaterialIds.electrum, folder, "electrum", true);
    metalMaterialRecipe(output, MaterialIds.steeleaf, folder, "steeleaf", true);
    // no plated slimewood, use repair kits
    // tier 4 (mod integration)
    metalMaterialRecipe(output, MaterialIds.fiery, folder, "fiery", true);
    metalMaterialRecipe(output, MaterialIds.nicrosil, folder, "nicrosil", true);

    // slimesuit
    materialRecipe(output, MaterialIds.enderslime, Ingredient.of(TinkerWorld.enderGeode), 1, 1, folder + "enderslime");
    materialRecipe(output, MaterialIds.phantom,    Ingredient.of(Items.PHANTOM_MEMBRANE), 1, 1, folder + "phantom_membrane");
  }

  private void addMaterialSmeltery(RecipeOutput output) {
    String folder = "tools/materials/";

    // melting and casting
    // tier 2
    materialMeltingCasting(output, MaterialIds.iron,          TinkerFluids.moltenIron,    folder);
    materialMeltingCasting(output, MaterialIds.copper,        TinkerFluids.moltenCopper,  folder);
    materialMeltingCasting(output, MaterialIds.gold,          TinkerFluids.moltenGold,    folder);
    materialMeltingCasting(output, MaterialIds.searedStone,   TinkerFluids.searedStone,   FluidValues.BRICK, folder);
    materialMeltingCasting(output, MaterialIds.scorchedStone, TinkerFluids.scorchedStone, FluidValues.BRICK, folder);
    // half a clay is 1 seared brick per grout amounts
    materialComposite(output, MaterialIds.rock, MaterialIds.searedStone,        TinkerFluids.moltenClay, FluidValues.BRICK / 2, folder);
    materialComposite(output, MaterialIds.flint, MaterialIds.scorchedStone,     TinkerFluids.magma,      FluidValues.SLIMEBALL / 2, folder);
    materialComposite(output, MaterialIds.wood,    MaterialIds.slimewoodComposite, TinkerFluids.earthSlime, FluidValues.SLIMEBALL, folder);
    materialComposite(output, MaterialIds.bone, MaterialIds.venombone,          TinkerFluids.venom,      FluidValues.SLIMEBALL, folder);
    // oxidize copper and iron via water, it does not rust iron because magic
    MaterialFluidRecipeBuilder.material(MaterialIds.oxidizedIron)
                              .setInputId(MaterialIds.iron)
                              .setFluid(MantleTags.Fluids.WATER, FluidValues.BOTTLE)
                              .setTemperature(1)
                              .save(output, location(folder + "composite/iron_oxidized"));
    MaterialFluidRecipeBuilder.material(MaterialIds.oxidizedCopper)
                              .setInputId(MaterialIds.copper)
                              .setFluid(MantleTags.Fluids.WATER, FluidValues.BOTTLE)
                              .setTemperature(1)
                              .save(output, location(folder + "composite/copper_oxidized"));
    // slimeskin
    String slimeskinFolder = folder + "slimeskin/";
    materialComposite(output, MaterialIds.leather,   MaterialIds.slimeskin,      TinkerFluids.earthSlime, FluidValues.SLIMEBALL, slimeskinFolder, "earth");
    materialComposite(output, MaterialIds.leather,   MaterialIds.skySlimeskin,   TinkerFluids.skySlime,   FluidValues.SLIMEBALL, slimeskinFolder, "sky");
    materialComposite(output, MaterialIds.leather,   MaterialIds.ichorskin,      TinkerFluids.ichor,      FluidValues.SLIMEBALL, slimeskinFolder, "ichor");
    materialComposite(output, MaterialIds.leather,   MaterialIds.enderSlimeskin, TinkerFluids.enderSlime, FluidValues.SLIMEBALL, slimeskinFolder, "ender");
    materialComposite(output, MaterialIds.slimeskin,      MaterialIds.leather, TinkerFluids.venom, FluidValues.SIP, slimeskinFolder, "earth_cleaning");
    materialComposite(output, MaterialIds.skySlimeskin,   MaterialIds.leather, TinkerFluids.venom, FluidValues.SIP, slimeskinFolder, "sky_cleaning");
    materialComposite(output, MaterialIds.ichorskin,      MaterialIds.leather, TinkerFluids.venom, FluidValues.SIP, slimeskinFolder, "ichor_cleaning");
    materialComposite(output, MaterialIds.enderSlimeskin, MaterialIds.leather, TinkerFluids.venom, FluidValues.SIP, slimeskinFolder, "ender_cleaning");

    // tier 3
    materialMeltingCasting(output, MaterialIds.slimesteel,     TinkerFluids.moltenSlimesteel, folder);
    materialMeltingCasting(output, MaterialIds.amethystBronze, TinkerFluids.moltenAmethystBronze, folder);
    materialMeltingCasting(output, MaterialIds.roseGold,       TinkerFluids.moltenRoseGold, folder);
    materialMeltingCasting(output, MaterialIds.pigIron,        TinkerFluids.moltenPigIron, folder);
    materialMeltingCasting(output, MaterialIds.cobalt,         TinkerFluids.moltenCobalt, folder);
    materialMeltingCasting(output, MaterialIds.steel,          TinkerFluids.moltenSteel, folder);
    materialMeltingCasting(output, MaterialIds.obsidian,       TinkerFluids.moltenObsidian, FluidValues.GLASS_PANE, folder);
    // allow rose gold as a bowstring by string composite, means we also get a redundant binding recipe, but thats fine
    materialComposite(output,        MaterialIds.string, MaterialIds.roseGold,   TinkerFluids.moltenRoseGold, FluidValues.INGOT, folder);
    materialMeltingComposite(output, MaterialIds.wood,   MaterialIds.nahuatl,    TinkerFluids.moltenObsidian, FluidValues.GLASS_PANE, folder);
    materialMeltingComposite(output, MaterialIds.string, MaterialIds.darkthread, TinkerFluids.moltenObsidian, FluidValues.GLASS_PANE, folder);
    MaterialMeltingRecipeBuilder.material(MaterialIds.ice, 10, FluidOutput.fromFluid(Fluids.WATER, FluidType.BUCKET_VOLUME * 9))
      .save(output, location(folder + "melting/ice"));
    materialComposite(output, MaterialIds.blaze, MaterialIds.jadeite, TinkerFluids.moltenEmerald, FluidValues.GEM, folder);

    // tier 4
    materialMeltingCasting(output, MaterialIds.cinderslime, TinkerFluids.moltenCinderslime, folder);
    materialMeltingCasting(output, MaterialIds.queensSlime, TinkerFluids.moltenQueensSlime, folder);
    materialMeltingCasting(output, MaterialIds.hepatizon,   TinkerFluids.moltenHepatizon,   folder);
    materialMeltingCasting(output, MaterialIds.manyullyn,   TinkerFluids.moltenManyullyn,   folder);
    materialMeltingCasting(output, MaterialIds.knightmetal, TinkerFluids.moltenKnightmetal, folder);
    materialMeltingCasting(output, MaterialIds.knightslime, TinkerFluids.moltenKnightslime, folder);
    materialComposite(output, MaterialIds.bloodshroom,  MaterialIds.blazewood,   TinkerFluids.blazingBlood, FluidType.BUCKET_VOLUME / 5, folder);
    materialComposite(output, MaterialIds.necroticBone, MaterialIds.blazingBone, TinkerFluids.blazingBlood, FluidType.BUCKET_VOLUME / 5, folder);
    materialMeltingComposite(output, MaterialIds.leather, MaterialIds.jeweledHide, TinkerFluids.moltenDiamond, FluidValues.GEM, folder);
    materialComposite(output, MaterialIds.jeweledHide, MaterialIds.leather, TinkerFluids.venom, FluidValues.SIP, folder, "jeweled_hide_cleaning");
    materialMelting(output, MaterialIds.ancientHide, TinkerFluids.moltenDebris, FluidValues.INGOT, folder);
    materialComposite(output, MaterialIds.ancientHide, MaterialIds.leather, TinkerFluids.venom, FluidValues.SIP, folder, "ancient_hide_cleaning");

    // tier 2 compat
    compatMeltingCasting(output, MaterialIds.osmium,   TinkerFluids.moltenOsmium,   folder);
    compatMeltingCasting(output, MaterialIds.silver,   TinkerFluids.moltenSilver,   folder);
    compatMeltingCasting(output, MaterialIds.lead,     TinkerFluids.moltenLead,     folder);
    compatMeltingCasting(output, MaterialIds.aluminum, TinkerFluids.moltenAluminum, folder);
    whitestoneCasting(output, TinkerFluids.moltenAluminum, folder);
    whitestoneCasting(output, TinkerFluids.moltenTin,      folder);
    whitestoneCasting(output, TinkerFluids.moltenZinc,     folder);
    whitestoneCasting(output, TinkerFluids.moltenNickel,   folder);
    whitestoneCasting(output, TinkerFluids.moltenChromium, folder);
    whitestoneCasting(output, TinkerFluids.moltenCadmium,  folder);
    TagKey<Fluid> creosote = getFluidTag(COMMON, "creosote");
    MaterialFluidRecipeBuilder.material(MaterialIds.treatedWood)
      .setInputId(MaterialIds.wood)
      .setFluid(FluidIngredient.of(creosote, 125))
      .setTemperature(600)
      .save(withCondition(output, new TagFilledCondition<>(creosote)), location(folder + "composite/treated_wood"));
    MaterialMeltingRecipeBuilder.material(MaterialIds.ironwood, TinkerFluids.moltenIron, FluidValues.INGOT)
      .addByproduct(TinkerFluids.moltenGold.result(FluidValues.NUGGET))
      .save(withCondition(output, tagCondition("ingots/ironwood")), location(folder + "melting/ironwood"));
    // tier 3 compat
    compatMeltingCasting(output, MaterialIds.constantan, TinkerFluids.moltenConstantan, "nickel", folder);
    compatMeltingCasting(output, MaterialIds.invar,      TinkerFluids.moltenInvar,      "nickel", folder);
    compatMeltingCasting(output, MaterialIds.electrum,   TinkerFluids.moltenElectrum,   "silver", folder);
    compatMeltingCasting(output, MaterialIds.bronze,     TinkerFluids.moltenBronze,     "tin", folder);
    compatMeltingCasting(output, MaterialIds.steeleaf,   TinkerFluids.moltenSteeleaf, folder);
    // pewter has two different ores that let it appear, tin and lead
    materialMeltingCasting(
      withCondition(output, new OrCondition(List.of(tagCondition("ingots/pewter"), tagCondition("ingots/tin"), tagCondition("ingots/lead")))),
      MaterialIds.pewter, TinkerFluids.moltenPewter, folder);
    materialMeltingComposite(withCondition(output, tagCondition("ingots/uranium")), MaterialIds.necroticBone, MaterialIds.necronium, TinkerFluids.moltenUranium, FluidValues.INGOT, folder);
    materialMeltingComposite(withCondition(output, new OrCondition(List.of(tagCondition("ingots/brass"), tagCondition("ingots/zinc")))),
                             MaterialIds.slimewood, MaterialIds.platedSlimewood, TinkerFluids.moltenBrass, FluidValues.INGOT, folder);
    // tier 4 compat
    RecipeOutput fieryConsumer = withCondition(output, tagCondition("ingots/fiery"));
    materialComposite(fieryConsumer, MaterialIds.iron, MaterialIds.fiery, TinkerFluids.fieryLiquid, FluidValues.BOTTLE, folder);
    MaterialMeltingRecipeBuilder.material(MaterialIds.fiery, TinkerFluids.fieryLiquid, FluidValues.BOTTLE)
      .addByproduct(TinkerFluids.moltenIron.result(FluidValues.INGOT))
      .save(fieryConsumer, location(folder + "melting/fiery"));
    // nicrosil has three different ores that let it appear, tin, nickel, and chromium
    materialMeltingCasting(
      withCondition(output, new OrCondition(List.of(tagCondition("ingots/nicrosil"), tagCondition("ingots/tin"), tagCondition("ingots/nickel"), tagCondition("ingots/chromium")))),
      MaterialIds.nicrosil, TinkerFluids.moltenNicrosil, folder);

    // slimesuit - slime
    materialMeltingCasting(output, MaterialIds.earthslime, TinkerFluids.earthSlime, FluidValues.SLIMEBALL, folder);
    materialMeltingCasting(output, MaterialIds.skyslime,   TinkerFluids.skySlime,   FluidValues.SLIMEBALL, folder);
    materialMeltingCasting(output, MaterialIds.ichor,      TinkerFluids.ichor,      FluidValues.SLIMEBALL, folder);
    materialMeltingCasting(output, MaterialIds.enderslime, TinkerFluids.enderSlime, FluidValues.SLIMEBALL, folder);
    materialMeltingCasting(output, MaterialIds.magma,      TinkerFluids.magma,      FluidValues.SLIMEBALL, folder);
    // slimesuit - pseudoslime
    materialMeltingCasting(output, MaterialIds.clay,       TinkerFluids.moltenClay,  FluidValues.BRICK,    folder);
    materialMeltingCasting(output, MaterialIds.enderPearl, TinkerFluids.moltenEnder, FluidValues.SLIMEBALL, folder);
    // slimesuit - repair kits
    materialMeltingCasting(output, MaterialIds.glass, TinkerFluids.moltenGlass, FluidValues.GLASS_PANE, folder);
  }

  /** Adds a  */
  private void whitestoneCasting(RecipeOutput output, FluidObject<?> fluid, String folder) {
    String name = TinkerFluids.withoutMolten(fluid);
    materialComposite(withCondition(output, tagCondition("ingots/" + name)), MaterialIds.rock, MaterialIds.whitestoneComposite, fluid, FluidValues.INGOT, folder, "whitestone_from_" + name);
  }
}
