package mekanism.client.gui.element;

import net.minecraft.client.util.math.MatrixStack;
import javax.annotation.Nonnull;
import mekanism.client.gui.IGuiWrapper;
import net.minecraft.util.Identifier;

public abstract class GuiScalableElement extends GuiTexturedElement {

    protected final int sideWidth;
    protected final int sideHeight;

    protected GuiScalableElement(Identifier resource, IGuiWrapper gui, int x, int y, int width, int height, int sideWidth, int sideHeight) {
        super(resource, gui, x, y, width, height);
        active = false;
        this.sideWidth = sideWidth;
        this.sideHeight = sideHeight;
    }

    @Override
    public void drawBackground(@Nonnull MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        super.drawBackground(matrix, mouseX, mouseY, partialTicks);
        renderBackgroundTexture(matrix, getResource(), sideWidth, sideHeight);
    }
}