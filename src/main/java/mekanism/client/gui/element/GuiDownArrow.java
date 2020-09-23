package mekanism.client.gui.element;

import mekanism.client.gui.IGuiWrapper;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.util.Identifier;

public class GuiDownArrow extends GuiTextureOnlyElement {

    private static final Identifier ARROW = MekanismUtils.getResource(ResourceType.GUI, "down_arrow.png");

    public GuiDownArrow(IGuiWrapper gui, int x, int y) {
        super(ARROW, gui, x, y, 8, 9);
    }
}