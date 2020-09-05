package mekanism.common.inventory.container.slot;

import mekanism.api.Action;
import mekanism.api.inventory.AutomationType;
import mekanism.api.inventory.IInventorySlot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public interface IInsertableSlot {

    //TODO: Improve these java docs at some point

    /**
     * Basically a container slot's equivalent of {@link IInventorySlot#insertItem(ItemStack, Action, AutomationType)} with {@link AutomationType#MANUAL}
     */
    @Nonnull
    ItemStack insertItem(@Nonnull ItemStack stack, Action action);
}