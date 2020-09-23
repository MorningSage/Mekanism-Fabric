package mekanism.common.capabilities.holder;

import net.minecraft.util.math.Direction;

import javax.annotation.Nullable;

public interface IHolder {

    default boolean canInsert(@Nullable Direction direction) {
        return true;
    }

    default boolean canExtract(@Nullable Direction direction) {
        return true;
    }
}