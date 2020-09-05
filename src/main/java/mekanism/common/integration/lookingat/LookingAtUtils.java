package mekanism.common.integration.lookingat;

import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.chemical.IMekanismChemicalHandler;
import mekanism.api.chemical.merged.ChemicalTankWrapper;
import mekanism.api.chemical.merged.MergedChemicalTank;
import mekanism.api.chemical.merged.MergedChemicalTank.Current;
import mekanism.api.energy.IStrictEnergyHandler;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.api.fluid.IMekanismFluidHandler;
import mekanism.api.text.ILangEntry;
import mekanism.common.MekanismLang;
import mekanism.common.capabilities.fluid.FluidTankWrapper;
import mekanism.common.capabilities.merged.MergedTank;
import mekanism.common.capabilities.merged.MergedTank.CurrentType;
import mekanism.common.capabilities.proxy.ProxyChemicalHandler;
import mekanism.common.lib.multiblock.IMultiblock;
import mekanism.common.lib.multiblock.IStructuralMultiblock;
import mekanism.common.lib.multiblock.MultiblockData;
import mekanism.common.lib.multiblock.MultiblockManager;
import mekanism.common.lib.multiblock.Structure;
import mekanism.common.util.CapabilityUtils;
import mekanism.common.util.MekanismUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

/**
 * Utils for simplifying the code for interacting with various mods that you look at things for (TOP, and Hwyla)
 */
public class LookingAtUtils {

    @Nullable
    public static MultiblockData getMultiblock(@Nonnull TileEntity tile) {
        if (tile instanceof IMultiblock) {
            return ((IMultiblock<?>) tile).getMultiblock();
        } else if (tile instanceof IStructuralMultiblock) {
            for (Entry<MultiblockManager<?>, Structure> entry : ((IStructuralMultiblock) tile).getStructureMap().entrySet()) {
                if (entry.getKey() != null) {
                    //TODO: Figure out if the structure map is supposed to be able to have nulls in it (in which handling it like this is correct)
                    // if it is not meant to have nulls then we should modify how Structure#getManager handles things
                    Structure s = entry.getValue();
                    if (s.isValid()) {
                        return s.getMultiblockData();
                    }
                }
            }
        }
        return null;
    }

    public static void displayFluid(LookingAtHelper info, IFluidHandler fluidHandler) {
        if (fluidHandler instanceof IMekanismFluidHandler) {
            IMekanismFluidHandler mekFluidHandler = (IMekanismFluidHandler) fluidHandler;
            for (IExtendedFluidTank fluidTank : mekFluidHandler.getFluidTanks(null)) {
                if (fluidTank instanceof FluidTankWrapper) {
                    MergedTank mergedTank = ((FluidTankWrapper) fluidTank).getMergedTank();
                    CurrentType currentType = mergedTank.getCurrentType();
                    if (currentType != CurrentType.EMPTY && currentType != CurrentType.FLUID) {
                        //Skip if the tank is on a chemical
                        continue;
                    }
                }
                addFluidInfo(info, fluidTank.getFluid(), fluidTank.getCapacity());
            }
        } else {
            //Fallback handling if it is not our fluid handler (probably never gets used)
            for (int tank = 0; tank < fluidHandler.getTanks(); tank++) {
                addFluidInfo(info, fluidHandler.getFluidInTank(tank), fluidHandler.getTankCapacity(tank));
            }
        }
    }

    private static void addFluidInfo(LookingAtHelper info, FluidStack fluidInTank, int capacity) {
        if (!fluidInTank.isEmpty()) {
            info.addText(MekanismLang.LIQUID.translate(fluidInTank));
        }
        info.addFluidElement(fluidInTank, capacity);
    }

    public static void displayEnergy(LookingAtHelper info, IStrictEnergyHandler energyHandler) {
        int containers = energyHandler.getEnergyContainerCount();
        for (int container = 0; container < containers; container++) {
            info.addEnergyElement(energyHandler.getEnergy(container), energyHandler.getMaxEnergy(container));
        }
    }

    public static <CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>, TANK extends IChemicalTank<CHEMICAL, STACK>,
          HANDLER extends IChemicalHandler<CHEMICAL, STACK>> void addInfo(TileEntity tile, @Nullable MultiblockData structure, Capability<HANDLER> capability,
          Function<MultiblockData, List<TANK>> multiBlockToTanks, LookingAtHelper info, ILangEntry langEntry, Current matchingCurrent, CurrentType matchingCurrentType) {
        Optional<HANDLER> cap = MekanismUtils.toOptional(CapabilityUtils.getCapability(tile, capability, null));
        if (cap.isPresent()) {
            HANDLER handler = cap.get();
            if (handler instanceof ProxyChemicalHandler) {
                List<TANK> tanks = ((ProxyChemicalHandler<CHEMICAL, STACK, ?>) handler).getTanksIfMekanism();
                if (!tanks.isEmpty()) {
                    //If there are any tanks add them and then exit, otherwise continue on assuming it is not a mekanism handler that is wrapped
                    for (TANK tank : tanks) {
                        addChemicalTankInfo(info, langEntry, tank, matchingCurrent, matchingCurrentType);
                    }
                    return;
                }
            }
            if (handler instanceof IMekanismChemicalHandler) {
                IMekanismChemicalHandler<CHEMICAL, STACK, TANK> mekHandler = (IMekanismChemicalHandler<CHEMICAL, STACK, TANK>) handler;
                for (TANK tank : mekHandler.getChemicalTanks(null)) {
                    addChemicalTankInfo(info, langEntry, tank, matchingCurrent, matchingCurrentType);
                }
            } else {
                for (int i = 0; i < handler.getTanks(); i++) {
                    addChemicalInfo(info, langEntry, handler.getChemicalInTank(i), handler.getTankCapacity(i));
                }
            }
        } else if (structure != null && structure.isFormed()) {
            //Special handling to allow viewing the chemicals in a multiblock when looking at things other than the ports
            for (TANK tank : multiBlockToTanks.apply(structure)) {
                addChemicalTankInfo(info, langEntry, tank, matchingCurrent, matchingCurrentType);
            }
        }
    }

    private static <CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>, TANK extends IChemicalTank<CHEMICAL, STACK>> void addChemicalTankInfo(
          LookingAtHelper info, ILangEntry langEntry, TANK chemicalTank, Current matchingCurrent, CurrentType matchingCurrentType) {
        if (chemicalTank instanceof ChemicalTankWrapper) {
            MergedChemicalTank mergedTank = ((ChemicalTankWrapper<CHEMICAL, STACK>) chemicalTank).getMergedTank();
            if (mergedTank instanceof MergedTank) {
                //If we are also support fluid, only show if we are the correct type
                CurrentType currentType = ((MergedTank) mergedTank).getCurrentType();
                if (((MergedTank) mergedTank).getCurrentType() != matchingCurrentType) {
                    //Skip if the tank is not the correct chemical type (fluid is default for merged tanks when empty)
                    return;
                }
            } else {
                Current current = mergedTank.getCurrent();
                if (current == Current.EMPTY) {
                    if (matchingCurrent != Current.GAS) {
                        //Skip tanks if overall it is empty and it is not the gas tank
                        return;
                    }
                } else if (current != matchingCurrent) {
                    //Skip if the tank is on the wrong type of chemical
                    return;
                }
            }
        }
        addChemicalInfo(info, langEntry, chemicalTank.getStack(), chemicalTank.getCapacity());
    }

    private static <CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>> void addChemicalInfo(LookingAtHelper info, ILangEntry langEntry,
          STACK chemicalInTank, long capacity) {
        if (!chemicalInTank.isEmpty()) {
            info.addText(langEntry.translate(chemicalInTank.getType()));
        }
        info.addChemicalElement(chemicalInTank, capacity);
    }

}