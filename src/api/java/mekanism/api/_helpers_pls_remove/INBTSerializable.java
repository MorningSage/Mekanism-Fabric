package mekanism.api._helpers_pls_remove;


import net.minecraft.nbt.Tag;

/**
 * An interface designed to unify various things in the Minecraft
 * code base that can be serialized to and from a NBT tag.
 */
public interface INBTSerializable<T extends Tag>
{
    T serializeNBT();
    void deserializeNBT(T nbt);
}
