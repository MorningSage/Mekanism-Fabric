package mekanism.client.gui.element.custom;

import net.minecraft.client.util.math.MatrixStack;
import java.util.function.BooleanSupplier;
import javax.annotation.Nonnull;
import mekanism.api.functions.ByteSupplier;
import mekanism.api.text.EnumColor;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.GuiTexturedElement;
import mekanism.common.MekanismLang;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class GuiTeleporterStatus extends GuiTexturedElement {

    private static final Identifier NEEDS_ENERGY = MekanismUtils.getResource(ResourceType.GUI, "teleporter_needs_energy.png");
    private static final Identifier NO_FRAME = MekanismUtils.getResource(ResourceType.GUI, "teleporter_no_frame.png");
    private static final Identifier NO_FREQUENCY = MekanismUtils.getResource(ResourceType.GUI, "teleporter_no_frequency.png");
    private static final Identifier NO_LINK = MekanismUtils.getResource(ResourceType.GUI, "teleporter_no_link.png");
    private static final Identifier READY = MekanismUtils.getResource(ResourceType.GUI, "teleporter_ready.png");

    private final BooleanSupplier hasFrequency;
    private final ByteSupplier statusSupplier;

    public GuiTeleporterStatus(IGuiWrapper gui, BooleanSupplier hasFrequency, ByteSupplier statusSupplier) {
        super(NO_FREQUENCY, gui, 6, 6, 18, 18);
        this.hasFrequency = hasFrequency;
        this.statusSupplier = statusSupplier;
        setButtonBackground(ButtonBackground.DEFAULT);
    }

    @Override
    protected int getYImage(boolean hovering) {
        return 1;
    }

    @Override
    protected Identifier getResource() {
        if (hasFrequency.getAsBoolean()) {
            switch (statusSupplier.getAsByte()) {
                case 1:
                    return READY;
                case 2:
                    return NO_FRAME;
                case 4:
                    return NEEDS_ENERGY;
                case 3:
                default:
                    return NO_LINK;
            }
        }
        return NO_FREQUENCY;
    }

    @Override
    public void drawBackground(@Nonnull MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        super.drawBackground(matrix, mouseX, mouseY, partialTicks);
        minecraft.getTextureManager().bindTexture(getResource());
        drawTexture(matrix, x, y, 0, 0, width, height, width, height);
    }

    @Override
    public void renderToolTip(@Nonnull MatrixStack matrix, int mouseX, int mouseY) {
        displayTooltip(matrix, getStatusDisplay(), mouseX, mouseY);
    }

    private Text getStatusDisplay() {
        if (hasFrequency.getAsBoolean()) {
            switch (statusSupplier.getAsByte()) {
                case 1:
                    return MekanismLang.TELEPORTER_READY.translateColored(EnumColor.DARK_GREEN);
                case 2:
                    return MekanismLang.TELEPORTER_NO_FRAME.translateColored(EnumColor.DARK_RED);
                case 4:
                    return MekanismLang.TELEPORTER_NEEDS_ENERGY.translateColored(EnumColor.DARK_RED);
                case 3:
                default:
                    return MekanismLang.TELEPORTER_NO_LINK.translateColored(EnumColor.DARK_RED);
            }
        }
        return MekanismLang.NO_FREQUENCY.translateColored(EnumColor.DARK_RED);
    }
}