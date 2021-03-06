package mekanism.client.gui.element;

import static mekanism.client.gui.element.GuiDigitalSwitch.BUTTON_SIZE_X;
import static mekanism.client.gui.element.GuiDigitalSwitch.BUTTON_SIZE_Y;
import static mekanism.client.gui.element.GuiDigitalSwitch.SWITCH;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import java.util.Collections;
import java.util.function.BooleanSupplier;
import javax.annotation.Nonnull;
import mekanism.client.gui.IGuiWrapper;
import mekanism.common.MekanismLang;
import mekanism.common.registries.MekanismSounds;
import net.minecraft.text.Text;

public class GuiScreenSwitch extends GuiInnerScreen {

    private final BooleanSupplier stateSupplier;
    private final Runnable onToggle;

    public GuiScreenSwitch(IGuiWrapper gui, int x, int y, int width, Text buttonName, BooleanSupplier stateSupplier, Runnable onToggle) {
        super(gui, x, y, width, BUTTON_SIZE_Y * 2 + 5, () -> Collections.singletonList(buttonName));
        this.stateSupplier = stateSupplier;
        this.onToggle = onToggle;
        active = true;
    }

    @Override
    public void drawBackground(@Nonnull MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        super.drawBackground(matrix, mouseX, mouseY, partialTicks);
        minecraft.getTextureManager().bindTexture(SWITCH);
        drawTexture(matrix, x + width - 2 - BUTTON_SIZE_X, y + 2, 0, stateSupplier.getAsBoolean() ? 0 : BUTTON_SIZE_Y, BUTTON_SIZE_X, BUTTON_SIZE_Y, BUTTON_SIZE_X, BUTTON_SIZE_Y * 2);
        drawTexture(matrix, x + width - 2 - BUTTON_SIZE_X, y + 2 + BUTTON_SIZE_Y + 1, 0, stateSupplier.getAsBoolean() ? BUTTON_SIZE_Y : 0, BUTTON_SIZE_X, BUTTON_SIZE_Y, BUTTON_SIZE_X, BUTTON_SIZE_Y * 2);
    }

    @Override
    public void renderForeground(MatrixStack matrix, int mouseX, int mouseY) {
        super.renderForeground(matrix, mouseX, mouseY);
        drawScaledCenteredText(matrix, MekanismLang.ON.translate(), relativeX + width - 9, relativeY + 2, 0x101010, 0.5F);
        drawScaledCenteredText(matrix, MekanismLang.OFF.translate(), relativeX + width - 9, relativeY + 11, 0x101010, 0.5F);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(MekanismSounds.BEEP.get(), 1.0F));
        onToggle.run();
    }
}
