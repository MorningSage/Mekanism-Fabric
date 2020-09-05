package mekanism.common.integration.energy.forgeenergy;

import mekanism.api.Action;
import mekanism.api.energy.IStrictEnergyHandler;
import mekanism.api.math.FloatingLong;
import mekanism.common.integration.energy.EnergyCompatUtils.EnergyType;
import net.minecraftforge.energy.IEnergyStorage;

public class ForgeEnergyIntegration implements IEnergyStorage {

    private final IStrictEnergyHandler handler;

    public ForgeEnergyIntegration(IStrictEnergyHandler handler) {
        this.handler = handler;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (maxReceive <= 0) {
            return 0;
        }
        FloatingLong toInsert = EnergyType.FORGE.convertFrom(maxReceive);
        return EnergyType.FORGE.convertToAsInt(toInsert.subtract(handler.insertEnergy(toInsert, Action.get(!simulate))));
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return maxExtract <= 0 ? 0 : EnergyType.FORGE.convertToAsInt(handler.extractEnergy(EnergyType.FORGE.convertFrom(maxExtract), Action.get(!simulate)));
    }

    @Override
    public int getEnergyStored() {
        int containers = handler.getEnergyContainerCount();
        if (containers > 0) {
            int energy = 0;
            for (int container = 0; container < containers; container++) {
                int total = EnergyType.FORGE.convertToAsInt(handler.getEnergy(container));
                if (total > Integer.MAX_VALUE - energy) {
                    //Ensure we don't overflow
                    energy = Integer.MAX_VALUE;
                    break;
                } else {
                    energy += total;
                }
            }
            return energy;
        }
        return 0;
    }

    @Override
    public int getMaxEnergyStored() {
        int containers = handler.getEnergyContainerCount();
        if (containers > 0) {
            int maxEnergy = 0;
            for (int container = 0; container < containers; container++) {
                int max = EnergyType.FORGE.convertToAsInt(handler.getMaxEnergy(container));
                if (max > Integer.MAX_VALUE - maxEnergy) {
                    //Ensure we don't overflow
                    maxEnergy = Integer.MAX_VALUE;
                    break;
                } else {
                    maxEnergy += max;
                }
            }
            return maxEnergy;
        }
        return 0;
    }

    @Override
    public boolean canExtract() {
        return !handler.extractEnergy(FloatingLong.ONE, Action.SIMULATE).isZero();
    }

    @Override
    public boolean canReceive() {
        return handler.insertEnergy(FloatingLong.ONE, Action.SIMULATE).smallerThan(FloatingLong.ONE);
    }
}