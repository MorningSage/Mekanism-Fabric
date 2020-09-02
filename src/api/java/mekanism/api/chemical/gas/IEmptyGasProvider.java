package mekanism.api.chemical.gas;

import mekanism.api.chemical.IEmptyStackProvider;

import javax.annotation.Nonnull;

public interface IEmptyGasProvider extends IEmptyStackProvider<Gas, GasStack> {

    @Nonnull
    @Override
    default GasStack getEmptyStack() {
        return GasStack.EMPTY;
    }
}