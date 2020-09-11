package mekanism.client.gui;

import net.minecraft.client.util.math.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.text.ILangEntry;
import mekanism.client.gui.element.GuiElement;
import mekanism.client.gui.element.GuiElement.IHoverable;
import mekanism.client.gui.element.GuiWindow;
import mekanism.client.gui.element.slot.GuiSlot;
import mekanism.client.gui.element.slot.SlotType;
import mekanism.client.render.IFancyFontRenderer;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.Mekanism;
import mekanism.common.inventory.container.slot.ContainerSlotType;
import mekanism.common.inventory.container.slot.InventoryContainerSlot;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.lib.LRU;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.interfaces.ISideConfiguration;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import org.apache.commons.lang3.tuple.Pair;

//TODO: Add our own "addButton" type thing for elements that are just "drawn" but don't actually have any logic behind them
public abstract class GuiMekanism<CONTAINER extends Container> extends ContainerScreen<CONTAINER> implements IGuiWrapper, IFancyFontRenderer {

    private static final NumberFormat intFormatter = NumberFormat.getIntegerInstance();
    public static final ResourceLocation BASE_BACKGROUND = MekanismUtils.getResource(ResourceType.GUI, "base.png");
    public static final ResourceLocation SHADOW = MekanismUtils.getResource(ResourceType.GUI, "shadow.png");
    public static final ResourceLocation BLUR = MekanismUtils.getResource(ResourceType.GUI, "blur.png");
    //TODO: Look into defaulting this to true
    protected boolean dynamicSlots;
    protected final LRU<GuiWindow> windows = new LRU<>();
    protected final List<GuiElement> focusListeners = new ArrayList<>();

    private boolean hasClicked = false;

    public static int maxZOffset;

    protected GuiMekanism(CONTAINER container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
    }

    @Override
    public void init() {
        super.init();
        initPreSlots();
        if (dynamicSlots) {
            addSlots();
        }
    }

    @Override
    public void tick() {
        super.tick();
        children.stream().filter(child -> child instanceof GuiElement).map(child -> (GuiElement) child).forEach(GuiElement::tick);
    }

    protected void initPreSlots() {
    }

    protected IHoverable getOnHover(ILangEntry translationHelper) {
        return getOnHover((Supplier<ITextComponent>) translationHelper::translate);
    }

    protected IHoverable getOnHover(Supplier<ITextComponent> componentSupplier) {
        return (onHover, matrix, xAxis, yAxis) -> displayTooltip(matrix, componentSupplier.get(), xAxis, yAxis);
    }

    protected ResourceLocation getButtonLocation(String name) {
        return MekanismUtils.getResource(ResourceType.GUI_BUTTON, name + ".png");
    }

    @Override
    public void addFocusListener(GuiElement element) {
        focusListeners.add(element);
    }

    @Override
    public void removeFocusListener(GuiElement element) {
        focusListeners.remove(element);
    }

    @Override
    public void focusChange(GuiElement changed) {
        focusListeners.stream().filter(e -> e != changed).forEach(e -> e.setFocused(false));
    }

    @Override
    public void incrementFocus(GuiElement current) {
        int index = focusListeners.indexOf(current);
        if (index != -1) {
            GuiElement next = focusListeners.get((index + 1) % focusListeners.size());
            next.setFocused(true);
            focusChange(next);
        }
    }

    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeftIn, int guiTopIn, int mouseButton) {
        return getWindowHovering(mouseX, mouseY) == null && super.hasClickedOutside(mouseX, mouseY, guiLeftIn, guiTopIn, mouseButton);
    }

    @Override
    public void resize(@Nonnull Minecraft minecraft, int sizeX, int sizeY) {
        List<Pair<Integer, GuiElement>> prevElements = new ArrayList<>();
        for (int i = 0; i < buttons.size(); i++) {
            Widget widget = buttons.get(i);
            if (widget instanceof GuiElement && ((GuiElement) widget).hasPersistentData()) {
                prevElements.add(Pair.of(i, (GuiElement) widget));
            }
        }
        // flush the focus listeners list unless it's an overlay
        focusListeners.removeIf(element -> !element.isOverlay);
        int prevLeft = getGuiLeft(), prevTop = getGuiTop();
        super.resize(minecraft, sizeX, sizeY);

        windows.forEach(window -> {
            window.resize(prevLeft, prevTop, getGuiLeft(), getGuiTop());
            children.add(window);
        });

        prevElements.forEach(e -> {
            if (e.getLeft() < buttons.size()) {
                Widget widget = buttons.get(e.getLeft());
                // we're forced to assume that the children list is the same before and after the resize.
                // for verification, we run a lightweight class equality check
                // Note: We do not perform an instance check on widget to ensure it is a GuiElement, as that is
                // ensured by the class comparison, and the restrictions of what can go in prevElements
                if (widget.getClass() == e.getRight().getClass()) {
                    ((GuiElement) widget).syncFrom(e.getRight());
                }
            }
        });
    }

    @Override
    protected void drawGuiContainerForegroundLayer(@Nonnull MatrixStack matrix, int mouseX, int mouseY) {
        matrix.translate(0, 0, 300);
        RenderSystem.translatef(-guiLeft, -guiTop, 0);
        children().stream().filter(c -> c instanceof GuiElement).forEach(c -> ((GuiElement) c).onDrawBackground(matrix, mouseX, mouseY, MekanismRenderer.getPartialTick()));
        RenderSystem.translatef(guiLeft, guiTop, 0);
        drawForegroundText(matrix, mouseX, mouseY);
        int xAxis = mouseX - getGuiLeft();
        int yAxis = mouseY - getGuiTop();
        // first render general foregrounds
        maxZOffset = 200;
        int zOffset = 200;
        for (Widget widget : this.buttons) {
            if (widget instanceof GuiElement) {
                matrix.push();
                ((GuiElement) widget).onRenderForeground(matrix, mouseX, mouseY, zOffset, zOffset);
                matrix.pop();
            }
        }

        // now render overlays in reverse-order (i.e. back to front)
        zOffset = maxZOffset;
        for (LRU<GuiWindow>.LRUIterator iter = getWindowsDescendingIterator(); iter.hasNext(); ) {
            GuiWindow overlay = iter.next();
            zOffset += 150;
            matrix.push();
            overlay.onRenderForeground(matrix, mouseX, mouseY, zOffset, zOffset);
            if (iter.hasNext()) {
                // if this isn't the focused window, render a 'blur' effect over it
                overlay.renderBlur(matrix);
            }
            matrix.pop();
        }
        // then render tooltips, translating above max z offset to prevent clashing
        GuiElement tooltipElement = getWindowHovering(mouseX, mouseY);
        if (tooltipElement == null) {
            for (int i = buttons.size() - 1; i >= 0; i--) {
                Widget widget = buttons.get(i);
                if (widget instanceof GuiElement && widget.isMouseOver(mouseX, mouseY)) {
                    tooltipElement = (GuiElement) widget;
                    break;
                }
            }
        }

        // translate forwards using RenderSystem. this should never have to happen as we do all the necessary translations with MatrixStacks,
        // but Minecraft has decided to not fully adopt MatrixStacks for many crucial ContainerScreen render operations. should be re-evaluated
        // when mc updates related logic on their end (IMPORTANT)
        RenderSystem.translatef(0, 0, maxZOffset);

        if (tooltipElement != null) {
            tooltipElement.renderToolTip(matrix, xAxis, yAxis);
        }

        // render item tooltips
        RenderSystem.translatef(-guiLeft, -guiTop, 0);
        func_230459_a_(matrix, mouseX, mouseY);
        RenderSystem.translatef(guiLeft, guiTop, 0);

        // IMPORTANT: additional hacky translation so held items render okay. re-evaluate as discussed above
        RenderSystem.translatef(0, 0, 200);
    }

    protected void drawForegroundText(@Nonnull MatrixStack matrix, int mouseX, int mouseY) {
    }

    @Nonnull
    @Override
    public Optional<IGuiEventListener> getEventListenerForPos(double mouseX, double mouseY) {
        GuiWindow window = getWindowHovering(mouseX, mouseY);
        return window != null ? Optional.of(window) : super.getEventListenerForPos(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        hasClicked = true;
        // first try to send the mouse event to our overlays
        GuiWindow top = windows.size() > 0 ? windows.iterator().next() : null;
        GuiWindow focused = windows.stream().filter(overlay -> overlay.mouseClicked(mouseX, mouseY, button)).findFirst().orElse(null);
        if (focused != null) {
            setListener(focused);
            if (button == 0) {
                setDragging(true);
            }
            // this check prevents us from moving the window to the top of the stack if the clicked window opened up an additional window
            if (top != focused) {
                windows.moveUp(focused);
            }
            return true;
        }
        // otherwise we send it to the current element
        for (int i = buttons.size() - 1; i >= 0; i--) {
            IGuiEventListener listener = buttons.get(i);
            if (listener.mouseClicked(mouseX, mouseY, button)) {
                setListener(listener);
                if (button == 0) {
                    setDragging(true);
                }
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (hasClicked) {
            // always pass mouse released events to windows for drag checks
            windows.forEach(w -> w.onRelease(mouseX, mouseY));
            return super.mouseReleased(mouseX, mouseY, button);
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return windows.stream().anyMatch(window -> window.keyPressed(keyCode, scanCode, modifiers)) ||
               GuiUtils.checkChildren(buttons, (child) -> child.keyPressed(keyCode, scanCode, modifiers)) ||
               super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char c, int keyCode) {
        return windows.stream().anyMatch(window -> window.charTyped(c, keyCode)) ||
               GuiUtils.checkChildren(buttons, (child) -> child.charTyped(c, keyCode)) ||
               super.charTyped(c, keyCode);
    }

    /**
     * @apiNote mouseXOld and mouseYOld are just guessed mappings I couldn't find any usage from a quick glance.
     */
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double mouseXOld, double mouseYOld) {
        super.mouseDragged(mouseX, mouseY, button, mouseXOld, mouseYOld);
        return getListener() != null && isDragging() && button == 0 && getListener().mouseDragged(mouseX, mouseY, button, mouseXOld, mouseYOld);
    }

    protected boolean isMouseOverSlot(Slot slot, double mouseX, double mouseY) {
        return isPointInRegion(slot.xPos, slot.yPos, 16, 16, mouseX, mouseY);
    }

    @Override
    protected boolean isPointInRegion(int x, int y, int width, int height, double mouseX, double mouseY) {
        // overridden to prevent slot interactions when a GuiElement is blocking
        return super.isPointInRegion(x, y, width, height, mouseX, mouseY) &&
               getWindowHovering(mouseX, mouseY) == null &&
               buttons.stream().noneMatch(button -> button.isMouseOver(mouseX, mouseY));
    }

    protected void addSlots() {
        int size = container.inventorySlots.size();
        for (int i = 0; i < size; i++) {
            Slot slot = container.inventorySlots.get(i);
            if (slot instanceof InventoryContainerSlot) {
                InventoryContainerSlot containerSlot = (InventoryContainerSlot) slot;
                ContainerSlotType slotType = containerSlot.getSlotType();
                DataType dataType = findDataType(containerSlot);
                //Shift the slots by one as the elements include the border of the slot
                SlotType type;
                if (dataType != null) {
                    type = SlotType.get(dataType);
                } else if (slotType == ContainerSlotType.INPUT || slotType == ContainerSlotType.OUTPUT || slotType == ContainerSlotType.EXTRA) {
                    type = SlotType.NORMAL;
                } else if (slotType == ContainerSlotType.POWER) {
                    type = SlotType.POWER;
                } else if (slotType == ContainerSlotType.NORMAL || slotType == ContainerSlotType.VALIDITY) {
                    type = SlotType.NORMAL;
                } else {//slotType == ContainerSlotType.IGNORED: don't do anything
                    continue;
                }
                GuiSlot guiSlot = new GuiSlot(type, this, slot.xPos - 1, slot.yPos - 1);
                SlotOverlay slotOverlay = containerSlot.getSlotOverlay();
                if (slotOverlay != null) {
                    guiSlot.with(slotOverlay);
                }
                if (slotType == ContainerSlotType.VALIDITY) {
                    int index = i;
                    guiSlot.validity(() -> checkValidity(index));
                }
                addButton(guiSlot);
            } else {
                addButton(new GuiSlot(SlotType.NORMAL, this, slot.xPos - 1, slot.yPos - 1));
            }
        }
    }

    @Nullable
    protected DataType findDataType(InventoryContainerSlot slot) {
        if (container instanceof MekanismTileContainer) {
            TileEntityMekanism tileEntity = ((MekanismTileContainer<?>) container).getTileEntity();
            if (tileEntity instanceof ISideConfiguration) {
                return ((ISideConfiguration) tileEntity).getActiveDataType(slot.getInventorySlot());
            }
        }
        return null;
    }

    protected ItemStack checkValidity(int slotIndex) {
        return ItemStack.EMPTY;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(@Nonnull MatrixStack matrix, float partialTick, int mouseX, int mouseY) {
        //Ensure the GL color is white as mods adding an overlay (such as JEI for bookmarks), might have left
        // it in an unexpected state.
        MekanismRenderer.resetColor();
        if (width < 8 || height < 8) {
            Mekanism.logger.warn("Gui: {}, was too small to draw the background of. Unable to draw a background for a gui smaller than 8 by 8.", getClass().getSimpleName());
            return;
        }
        GuiUtils.renderBackgroundTexture(matrix, BASE_BACKGROUND, 4, 4, getGuiLeft(), getGuiTop(), getXSize(), getYSize(), 256, 256);
    }

    @Override
    public FontRenderer getFont() {
        return font;
    }

    @Override
    public void render(@Nonnull MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        // shift back a whole lot so we can stack more windows
        RenderSystem.translated(0, 0, -500);
        matrix.push();
        renderBackground(matrix);
        //Apply our matrix stack to the render system and pass an unmodified one to the super method
        // Vanilla still renders the items into the GUI using render system transformations so this
        // is required to not have tooltips of GuiElements rendering behind the items
        super.render(matrix, mouseX, mouseY, partialTicks);
        matrix.pop();
        RenderSystem.translated(0, 0, 500);
    }

    @Override
    public void renderItemTooltip(MatrixStack matrix, @Nonnull ItemStack stack, int xAxis, int yAxis) {
        renderTooltip(matrix, stack, xAxis, yAxis);
    }

    @Override
    public ItemRenderer getItemRenderer() {
        return itemRenderer;
    }

    protected static String formatInt(long l) {
        return intFormatter.format(l);
    }

    @Override
    public void addWindow(GuiWindow window) {
        windows.add(window);
    }

    @Override
    public void removeWindow(GuiWindow window) {
        windows.remove(window);
    }

    @Nullable
    @Override
    public GuiWindow getWindowHovering(double mouseX, double mouseY) {
        return windows.stream().filter(w -> w.isMouseOver(mouseX, mouseY)).findFirst().orElse(null);
    }

    public Collection<GuiWindow> getWindows() {
        return windows;
    }

    public List<IGuiEventListener> children() {
        return children;
    }

    public LRU<GuiWindow>.LRUIterator getWindowsDescendingIterator() {
        return windows.descendingIterator();
    }

    //Some blit param namings
    //blit(matrix, int x, int y, int textureX, int textureY, int width, int height);
    //blit(matrix, int x, int y, TextureAtlasSprite icon, int width, int height);
    //blit(matrix, int x, int y, int textureX, int textureY, int width, int height, int textureWidth, int textureHeight);
    //blit(matrix, int x, int y, int zLevel, float textureX, float textureY, int width, int height, int textureWidth, int textureHeight);
    //blit(matrix, int x, int y, int desiredWidth, int desiredHeight, int textureX, int textureY, int width, int height, int textureWidth, int textureHeight);
    //innerblit(matrix, int x, int endX, int y, int endY, int zLevel, int width, int height, float textureX, float textureY, int textureWidth, int textureHeight);
    //    * calls innerblit(matrix, x, endX, y, endY, zLevel, (textureX + 0.0F) / textureWidth, (textureX + width) / textureWidth, (textureY + 0.0F) / textureHeight, (textureY + height) / textureHeight);
    //innerblit(matrix, int x, int endX, int y, int endY, int zLevel, float uMin, float uMax, float vMin, float vMax);
}