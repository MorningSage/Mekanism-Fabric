package mekanism.client.gui.robit;

import net.minecraft.client.util.math.MatrixStack;
import javax.annotation.Nonnull;
import mekanism.common.MekanismLang;
import mekanism.common.inventory.container.entity.robit.InventoryRobitContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class GuiRobitInventory extends GuiRobit<InventoryRobitContainer> {

    public GuiRobitInventory(InventoryRobitContainer container, PlayerInventory inv, Text title) {
        super(container, inv, title);
        dynamicSlots = true;
    }

    @Override
    protected void drawForegroundText(@Nonnull MatrixStack matrix, int mouseX, int mouseY) {
        drawString(matrix, MekanismLang.ROBIT_INVENTORY.translate(), 8, 6, titleTextColor());
        drawString(matrix, MekanismLang.INVENTORY.translate(), 8, getYSize() - 93, titleTextColor());
        super.drawForegroundText(matrix, mouseX, mouseY);
    }

    @Override
    protected boolean shouldOpenGui(RobitGuiType guiType) {
        return guiType != RobitGuiType.INVENTORY;
    }
}