package mekanism.common.block.transmitter;

import mekanism.common.block.interfaces.IHasTileEntity;
import mekanism.common.registries.MekanismTileEntityTypes;
import mekanism.common.tile.transmitter.TileEntityRestrictiveTransporter;
import net.minecraft.block.entity.BlockEntityType;

public class BlockRestrictiveTransporter extends BlockLargeTransmitter implements IHasTileEntity<TileEntityRestrictiveTransporter> {

    @Override
    public BlockEntityType<? extends TileEntityRestrictiveTransporter> getTileType() {
        return MekanismTileEntityTypes.RESTRICTIVE_TRANSPORTER.getTileEntityType();
    }
}