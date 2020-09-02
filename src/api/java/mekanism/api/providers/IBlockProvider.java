package mekanism.api.providers;

import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public interface IBlockProvider extends IItemProvider {

    @Nonnull
    Block getBlock();

    default boolean blockMatches(ItemStack otherStack) {
        Item item = otherStack.getItem();
        return item instanceof BlockItem && blockMatches(((BlockItem) item).getBlock());
    }

    default boolean blockMatches(Block other) {
        return getBlock() == other;
    }

    @Override
    default Identifier getRegistryName() {
        //Make sure to use the block's registry name in case it somehow doesn't match
        return Registry.BLOCK.getId(getBlock());
    }

    @Override
    default String getTranslationKey() {
        return getBlock().getTranslationKey();
    }
}