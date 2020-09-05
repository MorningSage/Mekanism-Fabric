package mekanism.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import mekanism.client.gui.element.tab.GuiSecurityTab;
import mekanism.common.MekanismLang;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.tile.TileEntityPersonalChest;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;

public class GuiPersonalChestTile extends GuiMekanismTile<TileEntityPersonalChest, MekanismTileContainer<TileEntityPersonalChest>> {

    public GuiPersonalChestTile(MekanismTileContainer<TileEntityPersonalChest> container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
        ySize += 64;
        dynamicSlots = true;
    }

    @Override
    public void init() {
        super.init();
        addButton(new GuiSecurityTab<>(this, tile));
    }

    @Override
    protected void drawForegroundText(@Nonnull MatrixStack matrix, int mouseX, int mouseY) {
        renderTitleText(matrix);
        drawString(matrix, MekanismLang.INVENTORY.translate(), 8, (getYSize() - 96) + 2, titleTextColor());
        super.drawForegroundText(matrix, mouseX, mouseY);
    }
}