package mekanism.api.providers;

import javax.annotation.Nonnull;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public interface IItemProvider extends IBaseProvider, ItemConvertible {

    @Nonnull
    Item getItem();

    @Nonnull
    @Override
    default Item asItem() {
        return getItem();
    }

    @Nonnull
    default ItemStack getItemStack() {
        return getItemStack(1);
    }

    @Nonnull
    default ItemStack getItemStack(int size) {
        return new ItemStack(getItem(), size);
    }

    default boolean itemMatches(ItemStack otherStack) {
        return itemMatches(otherStack.getItem());
    }

    default boolean itemMatches(Item other) {
        return getItem() == other;
    }

    @Override
    default Identifier getRegistryName() {
        return Registry.ITEM.getId(getItem());
    }

    @Override
    default String getTranslationKey() {
        return getItem().getTranslationKey();
    }
}