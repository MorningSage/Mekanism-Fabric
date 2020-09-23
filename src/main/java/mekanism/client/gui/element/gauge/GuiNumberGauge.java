package mekanism.client.gui.element.gauge;

import mekanism.client.gui.IGuiWrapper;
import mekanism.common.lib.transmitter.TransmissionType;
import net.minecraft.client.texture.Sprite;
import net.minecraft.text.Text;

import java.util.Collections;
import java.util.List;

public class GuiNumberGauge extends GuiGauge<Void> {

    private final INumberInfoHandler infoHandler;

    public GuiNumberGauge(INumberInfoHandler handler, GaugeType type, IGuiWrapper gui, int x, int y) {
        super(type, gui, x, y);
        infoHandler = handler;
    }

    @Override
    public TransmissionType getTransmission() {
        return null;
    }

    @Override
    public int getScaledLevel() {
        return (int) ((height - 2) * infoHandler.getScaledLevel());
    }

    @Override
    public Sprite getIcon() {
        return infoHandler.getIcon();
    }

    @Override
    public Text getLabel() {
        return null;
    }

    @Override
    public List<Text> getTooltipText() {
        return Collections.singletonList(infoHandler.getText());
    }

    public interface INumberInfoHandler {

        Sprite getIcon();

        double getLevel();

        double getScaledLevel();

        Text getText();
    }
}