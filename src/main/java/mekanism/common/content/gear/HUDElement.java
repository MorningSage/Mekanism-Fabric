package mekanism.common.content.gear;

import java.util.function.IntSupplier;
import mekanism.common.config.MekanismConfig;
import mekanism.common.lib.Color;
import mekanism.common.util.StorageUtils;
import mekanism.common.util.text.BooleanStateDisplay.OnOff;
import mekanism.common.util.text.TextUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class HUDElement {

    private final Identifier icon;
    private final Text text;
    private HUDColor color;

    private HUDElement(Identifier icon, Text text) {
        this.icon = icon;
        this.text = text;
    }

    public HUDElement color(HUDColor color) {
        this.color = color;
        return this;
    }

    public static HUDElement of(Identifier rl, Text text) {
        return new HUDElement(rl, text);
    }

    public static HUDElement enabled(Identifier rl, boolean enabled) {
        HUDElement ret = of(rl, OnOff.caps(enabled, false).getTextComponent());
        ret.color(enabled ? HUDColor.REGULAR : HUDColor.FADED);
        return ret;
    }

    public static HUDElement percent(Identifier rl, double ratio) {
        HUDElement ret = of(rl, new LiteralText(TextUtils.getPercent(ratio)));
        ret.color(ratio > 0.2 ? HUDColor.REGULAR : (ratio > 0.1 ? HUDColor.WARNING : HUDColor.DANGER));
        return ret;
    }

    public static HUDElement energyPercent(Identifier rl, ItemStack stack) {
        return percent(rl, StorageUtils.getEnergyRatio(stack));
    }

    public Identifier getIcon() {
        return icon;
    }

    public Text getText() {
        return text;
    }

    public int getColor() {
        return color.getColor();
    }

    public enum HUDColor {
        REGULAR(MekanismConfig.client.hudColor),
        FADED(() -> Color.argb(REGULAR.getColor()).darken(0.5).argb()),
        WARNING(MekanismConfig.client.hudWarningColor),
        DANGER(MekanismConfig.client.hudDangerColor);

        private final IntSupplier color;

        HUDColor(IntSupplier color) {
            this.color = color;
        }

        public int getColor() {
            return Color.rgb(color.getAsInt()).alpha(MekanismConfig.client.hudOpacity.get()).argb();
        }
    }
}
