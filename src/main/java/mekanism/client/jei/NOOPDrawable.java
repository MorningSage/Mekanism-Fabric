package mekanism.client.jei;

import net.minecraft.client.util.math.MatrixStack;
import mezz.jei.api.gui.drawable.IDrawable;

public class NOOPDrawable implements IDrawable {

    private final int width;
    private final int height;

    public NOOPDrawable(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void draw(MatrixStack matrix, int xOffset, int yOffset) {
        //NO-OP
    }
}