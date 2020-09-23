package mekanism.client.gui.qio;

import net.minecraft.client.util.math.MatrixStack;
import mekanism.api.text.EnumColor;
import mekanism.client.gui.GuiMekanismTile;
import mekanism.client.gui.element.GuiInnerScreen;
import mekanism.client.gui.element.slot.GuiSlot;
import mekanism.client.gui.element.slot.SlotType;
import mekanism.client.gui.element.tab.GuiQIOFrequencyTab;
import mekanism.client.gui.element.tab.GuiSecurityTab;
import mekanism.client.gui.element.text.GuiTextField;
import mekanism.client.gui.element.text.InputValidator;
import mekanism.client.sound.SoundHandler;
import mekanism.common.Mekanism;
import mekanism.common.MekanismLang;
import mekanism.common.content.qio.QIOFrequency;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.network.PacketGuiInteract;
import mekanism.common.network.PacketGuiInteract.GuiInteraction;
import mekanism.common.network.PacketGuiInteract.GuiInteractionItem;
import mekanism.common.registries.MekanismSounds;
import mekanism.common.tile.qio.TileEntityQIORedstoneAdapter;
import mekanism.common.util.StackUtils;
import mekanism.common.util.text.TextUtils;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class GuiQIORedstoneAdapter extends GuiMekanismTile<TileEntityQIORedstoneAdapter, MekanismTileContainer<TileEntityQIORedstoneAdapter>> {

    private GuiTextField text;

    public GuiQIORedstoneAdapter(MekanismTileContainer<TileEntityQIORedstoneAdapter> container, PlayerInventory inv, Text title) {
        super(container, inv, title);
        dynamicSlots = true;
        backgroundHeight += 16;
    }

    @Override
    public void init() {
        super.init();
        addButton(new GuiQIOFrequencyTab(this, tile));
        addButton(new GuiSecurityTab<>(this, tile));
        addButton(new GuiSlot(SlotType.NORMAL, this, 7, 30).setRenderHover(true));
        addButton(new GuiInnerScreen(this, 7, 16, backgroundWidth - 15, 12, () -> {
            List<Text> list = new ArrayList<>();
            QIOFrequency freq = tile.getQIOFrequency();
            if (freq != null) {
                list.add(MekanismLang.FREQUENCY.translate(freq.getKey()));
            } else {
                list.add(MekanismLang.NO_FREQUENCY.translate());
            }
            return list;
        }).tooltip(() -> {
            List<Text> list = new ArrayList<>();
            QIOFrequency freq = tile.getQIOFrequency();
            if (freq != null) {
                list.add(MekanismLang.QIO_ITEMS_DETAIL.translateColored(EnumColor.GRAY, EnumColor.INDIGO,
                      TextUtils.format(freq.getTotalItemCount()), TextUtils.format(freq.getTotalItemCountCapacity())));
                list.add(MekanismLang.QIO_TYPES_DETAIL.translateColored(EnumColor.GRAY, EnumColor.INDIGO,
                      TextUtils.format(freq.getTotalItemTypes(true)), TextUtils.format(freq.getTotalItemTypeCapacity())));
            }
            return list;
        }));
        addButton(new GuiInnerScreen(this, 27, 30, backgroundWidth - 27 - 8, 54, () -> {
            List<Text> list = new ArrayList<>();
            list.add(!tile.getItemType().isEmpty() ? tile.getItemType().getItem().getName() : MekanismLang.QIO_ITEM_TYPE_UNDEFINED.translate());
            list.add(MekanismLang.QIO_TRIGGER_COUNT.translate(TextUtils.format(tile.getCount())));
            if (!tile.getItemType().isEmpty() && tile.getQIOFrequency() != null) {
                list.add(MekanismLang.QIO_STORED_COUNT.translate(TextUtils.format(tile.getStoredCount())));
            }
            return list;
        }).clearFormat());
        addButton(text = new GuiTextField(this, 29, 70, backgroundWidth - 39, 12));
        text.setMaxStringLength(10);
        text.setInputValidator(InputValidator.DIGIT);
        text.setFocused(true);
        text.configureDigitalInput(this::setCount);
    }

    private void setCount() {
        if (!text.getText().isEmpty()) {
            long count = Long.parseLong(text.getText());
            Mekanism.packetHandler.sendToServer(new PacketGuiInteract(GuiInteraction.QIO_REDSTONE_ADAPTER_COUNT, tile, (int) Math.min(count, Integer.MAX_VALUE)));
            text.setText("");
        }
    }

    @Override
    protected void drawForegroundText(@Nonnull MatrixStack matrix, int mouseX, int mouseY) {
        renderTitleText(matrix);
        drawString(matrix, MekanismLang.INVENTORY.translate(), 8, (getYSize() - 96) + 2, titleTextColor());
        if (tile.getItemType() != null) {
            renderItem(matrix, tile.getItemType(), 8, 31);
        }
        super.drawForegroundText(matrix, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        if (button == 0) {
            double xAxis = mouseX - getGuiLeft();
            double yAxis = mouseY - getGuiTop();
            if (xAxis >= 8 && xAxis < 24 && yAxis >= 31 && yAxis < 47) {
                ItemStack stack = client.player.inventory.getCursorStack();
                if (!stack.isEmpty() && !hasShiftDown()) {
                    Mekanism.packetHandler.sendToServer(new PacketGuiInteract(GuiInteractionItem.QIO_REDSTONE_ADAPTER_STACK, tile, StackUtils.size(stack, 1)));
                } else if (stack.isEmpty() && hasShiftDown()) {
                    Mekanism.packetHandler.sendToServer(new PacketGuiInteract(GuiInteractionItem.QIO_REDSTONE_ADAPTER_STACK, tile, ItemStack.EMPTY));
                }
                SoundHandler.playSound(MekanismSounds.BEEP.get());
            }
        }
        return true;
    }
}