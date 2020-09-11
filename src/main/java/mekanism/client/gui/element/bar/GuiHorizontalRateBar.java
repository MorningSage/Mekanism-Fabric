package mekanism.client.gui.element.bar;

import net.minecraft.client.util.math.MatrixStack;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.bar.GuiBar.IBarInfoHandler;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.util.Identifier;

public class GuiHorizontalRateBar extends GuiBar<IBarInfoHandler> {

    private static final Identifier RATE_BAR = MekanismUtils.getResource(ResourceType.GUI_BAR, "horizontal_rate.png");
    private static final int texWidth = 78;
    private static final int texHeight = 8;

    public GuiHorizontalRateBar(IGuiWrapper gui, IBarInfoHandler handler, int x, int y) {
        super(RATE_BAR, gui, handler, x, y, texWidth, texHeight);
    }

    @Override
    protected void renderBarOverlay(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        int displayInt = (int) (getHandler().getLevel() * texWidth);
        drawTexture(matrix, x + 1, y + 1, 0, 0, displayInt, texHeight, texWidth, texHeight);
    }
}