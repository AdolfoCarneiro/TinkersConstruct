package slimeknights.tconstruct.tools.data;

import net.minecraft.data.PackOutput;
import net.minecraft.core.HolderLookup;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import net.neoforged.neoforge.common.crafting.DifferenceIngredient;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import slimeknights.mantle.recipe.data.ItemNameIngredient;
import slimeknights.mantle.recipe.ingredient.PotionDisplayIngredient;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.data.BaseRecipeProvider;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.data.recipe.IMaterialRecipeHelper;
import slimeknights.tconstruct.library.data.recipe.IToolRecipeHelper;
import slimeknights.tconstruct.library.json.predicate.material.MaterialHasPartPredicate;
import slimeknights.tconstruct.library.json.predicate.material.MaterialPredicate;
import slimeknights.tconstruct.library.json.predicate.material.MaterialStatTypePredicate;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.material.CompositeCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.material.PartSwapCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.material.ToolCastingRecipe.CastPurpose;
import slimeknights.tconstruct.library.recipe.ingredient.MaterialIngredient;
import slimeknights.tconstruct.library.recipe.ingredient.MaterialValueIngredient;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.recipe.material.MaterialsConsumerBuilder;
import slimeknights.tconstruct.library.recipe.partbuilder.PartRecipeBuilder;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;
import slimeknights.tconstruct.library.recipe.partbuilder.recycle.PartBuilderRecycleBuilder;
import slimeknights.tconstruct.library.recipe.partbuilder.recycle.PartBuilderToolRecycleBuilder;
import slimeknights.tconstruct.library.recipe.tinkerstation.building.MaterialSwappingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.tinkerstation.building.ToolBuildingRecipeBuilder;
import slimeknights.tconstruct.library.tools.layout.Patterns;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.data.material.MaterialIds;
import slimeknights.tconstruct.tools.stats.PlatingMaterialStats;
import slimeknights.tconstruct.tools.stats.StatlessMaterialStats;
import slimeknights.tconstruct.world.TinkerHeadType;
import slimeknights.tconstruct.world.TinkerWorld;


import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class ToolsRecipeProvider extends BaseRecipeProvider implements IMaterialRecipeHelper, IToolRecipeHelper {
  public ToolsRecipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
    super(packOutput, registries);
  }

  @Override
  protected void buildRecipes(RecipeOutput output) {
    this.addToolBuildingRecipes(output);
    this.addPartRecipes(output);
    this.addRecycleRecipes(output);
  }

  private void addToolBuildingRecipes(RecipeOutput output) {
    String folder = "tools/building/";
    String armorFolder = "tools/armor/";
    // stone
    toolBuilding(output, TinkerTools.pickaxe, folder);
    toolBuilding(output, TinkerTools.sledgeHammer, folder);
    toolBuilding(output, TinkerTools.veinHammer, folder);
    // dirt
    toolBuilding(output, TinkerTools.mattock, folder);
    toolBuilding(output, TinkerTools.pickadze, folder);
    toolBuilding(output, TinkerTools.excavator, folder);
    // wood
    toolBuilding(output, TinkerTools.handAxe, folder);
    toolBuilding(output, TinkerTools.broadAxe, folder);
    // plants
    toolBuilding(output, TinkerTools.kama, folder);
    toolBuilding(output, TinkerTools.scythe, folder);
    // sword
    ToolBuildingRecipeBuilder.toolBuildingRecipe(TinkerTools.dagger.get())
                             .outputSize(2)
                             .save(output, prefix(TinkerTools.dagger, folder));
    toolBuilding(output, TinkerTools.sword, folder);
    toolBuilding(output, TinkerTools.cleaver, folder);
    // bow
    toolBuilding(output, TinkerTools.crossbow, folder);
    toolBuilding(output, TinkerTools.longbow, folder);
    toolBuilding(output, TinkerTools.fishingRod, folder);
    toolBuilding(output, TinkerTools.javelin, folder);
    // ammo
    ToolBuildingRecipeBuilder.toolBuildingRecipe(TinkerTools.arrow.get())
      .outputSize(4)
      .save(output, prefix(TinkerTools.arrow, folder));
    ToolBuildingRecipeBuilder.toolBuildingRecipe(TinkerTools.shuriken.get())
      .layoutSlot(Patterns.THROWN_AMMO)
      .outputSize(4)
      .save(output, prefix(TinkerTools.shuriken, folder));
    ToolBuildingRecipeBuilder.toolBuildingRecipe(TinkerTools.throwingAxe.get())
      .layoutSlot(Patterns.THROWN_AMMO)
      .outputSize(2)
      .save(output, prefix(TinkerTools.throwingAxe, folder));
    ToolBuildingRecipeBuilder.toolBuildingRecipe(TinkerTools.arrow.get())
      .addExtraRequirement(Ingredient.of(Items.ARROW))
      .noParts()
      .addExtraMaterial(MaterialIds.flint, MaterialIds.wood, MaterialIds.feather)
      .layoutSlot(TinkerTables.tinkerStation.getId())
      .save(output, wrap(TinkerTools.arrow, folder, "_from_vanilla"));
    ToolBuildingRecipeBuilder.toolBuildingRecipe(TinkerTools.arrow.get())
      .addExtraRequirement(PotionDisplayIngredient.of(Items.TIPPED_ARROW))
      .noParts()
      .addExtraMaterial(MaterialIds.flint, MaterialIds.wood, MaterialIds.feather)
      .tippedModifier(ModifierIds.tipped)
      .layoutSlot(TinkerTables.tinkerStation.getId())
      .save(output, wrap(TinkerTools.arrow, folder, "_from_tipped"));

    // specialized
    ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, TinkerTools.flintAndBrick)
                          .requires(Items.FLINT)
                          .requires(Ingredient.of(TinkerSmeltery.searedBrick, TinkerSmeltery.scorchedBrick))
                          .unlockedBy("has_seared", has(TinkerSmeltery.searedBrick))
                          .unlockedBy("has_scorched", has(TinkerSmeltery.scorchedBrick))
                          .save(output, prefix(TinkerTools.flintAndBrick, folder));

    // staff
    ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, TinkerTools.skyStaff)
                       .pattern("CWC")
                       .pattern(" I ")
                       .pattern(" W ")
                       .define('C', TinkerWorld.skyGeode)
                       .define('W', TinkerWorld.skyroot.getLogItemTag())
                       .define('I', TinkerMaterials.roseGold.getIngotTag())
                       .unlockedBy("has_wood", has(TinkerWorld.skyroot.getLogItemTag()))
                       .save(output, prefix(TinkerTools.skyStaff, folder));
    ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, TinkerTools.earthStaff)
                       .pattern("CWC")
                       .pattern(" I ")
                       .pattern(" W ")
                       .define('C', TinkerWorld.earthGeode)
                       .define('W', TinkerWorld.greenheart.getLogItemTag())
                       .define('I', TinkerMaterials.cobalt.getIngotTag())
                       .unlockedBy("has_wood", has(TinkerWorld.greenheart.getLogItemTag()))
                       .save(output, prefix(TinkerTools.earthStaff, folder));
    ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, TinkerTools.ichorStaff)
                       .pattern("CWC")
                       .pattern(" I ")
                       .pattern(" W ")
                       .define('C', TinkerWorld.ichorGeode)
                       .define('W', TinkerWorld.bloodshroom.getLogItemTag())
                       .define('I', TinkerMaterials.queensSlime.getIngotTag())
                       .unlockedBy("has_wood", has(TinkerWorld.bloodshroom.getLogItemTag()))
                       .save(output, prefix(TinkerTools.ichorStaff, folder));
    ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, TinkerTools.enderStaff)
                       .pattern("CWC")
                       .pattern(" I ")
                       .pattern(" W ")
                       .define('C', TinkerWorld.enderGeode)
                       .define('W', TinkerWorld.enderbark.getLogItemTag())
                       .define('I', Tags.Items.INGOTS_NETHERITE)
                       .unlockedBy("has_wood", has(TinkerWorld.enderbark.getLogItemTag()))
                       .save(output, prefix(TinkerTools.enderStaff, folder));

    // travelers gear
    String travelersFolder = armorFolder + "travelers/";
    RecipeOutput shapedMaterial = MaterialsConsumerBuilder.shaped("c").material(MaterialIds.leather).build(output);
    // fake ingot allows things like bronze and pewter to craft it even if their ingot form is not registered
    Function<MaterialStatsId,Ingredient> travelersMaterial = type -> CompoundIngredient.of(
      MaterialValueIngredient.of(MaterialPredicate.and(MaterialPredicate.or(MaterialPredicate.CASTABLE, MaterialPredicate.COMPOSITE), new MaterialStatTypePredicate(type)), 1),
      MaterialIngredient.of(TinkerToolParts.fakeIngot, new MaterialStatTypePredicate(type))
    );
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, TinkerTools.travelersGear.get(ArmorItem.Type.HELMET))
      .pattern("l l")
      .pattern("glg")
      .pattern("c c")
      .define('c', travelersMaterial.apply(PlatingMaterialStats.HELMET.getId()))
      .define('l', Tags.Items.LEATHER)
      .define('g', Tags.Items.GLASS_PANES_COLORLESS)
      .unlockedBy("has_item", has(Tags.Items.LEATHER))
      .save(shapedMaterial, location(travelersFolder + "goggles"));
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, TinkerTools.travelersGear.get(ArmorItem.Type.CHESTPLATE))
      .pattern("l l")
      .pattern("lcl")
      .pattern("lcl")
      .define('c', travelersMaterial.apply(PlatingMaterialStats.CHESTPLATE.getId()))
      .define('l', Tags.Items.LEATHER)
      .unlockedBy("has_item", has(Tags.Items.LEATHER))
      .save(shapedMaterial, location(travelersFolder + "chestplate"));
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, TinkerTools.travelersGear.get(ArmorItem.Type.LEGGINGS))
      .pattern("lll")
      .pattern("c c")
      .pattern("l l")
      .define('c', travelersMaterial.apply(PlatingMaterialStats.LEGGINGS.getId()))
      .define('l', Tags.Items.LEATHER)
      .unlockedBy("has_item", has(Tags.Items.LEATHER))
      .save(shapedMaterial, location(travelersFolder + "pants"));
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, TinkerTools.travelersGear.get(ArmorItem.Type.BOOTS))
      .pattern("c c")
      .pattern("l l")
      .define('c', travelersMaterial.apply(PlatingMaterialStats.BOOTS.getId()))
      .define('l', Tags.Items.LEATHER)
      .unlockedBy("has_item", has(Tags.Items.LEATHER))
      .save(shapedMaterial, location(travelersFolder + "boots"));
    // shield needs no special variants, no compat shield cores exist
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, TinkerTools.travelersShield)
                       .pattern("cl")
                       .pattern("lc")
                       .define('l', Tags.Items.LEATHER)
                       .define('c', MaterialValueIngredient.of(new MaterialStatTypePredicate(StatlessMaterialStats.SHIELD_CORE.getIdentifier()), 1))
                       .unlockedBy("has_item", has(Tags.Items.LEATHER))
                       .save(shapedMaterial, location(travelersFolder + "shield"));

    // travelers part swapping
    PartSwapCastingRecipeBuilder.tableRecipe(Ingredient.of(TinkerTools.travelersGear.get(ArmorItem.Type.HELMET)), 3)
      .index(1)
      .save(output, location(travelersFolder + "goggles_leather"));
    PartSwapCastingRecipeBuilder.tableRecipe(Ingredient.of(TinkerTools.travelersGear.get(ArmorItem.Type.CHESTPLATE)), 6)
      .index(1)
      .save(output, location(travelersFolder + "chestplate_leather"));
    PartSwapCastingRecipeBuilder.tableRecipe(Ingredient.of(TinkerTools.travelersGear.get(ArmorItem.Type.LEGGINGS)), 5)
      .index(1)
      .save(output, location(travelersFolder + "pants_leather"));
    PartSwapCastingRecipeBuilder.tableRecipe(Ingredient.of(TinkerTools.travelersGear.get(ArmorItem.Type.BOOTS)), 2)
      .index(1)
      .save(output, location(travelersFolder + "boots_leather"));
    PartSwapCastingRecipeBuilder.tableRecipe(Ingredient.of(TinkerTools.travelersShield), 2)
      .index(1)
      .save(output, location(travelersFolder + "shield_leather"));
    PartSwapCastingRecipeBuilder.tableRecipe(Ingredient.of(TinkerTools.travelersGear.values().toArray(new Item[0])), 2)
      .save(output, location(travelersFolder + "swapping_metal"));

    // plate armor
    String plateFolder = armorFolder + "plate/";
    TinkerTools.plateArmor.forEach(item -> toolBuilding(output, item, plateFolder, Patterns.PLATE_ARMOR));
    MaterialCastingRecipeBuilder.tableRecipe(TinkerTools.plateShield.get())
                                .setPart(TinkerToolParts.shieldCore, true)
                                .setItemCost(3)
                                .save(output, location(plateFolder + "plate_shield"));
    PartSwapCastingRecipeBuilder.tableRecipe(Ingredient.of(TinkerTools.plateArmor.get(ArmorItem.Type.HELMET)), 3)
      .save(output, location(plateFolder + "helmet_swapping"));
    PartSwapCastingRecipeBuilder.tableRecipe(Ingredient.of(TinkerTools.plateArmor.get(ArmorItem.Type.CHESTPLATE)), 6)
      .save(output, location(plateFolder + "chestplate_swapping"));
    PartSwapCastingRecipeBuilder.tableRecipe(Ingredient.of(TinkerTools.plateArmor.get(ArmorItem.Type.LEGGINGS)), 5)
      .save(output, location(plateFolder + "leggings_swapping"));
    PartSwapCastingRecipeBuilder.tableRecipe(Ingredient.of(TinkerTools.plateArmor.get(ArmorItem.Type.BOOTS)), 2)
      .save(output, location(plateFolder + "boots_swapping"));

    // slimeskull
    slimeskull(output, MaterialIds.glass,       Items.CREEPER_HEAD, armorFolder);
    slimeskull(output, MaterialIds.dragonScale, Items.DRAGON_HEAD,  armorFolder);
    slimeskull(output, MaterialIds.enderPearl, TinkerWorld.heads.get(TinkerHeadType.ENDERMAN), armorFolder);
    slimeskull(output, MaterialIds.blaze,      TinkerWorld.heads.get(TinkerHeadType.BLAZE),    armorFolder);
    // zombie
    slimeskull(output, MaterialIds.leather, Items.ZOMBIE_HEAD, armorFolder);
    slimeskull(output, MaterialIds.iron,   TinkerWorld.heads.get(TinkerHeadType.HUSK),    armorFolder);
    slimeskull(output, MaterialIds.copper, TinkerWorld.heads.get(TinkerHeadType.DROWNED), armorFolder);
    // spider
    slimeskull(output, MaterialIds.string,     TinkerWorld.heads.get(TinkerHeadType.SPIDER),      armorFolder);
    slimeskull(output, MaterialIds.darkthread, TinkerWorld.heads.get(TinkerHeadType.CAVE_SPIDER), armorFolder);
    // skeleton
    slimeskull(output, MaterialIds.bone,         Items.SKELETON_SKULL,        armorFolder);
    slimeskull(output, MaterialIds.necroticBone, Items.WITHER_SKELETON_SKULL, armorFolder);
    slimeskull(output, MaterialIds.ice, TinkerWorld.heads.get(TinkerHeadType.STRAY), armorFolder);
    // piglin
    slimeskull(output, MaterialIds.gold, Items.PIGLIN_HEAD, armorFolder);
    slimeskull(output, MaterialIds.roseGold, TinkerWorld.heads.get(TinkerHeadType.PIGLIN_BRUTE),     armorFolder);
    slimeskull(output, MaterialIds.pigIron, TinkerWorld.heads.get(TinkerHeadType.ZOMBIFIED_PIGLIN), armorFolder);
    // crafted
    slimeskull(output, MaterialIds.venombone,   TinkerWorld.heads.get(TinkerHeadType.VENOMBONE),        armorFolder);
    slimeskull(output, MaterialIds.blazingBone, TinkerWorld.heads.get(TinkerHeadType.BLAZING_BONE),     armorFolder);
    slimeskull(output, MaterialIds.necronium,   TinkerWorld.heads.get(TinkerHeadType.NECRONIUM),        armorFolder);
    slimeskull(output, MaterialIds.knightmetal, TinkerSmeltery.endFluidCannon.get(),                    armorFolder);

    // slimelytra
    MaterialCastingRecipeBuilder.basinRecipe(TinkerTools.slimeWings.get())
      .setCast(Items.ELYTRA, CastPurpose.CONSUMED)
      .setItemCost(8)
      .save(output, location(armorFolder + "slimelytra"));

    // slimecage
    MaterialCastingRecipeBuilder.basinRecipe(TinkerTools.slimesuit.get(ArmorItem.Type.CHESTPLATE))
      .setPart(TinkerToolParts.ribcage, true)
      .setItemCost(8)
      .save(output, location(folder + "slimecage"));
    // slimeshell
    MaterialCastingRecipeBuilder.basinRecipe(TinkerTools.slimesuit.get(ArmorItem.Type.LEGGINGS))
      .setPart(TinkerToolParts.shell, true)
      .setItemCost(7)
      .save(output, location(folder + "slimeshell"));
    // slime boots
    MaterialCastingRecipeBuilder.basinRecipe(TinkerTools.slimesuit.get(ArmorItem.Type.BOOTS))
      .setPart(TinkerToolParts.laces, true)
      .setItemCost(4)
      .save(output, location(folder + "slime_boots"));
  }

  private void addRecycleRecipes(RecipeOutput output) {
    String folder = "tools/recycling/";

    // main recycling recipe - uses tool definition for parts list
    PartBuilderToolRecycleBuilder.tools(SizedIngredient.of(DifferenceIngredient.of(Ingredient.of(TinkerTags.Items.MULTIPART_TOOL), Ingredient.of(TinkerTags.Items.UNRECYCLABLE))))
        .save(output, location(folder + "general"));
    // daggers want to enforce stack size 2 when recycling to prevent dupes
    PartBuilderToolRecycleBuilder.tools(SizedIngredient.fromItems(2, TinkerTools.dagger))
      .save(output, location(folder + "dagger"));

    // travelers gear has a part for the plating, but that would be a dupe in all cases other than boot plating
    // plus, the boots plating won't let you recover travelers gear, so just recycle to repair kit
    PartBuilderToolRecycleBuilder.tools(SizedIngredient.fromItems(TinkerTools.travelersGear.values().toArray(Item[]::new)))
      // repair kit cost matches exactly
      .part(TinkerToolParts.repairKit)
      // bit of a material loss on some travelers pieces, but better than no recycling, right?
      .part(TinkerToolParts.maille)
      .save(output, location(folder + "travelers_gear"));
    PartBuilderToolRecycleBuilder.tool(TinkerTools.travelersShield)
      // repair kit cost matches exactly; would give you a shield core but that costs 4
      .part(TinkerToolParts.repairKit)
      .part(TinkerToolParts.maille)
      .save(output, location(folder + "travelers_shield"));

    // plate shields don't have a real tool part for the plating
    PartBuilderToolRecycleBuilder.tool(TinkerTools.plateShield)
      .part(TinkerToolParts.shieldCore)
      // repair kit costs 2 instead of 3, but is otherwise a good substitute
      .part(TinkerToolParts.repairKit)
      .save(output, location(folder + "plate_shield"));

    // TODO: consider if I want slimesuit recycling, it gets wierd with skull in particular needing a custom recipe likely

    // crafting table tool recycling
    // flint and brick loses the brick as we don't know if you used seared or scorched
    PartBuilderRecycleBuilder.tool(TinkerTools.flintAndBrick)
      .result(new Pattern(TConstruct.MOD_ID, "shard"), Items.FLINT, 1)
      .save(output, location(folder + "flint_and_brick"));
    // slimestaff
    Pattern log = new Pattern(TConstruct.MOD_ID, "block");
    Pattern ingot = new Pattern(TConstruct.MOD_ID, "ingot");
    Pattern crystal = new Pattern(TConstruct.MOD_ID, "crystal");
    PartBuilderRecycleBuilder.tool(TinkerTools.earthStaff)
      .result(crystal, TinkerWorld.earthGeode, 2)
      .result(log, TinkerWorld.greenheart.getLog(), 2)
      .result(ingot, TinkerMaterials.cobalt.getIngotTag(), 1)
      .save(output, location(folder + "earth_staff"));
    PartBuilderRecycleBuilder.tool(TinkerTools.skyStaff)
      .result(crystal, TinkerWorld.skyGeode, 2)
      .result(log, TinkerWorld.skyroot.getLog(), 2)
      .result(ingot, TinkerMaterials.roseGold.getIngotTag(), 1)
      .save(output, location(folder + "sky_staff"));
    PartBuilderRecycleBuilder.tool(TinkerTools.ichorStaff)
      .result(crystal, TinkerWorld.ichorGeode, 2)
      .result(log, TinkerWorld.bloodshroom.getLog(), 2)
      .result(ingot, TinkerMaterials.queensSlime.getIngotTag(), 1)
      .save(output, location(folder + "ichor_staff"));
    PartBuilderRecycleBuilder.tool(TinkerTools.enderStaff)
      .result(crystal, TinkerWorld.enderGeode, 2)
      .result(log, TinkerWorld.enderbark.getLog(), 2)
      .result(ingot, Tags.Items.INGOTS_NETHERITE, 1)
      .save(output, location(folder + "ender_staff"));


    // ancient tools are not craftable so no default recycling. Give them the canonical parts for recycling
    PartBuilderToolRecycleBuilder.tool(TinkerTools.meltingPan)
      // again, no shield plating part; repair kit is good enough
      .part(TinkerToolParts.repairKit)
      .part(TinkerToolParts.bowLimb)
      .save(output, location(folder + "melting_pan"));
    PartBuilderToolRecycleBuilder.tool(TinkerTools.warPick)
      .part(TinkerToolParts.pickHead)
      .part(TinkerToolParts.bowLimb)
      .part(TinkerToolParts.bowstring)
      .save(output, location(folder + "war_pick"));
    PartBuilderToolRecycleBuilder.tool(TinkerTools.battlesign)
      .part(TinkerToolParts.largePlate)
      .part(TinkerToolParts.repairKit)
      .save(output, location(folder + "battlesign"));
    PartBuilderToolRecycleBuilder.tool(TinkerTools.swasher)
      .part(TinkerToolParts.smallBlade)
      .part(TinkerToolParts.toolHandle)
      .part(TinkerToolParts.bowGrip)
      .save(output, location(folder + "swasher"));
    PartBuilderToolRecycleBuilder.tools(SizedIngredient.of(ItemNameIngredient.from(TinkerTools.minotaurAxe.getId())))
      .part(TinkerToolParts.smallAxeHead)
      .part(TinkerToolParts.repairKit)
      .part(TinkerToolParts.toolHandle)
      .save(withCondition(output, new ModLoadedCondition("twilightforest")), location(folder + "minotaur_axe"));
  }

  private void addPartRecipes(RecipeOutput output) {
    String partFolder = "tools/parts/";
    String castFolder = "smeltery/casts/";
    partRecipes(output, TinkerToolParts.repairKit, TinkerSmeltery.repairKitCast, 2, partFolder, castFolder);
    partCasting(output, TinkerToolParts.fakeIngot.get(), TinkerSmeltery.ingotCast, 1, partFolder);
    // fake storage items
    MaterialCastingRecipeBuilder.basinRecipe(TinkerToolParts.fakeStorageBlockItem.get())
      .setItemCost(9)
      .save(output, location(partFolder + "fake_storage_block_casting"));
    CompositeCastingRecipeBuilder.basin(TinkerToolParts.fakeStorageBlockItem.get(), 9)
      .save(output, location(partFolder + "fake_storage_block_composite"));
    // ingot to block
    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TinkerToolParts.fakeStorageBlock)
      .define('#', MaterialIngredient.of(TinkerToolParts.fakeIngot.get(), new MaterialHasPartPredicate(TinkerToolParts.fakeStorageBlockItem.get())))
      .pattern("###")
      .pattern("###")
      .pattern("###")
      .unlockedBy("has_item", has(TinkerToolParts.fakeIngot))
      .save(MaterialsConsumerBuilder.shaped("#").build(output), location(partFolder + "fake_ingot_to_block"));
    // block to ingot
    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, TinkerToolParts.fakeIngot, 9)
      .requires(MaterialIngredient.of(TinkerToolParts.fakeStorageBlock, new MaterialHasPartPredicate(TinkerToolParts.fakeIngot.get())))
      .unlockedBy("has_item", has(TinkerToolParts.fakeStorageBlock))
      .save(MaterialsConsumerBuilder.shapeless(1).build(output), location(partFolder + "fake_block_to_ingots"));

    // head
    partRecipes(output, TinkerToolParts.pickHead,     TinkerSmeltery.pickHeadCast,     2, partFolder, castFolder);
    partRecipes(output, TinkerToolParts.hammerHead,   TinkerSmeltery.hammerHeadCast,   8, partFolder, castFolder);
    partRecipes(output, TinkerToolParts.smallAxeHead, TinkerSmeltery.smallAxeHeadCast, 2, partFolder, castFolder);
    partRecipes(output, TinkerToolParts.broadAxeHead, TinkerSmeltery.broadAxeHeadCast, 8, partFolder, castFolder);
    partRecipes(output, TinkerToolParts.smallBlade,   TinkerSmeltery.smallBladeCast,   2, partFolder, castFolder);
    partRecipes(output, TinkerToolParts.broadBlade,   TinkerSmeltery.broadBladeCast,   8, partFolder, castFolder);
    partRecipes(output, TinkerToolParts.bowLimb,      TinkerSmeltery.bowLimbCast,      2, partFolder, castFolder);
    partRecipes(output, TinkerToolParts.bowGrip,      TinkerSmeltery.bowGripCast,      2, partFolder, castFolder);
    // arrow patterns are just a reusable pattern for the part builder
    ItemCastingRecipeBuilder.tableRecipe(TinkerSmeltery.arrowCast)
      .setFluidAndTime(TinkerFluids.moltenGold, FluidValues.INGOT)
      .setCast(ItemTags.ARROWS, true)
      .save(output, location(castFolder + "gold/arrow"));
    // other parts
    partRecipes(output, TinkerToolParts.toolBinding,  TinkerSmeltery.toolBindingCast,  1, partFolder, castFolder);
    partRecipes(output, TinkerToolParts.toughBinding, TinkerSmeltery.toughBindingCast, 3, partFolder, castFolder);
    partRecipes(output, TinkerToolParts.adzeHead,     TinkerSmeltery.adzeHeadCast,     2, partFolder, castFolder);
    partRecipes(output, TinkerToolParts.largePlate,   TinkerSmeltery.largePlateCast,   4, partFolder, castFolder);
    partRecipes(output, TinkerToolParts.toolHandle,   TinkerSmeltery.toolHandleCast,   1, partFolder, castFolder);
    partRecipes(output, TinkerToolParts.toughHandle,  TinkerSmeltery.toughHandleCast,  3, partFolder, castFolder);
    // armor
    partWithDummy(output, TinkerToolParts.plating.get(ArmorItem.Type.HELMET),     TinkerSmeltery.dummyPlating.get(ArmorItem.Type.HELMET),     TinkerSmeltery.helmetPlatingCast,     3, partFolder, castFolder);
    partWithDummy(output, TinkerToolParts.plating.get(ArmorItem.Type.CHESTPLATE), TinkerSmeltery.dummyPlating.get(ArmorItem.Type.CHESTPLATE), TinkerSmeltery.chestplatePlatingCast, 6, partFolder, castFolder);
    partWithDummy(output, TinkerToolParts.plating.get(ArmorItem.Type.LEGGINGS),   TinkerSmeltery.dummyPlating.get(ArmorItem.Type.LEGGINGS),   TinkerSmeltery.leggingsPlatingCast,   5, partFolder, castFolder);
    partWithDummy(output, TinkerToolParts.plating.get(ArmorItem.Type.BOOTS),      TinkerSmeltery.dummyPlating.get(ArmorItem.Type.BOOTS),      TinkerSmeltery.bootsPlatingCast,      2, partFolder, castFolder);
    partRecipes(output, TinkerToolParts.maille, TinkerSmeltery.mailleCast, 2, partFolder, castFolder);

    // bowstrings and shield cores are part builder exclusive. Shield core additionally disallows anything that conflicts with casting shield plating (obsidian/nahuatl conflict)
    uncastablePart(output, TinkerToolParts.bowstring.get(), 1, null, partFolder);
    uncastablePart(output, TinkerToolParts.shieldCore.get(), 4, PlatingMaterialStats.SHIELD.getId(), partFolder);
    // slimesuit - not castable
    uncastablePart(output, TinkerToolParts.ribcage.get(), 2, PlatingMaterialStats.SHIELD.getId(), partFolder);
    uncastablePart(output, TinkerToolParts.shell.get(), 2, PlatingMaterialStats.SHIELD.getId(), partFolder);
    uncastablePart(output, TinkerToolParts.laces.get(), 2, PlatingMaterialStats.SHIELD.getId(), partFolder);
    // arrow parts are just part builder, no composite currently
    Ingredient arrowPattern = CompoundIngredient.of(Ingredient.of(TinkerTags.Items.DEFAULT_PATTERNS), Ingredient.of(TinkerSmeltery.arrowCast));
    PartRecipeBuilder.partRecipe(TinkerToolParts.arrowHead.get())
      .setPattern(TinkerToolParts.arrowHead.getId())
      .setPatternItem(arrowPattern)
      .setCost(1)
      .setAllowUncraftable(true)
      .save(output, location(partFolder + "builder/arrow_head"));
    PartRecipeBuilder.partRecipe(TinkerToolParts.arrowShaft.get())
      .setPattern(TinkerToolParts.arrowShaft.getId())
      .setPatternItem(arrowPattern)
      .setCost(1)
      .setAllowUncraftable(true)
      .save(output, location(partFolder + "builder/arrow_shaft"));
    PartRecipeBuilder.partRecipe(TinkerToolParts.fletching.get())
      .setPattern(TinkerToolParts.fletching.getId())
      .setPatternItem(arrowPattern)
      .setCost(1)
      .setAllowUncraftable(true)
      .save(output, location(partFolder + "builder/fletching"));
  }

  /** Helper to create a casting recipe for a slimeskull variant */
  private void slimeskull(RecipeOutput output, MaterialId material, ItemLike skull, String folder) {
    MaterialCastingRecipeBuilder.basinRecipe(TinkerTools.slimesuit.get(ArmorItem.Type.HELMET))
      .setCast(skull, CastPurpose.CONSUMED_OFFSET)
      .addExtraMaterial(material)
      .setItemCost(5)
      .save(output, location(folder + "slime_skull/" + material.getPath()));
    MaterialSwappingRecipeBuilder.tools(TinkerTags.Items.SWAPPABLE_SKULLS)
      .index(0).material(material, skull).repairValue((int) (MaterialRecipe.INGOTS_PER_REPAIR * 2))
      .save(output, location(folder + "slime_skull/swapping/" + material.getPath()));
  }
}
