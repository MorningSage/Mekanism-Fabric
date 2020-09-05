package mekanism.common.capabilities.chemical.dynamic;

import mekanism.api.IContentsListener;
import mekanism.api.chemical.gas.IGasTank;
import net.minecraft.util.Direction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public interface IGasTracker extends IContentsListener {

    @Nonnull
    List<IGasTank> getGasTanks(@Nullable Direction side);
}