package mekanism.common.capabilities.chemical.dynamic;

import mekanism.api.IContentsListener;
import mekanism.api.chemical.infuse.IInfusionTank;
import net.minecraft.util.Direction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public interface IInfusionTracker extends IContentsListener {

    @Nonnull
    List<IInfusionTank> getInfusionTanks(@Nullable Direction side);
}