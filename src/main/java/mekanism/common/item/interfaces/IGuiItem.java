package mekanism.common.item.interfaces;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.Hand;

public interface IGuiItem {

    NamedScreenHandlerFactory getContainerProvider(ItemStack stack, Hand hand);
}
