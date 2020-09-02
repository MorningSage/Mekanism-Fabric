package mekanism.core;

import mekanism.blocks.MekanismBlocks;
import mekanism.items.MekanismItems;
import mekanism.utils.ItemGroupHelper;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class MekanismCommon implements ModInitializer {
    public static final String MOD_ID = "mekanism";

    public static final ItemGroup ITEM_GROUP = new ItemGroupHelper(MOD_ID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(Blocks.ACACIA_LOG);
        }

        @Override
        public boolean getIsSearchable() {
            return true;
        }

        @Override
        public int getSearchWidth() {
            return 61;
        }

        @Override
        public String getTexture() {
            return MOD_ID + ".png";
        }
    };
    @Override
    public void onInitialize() {
        MekanismItems.init();
        MekanismBlocks.init();
    }
}
