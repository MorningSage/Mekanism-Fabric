package mekanism.common.block;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.NBTConstants;
import mekanism.common.block.interfaces.IHasTileEntity;
import mekanism.common.block.states.IStateStorage;
import mekanism.common.item.block.ItemBlockCardboardBox;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismTileEntityTypes;
import mekanism.common.tile.TileEntityCardboardBox;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.NBTUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class BlockCardboardBox extends BlockMekanism implements IStateStorage, IHasTileEntity<TileEntityCardboardBox> {

    public BlockCardboardBox() {
        super(Block.Settings.of(Material.WOOL).strength(0.5F, 1F));
    }

    @Nonnull
    @Override
    @Deprecated
    public ActionResult onUse(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand hand,
          @Nonnull BlockHitResult hit) {
        if (!world.isClient && player.isSneaking()) {
            TileEntityCardboardBox box = MekanismUtils.getTileEntity(TileEntityCardboardBox.class, world, pos);
            if (box != null && box.storedData != null) {
                BlockData data = box.storedData;
                //TODO: Note - this will not allow for rotation of the block based on how it is placed direction wise via the removal of
                // the cardboard box and will instead leave it how it was when the box was initially put on
                world.setBlockState(pos, data.blockState);
                if (data.tileTag != null) {
                    data.updateLocation(pos);
                    BlockEntity tile = MekanismUtils.getTileEntity(world, pos);
                    if (tile != null) {
                        tile.fromTag(state, data.tileTag);
                    }
                }
                //TODO: Do we need to call onBlockPlacedBy or not bother given we are setting the blockstate to what it was AND setting any tile data
                //data.blockState.getBlock().onBlockPlacedBy(world, pos, data.blockState, player, new ItemStack(data.block));
                spawnAsEntity(world, pos, MekanismBlocks.CARDBOARD_BOX.getItemStack());
            }
        }
        return player.isSneaking() ? ActionResult.SUCCESS : ActionResult.PASS;
    }

    @Nonnull
    @Override
    public ItemStack getPickBlock(@Nonnull BlockState state, HitResult target, @Nonnull IBlockReader world, @Nonnull BlockPos pos, PlayerEntity player) {
        ItemStack itemStack = new ItemStack(this);
        TileEntityCardboardBox tile = MekanismUtils.getTileEntity(TileEntityCardboardBox.class, world, pos);
        if (tile == null) {
            return itemStack;
        }
        if (tile.storedData != null) {
            ((ItemBlockCardboardBox) itemStack.getItem()).setBlockData(itemStack, tile.storedData);
        }
        return itemStack;
    }

    @Override
    public BlockEntityType<? extends TileEntityCardboardBox> getTileType() {
        return MekanismTileEntityTypes.CARDBOARD_BOX.getTileEntityType();
    }

    public static class BlockData {

        @Nonnull
        public final BlockState blockState;
        @Nullable
        public CompoundTag tileTag;

        public BlockData(@Nonnull BlockState blockState) {
            this.blockState = blockState;
        }

        public static BlockData read(CompoundTag nbtTags) {
            BlockData data = new BlockData(NbtHelper.toBlockState(nbtTags.getCompound(NBTConstants.BLOCK_STATE)));
            NBTUtils.setCompoundIfPresent(nbtTags, NBTConstants.TILE_TAG, nbt -> data.tileTag = nbt);
            return data;
        }

        public void updateLocation(BlockPos pos) {
            if (tileTag != null) {
                tileTag.putInt(NBTConstants.X, pos.getX());
                tileTag.putInt(NBTConstants.Y, pos.getY());
                tileTag.putInt(NBTConstants.Z, pos.getZ());
            }
        }

        public CompoundTag write(CompoundTag nbtTags) {
            nbtTags.put(NBTConstants.BLOCK_STATE, NbtHelper.fromBlockState(blockState));
            if (tileTag != null) {
                nbtTags.put(NBTConstants.TILE_TAG, tileTag);
            }
            return nbtTags;
        }
    }
}