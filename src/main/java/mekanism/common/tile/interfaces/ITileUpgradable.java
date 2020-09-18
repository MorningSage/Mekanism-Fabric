package mekanism.common.tile.interfaces;

import java.util.List;
import java.util.Set;
import mekanism.api.Upgrade;
import mekanism.api.Upgrade.IUpgradeInfoHandler;
import mekanism.common.util.UpgradeUtils;
import net.minecraft.text.Text;

public interface ITileUpgradable extends IUpgradeTile, IUpgradeInfoHandler {

    Set<Upgrade> getSupportedUpgrade();

    @Override
    default List<Text> getInfo(Upgrade upgrade) {
        return upgrade == Upgrade.SPEED ? UpgradeUtils.getExpScaledInfo(this, upgrade) : UpgradeUtils.getMultScaledInfo(this, upgrade);
    }
}