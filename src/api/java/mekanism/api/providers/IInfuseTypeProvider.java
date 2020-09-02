package mekanism.api.providers;

import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.infuse.InfusionStack;

import javax.annotation.Nonnull;

public interface IInfuseTypeProvider extends IChemicalProvider<InfuseType> {

    @Nonnull
    @Override
    default InfusionStack getStack(long size) {
        return new InfusionStack(getChemical(), size);
    }
}