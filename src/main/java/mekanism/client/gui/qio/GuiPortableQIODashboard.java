package mekanism.client.gui.qio;

import net.minecraft.client.util.math.MatrixStack;
import javax.annotation.Nonnull;
import mekanism.client.gui.element.tab.GuiQIOFrequencyTab;
import mekanism.common.inventory.container.item.PortableQIODashboardContainer;
import mekanism.common.lib.frequency.Frequency.FrequencyIdentity;
import mekanism.common.lib.frequency.IFrequencyItem;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class GuiPortableQIODashboard extends GuiQIOItemViewer<PortableQIODashboardContainer> {

    public GuiPortableQIODashboard(PortableQIODashboardContainer container, PlayerInventory inv, Text title) {
        super(container, inv, title);
        dynamicSlots = true;
    }

    @Override
    public void init() {
        super.init();
        addButton(new GuiQIOFrequencyTab(this, handler.getHand()));
    }

    @Override
    public GuiQIOItemViewer<PortableQIODashboardContainer> recreate(PortableQIODashboardContainer container) {
        return new GuiPortableQIODashboard(container, playerInventory, title);
    }

    @Override
    protected void drawForegroundText(@Nonnull MatrixStack matrix, int mouseX, int mouseY) {
        drawTitleText(matrix, getName(), 5);
        super.drawForegroundText(matrix, mouseX, mouseY);
    }

    @Override
    public FrequencyIdentity getFrequency() {
        return ((IFrequencyItem) handler.getStack().getItem()).getFrequency(handler.getStack());
    }

    private Text getName() {
        return handler.getStack().getName();
    }
}
