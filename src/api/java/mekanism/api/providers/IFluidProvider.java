package mekanism.api.providers;

import javax.annotation.Nonnull;

import mekanism.api._helpers_pls_remove.FluidStack;
import net.minecraft.fluid.Fluid;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public interface IFluidProvider extends IBaseProvider {

    @Nonnull
    Fluid getFluid();

    //Note: Uses FluidStack in case we want to check NBT or something
    default boolean fluidMatches(FluidStack other) {
        return getFluid() == other.getFluid();
    }

    @Nonnull
    default FluidStack getFluidStack(int size) {
        return new FluidStack(getFluid(), size);
    }

    @Override
    default Identifier getRegistryName() {
        return Registry.FLUID.getId(getFluid());
    }

    @Override
    default Text getTextComponent() {
        return getFluid().getAttributes().getDisplayName(getFluidStack(1));
    }

    @Override
    default String getTranslationKey() {
        return getFluid().getAttributes().getTranslationKey();
    }
}