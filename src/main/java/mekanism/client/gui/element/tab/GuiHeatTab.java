package mekanism.client.gui.element.tab;

import net.minecraft.client.util.math.MatrixStack;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nonnull;
import mekanism.client.gui.IGuiWrapper;
import mekanism.common.MekanismLang;
import mekanism.common.config.MekanismConfig;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.common.util.UnitDisplayUtils.TempType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class GuiHeatTab extends GuiBiDirectionalTab {

    private final IInfoHandler infoHandler;
    private final Map<TempType, Identifier> icons = new Object2ObjectOpenHashMap<>();

    public GuiHeatTab(IInfoHandler handler, IGuiWrapper gui) {
        super(MekanismUtils.getResource(ResourceType.GUI, "heat_info.png"), gui, -26, 109, 26, 26);
        infoHandler = handler;
    }

    @Override
    public void drawBackground(@Nonnull MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        super.drawBackground(matrix, mouseX, mouseY, partialTicks);
        minecraft.getTextureManager().bindTexture(getResource());
        drawTexture(matrix, x, y, 0, 0, width, height, width, height);
    }

    @Override
    public void renderToolTip(@Nonnull MatrixStack matrix, int mouseX, int mouseY) {
        List<Text> info = new ArrayList<>(infoHandler.getInfo());
        info.add(MekanismLang.UNIT.translate(MekanismConfig.general.tempUnit.get()));
        displayTooltips(matrix, info, mouseX, mouseY);
    }

    @Override
    protected Identifier getResource() {
        return icons.computeIfAbsent(MekanismConfig.general.tempUnit.get(), (type) -> MekanismUtils.getResource(ResourceType.GUI, "tabs/heat_info_" +
                                                                                                                                  type.name().toLowerCase(Locale.ROOT) + ".png"));
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        MekanismConfig.general.tempUnit.set(MekanismConfig.general.tempUnit.get().getNext());
    }


    @Override
    protected void onRightClick(double mouseX, double mouseY) {
        MekanismConfig.general.tempUnit.set(MekanismConfig.general.tempUnit.get().getPrevious());
    }
}