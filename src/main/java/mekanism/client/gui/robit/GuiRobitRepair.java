package mekanism.client.gui.robit;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nonnull;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.MekanismLang;
import mekanism.common.inventory.container.entity.robit.RepairRobitContainer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

public class GuiRobitRepair extends GuiRobit<RepairRobitContainer> implements ScreenHandlerListener {

    //Use the vanilla anvil's gui texture
    private static final Identifier ANVIL_RESOURCE = new Identifier("textures/gui/container/anvil.png");
    private TextFieldWidget itemNameField;

    public GuiRobitRepair(RepairRobitContainer container, PlayerInventory inv, Text title) {
        super(container, inv, title);
    }

    @Override
    public void init() {
        super.init();
        client.keyboardListener.enableRepeatEvents(true);
        addButton(itemNameField = new TextFieldWidget(textRenderer, getGuiLeft() + 62, getGuiTop() + 24, 103, 12, new StringTextComponent("")));
        itemNameField.setFocusUnlocked(false);
        itemNameField.changeFocus(true);
        itemNameField.setEditableColor(-1);
        itemNameField.setUneditableColor(-1);
        itemNameField.setEnableBackgroundDrawing(false);
        itemNameField.setMaxLength(35);
        itemNameField.setChangedListener(this::onTextUpdate);
        handler.removeListener(this);
        handler.addListener(this);
    }

    @Override
    public void resize(@Nonnull MinecraftClient minecraft, int scaledWidth, int scaledHeight) {
        String s = itemNameField.getText();
        super.resize(minecraft, scaledWidth, scaledHeight);
        itemNameField.setText(s);
    }

    private void onTextUpdate(String newText) {
        if (!newText.isEmpty()) {
            Slot slot = handler.getSlot(0);
            if (slot.hasStack() && !slot.getStack().hasCustomName() && newText.equals(slot.getStack().getName().getString())) {
                newText = "";
            }
            handler.setNewItemName(newText);
            client.player.connection.sendPacket(new CRenameItemPacket(newText));
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        client.keyboardListener.enableRepeatEvents(false);
        handler.removeListener(this);
    }

    @Override
    protected void drawForegroundText(@Nonnull MatrixStack matrix, int mouseX, int mouseY) {
        RenderSystem.disableBlend();
        drawString(matrix, MekanismLang.ROBIT_REPAIR.translate(), 60, 6, titleTextColor());
        int maximumCost = handler.getLevelCost();
        if (maximumCost > 0) {
            int k = 0x80FF20;
            boolean flag = true;
            Text component = MekanismLang.REPAIR_COST.translate(maximumCost);
            if (maximumCost >= 40 && !client.player.isCreative()) {
                component = MekanismLang.REPAIR_EXPENSIVE.translate();
                k = 0xFF6060;
            } else {
                Slot slot = handler.getSlot(2);
                if (!slot.hasStack()) {
                    flag = false;
                } else if (!slot.canTakeItems(playerInventory.player)) {
                    k = 0xFF6060;
                }
            }

            if (flag) {
                int width = getXSize() - 8 - getStringWidth(component) - 2;
                fill(matrix, width - 2, 67, getXSize() - 8, 79, 0x4F000000);
                getFont().func_238407_a_(matrix, component, width, 69.0F, k);
                MekanismRenderer.resetColor();
            }
        }
        super.drawForegroundText(matrix, mouseX, mouseY);
    }

    @Override
    public boolean charTyped(char c, int keyCode) {
        if (itemNameField.canWrite()) {
            return itemNameField.charTyped(c, keyCode);
        }
        return super.charTyped(c, keyCode);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode != GLFW.GLFW_KEY_ESCAPE && itemNameField.canWrite()) {
            return itemNameField.keyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected boolean shouldOpenGui(RobitGuiType guiType) {
        return guiType != RobitGuiType.REPAIR;
    }

    @Override
    protected void drawBackground(@NotNull MatrixStack matrices, float delta, int mouseX, int mouseY) {
        client.getTextureManager().bindTexture(ANVIL_RESOURCE);
        drawTexture(matrices, getGuiLeft(), getGuiTop(), 0, 0, getXSize(), getYSize());
        drawTexture(matrices, getGuiLeft() + 59, getGuiTop() + 20, 0, getYSize() + (handler.getSlot(0).hasStack() ? 0 : 16), 110, 16);
        if ((handler.getSlot(0).hasStack() || handler.getSlot(1).hasStack()) && !handler.getSlot(2).hasStack()) {
            drawTexture(matrices, getGuiLeft() + 99, getGuiTop() + 45, getXSize(), 0, 28, 21);
        }
    }

    @Override
    public void onHandlerRegistered(@Nonnull ScreenHandler container, @Nonnull DefaultedList<ItemStack> list) {
        onSlotUpdate(container, 0, container.getSlot(0).getStack());
    }

    @Override
    public void onSlotUpdate(@Nonnull ScreenHandler container, int slotID, @Nonnull ItemStack stack) {
        if (slotID == 0) {
            itemNameField.setText(stack.isEmpty() ? "" : stack.getName().getString());
            itemNameField.setEditable(!stack.isEmpty());
        }
    }

    @Override
    public void onPropertyUpdate(@Nonnull ScreenHandler handler, int varToUpdate, int newValue) {

    }
}