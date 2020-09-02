package mekanism.api.chemical.slurry;

import mekanism.api.chemical.IEmptyStackProvider;

import javax.annotation.Nonnull;

public interface IEmptySlurryProvider extends IEmptyStackProvider<Slurry, SlurryStack> {

    @Nonnull
    @Override
    default SlurryStack getEmptyStack() {
        return SlurryStack.EMPTY;
    }
}