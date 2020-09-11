package mekanism.client.gui.element.bar;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.GuiTexturedElement;
import mekanism.client.gui.element.bar.GuiBar.IBarInfoHandler;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public abstract class GuiBar<INFO extends IBarInfoHandler> extends GuiTexturedElement {

    public static final Identifier BAR = MekanismUtils.getResource(ResourceType.GUI_BAR, "base.png");

    private final INFO handler;

    public GuiBar(Identifier resource, IGuiWrapper gui, INFO handler, int x, int y, int width, int height) {
        super(resource, gui, x, y, width + 2, height + 2);
        this.handler = handler;
    }

    public INFO getHandler() {
        return handler;
    }

    protected abstract void renderBarOverlay(MatrixStack matrix, int mouseX, int mouseY, float partialTicks);

    @Override
    public void drawBackground(@Nonnull MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        //Render the bar
        renderExtendedTexture(matrix, BAR, 2, 2);
        //If there are any contents render them
        if (handler.getLevel() > 0) {
            minecraft.getTextureManager().bindTexture(getResource());
            renderBarOverlay(matrix, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public void renderToolTip(@Nonnull MatrixStack matrix, int mouseX, int mouseY) {
        Text tooltip = handler.getTooltip();
        if (tooltip != null) {
            displayTooltip(matrix, tooltip, mouseX, mouseY);
        }
    }

    protected static int calculateScaled(double scale, int value) {
        if (scale == 1) {
            return value;
        } else if (scale < 1) {
            //Round down
            return (int) (scale * value);
        }//else > 1
        //Allow rounding up
        return (int) Math.round(scale * value);
    }

    public interface IBarInfoHandler {

        @Nullable
        default Text getTooltip() {
            return null;
        }

        double getLevel();
    }
}