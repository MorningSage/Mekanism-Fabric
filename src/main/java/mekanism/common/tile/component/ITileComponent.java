package mekanism.common.tile.component;

import mekanism.common.inventory.container.MekanismContainer;
import net.minecraft.nbt.CompoundTag;

public interface ITileComponent {

    void tick();

    void read(CompoundTag nbtTags);

    void write(CompoundTag nbtTags);

    default void invalidate() {
    }

    default void onChunkUnload() {
    }

    void trackForMainContainer(MekanismContainer container);

    void addToUpdateTag(CompoundTag updateTag);

    void readFromUpdateTag(CompoundTag updateTag);
}