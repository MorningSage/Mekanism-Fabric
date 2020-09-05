package mekanism.common.item.interfaces;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

public interface IItemHUDProvider {

    void addHUDStrings(List<ITextComponent> list, ItemStack stack, EquipmentSlotType slotType);
}