package mekanism.client.gui.element;

import net.minecraft.client.util.math.MatrixStack;
import javax.annotation.Nonnull;
import mekanism.client.gui.IGuiWrapper;
import net.minecraft.util.Identifier;

public class GuiTextureOnlyElement extends GuiTexturedElement {

    private final int textureWidth;
    private final int textureHeight;

    public GuiTextureOnlyElement(Identifier resource, IGuiWrapper gui, int x, int y, int width, int height) {
        this(resource, gui, x, y, width, height, width, height);
    }

    public GuiTextureOnlyElement(Identifier resource, IGuiWrapper gui, int x, int y, int width, int height, int textureWidth, int textureHeight) {
        super(resource, gui, x, y, width, height);
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        active = false;
    }

    @Override
    public void drawBackground(@Nonnull MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        super.drawBackground(matrix, mouseX, mouseY, partialTicks);
        minecraft.getTextureManager().bindTexture(getResource());
        drawTexture(matrix, x, y, 0, 0, width, height, textureWidth, textureHeight);
    }
}