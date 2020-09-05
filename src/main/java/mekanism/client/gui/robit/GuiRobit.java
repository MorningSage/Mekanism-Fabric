package mekanism.client.gui.robit;

import mekanism.client.gui.GuiMekanism;
import mekanism.client.gui.element.GuiSideHolder;
import mekanism.client.gui.element.button.MekanismImageButton;
import mekanism.common.Mekanism;
import mekanism.common.MekanismLang;
import mekanism.common.entity.EntityRobit;
import mekanism.common.inventory.container.entity.IEntityContainer;
import mekanism.common.network.PacketGuiButtonPress;
import mekanism.common.network.PacketGuiButtonPress.ClickedEntityButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;

public abstract class GuiRobit<CONTAINER extends Container & IEntityContainer<EntityRobit>> extends GuiMekanism<CONTAINER> {

    protected final EntityRobit robit;

    protected GuiRobit(CONTAINER container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
        robit = container.getEntity();
    }

    @Override
    public void init() {
        super.init();
        addButton(new GuiSideHolder(this, 176, 6, 106, false));
        addButton(new MekanismImageButton(this, getGuiLeft() + 179, getGuiTop() + 10, 18, getButtonLocation("main"),
              () -> Mekanism.packetHandler.sendToServer(new PacketGuiButtonPress(ClickedEntityButton.ROBIT_MAIN, robit.getEntityId())),
              getOnHover(MekanismLang.ROBIT)));
        addButton(new MekanismImageButton(this, getGuiLeft() + 179, getGuiTop() + 30, 18, getButtonLocation("crafting"), () -> {
            if (shouldOpenGui(RobitGuiType.CRAFTING)) {
                Mekanism.packetHandler.sendToServer(new PacketGuiButtonPress(ClickedEntityButton.ROBIT_CRAFTING, robit.getEntityId()));
            }
        }, getOnHover(MekanismLang.ROBIT_CRAFTING)));
        addButton(new MekanismImageButton(this, getGuiLeft() + 179, getGuiTop() + 50, 18, getButtonLocation("inventory"), () -> {
            if (shouldOpenGui(RobitGuiType.INVENTORY)) {
                Mekanism.packetHandler.sendToServer(new PacketGuiButtonPress(ClickedEntityButton.ROBIT_INVENTORY, robit.getEntityId()));
            }
        }, getOnHover(MekanismLang.ROBIT_INVENTORY)));
        addButton(new MekanismImageButton(this, getGuiLeft() + 179, getGuiTop() + 70, 18, getButtonLocation("smelting"), () -> {
            if (shouldOpenGui(RobitGuiType.SMELTING)) {
                Mekanism.packetHandler.sendToServer(new PacketGuiButtonPress(ClickedEntityButton.ROBIT_SMELTING, robit.getEntityId()));
            }
        }, getOnHover(MekanismLang.ROBIT_SMELTING)));
        addButton(new MekanismImageButton(this, getGuiLeft() + 179, getGuiTop() + 90, 18, getButtonLocation("repair"), () -> {
            if (shouldOpenGui(RobitGuiType.REPAIR)) {
                Mekanism.packetHandler.sendToServer(new PacketGuiButtonPress(ClickedEntityButton.ROBIT_REPAIR, robit.getEntityId()));
            }
        }, getOnHover(MekanismLang.ROBIT_REPAIR)));
    }

    protected abstract boolean shouldOpenGui(RobitGuiType guiType);

    public enum RobitGuiType {
        CRAFTING,
        INVENTORY,
        SMELTING,
        REPAIR
    }
}