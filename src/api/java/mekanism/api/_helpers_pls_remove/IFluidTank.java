package mekanism.api._helpers_pls_remove;

import javax.annotation.Nonnull;

public interface IFluidTank {
    @Nonnull
    FluidStack getFluid();
    int getFluidAmount();
    int getCapacity();
    boolean isFluidValid(FluidStack stack);
    int fill(FluidStack resource, FluidAction action);
    @Nonnull
    FluidStack drain(int maxDrain, FluidAction action);
    @Nonnull
    FluidStack drain(FluidStack resource, FluidAction action);
}
