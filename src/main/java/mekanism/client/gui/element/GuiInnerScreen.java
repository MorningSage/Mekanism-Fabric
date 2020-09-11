package mekanism.client.gui.element;

import net.minecraft.client.util.math.MatrixStack;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.jei.interfaces.IJEIRecipeArea;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class GuiInnerScreen extends GuiScalableElement implements IJEIRecipeArea<GuiInnerScreen> {

    public static final Identifier SCREEN = MekanismUtils.getResource(ResourceType.GUI, "inner_screen.png");

    private Supplier<List<Text>> renderStrings;
    private Supplier<List<Text>> tooltipStrings;

    private Identifier[] recipeCategories;
    private boolean centerY;
    private int spacing = 1;
    private int padding = 3;
    private float textScale = 1.0F;

    public GuiInnerScreen(IGuiWrapper gui, int x, int y, int width, int height) {
        super(SCREEN, gui, x, y, width, height, 32, 32);
    }

    public GuiInnerScreen(IGuiWrapper gui, int x, int y, int width, int height, Supplier<List<Text>> renderStrings) {
        this(gui, x, y, width, height);
        this.renderStrings = renderStrings;
        defaultFormat();
    }

    public GuiInnerScreen tooltip(Supplier<List<Text>> tooltipStrings) {
        this.tooltipStrings = tooltipStrings;
        active = true;
        return this;
    }

    public GuiInnerScreen spacing(int spacing) {
        this.spacing = spacing;
        return this;
    }

    public GuiInnerScreen padding(int padding) {
        this.padding = padding;
        return this;
    }

    public GuiInnerScreen textScale(float textScale) {
        this.textScale = textScale;
        return this;
    }

    public GuiInnerScreen centerY() {
        centerY = true;
        return this;
    }

    public GuiInnerScreen clearFormat() {
        centerY = false;
        return this;
    }

    public GuiInnerScreen defaultFormat() {
        return padding(5).spacing(3).textScale(0.8F).centerY();
    }

    @Override
    public void renderForeground(MatrixStack matrix, int mouseX, int mouseY) {
        super.renderForeground(matrix, mouseX, mouseY);

        if (renderStrings != null) {
            List<Text> list = renderStrings.get();
            float startY = relativeY + padding;
            if (centerY) {
                int totalHeight = list.size() * 8 + spacing * (list.size() - 1);
                startY = relativeY + getHeight() / 2F - totalHeight / 2F;
            }
            for (Text text : renderStrings.get()) {
                drawText(matrix, text, relativeX + padding, startY);
                startY += 8 + spacing;
            }
        }
    }

    @Override
    public void renderToolTip(@Nonnull MatrixStack matrix, int mouseX, int mouseY) {
        super.renderToolTip(matrix, mouseX, mouseY);
        if (tooltipStrings != null) {
            List<Text> list = tooltipStrings.get();
            if (list != null && !list.isEmpty()) {
                displayTooltips(matrix, list, mouseX, mouseY);
            }
        }
    }

    private void drawText(MatrixStack matrix, Text text, float x, float y) {
        drawScaledTextScaledBound(matrix, text, x, y, screenTextColor(), getWidth() - padding * 2, textScale);
    }

    @Nonnull
    @Override
    public GuiInnerScreen jeiCategories(@Nullable Identifier... recipeCategories) {
        this.recipeCategories = recipeCategories;
        return this;
    }

    @Nullable
    @Override
    public Identifier[] getRecipeCategories() {
        return recipeCategories;
    }

    @Override
    public boolean isMouseOverJEIArea(double mouseX, double mouseY) {
        return visible && mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }
}