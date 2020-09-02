package mekanism.api.chemical.pigment;

import mekanism.api._helpers_pls_remove.NBT;
import mekanism.api.annotations.MethodsReturnNonnullByDefault;
import mekanism.api.NBTConstants;
import mekanism.api.chemical.IChemicalTank;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Convenience extension to make working with generics easier.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IPigmentTank extends IChemicalTank<Pigment, PigmentStack>, IEmptyPigmentProvider {

    @Override
    default PigmentStack createStack(PigmentStack stored, long size) {
        return new PigmentStack(stored, size);
    }

    @Override
    default void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains(NBTConstants.STORED, NBT.COMPOUND)) {
            setStackUnchecked(PigmentStack.readFromNBT(nbt.getCompound(NBTConstants.STORED)));
        }
    }
}