package mekanism.client.gui.element.tab;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public interface TabType<TILE extends BlockEntity> {

    Identifier getResource();

    void onClick(TILE tile);

    Text getDescription();

    int getYPos();
}