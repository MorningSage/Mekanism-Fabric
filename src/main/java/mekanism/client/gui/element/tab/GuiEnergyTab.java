package mekanism.client.gui.element.tab;

import net.minecraft.client.util.math.MatrixStack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nonnull;
import mekanism.client.gui.IGuiWrapper;
import mekanism.common.MekanismLang;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.config.MekanismConfig;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.common.util.UnitDisplayUtils.EnergyType;
import mekanism.common.util.text.EnergyDisplay;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class GuiEnergyTab extends GuiBiDirectionalTab {

    private final IInfoHandler infoHandler;
    private final Map<EnergyType, Identifier> icons = new EnumMap<>(EnergyType.class);

    public GuiEnergyTab(IInfoHandler handler, IGuiWrapper gui) {
        super(MekanismUtils.getResource(ResourceType.GUI, "energy_info.png"), gui, -26, 137, 26, 26);
        infoHandler = handler;
    }

    public GuiEnergyTab(MachineEnergyContainer<?> energyContainer, IGuiWrapper gui) {
        this(() -> Arrays.asList(MekanismLang.USING.translate(EnergyDisplay.of(energyContainer.getEnergyPerTick())),
              MekanismLang.NEEDED.translate(EnergyDisplay.of(energyContainer.getNeeded()))), gui);
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
        info.add(MekanismLang.UNIT.translate(MekanismConfig.general.energyUnit.get()));
        displayTooltips(matrix, info, mouseX, mouseY);
    }

    @Override
    protected Identifier getResource() {
        return icons.computeIfAbsent(MekanismConfig.general.energyUnit.get(), (type) -> MekanismUtils.getResource(ResourceType.GUI, "tabs/energy_info_" +
                                                                                                                                    type.name().toLowerCase(Locale.ROOT) + ".png"));
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        MekanismConfig.general.energyUnit.set(MekanismConfig.general.energyUnit.get().getNext());
    }

    @Override
    protected void onRightClick(double mouseX, double mouseY) {
        MekanismConfig.general.energyUnit.set(MekanismConfig.general.energyUnit.get().getPrevious());
    }
}