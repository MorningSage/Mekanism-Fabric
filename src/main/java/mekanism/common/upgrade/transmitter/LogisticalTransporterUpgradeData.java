package mekanism.common.upgrade.transmitter;

import mekanism.api.text.EnumColor;
import mekanism.common.lib.transmitter.ConnectionType;
import net.minecraft.nbt.ListNBT;

import javax.annotation.Nullable;

public class LogisticalTransporterUpgradeData extends TransmitterUpgradeData {

    @Nullable
    public final EnumColor color;
    public final ListNBT stacks;

    //Note: Currently redstone reactive is always false here
    public LogisticalTransporterUpgradeData(boolean redstoneReactive, ConnectionType[] connectionTypes, @Nullable EnumColor color, ListNBT stacks) {
        super(redstoneReactive, connectionTypes);
        this.color = color;
        this.stacks = stacks;
    }
}