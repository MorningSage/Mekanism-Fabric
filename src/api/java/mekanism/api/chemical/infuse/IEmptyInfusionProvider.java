package mekanism.api.chemical.infuse;

import mekanism.api.chemical.IEmptyStackProvider;

import javax.annotation.Nonnull;

public interface IEmptyInfusionProvider extends IEmptyStackProvider<InfuseType, InfusionStack> {

    @Nonnull
    @Override
    default InfusionStack getEmptyStack() {
        return InfusionStack.EMPTY;
    }
}