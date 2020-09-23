package mekanism.client.gui.element.tab;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.util.math.MatrixStack;
import javax.annotation.Nonnull;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.GuiInsetElement;

public abstract class GuiTabElementType<TILE extends BlockEntity, TAB extends Enum<?> & TabType<TILE>> extends GuiInsetElement<TILE> {

    private final TAB tabType;

    public GuiTabElementType(IGuiWrapper gui, TILE tile, TAB type) {
        super(type.getResource(), gui, tile, -26, type.getYPos(), 26, 18, true);
        tabType = type;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        tabType.onClick(tile);
    }

    @Override
    public void renderToolTip(@Nonnull MatrixStack matrix, int mouseX, int mouseY) {
        displayTooltip(matrix, tabType.getDescription(), mouseX, mouseY);
    }
}