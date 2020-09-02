package mekanism.api.providers;

import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.pigment.PigmentStack;

import javax.annotation.Nonnull;

public interface IPigmentProvider extends IChemicalProvider<Pigment> {

    @Nonnull
    @Override
    default PigmentStack getStack(long size) {
        return new PigmentStack(getChemical(), size);
    }
}