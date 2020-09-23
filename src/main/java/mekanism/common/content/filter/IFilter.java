package mekanism.common.content.filter;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;

public interface IFilter<FILTER extends IFilter<FILTER>> {

    FILTER clone();

    FilterType getFilterType();

    CompoundTag write(CompoundTag nbtTags);

    void read(CompoundTag nbtTags);

    void write(PacketByteBuf buffer);

    void read(PacketByteBuf dataStream);

    boolean hasFilter();
}