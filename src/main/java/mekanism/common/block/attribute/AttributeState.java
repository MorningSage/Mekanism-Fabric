package mekanism.common.block.attribute;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Contract;

public abstract class AttributeState implements Attribute {

    public abstract BlockState copyStateData(BlockState oldState, BlockState newState);

    public abstract void fillBlockStateContainer(Block block, List<Property<?>> properties);

    public BlockState getDefaultState(@Nonnull BlockState state) {
        return state;
    }

    @Contract("_, null, _, _, _, _ -> null")
    public BlockState getStateForPlacement(Block block, @Nullable BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nullable PlayerEntity player, @Nonnull Direction face) {
        return state;
    }
}
