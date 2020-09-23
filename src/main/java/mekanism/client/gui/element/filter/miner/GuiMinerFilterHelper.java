package mekanism.client.gui.element.filter.miner;

import net.minecraft.client.util.math.MatrixStack;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.GuiElement;
import mekanism.client.gui.element.button.MekanismImageButton;
import mekanism.client.gui.element.filter.GuiFilter;
import mekanism.client.gui.element.filter.GuiFilterHelper;
import mekanism.client.gui.element.filter.GuiFilterSelect;
import mekanism.client.gui.element.slot.GuiSlot;
import mekanism.client.gui.element.slot.SlotType;
import mekanism.client.jei.interfaces.IJEIGhostTarget.IGhostBlockItemConsumer;
import mekanism.client.sound.SoundHandler;
import mekanism.common.MekanismLang;
import mekanism.common.content.miner.MinerFilter;
import mekanism.common.tile.machine.TileEntityDigitalMiner;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.common.util.StackUtils;
import mekanism.common.util.text.BooleanStateDisplay.YesNo;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;

import java.util.function.Consumer;

public interface GuiMinerFilterHelper extends GuiFilterHelper<TileEntityDigitalMiner> {

    default void addMinerDefaults(IGuiWrapper gui, MinerFilter<?> filter, int slotOffset, Consumer<GuiElement> childAdder) {
        childAdder.accept(new GuiSlot(SlotType.NORMAL, gui, getRelativeX() + 148, getRelativeY() + slotOffset).setRenderHover(true)
              .setGhostHandler((IGhostBlockItemConsumer) ingredient -> {
                  filter.replaceStack = StackUtils.size((ItemStack) ingredient, 1);
                  SoundHandler.playSound(SoundEvents.UI_BUTTON_CLICK);
              }));
        childAdder.accept(new MekanismImageButton(gui, gui.getLeft() + getRelativeX() + 148, gui.getTop() + getRelativeY() + 45, 14, 16,
              MekanismUtils.getResource(ResourceType.GUI_BUTTON, "exclamation.png"), () -> filter.requireStack = !filter.requireStack,
              (onHover, matrix, xAxis, yAxis) -> gui.displayTooltip(matrix, MekanismLang.MINER_REQUIRE_REPLACE.translate(YesNo.of(filter.requireStack)), xAxis, yAxis)));
    }

    @Override
    default GuiFilterSelect getFilterSelect(IGuiWrapper gui, TileEntityDigitalMiner tile) {
        return new GuiMinerFilerSelect(gui, tile);
    }

    default void renderReplaceStack(MatrixStack matrix, IGuiWrapper gui, MinerFilter<?> filter) {
        if (!filter.replaceStack.isEmpty()) {
            gui.getItemRenderer().zOffset += 200;
            gui.renderItem(matrix, filter.replaceStack, getRelativeX() + 149, getRelativeY() + 19);
            gui.getItemRenderer().zOffset -= 200;
        }
    }

    default boolean tryClickReplaceStack(IGuiWrapper gui, double mouseX, double mouseY, int button, int slotOffset, MinerFilter<?> filter) {
        return GuiFilter.mouseClickSlot(gui, button, mouseX, mouseY, getRelativeX() + 149, getRelativeY() + slotOffset + 1, GuiFilter.NOT_EMPTY_BLOCK, stack -> {
            filter.replaceStack = stack;
            SoundHandler.playSound(SoundEvents.UI_BUTTON_CLICK);
        });
    }
}