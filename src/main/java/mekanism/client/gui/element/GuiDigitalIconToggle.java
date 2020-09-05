package mekanism.client.gui.element;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import mekanism.client.gui.IGuiWrapper;
import mekanism.common.inventory.GuiComponents.IToggleEnum;
import mekanism.common.registries.MekanismSounds;
import net.minecraft.client.audio.SimpleSound;

public class GuiDigitalIconToggle<TYPE extends Enum<TYPE> & IToggleEnum<TYPE>> extends GuiInnerScreen {

    private final Supplier<TYPE> typeSupplier;
    private final Consumer<TYPE> typeSetter;
    private final TYPE[] options;

    public GuiDigitalIconToggle(IGuiWrapper gui, int x, int y, int width, int height, Class<TYPE> enumClass, Supplier<TYPE> typeSupplier, Consumer<TYPE> typeSetter) {
        super(gui, x, y, width, height);
        this.typeSupplier = typeSupplier;
        this.typeSetter = typeSetter;
        this.options = enumClass.getEnumConstants();
        active = true;
    }

    @Override
    public void drawBackground(@Nonnull MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        super.drawBackground(matrix, mouseX, mouseY, partialTicks);
        TYPE type = typeSupplier.get();
        minecraft.textureManager.bindTexture(type.getIcon());
        blit(matrix, x + 3, y + 3, 0, 0, width - 6, height - 6, 6, 6);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        minecraft.getSoundHandler().play(SimpleSound.master(MekanismSounds.BEEP.get(), 1.0F));
        TYPE nextType = options[(typeSupplier.get().ordinal() + 1) % options.length];
        typeSetter.accept(nextType);
    }

    @Override
    public void renderToolTip(@Nonnull MatrixStack matrix, int mouseX, int mouseY) {
        displayTooltip(matrix, typeSupplier.get().getTooltip(), mouseX, mouseY);
    }
}
