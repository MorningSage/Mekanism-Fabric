package mekanism.tools.common;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public interface IHasPreferredEquipmentSlot {
    EquipmentSlot getPreferredEquipmentSlot(ItemStack stack, @Nullable LivingEntity entity);
}
