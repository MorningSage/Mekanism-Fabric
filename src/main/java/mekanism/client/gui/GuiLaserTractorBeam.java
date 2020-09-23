package mekanism.client.gui;

import net.minecraft.client.util.math.MatrixStack;
import javax.annotation.Nonnull;
import mekanism.client.gui.element.tab.GuiSecurityTab;
import mekanism.common.MekanismLang;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.tile.laser.TileEntityLaserTractorBeam;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class GuiLaserTractorBeam extends GuiMekanismTile<TileEntityLaserTractorBeam, MekanismTileContainer<TileEntityLaserTractorBeam>> {

    public GuiLaserTractorBeam(MekanismTileContainer<TileEntityLaserTractorBeam> container, PlayerInventory inv, Text title) {
        super(container, inv, title);
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