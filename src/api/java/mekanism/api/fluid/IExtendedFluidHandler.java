package mekanism.api.fluid;

import mekanism.api._helpers_pls_remove.FluidAction;
import mekanism.api._helpers_pls_remove.FluidStack;
import mekanism.api.annotations.MethodsReturnNonnullByDefault;
import mekanism.api.Action;
import net.minecraft.fluid.Fluid;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IExtendedFluidHandler {


    int getTanks();
    @Nonnull FluidStack getFluidInTank(int tank);

    /**
     * Overrides the stack in the given tank. This method may throw an error if it is called unexpectedly.
     *
     * @param tank  Tank to modify
     * @param stack {@link FluidStack} to set tank to (may be empty).
     *
     * @throws RuntimeException if the handler is called in a way that the handler was not expecting.
     **/
    void setFluidInTank(int tank, FluidStack stack);

    /**
     * <p>
     * Inserts a {@link FluidStack} into a given tank and return the remainder. The {@link FluidStack} <em>should not</em> be modified in this function!
     * </p>
     *
     * @param tank   Tank to insert to.
     * @param stack  {@link FluidStack} to insert. This must not be modified by the tank.
     * @param action The action to perform, either {@link Action#EXECUTE} or {@link Action#SIMULATE}
     *
     * @return The remaining {@link FluidStack} that was not inserted (if the entire stack is accepted, then return an empty {@link FluidStack}). May be the same as the
     * input {@link FluidStack} if unchanged, otherwise a new {@link FluidStack}. The returned {@link FluidStack} can be safely modified after
     */
    FluidStack insertFluid(int tank, FluidStack stack, Action action);

    /**
     * Extracts a {@link FluidStack} from a specific tank in this handler.
     * <p>
     * The returned value must be empty if nothing is extracted, otherwise its stack size must be less than or equal to {@code amount}.
     * </p>
     *
     * @param tank   Tank to extract from.
     * @param amount Amount to extract (may be greater than the current stack's amount or the tank's capacity)
     * @param action The action to perform, either {@link Action#EXECUTE} or {@link Action#SIMULATE}
     *
     * @return {@link FluidStack} extracted from the tank, must be empty if nothing can be extracted. The returned {@link FluidStack} can be safely modified after, so the
     * tank should return a new or copied stack.
     */
    FluidStack extractFluid(int tank, int amount, Action action);

    /**
     * <p>
     * Inserts a {@link FluidStack} into this handler, distribution is left <strong>entirely</strong> to this {@link IExtendedFluidHandler}. The {@link FluidStack}
     * <em>should not</em> be modified in this function!
     * </p>
     *
     * @param stack  {@link FluidStack} to insert. This must not be modified by the handler.
     * @param action The action to perform, either {@link Action#EXECUTE} or {@link Action#SIMULATE}
     *
     * @return The remaining {@link FluidStack} that was not inserted (if the entire stack is accepted, then return an empty {@link FluidStack}). May be the same as the
     * input {@link FluidStack} if unchanged, otherwise a new {@link FluidStack}. The returned {@link FluidStack} can be safely modified after
     *
     * @implNote The default implementation of this method, attempts to insert into tanks that contain the same type of fluid as the supplied type, and if it will not all
     * fit, falls back to inserting into any empty tanks.
     * @apiNote It is not guaranteed that the default implementation will be how this {@link IExtendedFluidHandler} ends up distributing the insertion.
     */
    default FluidStack insertFluid(FluidStack stack, Action action) {
        return ExtendedFluidHandlerUtils.insert(stack, action, this::getTanks, this::getFluidInTank, this::insertFluid);
    }

    /**
     * Extracts a {@link FluidStack} from this handler, distribution is left <strong>entirely</strong> to this {@link IExtendedFluidHandler}.
     * <p>
     * The returned value must be empty if nothing is extracted, otherwise its stack size must be less than or equal to {@code amount}.
     * </p>
     *
     * @param amount Amount to extract (may be greater than the current stack's amount or the tank's capacity)
     * @param action The action to perform, either {@link Action#EXECUTE} or {@link Action#SIMULATE}
     *
     * @return {@link FluidStack} extracted from the tank, must be empty if nothing can be extracted. The returned {@link FluidStack} can be safely modified after, so the
     * tank should return a new or copied stack.
     *
     * @implNote The default implementation of this method, extracts across all tanks to try and reach the desired amount to extract. Once the first fluid that can be
     * extracted is found, all future extractions will make sure to also make sure they are for the same type of fluid.
     * @apiNote It is not guaranteed that the default implementation will be how this {@link IExtendedFluidHandler} ends up distributing the extraction.
     */
    default FluidStack extractFluid(int amount, Action action) {
        return ExtendedFluidHandlerUtils.extract(amount, action, this::getTanks, this::getFluidInTank, this::extractFluid);
    }

    /**
     * Extracts a {@link FluidStack} from this handler, distribution is left <strong>entirely</strong> to this {@link IExtendedFluidHandler}.
     * <p>
     * The returned value must be empty if nothing is extracted, otherwise its stack size must be less than or equal to {@code amount}.
     * </p>
     *
     * @param stack  {@link FluidStack} representing the {@link Fluid} and maximum amount to be drained.
     * @param action The action to perform, either {@link Action#EXECUTE} or {@link Action#SIMULATE}
     *
     * @return {@link FluidStack} extracted from the tank, must be empty if nothing can be extracted. The returned {@link FluidStack} can be safely modified after, so the
     * tank should return a new or copied stack.
     *
     * @implNote The default implementation of this method, extracts across all tanks that contents match the type of fluid passed into this method.
     * @apiNote It is not guaranteed that the default implementation will be how this {@link IExtendedFluidHandler} ends up distributing the extraction.
     */
    default FluidStack extractFluid(FluidStack stack, Action action) {
        return ExtendedFluidHandlerUtils.extract(stack, action, this::getTanks, this::getFluidInTank, this::extractFluid);
    }

    /**
     * {@inheritDoc}
     *
     * Wrapped to properly use our method declarations
     */
    @Deprecated
    default int fill(FluidStack stack, FluidAction action) {
        return stack.getAmount() - insertFluid(stack, Action.fromFluidAction(action)).getAmount();
    }

    /**
     * {@inheritDoc}
     *
     * Wrapped to properly use our method declarations
     */
    @Deprecated
    default FluidStack drain(FluidStack stack, FluidAction action) {
        return extractFluid(stack, Action.fromFluidAction(action));
    }

    /**
     * {@inheritDoc}
     *
     * Wrapped to properly use our method declarations
     */
    @Deprecated
    default FluidStack drain(int amount, FluidAction action) {
        return extractFluid(amount, Action.fromFluidAction(action));
    }
}