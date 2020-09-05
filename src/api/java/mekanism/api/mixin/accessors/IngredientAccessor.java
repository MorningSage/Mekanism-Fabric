package mekanism.api.mixin.accessors;

import net.minecraft.item.ItemStack;

public interface IngredientAccessor {
    ItemStack[] getMatchingStacksSafe();
}
