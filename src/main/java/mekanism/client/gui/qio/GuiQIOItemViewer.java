package mekanism.client.gui.qio;

import com.google.common.collect.Sets;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.util.math.MatrixStack;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import mekanism.api.text.EnumColor;
import mekanism.client.gui.GuiMekanism;
import mekanism.client.gui.element.GuiDigitalIconToggle;
import mekanism.client.gui.element.GuiDropdown;
import mekanism.client.gui.element.GuiInnerScreen;
import mekanism.client.gui.element.custom.GuiResizeControls;
import mekanism.client.gui.element.custom.GuiResizeControls.ResizeType;
import mekanism.client.gui.element.scroll.GuiSlotScroll;
import mekanism.client.gui.element.text.BackgroundType;
import mekanism.client.gui.element.text.GuiTextField;
import mekanism.common.MekanismLang;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.qio.SearchQueryParser.QueryType;
import mekanism.common.inventory.container.QIOItemViewerContainer;
import mekanism.common.inventory.container.QIOItemViewerContainer.ListSortType;
import mekanism.common.inventory.container.QIOItemViewerContainer.SortDirection;
import mekanism.common.lib.frequency.Frequency.FrequencyIdentity;
import mekanism.common.util.text.TextUtils;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public abstract class GuiQIOItemViewer<CONTAINER extends QIOItemViewerContainer> extends GuiMekanism<CONTAINER> {

    private GuiTextField searchField;
    private final Set<Character> ALLOWED_SPECIAL_CHARS = Sets.newHashSet('_', ' ', '-', '/', '.', '\"', '\'', '|', '(', ')', ':');

    {
        // include all search prefix chars
        ALLOWED_SPECIAL_CHARS.addAll(QueryType.getPrefixChars());
    }

    protected GuiQIOItemViewer(CONTAINER container, PlayerInventory inv, Text title) {
        super(container, inv, title);
        backgroundWidth = 16 + MekanismConfig.client.qioItemViewerSlotsX.get() * 18 + 18;
        backgroundHeight = QIOItemViewerContainer.SLOTS_START_Y + MekanismConfig.client.qioItemViewerSlotsY.get() * 18 + 96;
    }

    @Override
    public void init() {
        super.init();
        int slotsY = MekanismConfig.client.qioItemViewerSlotsY.get();
        client.keyboardListener.enableRepeatEvents(true);
        addButton(new GuiInnerScreen(this, 7, 15, backgroundWidth - 16, 12, () -> {
            List<Text> list = new ArrayList<>();
            FrequencyIdentity freq = getFrequency();
            if (freq != null) {
                list.add(MekanismLang.FREQUENCY.translate(freq.getKey()));
            } else {
                list.add(MekanismLang.NO_FREQUENCY.translate());
            }
            return list;
        }).tooltip(() -> {
            List<Text> list = new ArrayList<>();
            if (getFrequency() != null) {
                list.add(MekanismLang.QIO_ITEMS_DETAIL.translateColored(EnumColor.GRAY, EnumColor.INDIGO,
                      TextUtils.format(handler.getTotalItems()), TextUtils.format(handler.getCountCapacity())));
                list.add(MekanismLang.QIO_TYPES_DETAIL.translateColored(EnumColor.GRAY, EnumColor.INDIGO,
                      TextUtils.format(handler.getTotalTypes()), TextUtils.format(handler.getTypeCapacity())));
            }
            return list;
        }));
        addButton(searchField = new GuiTextField(this, 50, 15 + 12 + 3, backgroundWidth - 50 - 10, 10));
        searchField.setOffset(0, -1);
        searchField.setInputValidator(this::isValidSearchChar);
        searchField.setResponder(handler::updateSearch);
        searchField.setMaxStringLength(50);
        searchField.setBackground(BackgroundType.ELEMENT_HOLDER);
        searchField.setVisible(true);
        searchField.setTextColor(0xFFFFFF);
        searchField.setFocused(true);
        addButton(new GuiSlotScroll(this, 7, QIOItemViewerContainer.SLOTS_START_Y, MekanismConfig.client.qioItemViewerSlotsX.get(), slotsY,
            handler::getQIOItemList, handler));
        addButton(new GuiDropdown<>(this, backgroundWidth - 9 - 54, QIOItemViewerContainer.SLOTS_START_Y + slotsY * 18 + 1,
              41, ListSortType.class, handler::getSortType, handler::setSortType));
        addButton(new GuiDigitalIconToggle<>(this, backgroundWidth - 9 - 12, QIOItemViewerContainer.SLOTS_START_Y + slotsY * 18 + 1,
              12, 12, SortDirection.class, handler::getSortDirection, handler::setSortDirection));
        addButton(new GuiResizeControls(this, (client.getWindow().getScaledHeight() / 2) - 20 - getGuiTop(), this::resize));
    }

    @Override
    protected void drawForegroundText(@Nonnull MatrixStack matrix, int mouseX, int mouseY) {
        drawString(matrix, MekanismLang.INVENTORY.translate(), 8, (getYSize() - 96) + 4, titleTextColor());
        drawString(matrix, MekanismLang.LIST_SEARCH.translate(), 7, 31, titleTextColor());
        Text text = MekanismLang.LIST_SORT.translate();
        int width = getStringWidth(text);
        drawString(matrix, text, backgroundWidth - 66 - width, (getYSize() - 96) + 4, titleTextColor());
        super.drawForegroundText(matrix, mouseX, mouseY);
    }

    @Override
    public void resize(@Nonnull MinecraftClient minecraft, int sizeX, int sizeY) {
        super.resize(minecraft, sizeX, sizeY);
        handler.updateSearch(searchField.getText());
        //Validate the height is still valid, and if it isn't recreate it
        int maxY = QIOItemViewerContainer.getSlotsYMax();
        if (MekanismConfig.client.qioItemViewerSlotsY.get() > maxY) {
            //Note: We need to update it here to ensure that it refreshes when recreating the viewer on the client when connected to a server
            MekanismConfig.client.qioItemViewerSlotsY.set(maxY);
            // save the updated config info
            MekanismConfig.client.getConfigSpec().save();
            recreateViewer();
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        client.keyboardListener.enableRepeatEvents(false);
    }

    private boolean isValidSearchChar(char c) {
        return ALLOWED_SPECIAL_CHARS.contains(c) || Character.isDigit(c) || Character.isAlphabetic(c);
    }

    public abstract FrequencyIdentity getFrequency();

    private void resize(ResizeType type) {
        int sizeX = MekanismConfig.client.qioItemViewerSlotsX.get(), sizeY = MekanismConfig.client.qioItemViewerSlotsY.get();
        boolean changed = false;
        if (type == ResizeType.EXPAND_X && sizeX < QIOItemViewerContainer.SLOTS_X_MAX) {
            MekanismConfig.client.qioItemViewerSlotsX.set(sizeX + 1);
            changed = true;
        } else if (type == ResizeType.EXPAND_Y && sizeY < QIOItemViewerContainer.getSlotsYMax()) {
            MekanismConfig.client.qioItemViewerSlotsY.set(sizeY + 1);
            changed = true;
        } else if (type == ResizeType.SHRINK_X && sizeX > QIOItemViewerContainer.SLOTS_X_MIN) {
            MekanismConfig.client.qioItemViewerSlotsX.set(sizeX - 1);
            changed = true;
        } else if (type == ResizeType.SHRINK_Y && sizeY > QIOItemViewerContainer.SLOTS_Y_MIN) {
            MekanismConfig.client.qioItemViewerSlotsY.set(sizeY - 1);
            changed = true;
        }
        if (changed) {
            // save the updated config info
            MekanismConfig.client.getConfigSpec().save();
            // And recreate the viewer
            recreateViewer();
        }
    }

    private void recreateViewer() {
        // here we subtly recreate the entire interface + container, maintaining the same window ID
        @SuppressWarnings("unchecked")
        CONTAINER c = (CONTAINER) handler.recreate();
        GuiQIOItemViewer<CONTAINER> s = recreate(c);
        client.currentScreen = null;
        client.player.currentScreenHandler = ((ScreenHandlerProvider<?>) s).getScreenHandler();
        client.displayGuiScreen(s);
        s.searchField.setText(searchField.getText());
        c.updateSearch(searchField.getText());
    }

    public abstract GuiQIOItemViewer<CONTAINER> recreate(CONTAINER container);
}
