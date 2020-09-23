package mekanism.common.content.assemblicator;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import javax.annotation.Nullable;
import mekanism.api.inventory.IInventorySlot;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.StackUtils;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class RecipeFormula {

    public final DefaultedList<ItemStack> input = DefaultedList.ofSize(9, ItemStack.EMPTY);
    @Nullable
    public ICraftingRecipe recipe;
    private final CraftingInventory dummy = MekanismUtils.getDummyCraftingInv();

    public RecipeFormula(World world, DefaultedList<ItemStack> inv) {
        for (int i = 0; i < 9; i++) {
            input.set(i, StackUtils.size(inv.get(i), 1));
        }
        resetToRecipe();
        recipe = getRecipeFromGrid(dummy, world);
    }

    public RecipeFormula(World world, List<IInventorySlot> craftingGridSlots) {
        //Should always be 9 for the size
        for (int i = 0; i < craftingGridSlots.size(); i++) {
            IInventorySlot craftingSlot = craftingGridSlots.get(i);
            if (!craftingSlot.isEmpty()) {
                input.set(i, StackUtils.size(craftingSlot.getStack(), 1));
            }
        }
        resetToRecipe();
        recipe = getRecipeFromGrid(dummy, world);
    }

    public ItemStack getInputStack(int slot) {
        return input.get(slot);
    }

    private void resetToRecipe() {
        for (int i = 0; i < 9; i++) {
            dummy.setStack(i, input.get(i));
        }
    }

    public boolean matches(World world, List<IInventorySlot> craftingGridSlots) {
        if (recipe == null) {
            return false;
        }
        //Should always be 9 for the size
        for (int i = 0; i < craftingGridSlots.size(); i++) {
            dummy.setStack(i, StackUtils.size(craftingGridSlots.get(i).getStack(), 1));
        }
        return recipe.matches(dummy, world);
    }

    public boolean isIngredientInPos(World world, ItemStack stack, int i) {
        if (recipe == null) {
            return false;
        }
        resetToRecipe();
        dummy.setStack(i, stack);
        return recipe.matches(dummy, world);
    }

    public IntList getIngredientIndices(World world, ItemStack stack) {
        IntList ret = new IntArrayList();
        if (recipe != null) {
            for (int i = 0; i < 9; i++) {
                dummy.setStack(i, stack);
                if (recipe.matches(dummy, world)) {
                    ret.add(i);
                }
                dummy.setStack(i, input.get(i));
            }
        }
        return ret;
    }

    public boolean isValidFormula() {
        return getRecipe() != null;
    }

    @Nullable
    public ICraftingRecipe getRecipe() {
        return recipe;
    }

    public boolean isFormulaEqual(RecipeFormula formula) {
        return formula.getRecipe() == getRecipe();
    }

    public void setStack(World world, int index, ItemStack stack) {
        input.set(index, stack);
        resetToRecipe();
        recipe = getRecipeFromGrid(dummy, world);
    }

    @Nullable
    private static ICraftingRecipe getRecipeFromGrid(CraftingInventory inv, World world) {
        return world.getRecipeManager().getRecipe(RecipeType.CRAFTING, inv, world).orElse(null);
    }
}