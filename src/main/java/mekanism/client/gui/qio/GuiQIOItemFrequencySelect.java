package mekanism.client.gui.qio;

import java.util.List;
import java.util.UUID;
import mekanism.client.gui.element.button.MekanismImageButton;
import mekanism.common.Mekanism;
import mekanism.common.content.qio.QIOFrequency;
import mekanism.common.inventory.container.item.QIOFrequencySelectItemContainer;
import mekanism.common.lib.frequency.Frequency.FrequencyIdentity;
import mekanism.common.lib.frequency.FrequencyType;
import mekanism.common.network.PacketGuiButtonPress;
import mekanism.common.network.PacketGuiButtonPress.ClickedItemButton;
import mekanism.common.network.PacketGuiSetFrequency;
import mekanism.common.network.PacketGuiSetFrequency.FrequencyUpdate;
import mekanism.common.network.PacketQIOSetColor;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class GuiQIOItemFrequencySelect extends GuiQIOFrequencySelect<QIOFrequencySelectItemContainer> {

    public GuiQIOItemFrequencySelect(QIOFrequencySelectItemContainer container, PlayerInventory inv, Text title) {
        super(container, inv, title);
    }

    @Override
    public void init() {
        super.init();
        addButton(new MekanismImageButton(this, getGuiLeft() + 6, getGuiTop() + 6, 14, getButtonLocation("back"),
              () -> Mekanism.packetHandler.sendToServer(new PacketGuiButtonPress(ClickedItemButton.BACK_BUTTON, handler.getHand()))));
    }

    @Override
    public void sendSetFrequency(FrequencyIdentity identity) {
        Mekanism.packetHandler.sendToServer(PacketGuiSetFrequency.create(FrequencyUpdate.SET_ITEM, FrequencyType.QIO, identity, handler.getHand()));
    }

    @Override
    public void sendRemoveFrequency(FrequencyIdentity identity) {
        Mekanism.packetHandler.sendToServer(PacketGuiSetFrequency.create(FrequencyUpdate.REMOVE_ITEM, FrequencyType.QIO, identity, handler.getHand()));
    }

    @Override
    public void sendColorUpdate(int extra) {
        QIOFrequency freq = getFrequency();
        if (freq != null) {
            Mekanism.packetHandler.sendToServer(PacketQIOSetColor.create(handler.getHand(), freq, extra));
        }
    }

    @Override
    public QIOFrequency getFrequency() {
        return handler.getFrequency();
    }

    @Override
    public String getOwnerUsername() {
        return handler.getOwnerUsername();
    }

    @Override
    public UUID getOwnerUUID() {
        return handler.getOwnerUUID();
    }

    @Override
    public List<QIOFrequency> getPublicFrequencies() {
        return handler.getPublicCache();
    }

    @Override
    public List<QIOFrequency> getPrivateFrequencies() {
        return handler.getPrivateCache();
    }
}
