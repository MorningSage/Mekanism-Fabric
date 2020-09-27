package mekanism.common.tags;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.EnumMap;
import java.util.Map;
import mekanism.api.chemical.ChemicalTags;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.common.Mekanism;
import mekanism.common.resource.OreType;
import mekanism.common.resource.PrimaryResource;
import mekanism.common.resource.ResourceType;
import mekanism.common.util.EnumUtils;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;

public class MekanismTags {

    public static class Items {

        public static final Table<ResourceType, PrimaryResource, Tag.Identified<Item>> PROCESSED_RESOURCES = HashBasedTable.create();
        public static final Map<PrimaryResource, Tag.Identified<Item>> PROCESSED_RESOURCE_BLOCKS = new EnumMap<>(PrimaryResource.class);
        public static final Map<OreType, Tag.Identified<Item>> ORES = new EnumMap<>(OreType.class);

        static {
            for (PrimaryResource resource : EnumUtils.PRIMARY_RESOURCES) {
                for (ResourceType type : EnumUtils.RESOURCE_TYPES) {
                    if (type.usedByPrimary()) {
                        if (type == ResourceType.INGOT || type == ResourceType.NUGGET || type == ResourceType.DUST) {
                            PROCESSED_RESOURCES.put(type, resource, forgeTag(type.getPluralPrefix() + "/" + resource.getName()));
                        } else {
                            PROCESSED_RESOURCES.put(type, resource, tag(type.getPluralPrefix() + "/" + resource.getName()));
                        }
                    }
                }
                if (!resource.isVanilla()) {
                    PROCESSED_RESOURCE_BLOCKS.put(resource, forgeTag("storage_blocks/" + resource.getName()));
                }
            }
            for (OreType ore : EnumUtils.ORE_TYPES) {
                ORES.put(ore, forgeTag("ores/" + ore.getResource().getRegistrySuffix()));
            }
        }

        public static final Tag.Identified<Item> WRENCHES = forgeTag("wrenches");

        public static final Tag.Identified<Item> BATTERIES = forgeTag("batteries");

        public static final Tag.Identified<Item> RODS_PLASTIC = forgeTag("rods/plastic");

        public static final Tag.Identified<Item> FUELS = forgeTag("fuels");
        public static final Tag.Identified<Item> FUELS_BIO = forgeTag("fuels/bio");

        public static final Tag.Identified<Item> CHESTS_ELECTRIC = forgeTag("chests/electric");
        public static final Tag.Identified<Item> CHESTS_PERSONAL = forgeTag("chests/personal");

        public static final Tag.Identified<Item> SALT = forgeTag("salt");
        public static final Tag.Identified<Item> SAWDUST = forgeTag("sawdust");
        public static final Tag.Identified<Item> YELLOW_CAKE_URANIUM = forgeTag("yellow_cake_uranium");

        public static final Tag.Identified<Item> PELLETS_ANTIMATTER = forgeTag("pellets/antimatter");
        public static final Tag.Identified<Item> PELLETS_PLUTONIUM = forgeTag("pellets/plutonium");
        public static final Tag.Identified<Item> PELLETS_POLONIUM = forgeTag("pellets/polonium");

        public static final Tag.Identified<Item> DUSTS_BRONZE = forgeTag("dusts/bronze");
        public static final Tag.Identified<Item> DUSTS_CHARCOAL = forgeTag("dusts/charcoal");
        public static final Tag.Identified<Item> DUSTS_COAL = forgeTag("dusts/coal");
        public static final Tag.Identified<Item> DUSTS_DIAMOND = forgeTag("dusts/diamond");
        public static final Tag.Identified<Item> DUSTS_EMERALD = forgeTag("dusts/emerald");
        public static final Tag.Identified<Item> DUSTS_NETHERITE = forgeTag("dusts/netherite");
        public static final Tag.Identified<Item> DUSTS_LAPIS = forgeTag("dusts/lapis");
        public static final Tag.Identified<Item> DUSTS_LITHIUM = forgeTag("dusts/lithium");
        public static final Tag.Identified<Item> DUSTS_OBSIDIAN = forgeTag("dusts/obsidian");
        public static final Tag.Identified<Item> DUSTS_QUARTZ = forgeTag("dusts/quartz");
        public static final Tag.Identified<Item> DUSTS_REFINED_OBSIDIAN = forgeTag("dusts/refined_obsidian");
        public static final Tag.Identified<Item> DUSTS_SALT = forgeTag("dusts/salt");
        public static final Tag.Identified<Item> DUSTS_STEEL = forgeTag("dusts/steel");
        public static final Tag.Identified<Item> DUSTS_SULFUR = forgeTag("dusts/sulfur");
        public static final Tag.Identified<Item> DUSTS_WOOD = forgeTag("dusts/wood");
        public static final Tag.Identified<Item> DUSTS_FLUORITE = forgeTag("dusts/fluorite");

        public static final Tag.Identified<Item> NUGGETS_BRONZE = forgeTag("nuggets/bronze");
        public static final Tag.Identified<Item> NUGGETS_REFINED_GLOWSTONE = forgeTag("nuggets/refined_glowstone");
        public static final Tag.Identified<Item> NUGGETS_REFINED_OBSIDIAN = forgeTag("nuggets/refined_obsidian");
        public static final Tag.Identified<Item> NUGGETS_STEEL = forgeTag("nuggets/steel");

        public static final Tag.Identified<Item> INGOTS_BRONZE = forgeTag("ingots/bronze");
        public static final Tag.Identified<Item> INGOTS_REFINED_GLOWSTONE = forgeTag("ingots/refined_glowstone");
        public static final Tag.Identified<Item> INGOTS_REFINED_OBSIDIAN = forgeTag("ingots/refined_obsidian");
        public static final Tag.Identified<Item> INGOTS_STEEL = forgeTag("ingots/steel");

        public static final Tag.Identified<Item> STORAGE_BLOCKS_BRONZE = forgeTag("storage_blocks/bronze");
        public static final Tag.Identified<Item> STORAGE_BLOCKS_CHARCOAL = forgeTag("storage_blocks/charcoal");
        public static final Tag.Identified<Item> STORAGE_BLOCKS_REFINED_GLOWSTONE = forgeTag("storage_blocks/refined_glowstone");
        public static final Tag.Identified<Item> STORAGE_BLOCKS_REFINED_OBSIDIAN = forgeTag("storage_blocks/refined_obsidian");
        public static final Tag.Identified<Item> STORAGE_BLOCKS_STEEL = forgeTag("storage_blocks/steel");

        public static final Tag.Identified<Item> CIRCUITS = forgeTag("circuits");
        public static final Tag.Identified<Item> CIRCUITS_BASIC = forgeTag("circuits/basic");
        public static final Tag.Identified<Item> CIRCUITS_ADVANCED = forgeTag("circuits/advanced");
        public static final Tag.Identified<Item> CIRCUITS_ELITE = forgeTag("circuits/elite");
        public static final Tag.Identified<Item> CIRCUITS_ULTIMATE = forgeTag("circuits/ultimate");

        public static final Tag.Identified<Item> ALLOYS = tag("alloys");
        public static final Tag.Identified<Item> ALLOYS_BASIC = tag("alloys/basic");
        public static final Tag.Identified<Item> ALLOYS_INFUSED = tag("alloys/infused");
        public static final Tag.Identified<Item> ALLOYS_REINFORCED = tag("alloys/reinforced");
        public static final Tag.Identified<Item> ALLOYS_ATOMIC = tag("alloys/atomic");
        //Forge alloy tags
        public static final Tag.Identified<Item> FORGE_ALLOYS = forgeTag("alloys");
        public static final Tag.Identified<Item> ALLOYS_ADVANCED = forgeTag("alloys/advanced");
        public static final Tag.Identified<Item> ALLOYS_ELITE = forgeTag("alloys/elite");
        public static final Tag.Identified<Item> ALLOYS_ULTIMATE = forgeTag("alloys/ultimate");

        public static final Tag.Identified<Item> ENRICHED = tag("enriched");
        public static final Tag.Identified<Item> ENRICHED_CARBON = tag("enriched/carbon");
        public static final Tag.Identified<Item> ENRICHED_DIAMOND = tag("enriched/diamond");
        public static final Tag.Identified<Item> ENRICHED_OBSIDIAN = tag("enriched/obsidian");
        public static final Tag.Identified<Item> ENRICHED_REDSTONE = tag("enriched/redstone");
        public static final Tag.Identified<Item> ENRICHED_GOLD = tag("enriched/gold");
        public static final Tag.Identified<Item> ENRICHED_TIN = tag("enriched/tin");

        public static final Tag.Identified<Item> DIRTY_DUSTS = tag("dirty_dusts");
        public static final Tag.Identified<Item> CLUMPS = tag("clumps");
        public static final Tag.Identified<Item> SHARDS = tag("shards");
        public static final Tag.Identified<Item> CRYSTALS = tag("crystals");

        public static final Tag.Identified<Item> GEMS_FLUORITE = forgeTag("gems/fluorite");

        // Originally provided by forge but not available in Fabric
        public static final Tag.Identified<Item> GEMS_LAPIS = forgeTag("lapis_lazulis");

        private static Tag.Identified<Item> forgeTag(String name) {
            return ItemTags.makeWrapperTag("forge:" + name);
        }

        private static Tag.Identified<Item> tag(String name) {
            return ItemTags.makeWrapperTag(Mekanism.rl(name).toString());
        }
    }

    public static class Blocks {

        public static final Map<PrimaryResource, Tag.Identified<Block>> RESOURCE_STORAGE_BLOCKS = new EnumMap<>(PrimaryResource.class);
        public static final Map<OreType, Tag.Identified<Block>> ORES = new EnumMap<>(OreType.class);

        static {
            for (PrimaryResource resource : EnumUtils.PRIMARY_RESOURCES) {
                if (!resource.isVanilla()) {
                    RESOURCE_STORAGE_BLOCKS.put(resource, forgeTag("storage_blocks/" + resource.getName()));
                }
            }
            for (OreType ore : EnumUtils.ORE_TYPES) {
                ORES.put(ore, forgeTag("ores/" + ore.getResource().getRegistrySuffix()));
            }
        }

        public static final Tag.Identified<Block> RELOCATION_NOT_SUPPORTED = forgeTag("relocation_not_supported");
        public static final Tag.Identified<Block> CARDBOARD_BLACKLIST = tag("cardboard_blacklist");
        public static final Tag.Identified<Block> ATOMIC_DISASSEMBLER_ORE = tag("atomic_disassembler_ore");

        public static final Tag.Identified<Block> CHESTS_ELECTRIC = forgeTag("chests/electric");
        public static final Tag.Identified<Block> CHESTS_PERSONAL = forgeTag("chests/personal");

        public static final Tag.Identified<Block> STORAGE_BLOCKS_BRONZE = forgeTag("storage_blocks/bronze");
        public static final Tag.Identified<Block> STORAGE_BLOCKS_CHARCOAL = forgeTag("storage_blocks/charcoal");
        public static final Tag.Identified<Block> STORAGE_BLOCKS_REFINED_GLOWSTONE = forgeTag("storage_blocks/refined_glowstone");
        public static final Tag.Identified<Block> STORAGE_BLOCKS_REFINED_OBSIDIAN = forgeTag("storage_blocks/refined_obsidian");
        public static final Tag.Identified<Block> STORAGE_BLOCKS_STEEL = forgeTag("storage_blocks/steel");

        private static Tag.Identified<Block> forgeTag(String name) {
            return BlockTags.makeWrapperTag("forge:" + name);
        }

        private static Tag.Identified<Block> tag(String name) {
            return BlockTags.makeWrapperTag(Mekanism.rl(name).toString());
        }
    }

    public static class Fluids {

        public static final Tag.Identified<Fluid> BRINE = forgeTag("brine");
        public static final Tag.Identified<Fluid> CHLORINE = forgeTag("chlorine");
        public static final Tag.Identified<Fluid> ETHENE = forgeTag("ethene");
        public static final Tag.Identified<Fluid> HEAVY_WATER = forgeTag("heavy_water");
        public static final Tag.Identified<Fluid> HYDROGEN = forgeTag("hydrogen");
        public static final Tag.Identified<Fluid> HYDROGEN_CHLORIDE = forgeTag("hydrogen_chloride");
        public static final Tag.Identified<Fluid> LITHIUM = forgeTag("lithium");
        public static final Tag.Identified<Fluid> OXYGEN = forgeTag("oxygen");
        public static final Tag.Identified<Fluid> SODIUM = forgeTag("sodium");
        public static final Tag.Identified<Fluid> STEAM = forgeTag("steam");
        public static final Tag.Identified<Fluid> SULFUR_DIOXIDE = forgeTag("sulfur_dioxide");
        public static final Tag.Identified<Fluid> SULFUR_TRIOXIDE = forgeTag("sulfur_trioxide");
        public static final Tag.Identified<Fluid> SULFURIC_ACID = forgeTag("sulfuric_acid");
        public static final Tag.Identified<Fluid> HYDROFLUORIC_ACID = forgeTag("hydrofluoric_acid");

        private static Tag.Identified<Fluid> forgeTag(String name) {
            return FluidTags.makeWrapperTag("forge:" + name);
        }
    }

    public static class Gases {

        public static final Tag.Identified<Gas> WATER_VAPOR = tag("water_vapor");

        private static Tag.Identified<Gas> tag(String name) {
            return ChemicalTags.GAS.tag(Mekanism.rl(name));
        }
    }

    public static class InfuseTypes {

        public static final Tag.Identified<InfuseType> CARBON = tag("carbon");
        public static final Tag.Identified<InfuseType> REDSTONE = tag("redstone");
        public static final Tag.Identified<InfuseType> DIAMOND = tag("diamond");
        public static final Tag.Identified<InfuseType> REFINED_OBSIDIAN = tag("refined_obsidian");
        public static final Tag.Identified<InfuseType> BIO = tag("bio");
        public static final Tag.Identified<InfuseType> FUNGI = tag("fungi");
        public static final Tag.Identified<InfuseType> GOLD = tag("gold");
        public static final Tag.Identified<InfuseType> TIN = tag("tin");

        private static Tag.Identified<InfuseType> tag(String name) {
            return ChemicalTags.INFUSE_TYPE.tag(Mekanism.rl(name));
        }
    }

    public static class Pigments {

        private static Tag.Identified<Pigment> tag(String name) {
            return ChemicalTags.PIGMENT.tag(Mekanism.rl(name));
        }
    }

    public static class Slurries {

        public static final Tag.Identified<Slurry> DIRTY = tag("dirty");
        public static final Tag.Identified<Slurry> CLEAN = tag("clean");

        private static Tag.Identified<Slurry> tag(String name) {
            return ChemicalTags.SLURRY.tag(Mekanism.rl(name));
        }
    }
}