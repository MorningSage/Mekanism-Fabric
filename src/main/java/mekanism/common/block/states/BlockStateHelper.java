package mekanism.common.block.states;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.attribute.AttributeState;
import mekanism.common.tile.TileEntityCardboardBox;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Contract;

public class BlockStateHelper {

    //Cardboard Box storage
    public static final BooleanProperty storageProperty = BooleanProperty.of("storage");
    //Fluid logged. TODO: We may eventually want to make this not be using the same exact property as WATERLOGGED but name it differently
    public static final BooleanProperty FLUID_LOGGED = Properties.WATERLOGGED;

    public static BlockState getDefaultState(@Nonnull BlockState state) {
        Block block = state.getBlock();

        for (Attribute attr : Attribute.getAll(block)) {
            if (attr instanceof AttributeState) {
                state = ((AttributeState) attr).getDefaultState(state);
            }
        }
        if (block instanceof IStateFluidLoggable) {
            //Default the blocks to not being waterlogged, they have code to force waterlogging to true if being placed in water
            state = state.with(FLUID_LOGGED, false);
        }
        return state;
    }

    public static void fillBlockStateContainer(Block block, StateManager.Builder<Block, BlockState> builder) {
        List<Property<?>> properties = new ArrayList<>();

        for (Attribute attr : Attribute.getAll(block)) {
            if (attr instanceof AttributeState) {
                ((AttributeState) attr).fillBlockStateContainer(block, properties);
            }
        }
        if (block instanceof IStateStorage) {
            properties.add(storageProperty);
        }
        if (block instanceof IStateFluidLoggable) {
            properties.add(FLUID_LOGGED);
        }
        if (!properties.isEmpty()) {
            builder.add(properties.toArray(new Property[0]));
        }
    }

    @Contract("_, null, _ -> null")
    public static BlockState getStateForPlacement(Block block, @Nullable BlockState state, ItemPlacementContext context) {
        return getStateForPlacement(block, state, context.getWorld(), context.getBlockPos(), context.getPlayer(), context.getSide());
    }

    @Contract("_, null, _, _, _, _ -> null")
    public static BlockState getStateForPlacement(Block block, @Nullable BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nullable PlayerEntity player, @Nonnull Direction face) {
        if (state == null) {
            return null;
        }
        for (Attribute attr : Attribute.getAll(block)) {
            if (attr instanceof AttributeState) {
                state = ((AttributeState) attr).getStateForPlacement(block, state, world, pos, player, face);
            }
        }
        if (block instanceof IStateFluidLoggable) {
            FluidState fluidState = world.getFluidState(pos);
            state = state.with(FLUID_LOGGED, fluidState.getFluid() == Fluids.WATER);
        }
        return state;
    }

    private static boolean isStoring(@Nonnull BlockEntity tile) {
        if (tile instanceof TileEntityCardboardBox) {
            return ((TileEntityCardboardBox) tile).storedData != null;
        }
        return false;
    }

    public static BlockState copyStateData(BlockState oldState, BlockState newState) {
        Block oldBlock = oldState.getBlock();
        Block newBlock = newState.getBlock();

        for (Attribute attr : Attribute.getAll(oldBlock)) {
            if (attr instanceof AttributeState) {
                newState = ((AttributeState) attr).copyStateData(oldState, newState);
            }
        }
        if (oldBlock instanceof IStateStorage && newBlock instanceof IStateStorage) {
            newState = newState.with(storageProperty, oldState.get(storageProperty));
        }
        if (oldBlock instanceof IStateFluidLoggable && newBlock instanceof IStateFluidLoggable) {
            newState = newState.with(FLUID_LOGGED, oldState.get(FLUID_LOGGED));
        }
        return newState;
    }
}