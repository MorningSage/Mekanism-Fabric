package mekanism.api.chemical.infuse;

import mekanism.api.NBTConstants;
import mekanism.api._helpers_pls_remove.NBT;
import mekanism.api.annotations.MethodsReturnNonnullByDefault;
import mekanism.api.chemical.IChemicalTank;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Convenience extension to make working with generics easier.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IInfusionTank extends IChemicalTank<InfuseType, InfusionStack>, IEmptyInfusionProvider {

    @Override
    default InfusionStack createStack(InfusionStack stored, long size) {
        return new InfusionStack(stored, size);
    }

    @Override
    default void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains(NBTConstants.STORED, NBT.COMPOUND)) {
            setStackUnchecked(InfusionStack.readFromNBT(nbt.getCompound(NBTConstants.STORED)));
        }
    }
}