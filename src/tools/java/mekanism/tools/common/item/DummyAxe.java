package mekanism.tools.common.item;

import net.minecraft.block.Block;
import net.minecraft.item.AxeItem;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;

import java.util.Map;

public class DummyAxe extends AxeItem {
    protected DummyAxe(ToolMaterial material, int attackDamage, float attackSpeed) {
        super(material, attackDamage, attackSpeed, new Settings().maxDamage(0));
    }

    public static Map<Block, Block> STRIPPED_BLOCKS() {
        return STRIPPED_BLOCKS;
    }
}
