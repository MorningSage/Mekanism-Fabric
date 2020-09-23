package mekanism.common.registration.impl;

import javax.annotation.Nonnull;
import mekanism.common.registration.WrappedRegistryObject;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;

public class TileEntityTypeRegistryObject<TILE extends BlockEntity> extends WrappedRegistryObject<BlockEntityType<TILE>> {

    public TileEntityTypeRegistryObject(RegistryObject<BlockEntityType<TILE>> registryObject) {
        super(registryObject);
    }

    @Nonnull
    public BlockEntityType<TILE> getTileEntityType() {
        return get();
    }
}