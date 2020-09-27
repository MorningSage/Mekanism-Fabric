package mekanism.common.block.states;

import javax.annotation.Nonnull;

import mekanism.api._helpers_pls_remove.BlockFlags;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.FluidFillable;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

//TODO: The below TODOs go off an assumption of there being some form of forge patch first to support position information for fluid states
public interface IStateFluidLoggable extends FluidDrainable, FluidFillable {

    default boolean isValidFluid(@Nonnull Fluid fluid) {
        //TODO: If we support a tile entity then return true, otherwise only allow water
        return fluid == getSupportedFluid();
    }

    /**
     * Gets the fluid this fluid loggable block supports. Overriding this is an easy way to change the block from supporting water logging to supporting a specific
     * different type of fluid, but dynamic fluid stuff cannot be done without a sizeable patch to forge/a change in vanilla so that {@link BlockState#getFluidState()}
     * has position information.
     */
    default Fluid getSupportedFluid() {
        return Fluids.WATER;
    }

    @Nonnull
    default FluidState getFluid(@Nonnull BlockState state) {
        if (state.get(BlockStateHelper.FLUID_LOGGED)) {
            //TODO: Proxy this via the TileEntity if there is one, rather than using a hard coded getSupportedFluid
            Fluid fluid = getSupportedFluid();
            if (fluid instanceof FlowableFluid) {
                return ((FlowableFluid) fluid).getStill(false);
            }
            return fluid.getDefaultState();
        }
        return Fluids.EMPTY.getDefaultState();
    }

    default void updateFluids(@Nonnull BlockState state, @Nonnull WorldAccess world, @Nonnull BlockPos currentPos) {
        if (state.get(BlockStateHelper.FLUID_LOGGED)) {
            //TODO: Get proper fluid from the TileEntity
            Fluid fluid = getSupportedFluid();
            world.getFluidTickScheduler().schedule(currentPos, fluid, fluid.getTickRate(world));
        }
    }

    @Override
    default boolean canFillWithFluid(@Nonnull BlockView world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull Fluid fluid) {
        return !state.get(BlockStateHelper.FLUID_LOGGED) && isValidFluid(fluid);
    }

    /**
     * Overwritten to check against canContainFluid instead of inlining the check to water directly.
     */
    @Override
    default boolean tryFillWithFluid(@Nonnull WorldAccess world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull FluidState fluidState) {
        Fluid fluid = fluidState.getFluid();
        if (canFillWithFluid(world, pos, state, fluid)) {
            if (!world.isClient()) {
                world.setBlockState(pos, state.with(BlockStateHelper.FLUID_LOGGED, true), BlockFlags.DEFAULT);
                world.getFluidTickScheduler().schedule(pos, fluid, fluid.getTickRate(world));
                //TODO: Update the TileEntity if there is one with the proper fluid type
            }
            return true;
        }
        return false;
    }

    @Nonnull
    @Override
    default Fluid tryDrainFluid(@Nonnull WorldAccess world, @Nonnull BlockPos pos, @Nonnull BlockState state) {
        if (state.get(BlockStateHelper.FLUID_LOGGED)) {
            world.setBlockState(pos, state.with(BlockStateHelper.FLUID_LOGGED, false), BlockFlags.DEFAULT);
            //TODO: Get proper fluid from block
            return getSupportedFluid();
        }
        return Fluids.EMPTY;
    }
}