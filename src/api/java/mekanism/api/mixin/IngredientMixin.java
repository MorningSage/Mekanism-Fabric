package mekanism.api.mixin;

import mekanism.api.mixin.accessors.IngredientAccessor;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Ingredient.class)
public abstract class IngredientMixin implements IngredientAccessor {

    @Shadow protected abstract void cacheMatchingStacks();
    @Shadow private ItemStack[] matchingStacks;

    @Override
    public ItemStack[] getMatchingStacksSafe() {
        this.cacheMatchingStacks();;
        return this.matchingStacks;
    }
}
