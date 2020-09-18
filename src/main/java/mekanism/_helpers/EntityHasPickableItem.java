package mekanism._helpers;

import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.HitResult;

public interface EntityHasPickableItem {
    ItemStack getPickedResult(HitResult target);
}
