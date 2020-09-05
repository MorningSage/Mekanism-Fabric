package mekanism.common.block;

import javax.annotation.Nonnull;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.attribute.AttributeStateFacing;
import mekanism.common.block.prefab.BlockTile;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.registries.MekanismBlockTypes;
import mekanism.common.tile.TileEntityIndustrialAlarm;
import mekanism.common.util.EnumUtils;
import mekanism.common.util.VoxelShapeUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

public class BlockIndustrialAlarm extends BlockTile<TileEntityIndustrialAlarm, BlockTypeTile<TileEntityIndustrialAlarm>> {

    private static final VoxelShape[] MIN_SHAPES = new VoxelShape[EnumUtils.DIRECTIONS.length];

    static {
        VoxelShapeUtils.setShape(makeCuboidShape(5, 0, 5, 11, 16, 11), MIN_SHAPES, true);
    }

    public BlockIndustrialAlarm() {
        super(MekanismBlockTypes.INDUSTRIAL_ALARM, Block.Properties.create(Material.GLASS).hardnessAndResistance(2F, 4F));
    }

    @Nonnull
    @Override
    @Deprecated
    public BlockState updatePostPlacement(BlockState state, @Nonnull Direction facing, @Nonnull BlockState facingState, @Nonnull IWorld world,
          @Nonnull BlockPos currentPos, @Nonnull BlockPos facingPos) {
        if (facing.getOpposite() == Attribute.get(state.getBlock(), AttributeStateFacing.class).getDirection(state) && !state.isValidPosition(world, currentPos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.updatePostPlacement(state, facing, facingState, world, currentPos, facingPos);
    }

    @Override
    @Deprecated
    public boolean isValidPosition(BlockState state, @Nonnull IWorldReader world, @Nonnull BlockPos pos) {
        Direction side = Attribute.get(state.getBlock(), AttributeStateFacing.class).getDirection(state);
        Direction sideOn = side.getOpposite();
        BlockPos offsetPos = pos.offset(sideOn);
        VoxelShape projected = world.getBlockState(offsetPos).getCollisionShape(world, offsetPos).project(side);
        //hasEnoughSolidSide does not quite work for us, as the shape is incorrect
        //Don't allow placing on leaves or a block that is too small
        // same restrictions as vanilla except we have a better check for placing against the side
        return !state.isIn(BlockTags.LEAVES) && !VoxelShapes.compare(projected, MIN_SHAPES[sideOn.ordinal()], IBooleanFunction.ONLY_SECOND);
    }
}
