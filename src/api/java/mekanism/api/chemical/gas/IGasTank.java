package mekanism.api.chemical.gas;

import mekanism.api.NBTConstants;
import mekanism.api._helpers_pls_remove.NBTFlags;
import mekanism.api.annotations.MethodsReturnNonnullByDefault;
import mekanism.api.chemical.IChemicalTank;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Convenience extension to make working with generics easier.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IGasTank extends IChemicalTank<Gas, GasStack>, IEmptyGasProvider {

    @Override
    default GasStack createStack(GasStack stored, long size) {
        return new GasStack(stored, size);
    }

    @Override
    default void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains(NBTConstants.STORED, NBTFlags.COMPOUND)) {
            setStackUnchecked(GasStack.readFromNBT(nbt.getCompound(NBTConstants.STORED)));
        }
    }
}