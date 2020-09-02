package mekanism.api.chemical.pigment;

import mekanism.api.chemical.IEmptyStackProvider;

import javax.annotation.Nonnull;

public interface IEmptyPigmentProvider extends IEmptyStackProvider<Pigment, PigmentStack> {

    @Nonnull
    @Override
    default PigmentStack getEmptyStack() {
        return PigmentStack.EMPTY;
    }
}