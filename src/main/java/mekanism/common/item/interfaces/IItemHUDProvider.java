package mekanism.common.item.interfaces;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;

public interface IItemHUDProvider {

    void addHUDStrings(List<Text> list, ItemStack stack, EquipmentSlot slotType);
}