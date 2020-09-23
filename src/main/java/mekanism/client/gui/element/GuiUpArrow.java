package mekanism.client.gui.element;

import mekanism.client.gui.IGuiWrapper;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.util.Identifier;

public class GuiUpArrow extends GuiTextureOnlyElement {

    private static final Identifier ARROW = MekanismUtils.getResource(ResourceType.GUI, "up_arrow.png");

    public GuiUpArrow(IGuiWrapper gui, int x, int y) {
        super(ARROW, gui, x, y, 8, 10);
    }
}