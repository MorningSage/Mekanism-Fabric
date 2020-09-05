package mekanism.tools.common.item;

import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;

public class DummyPickaxe extends PickaxeItem {
    protected DummyPickaxe(ToolMaterial material, int attackDamage, float attackSpeed) {
        super(material, attackDamage, attackSpeed, new Settings().maxDamage(0));
    }
}
