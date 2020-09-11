package mekanism.client.gui.element;

import java.util.List;
import mekanism.client.gui.IGuiWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public abstract class GuiTexturedElement extends GuiRelativeElement {

    protected final Identifier resource;

    public GuiTexturedElement(Identifier resource, IGuiWrapper gui, int x, int y, int width, int height) {
        super(gui, x, y, width, height);
        this.resource = resource;
    }

    protected Identifier getResource() {
        return resource;
    }

    public interface IInfoHandler {

        List<Text> getInfo();
    }
}