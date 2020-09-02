package mekanism.api.providers;

import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;

import javax.annotation.Nonnull;

public interface IGasProvider extends IChemicalProvider<Gas> {

    @Nonnull
    @Override
    default GasStack getStack(long size) {
        return new GasStack(getChemical(), size);
    }
}