package mekanism.client.gui;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.client.gui.element.GuiElement;
import mekanism.client.gui.element.GuiWindow;
import mekanism.common.Mekanism;
import mekanism.mixin.HandledScreenAccessor;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public interface IGuiWrapper {

    default void displayTooltip(MatrixStack matrix, Text component, int x, int y, int maxWidth) {
        this.displayTooltips(matrix, Collections.singletonList(component), x, y, maxWidth);
    }

    default void displayTooltip(MatrixStack matrix, Text component, int x, int y) {
        this.displayTooltips(matrix, Collections.singletonList(component), x, y);
    }

    default void displayTooltips(MatrixStack matrix, List<Text> components, int xAxis, int yAxis) {
        displayTooltips(matrix, components, xAxis, yAxis, -1);
    }

    default void displayTooltips(MatrixStack matrix, List<Text> components, int xAxis, int yAxis, int maxWidth) {
        //TODO - 1.16.2: https://github.com/MinecraftForge/MinecraftForge/pull/7268
        //net.minecraftforge.fml.client.gui.GuiUtils.drawHoveringText(matrix, components, xAxis, yAxis, getWidth(), getHeight(), maxWidth, getFont());
    }

    default int getLeft() {
        if (this instanceof HandledScreen) {
            return ((HandledScreenAccessor) this).getGuiLeft();
        }
        return 0;
    }

    default int getTop() {
        if (this instanceof HandledScreen) {
            return ((HandledScreenAccessor) this).getGuiTop();
        }
        return 0;
    }

    default int getWidth() {
        if (this instanceof HandledScreen) {
            return ((HandledScreenAccessor) this).getXSize();
        }
        return 0;
    }

    default int getHeight() {
        if (this instanceof HandledScreen) {
            return ((HandledScreenAccessor) this).getYSize();
        }
        return 0;
    }

    default void addWindow(GuiWindow window) {
        Mekanism.logger.error("Tried to call 'addWindow' but unsupported in {}", getClass().getName());
    }

    default void removeWindow(GuiWindow window) {
        Mekanism.logger.error("Tried to call 'removeWindow' but unsupported in {}", getClass().getName());
    }

    @Nullable
    default GuiWindow getWindowHovering(double mouseX, double mouseY) {
        Mekanism.logger.error("Tried to call 'getWindowHovering' but unsupported in {}", getClass().getName());
        return null;
    }

    @Nullable
    TextRenderer getFont();

    default void renderItem(MatrixStack matrix, @Nonnull ItemStack stack, int xAxis, int yAxis) {
        renderItem(matrix, stack, xAxis, yAxis, 1);
    }

    default void renderItem(MatrixStack matrix, @Nonnull ItemStack stack, int xAxis, int yAxis, float scale) {
        GuiUtils.renderItem(matrix, getItemRenderer(), stack, xAxis, yAxis, scale, getFont(), null, false);
    }

    ItemRenderer getItemRenderer();

    default void renderItemTooltip(MatrixStack matrix, @Nonnull ItemStack stack, int xAxis, int yAxis) {
        Mekanism.logger.error("Tried to call 'renderItemTooltip' but unsupported in {}", getClass().getName());
    }

    default void renderItemWithOverlay(MatrixStack matrix, @Nonnull ItemStack stack, int xAxis, int yAxis, float scale, String text) {
        GuiUtils.renderItem(matrix, getItemRenderer(), stack, xAxis, yAxis, scale, getFont(), text, true);
    }

    default void addFocusListener(GuiElement element) {
        Mekanism.logger.error("Tried to call 'addFocusListener' but unsupported in {}", getClass().getName());
    }

    default void removeFocusListener(GuiElement element) {
        Mekanism.logger.error("Tried to call 'removeFocusListener' but unsupported in {}", getClass().getName());
    }

    default void focusChange(GuiElement changed) {
        Mekanism.logger.error("Tried to call 'focusChange' but unsupported in {}", getClass().getName());
    }

    default void incrementFocus(GuiElement current) {
        Mekanism.logger.error("Tried to call 'incrementFocus' but unsupported in {}", getClass().getName());
    }
}