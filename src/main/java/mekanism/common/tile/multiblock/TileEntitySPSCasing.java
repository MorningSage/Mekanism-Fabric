package mekanism.common.tile.multiblock;

import java.util.LinkedList;
import java.util.Queue;
import javax.annotation.Nonnull;
import mekanism.api.NBTConstants;
import mekanism.api.providers.IBlockProvider;
import mekanism.common.Mekanism;
import mekanism.common.content.sps.SPSMultiblockData;
import mekanism.common.lib.multiblock.MultiblockManager;
import mekanism.common.particle.SPSOrbitEffect;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.tile.prefab.TileEntityMultiblock;
import mekanism.common.util.NBTUtils;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;

public class TileEntitySPSCasing extends TileEntityMultiblock<SPSMultiblockData> {

    public final Queue<SPSOrbitEffect> orbitEffects = new LinkedList<>();

    private boolean handleSound;
    private boolean prevActive;

    public TileEntitySPSCasing() {
        super(MekanismBlocks.SPS_CASING);
    }

    public TileEntitySPSCasing(IBlockProvider provider) {
        super(provider);
    }

    @Override
    protected void onUpdateClient() {
        super.onUpdateClient();
        orbitEffects.removeIf(effect -> !isMaster || effect.tick());
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        boolean active = getMultiblock().isFormed() && getMultiblock().handlesSound(this) && getMultiblock().lastProcessed > 0;
        if (active != prevActive) {
            prevActive = active;
            sendUpdatePacket();
        }
    }

    @Override
    public SPSMultiblockData createMultiblock() {
        return new SPSMultiblockData(this);
    }

    @Override
    public MultiblockManager<SPSMultiblockData> getManager() {
        return Mekanism.spsManager;
    }

    @Override
    protected boolean canPlaySound() {
        return getMultiblock().isFormed() && getMultiblock().lastProcessed > 0 && handleSound;
    }

    @Nonnull
    @Override
    public CompoundNBT getReducedUpdateTag() {
        CompoundNBT updateTag = super.getReducedUpdateTag();
        updateTag.putBoolean(NBTConstants.HANDLE_SOUND, getMultiblock().isFormed() && getMultiblock().handlesSound(this));
        if (getMultiblock().isFormed()) {
            updateTag.putDouble(NBTConstants.LAST_PROCESSED, getMultiblock().lastProcessed);
        }
        return updateTag;
    }

    @Override
    public void handleUpdateTag(BlockState state, @Nonnull CompoundNBT tag) {
        super.handleUpdateTag(state, tag);
        NBTUtils.setBooleanIfPresent(tag, NBTConstants.HANDLE_SOUND, value -> handleSound = value);
        if (getMultiblock().isFormed()) {
            getMultiblock().lastProcessed = tag.getDouble(NBTConstants.LAST_PROCESSED);
        }
    }
}
