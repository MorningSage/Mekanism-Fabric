package mekanism.api._helpers_pls_remove;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemHandlerHelper {
    public static boolean canItemStacksStack(@Nonnull ItemStack a, @Nonnull ItemStack b) {
        if (a.isEmpty() || !a.isItemEqual(b) || a.hasTag() != b.hasTag())
            return false;

        return (!a.hasTag() || a.getTag().equals(b.getTag()));
    }
}
