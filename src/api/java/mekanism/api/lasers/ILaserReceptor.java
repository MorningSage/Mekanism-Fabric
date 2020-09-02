package mekanism.api.lasers;

import mekanism.api.math.FloatingLong;
import net.minecraft.util.math.Direction;

import javax.annotation.Nonnull;

public interface ILaserReceptor {

    void receiveLaserEnergy(@Nonnull FloatingLong energy, Direction side);

    boolean canLasersDig();
}