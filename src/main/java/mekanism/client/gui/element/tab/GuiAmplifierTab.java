package mekanism.client.gui.element.tab;

import net.minecraft.client.util.math.MatrixStack;
import javax.annotation.Nonnull;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.GuiInsetElement;
import mekanism.common.Mekanism;
import mekanism.common.MekanismLang;
import mekanism.common.network.PacketGuiInteract;
import mekanism.common.network.PacketGuiInteract.GuiInteraction;
import mekanism.common.tile.laser.TileEntityLaserAmplifier;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.util.Identifier;

public class GuiAmplifierTab extends GuiInsetElement<TileEntityLaserAmplifier> {

    private static final Identifier OFF = MekanismUtils.getResource(ResourceType.GUI, "amplifier_off.png");
    private static final Identifier ENTITY = MekanismUtils.getResource(ResourceType.GUI, "amplifier_entity.png");
    private static final Identifier CONTENTS = MekanismUtils.getResource(ResourceType.GUI, "amplifier_contents.png");

    public GuiAmplifierTab(IGuiWrapper gui, TileEntityLaserAmplifier tile) {
        super(OFF, gui, tile, -26, 138, 26, 18, true);
    }

    @Override
    protected Identifier getOverlay() {
        switch (tile.outputMode) {
            case ENTITY_DETECTION:
                return ENTITY;
            case ENERGY_CONTENTS:
                return CONTENTS;
        }
        return super.getOverlay();
    }

    @Override
    public void renderToolTip(@Nonnull MatrixStack matrix, int mouseX, int mouseY) {
        displayTooltip(matrix, MekanismLang.REDSTONE_OUTPUT.translate(tile.outputMode), mouseX, mouseY);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        Mekanism.packetHandler.sendToServer(new PacketGuiInteract(GuiInteraction.NEXT_MODE, tile));
    }
}