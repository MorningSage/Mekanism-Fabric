package mekanism.client.gui.element.progress;

import net.minecraft.client.util.math.MatrixStack;
import javax.annotation.Nonnull;
import mekanism.client.gui.IGuiWrapper;

public class GuiFlame extends GuiProgress {

    public GuiFlame(IProgressInfoHandler handler, IGuiWrapper gui, int x, int y) {
        super(handler, ProgressType.FLAME, gui, x, y);
    }

    @Override
    public void drawBackground(@Nonnull MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        super.drawBackground(matrix, mouseX, mouseY, partialTicks);
        minecraft.getTextureManager().bindTexture(getResource());
        drawTexture(matrix, x, y, 0, 0, width, height, type.getTextureWidth(), type.getTextureHeight());
        if (handler.isActive()) {
            int displayInt = (int) (handler.getProgress() * height);
            drawTexture(matrix, x, y + height - displayInt, width, height - displayInt, width, displayInt, type.getTextureWidth(), type.getTextureHeight());
        }
    }
}