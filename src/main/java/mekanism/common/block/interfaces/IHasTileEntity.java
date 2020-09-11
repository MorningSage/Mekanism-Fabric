package mekanism.common.block.interfaces;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;

public interface IHasTileEntity<TILE extends BlockEntity> {

    BlockEntityType<? extends TILE> getTileType();
}