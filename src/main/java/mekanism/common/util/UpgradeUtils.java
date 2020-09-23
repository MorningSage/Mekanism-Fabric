package mekanism.common.util;

import java.util.ArrayList;
import java.util.List;
import mekanism.api.Upgrade;
import mekanism.api.Upgrade.IUpgradeInfoHandler;
import mekanism.common.MekanismLang;
import mekanism.common.config.MekanismConfig;
import mekanism.common.registries.MekanismItems;
import mekanism.common.tile.interfaces.IUpgradeTile;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class UpgradeUtils {

    public static ItemStack getStack(Upgrade upgrade) {
        switch (upgrade) {
            case SPEED:
                return MekanismItems.SPEED_UPGRADE.getItemStack();
            case ENERGY:
                return MekanismItems.ENERGY_UPGRADE.getItemStack();
            case FILTER:
                return MekanismItems.FILTER_UPGRADE.getItemStack();
            case MUFFLING:
                return MekanismItems.MUFFLING_UPGRADE.getItemStack();
            case GAS:
                return MekanismItems.GAS_UPGRADE.getItemStack();
            case ANCHOR:
                return MekanismItems.ANCHOR_UPGRADE.getItemStack();
        }
        return ItemStack.EMPTY;
    }

    public static List<Text> getInfo(BlockEntity tile, Upgrade upgrade) {
        List<Text> ret = new ArrayList<>();
        if (tile instanceof IUpgradeTile) {
            if (tile instanceof IUpgradeInfoHandler) {
                return ((IUpgradeInfoHandler) tile).getInfo(upgrade);
            } else {
                ret = getMultScaledInfo((IUpgradeTile) tile, upgrade);
            }
        }
        return ret;
    }

    public static List<Text> getMultScaledInfo(IUpgradeTile tile, Upgrade upgrade) {
        List<Text> ret = new ArrayList<>();
        if (tile.supportsUpgrades() && upgrade.canMultiply()) {
            double effect = Math.pow(MekanismConfig.general.maxUpgradeMultiplier.get(), (float) tile.getComponent().getUpgrades(upgrade) / (float) upgrade.getMax());
            ret.add(MekanismLang.UPGRADES_EFFECT.translate(Math.round(effect * 100) / 100F));
        }
        return ret;
    }

    public static List<Text> getExpScaledInfo(IUpgradeTile tile, Upgrade upgrade) {
        List<Text> ret = new ArrayList<>();
        if (tile.supportsUpgrades() && upgrade.canMultiply()) {
            ret.add(MekanismLang.UPGRADES_EFFECT.translate(Math.pow(2, (float) tile.getComponent().getUpgrades(upgrade))));
        }
        return ret;
    }
}