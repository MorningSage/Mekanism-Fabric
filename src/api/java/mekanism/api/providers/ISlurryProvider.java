package mekanism.api.providers;

import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryStack;

import javax.annotation.Nonnull;

public interface ISlurryProvider extends IChemicalProvider<Slurry> {

    @Nonnull
    @Override
    default SlurryStack getStack(long size) {
        return new SlurryStack(getChemical(), size);
    }
}