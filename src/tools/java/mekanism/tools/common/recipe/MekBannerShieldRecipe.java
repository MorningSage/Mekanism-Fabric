package mekanism.tools.common.recipe;

import mekanism.api.NBTConstants;
import mekanism.tools.common.item.ItemMekanismShield;
import mekanism.tools.common.registries.ToolsRecipeSerializers;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class MekBannerShieldRecipe extends SpecialCraftingRecipe {

    public MekBannerShieldRecipe(Identifier id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingInventory inv, @Nonnull World world) {
        ItemStack shieldStack = ItemStack.EMPTY;
        ItemStack bannerStack = ItemStack.EMPTY;
        for (int i = 0; i < inv.size(); ++i) {
            ItemStack stackInSlot = inv.getStack(i);
            if (!stackInSlot.isEmpty()) {
                if (stackInSlot.getItem() instanceof BannerItem) {
                    if (!bannerStack.isEmpty()) {
                        return false;
                    }
                    bannerStack = stackInSlot;
                } else {
                    if (!(stackInSlot.getItem() instanceof ItemMekanismShield) || !shieldStack.isEmpty() || stackInSlot.getSubTag(NBTConstants.BLOCK_ENTITY_TAG) != null) {
                        return false;
                    }
                    shieldStack = stackInSlot;
                }
            }
        }
        return !shieldStack.isEmpty() && !bannerStack.isEmpty();
    }


    @Nonnull
    @Override
    public ItemStack craft(CraftingInventory inv) {
        ItemStack bannerStack = ItemStack.EMPTY;
        ItemStack shieldStack = ItemStack.EMPTY;
        for (int i = 0; i < inv.size(); ++i) {
            ItemStack stackInSlot = inv.getStack(i);
            if (!stackInSlot.isEmpty()) {
                if (stackInSlot.getItem() instanceof BannerItem) {
                    bannerStack = stackInSlot;
                } else if (stackInSlot.getItem() instanceof ItemMekanismShield) {
                    shieldStack = stackInSlot.copy();
                }
            }
        }
        if (shieldStack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        CompoundTag blockEntityTag = bannerStack.getSubTag(NBTConstants.BLOCK_ENTITY_TAG);
        CompoundTag tag = blockEntityTag == null ? new CompoundTag() : blockEntityTag.copy();
        tag.putInt(NBTConstants.BASE, ((BannerItem) bannerStack.getItem()).getColor().getId());
        shieldStack.putSubTag(NBTConstants.BLOCK_ENTITY_TAG, tag);
        return shieldStack;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    @Nonnull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ToolsRecipeSerializers.BANNER_SHIELD.get();
    }
}