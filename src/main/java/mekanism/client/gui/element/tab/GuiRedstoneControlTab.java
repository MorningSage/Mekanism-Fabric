package mekanism.client.gui.element.tab;

import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import javax.annotation.Nonnull;
import mekanism.client.SpecialColors;
import mekanism.client.gui.GuiUtils;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.GuiInsetElement;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.Mekanism;
import mekanism.common.network.PacketGuiInteract;
import mekanism.common.network.PacketGuiInteract.GuiInteraction;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.interfaces.IRedstoneControl;
import mekanism.common.tile.interfaces.IRedstoneControl.RedstoneControl;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.util.Identifier;

public class GuiRedstoneControlTab extends GuiInsetElement<TileEntityMekanism> {

    private static final Identifier DISABLED = MekanismUtils.getResource(ResourceType.GUI, "redstone_control_disabled.png");
    private static final Identifier HIGH = MekanismUtils.getResource(ResourceType.GUI, "redstone_control_high.png");
    private static final Identifier LOW = MekanismUtils.getResource(ResourceType.GUI, "redstone_control_low.png");
    private static final Identifier PULSE = MekanismUtils.getResource(ResourceType.GUI, "redstone_control_pulse.png");

    public GuiRedstoneControlTab(IGuiWrapper gui, TileEntityMekanism tile) {
        super(DISABLED, gui, tile, gui.getWidth(), 137, 26, 18, false);
    }

    @Override
    public void renderToolTip(@Nonnull MatrixStack matrix, int mouseX, int mouseY) {
        displayTooltip(matrix, ((IRedstoneControl) tile).getControlType().getTextComponent(), mouseX, mouseY);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        Mekanism.packetHandler.sendToServer(new PacketGuiInteract(GuiInteraction.NEXT_REDSTONE_CONTROL, tile));
    }

    @Override
    protected Identifier getOverlay() {
        switch (((IRedstoneControl) tile).getControlType()) {
            case HIGH:
                return HIGH;
            case LOW:
                return LOW;
            case PULSE:
                return PULSE;
        }
        return super.getOverlay();
    }

    @Override
    protected void colorTab() {
        MekanismRenderer.color(SpecialColors.TAB_REDSTONE_CONTROL.get());
    }

    @Override
    public void drawBackground(@Nonnull MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        super.drawBackground(matrix, mouseX, mouseY, partialTicks);
        if (((IRedstoneControl) tile).getControlType() == RedstoneControl.PULSE) {
            //Draw the button background
            drawButton(matrix, mouseX, mouseY);
            //Draw the overlay onto the button
            minecraft.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
            GuiUtils.drawSprite(matrix, getButtonX() + 1, getButtonY() + 1, innerWidth - 2, innerHeight - 2, 0, MekanismRenderer.redstonePulse);
        }
    }
}