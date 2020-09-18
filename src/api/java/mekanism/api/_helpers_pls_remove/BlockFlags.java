package mekanism.api._helpers_pls_remove;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class BlockFlags {
    /**
     * Calls
     * {@link Block#neighborUpdate(BlockState, World, BlockPos, Block, BlockPos, boolean)}
     * on surrounding blocks (with isMoving as false). Also updates comparator output state.
     */
    public static final int NOTIFY_NEIGHBORS     = (1 << 0);
    /**
     * Calls {@link World#neighborUpdate(BlockPos, BlockState, BlockState, int)}.<br>
     * Server-side, this updates all the path-finding navigators.
     */
    public static final int BLOCK_UPDATE         = (1 << 1);
    /**
     * Stops the blocks from being marked for a render update
     */
    public static final int NO_RERENDER          = (1 << 2);
    /**
     * Makes the block be re-rendered immediately, on the main thread.
     * If NO_RERENDER is set, then this will be ignored
     */
    public static final int RERENDER_MAIN_THREAD = (1 << 3);
    /**
     * Causes neighbor updates to be sent to all surrounding blocks (including
     * diagonals). This in turn will call
     * {@link Block#updateDiagonalNeighbors(BlockState, IWorld, BlockPos, int)
     * updateDiagonalNeighbors} on both old and new states, and
     * {@link Block#updateNeighbors(BlockState, IWorld, BlockPos, int)
     * updateNeighbors} on the new state.
     */
    public static final int UPDATE_NEIGHBORS     = (1 << 4);

    /**
     * Prevents neighbor changes from spawning item drops, used by
     * {@link Block#replaceBlock(BlockState, BlockState, IWorld, BlockPos, int)}.
     */
    public static final int NO_NEIGHBOR_DROPS    = (1 << 5);

    /**
     * Tell the block being changed that it was moved, rather than removed/replaced,
     * the boolean value is eventually passed to
     * {@link Block#onReplaced(BlockState, World, BlockPos, BlockState, boolean)}
     * as the last parameter.
     */
    public static final int IS_MOVING            = (1 << 6);

    public static final int DEFAULT = NOTIFY_NEIGHBORS | BLOCK_UPDATE;
    public static final int DEFAULT_AND_RERENDER = DEFAULT | RERENDER_MAIN_THREAD;
}
