package mekanism.common.integration;

import mekanism.common.integration.lookingat.theoneprobe.TOPProvider;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;

import java.util.Collection;

/**
 * Hooks for Mekanism. Use to grab items or blocks out of different mods.
 *
 * @author AidanBrady
 */
public final class MekanismHooks {

    public static final String IC2_MOD_ID = "ic2";
    public static final String JEI_MOD_ID = "jei";
    public static final String TOP_MOD_ID = "theoneprobe";
    public static final String CRAFTTWEAKER_MOD_ID = "crafttweaker";
    public static final String PROJECTE_MOD_ID = "projecte";
    public static final String FLUX_NETWORKS_MOD_ID = "flux-networks";

    public boolean JEILoaded = false;
    public boolean CraftTweakerLoaded = false;
    public boolean IC2Loaded = false;
    public boolean FluxNetworksLoaded = false;
    public boolean ProjectELoaded = false;
    public boolean TOPLoaded = false;

    public void hookCommonSetup() {
        FabricLoader modList = FabricLoader.getInstance();
        CraftTweakerLoaded = modList.isModLoaded(CRAFTTWEAKER_MOD_ID);
        IC2Loaded = modList.isModLoaded(IC2_MOD_ID);
        JEILoaded = modList.isModLoaded(JEI_MOD_ID);
        ProjectELoaded = modList.isModLoaded(PROJECTE_MOD_ID);
        TOPLoaded = modList.isModLoaded(TOP_MOD_ID);
        FluxNetworksLoaded = modList.isModLoaded(FLUX_NETWORKS_MOD_ID);
    }

    public void sendIMCMessages(InterModEnqueueEvent event) {
        if (TOPLoaded) {
            InterModComms.sendTo(TOP_MOD_ID, "getTheOneProbe", TOPProvider::new);
        }
        if (ProjectELoaded) {
            //NSSHelper.init();//TODO - ProjectE
        }
    }
}