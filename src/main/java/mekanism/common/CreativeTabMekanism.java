package mekanism.common;

import javax.annotation.Nonnull;
import mekanism.common.registries.MekanismItems;
import net.fabricmc.fabric.impl.item.group.ItemGroupExtensions;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class CreativeTabMekanism extends ItemGroup {

    private static int expandCreativeArray() {
        ((ItemGroupExtensions) ItemGroup.BUILDING_BLOCKS).fabric_expandArray();
        return ItemGroup.GROUPS.length - 1;
    }

    public CreativeTabMekanism() {
        super(expandCreativeArray(), Mekanism.MODID);
    }

    @Nonnull
    @Override
    public ItemStack createIcon() {
        return MekanismItems.ATOMIC_ALLOY.getItemStack();
    }

    @Nonnull
    @Override
    public Text getTranslationKey() {
        //Overwrite the lang key to match the one representing Mekanism
        return MekanismLang.MEKANISM.translate();
    }
}