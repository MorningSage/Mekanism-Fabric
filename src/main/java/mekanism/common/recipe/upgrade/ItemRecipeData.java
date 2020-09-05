package mekanism.common.recipe.upgrade;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import mekanism.api.DataHandlerUtils;
import mekanism.api.NBTConstants;
import mekanism.api.annotations.FieldsAreNonnullByDefault;
import mekanism.api.annotations.NonNull;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.inventory.IMekanismInventory;
import mekanism.common.block.interfaces.IHasTileEntity;
import mekanism.common.inventory.slot.BasicInventorySlot;
import mekanism.common.item.block.ItemBlockBin;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.interfaces.ISustainedInventory;
import mekanism.common.util.MekanismUtils;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

@FieldsAreNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemRecipeData implements RecipeUpgradeData<ItemRecipeData> {

    private final List<IInventorySlot> slots;

    ItemRecipeData(ListNBT slots) {
        int count = DataHandlerUtils.getMaxId(slots, NBTConstants.SLOT);
        this.slots = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            this.slots.add(new DummyInventorySlot());
        }
        DataHandlerUtils.readContainers(this.slots, slots);
    }

    private ItemRecipeData(List<IInventorySlot> slots) {
        this.slots = slots;
    }

    @Nullable
    @Override
    public ItemRecipeData merge(ItemRecipeData other) {
        List<IInventorySlot> allSlots = new ArrayList<>(slots.size() + other.slots.size());
        allSlots.addAll(slots);
        allSlots.addAll(other.slots);
        return new ItemRecipeData(allSlots);
    }

    @Override
    public boolean applyToStack(ItemStack stack) {
        if (slots.isEmpty()) {
            return true;
        }
        Item item = stack.getItem();
        boolean isBin = item instanceof ItemBlockBin;
        Optional<IItemHandler> capability = MekanismUtils.toOptional(stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY));
        List<IInventorySlot> slots = new ArrayList<>();
        if (capability.isPresent()) {
            IItemHandler itemHandler = capability.get();
            for (int i = 0; i < itemHandler.getSlots(); i++) {
                int slot = i;
                slots.add(new DummyInventorySlot(itemHandler.getSlotLimit(slot), itemStack -> itemHandler.isItemValid(slot, itemStack), isBin));
            }
        } else if (item instanceof BlockItem) {
            TileEntityMekanism tile = null;
            Block block = ((BlockItem) item).getBlock();
            if (block instanceof IHasTileEntity) {
                TileEntity tileEntity = ((IHasTileEntity<?>) block).getTileType().create();
                if (tileEntity instanceof TileEntityMekanism) {
                    tile = (TileEntityMekanism) tileEntity;
                }
            }
            if (tile == null || !tile.persistInventory()) {
                //Something went wrong
                return false;
            }
            TileEntityMekanism mekTile = tile;
            for (int i = 0; i < tile.getSlots(); i++) {
                int slot = i;
                slots.add(new DummyInventorySlot(tile.getSlotLimit(slot), itemStack -> mekTile.isItemValid(slot, itemStack), isBin));
            }
        } else if (item instanceof ISustainedInventory) {
            //Fallback just save it all
            for (IInventorySlot slot : this.slots) {
                if (!slot.isEmpty()) {
                    //We have no information about what our item supports, but we have at least some stacks we want to transfer
                    ((ISustainedInventory) stack.getItem()).setInventory(DataHandlerUtils.writeContainers(this.slots), stack);
                    return true;
                }
            }
            return true;
        } else {
            return false;
        }
        if (slots.isEmpty()) {
            //We don't actually have any tanks in the output
            return true;
        }
        //TODO: Improve the logic so that it maybe tries multiple different slot combinations
        IMekanismInventory outputHandler = new IMekanismInventory() {
            @Nonnull
            @Override
            public List<IInventorySlot> getInventorySlots(@Nullable Direction side) {
                return slots;
            }

            @Override
            public void onContentsChanged() {
            }
        };
        boolean hasData = false;
        for (IInventorySlot slot : this.slots) {
            if (!slot.isEmpty()) {
                if (!ItemHandlerHelper.insertItemStacked(outputHandler, slot.getStack(), false).isEmpty()) {
                    //If we have a remainder something failed so bail
                    return false;
                }
                hasData = true;
            }
        }
        if (hasData) {
            //We managed to transfer it all into valid slots, so save it to the stack
            ((ISustainedInventory) stack.getItem()).setInventory(DataHandlerUtils.writeContainers(slots), stack);
        }
        return true;
    }

    private static class DummyInventorySlot extends BasicInventorySlot {

        private DummyInventorySlot() {
            this(Integer.MAX_VALUE, alwaysTrue, true);
        }

        private DummyInventorySlot(int capacity, Predicate<@NonNull ItemStack> validator, boolean isBin) {
            super(capacity, alwaysTrueBi, alwaysTrueBi, validator, null, 0, 0);
            if (isBin) {
                obeyStackLimit = false;
            }
        }
    }
}