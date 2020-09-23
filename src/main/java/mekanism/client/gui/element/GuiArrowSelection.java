package mekanism.client.gui.element;

import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import mekanism.client.gui.GuiUtils;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class GuiArrowSelection extends GuiTexturedElement {

    private static final Identifier ARROW = MekanismUtils.getResource(ResourceType.GUI, "arrow_selection.png");

    private final Supplier<Text> textComponentSupplier;

    public GuiArrowSelection(IGuiWrapper gui, int x, int y, Supplier<Text> textComponentSupplier) {
        super(ARROW, gui, x, y, 33, 19);
        this.textComponentSupplier = textComponentSupplier;
    }

    @Override
    public boolean isMouseOver(double xAxis, double yAxis) {
        //TODO: override isHovered
        return this.active && this.visible && xAxis >= x + 16 && xAxis < x + width - 1 && yAxis >= y + 1 && yAxis < y + height - 1;
    }

    @Override
    public void renderToolTip(@Nonnull MatrixStack matrix, int mouseX, int mouseY) {
        Text component = textComponentSupplier.get();
        if (component != null) {
            int tooltipX = mouseX + 5;
            int tooltipY = mouseY - 5;
            GuiUtils.renderExtendedTexture(matrix, GuiInnerScreen.SCREEN, 2, 2, tooltipX - 3, tooltipY - 4, getStringWidth(component) + 6, 16);
            VertexConsumerProvider.Immediate renderType = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
            matrix.push();
            //Make sure the text is above other renders like JEI
            matrix.translate(0.0D, 0.0D, 300);
            getFont().draw(component, tooltipX, tooltipY, screenTextColor(), false, matrix.peek().getModel(),
                  renderType, false, 0, MekanismRenderer.FULL_LIGHT);
            matrix.pop();
            renderType.draw();
        }
    }

    @Override
    public void drawBackground(@Nonnull MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        super.drawBackground(matrix, mouseX, mouseY, partialTicks);
        minecraft.getTextureManager().bindTexture(getResource());
        drawTexture(matrix, x, y, 0, 0, width, height, width, height);
    }
}