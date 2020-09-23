package mekanism.client.gui.element;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import java.util.function.BooleanSupplier;
import javax.annotation.Nonnull;
import mekanism.client.gui.IGuiWrapper;
import mekanism.common.MekanismLang;
import mekanism.common.registries.MekanismSounds;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class GuiDigitalSwitch extends GuiTexturedElement {

    public static final Identifier SWITCH = MekanismUtils.getResource(ResourceType.GUI, "switch/switch.png");
    public static final int BUTTON_SIZE_X = 15, BUTTON_SIZE_Y = 8;

    private final SwitchType type;
    private final Identifier icon;
    private final BooleanSupplier stateSupplier;
    private final Text tooltip;
    private final Runnable onToggle;

    public GuiDigitalSwitch(IGuiWrapper gui, int x, int y, Identifier icon, BooleanSupplier stateSupplier, Text tooltip,
          Runnable onToggle, SwitchType type) {
        super(SWITCH, gui, x, y, type.sizeX, type.sizeY);
        this.type = type;
        this.icon = icon;
        this.stateSupplier = stateSupplier;
        this.tooltip = tooltip;
        this.onToggle = onToggle;
    }

    @Override
    public void renderToolTip(@Nonnull MatrixStack matrix, int mouseX, int mouseY) {
        displayTooltip(matrix, tooltip, mouseX, mouseY);
    }

    @Override
    public void drawBackground(@Nonnull MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        super.drawBackground(matrix, mouseX, mouseY, partialTicks);
        minecraft.getTextureManager().bindTexture(getResource());
        drawTexture(matrix, x + type.switchX, y + type.switchY, 0, stateSupplier.getAsBoolean() ? 0 : BUTTON_SIZE_Y, BUTTON_SIZE_X, BUTTON_SIZE_Y, BUTTON_SIZE_X, BUTTON_SIZE_Y * 2);
        drawTexture(matrix, x + type.switchX, y + type.switchY + BUTTON_SIZE_Y + 1, 0, stateSupplier.getAsBoolean() ? BUTTON_SIZE_Y : 0, BUTTON_SIZE_X, BUTTON_SIZE_Y, BUTTON_SIZE_X, BUTTON_SIZE_Y * 2);

        minecraft.getTextureManager().bindTexture(icon);
        drawTexture(matrix, x + 6, y + 21, 0, 0, 5, 5, 5, 5);
    }

    @Override
    public void renderForeground(MatrixStack matrix, int mouseX, int mouseY) {
        super.renderForeground(matrix, mouseX, mouseY);
        drawScaledCenteredText(matrix, MekanismLang.ON.translate(), relativeX + 8, relativeY + 1, 0x101010, 0.5F);
        drawScaledCenteredText(matrix, MekanismLang.OFF.translate(), relativeX + 8, relativeY + 10, 0x101010, 0.5F);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(MekanismSounds.BEEP.get(), 1.0F));
        onToggle.run();
    }

    public enum SwitchType {
        LOWER_ICON(BUTTON_SIZE_X, 40, 0, 0);

        private final int sizeX, sizeY;
        private final int switchX, switchY;

        SwitchType(int sizeX, int sizeY, int switchX, int switchY) {
            this.sizeX = sizeX;
            this.sizeY = sizeY;
            this.switchX = switchX;
            this.switchY = switchY;
        }
    }
}
