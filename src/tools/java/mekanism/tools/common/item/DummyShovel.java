package mekanism.tools.common.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.ToolMaterial;

import java.util.Map;

public class DummyShovel extends ShovelItem {
    protected DummyShovel(ToolMaterial material, int attackDamage, float attackSpeed) {
        super(material, attackDamage, attackSpeed, new Settings().maxDamage(0));
    }

    public static Map<Block, BlockState> PATH_STATES() {
        return PATH_STATES;
    }
}
