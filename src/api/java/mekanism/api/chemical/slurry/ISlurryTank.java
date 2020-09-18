package mekanism.api.chemical.slurry;

import mekanism.api._helpers_pls_remove.NBTFlags;
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
public interface ISlurryTank extends IChemicalTank<Slurry, SlurryStack>, IEmptySlurryProvider {

    @Override
    default SlurryStack createStack(SlurryStack stored, long size) {
        return new SlurryStack(stored, size);
    }

    @Override
    default void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains(NBTConstants.STORED, NBTFlags.COMPOUND)) {
            setStackUnchecked(SlurryStack.readFromNBT(nbt.getCompound(NBTConstants.STORED)));
        }
    }
}