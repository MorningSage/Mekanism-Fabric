package mekanism.client.gui.element.button;

import net.minecraft.client.util.math.MatrixStack;
import javax.annotation.Nonnull;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.render.MekanismRenderer;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

public class MekanismImageButton extends MekanismButton {

    private final Identifier resourceLocation;
    private final int textureWidth;
    private final int textureHeight;

    public MekanismImageButton(IGuiWrapper gui, int x, int y, int size, Identifier resource, Runnable onPress) {
        this(gui, x, y, size, size, resource, onPress);
    }

    public MekanismImageButton(IGuiWrapper gui, int x, int y, int size, Identifier resource, Runnable onPress, IHoverable onHover) {
        this(gui, x, y, size, size, resource, onPress, onHover);
    }

    public MekanismImageButton(IGuiWrapper gui, int x, int y, int size, int textureSize, Identifier resource, Runnable onPress) {
        this(gui, x, y, size, textureSize, resource, onPress, null);
    }

    public MekanismImageButton(IGuiWrapper gui, int x, int y, int size, int textureSize, Identifier resource, Runnable onPress, IHoverable onHover) {
        this(gui, x, y, size, size, textureSize, textureSize, resource, onPress, onHover);
    }

    public MekanismImageButton(IGuiWrapper gui, int x, int y, int width, int height, int textureWidth, int textureHeight, Identifier resource, Runnable onPress) {
        this(gui, x, y, width, height, textureWidth, textureHeight, resource, onPress, null);
    }

    public MekanismImageButton(IGuiWrapper gui, int x, int y, int width, int height, int textureWidth, int textureHeight, Identifier resource, Runnable onPress, IHoverable onHover) {
        super(gui, x, y, width, height, LiteralText.EMPTY, onPress, onHover);
        this.resourceLocation = resource;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    @Override
    public void drawBackground(@Nonnull MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        super.drawBackground(matrix, mouseX, mouseY, partialTicks);
        MekanismRenderer.bindTexture(getResource());
        drawTexture(matrix, x, y, width, height, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);
    }

    protected Identifier getResource() {
        return resourceLocation;
    }
}