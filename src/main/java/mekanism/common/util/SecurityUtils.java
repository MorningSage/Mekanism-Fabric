package mekanism.common.util;

import java.util.UUID;
import mekanism.api.text.EnumColor;
import mekanism.client.MekanismClient;
import mekanism.common.MekanismLang;
import mekanism.common.config.MekanismConfig;
import mekanism.common.lib.frequency.FrequencyType;
import mekanism.common.lib.security.IOwnerItem;
import mekanism.common.lib.security.ISecurityItem;
import mekanism.common.lib.security.ISecurityTile;
import mekanism.common.lib.security.ISecurityTile.SecurityMode;
import mekanism.common.lib.security.SecurityData;
import mekanism.common.lib.security.SecurityFrequency;
import net.fabricmc.api.EnvType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;

public final class SecurityUtils {

    public static boolean canAccess(PlayerEntity player, ItemStack stack) {
        // If protection is disabled, access is always granted
        if (!MekanismConfig.general.allowProtection.get()) {
            return true;
        }
        if (!(stack.getItem() instanceof ISecurityItem) && stack.getItem() instanceof IOwnerItem) {
            UUID owner = ((IOwnerItem) stack.getItem()).getOwnerUUID(stack);
            return owner == null || owner.equals(player.getUuid());
        }
        if (stack.isEmpty() || !(stack.getItem() instanceof ISecurityItem)) {
            return true;
        }
        ISecurityItem security = (ISecurityItem) stack.getItem();
        if (MekanismUtils.isOp(player)) {
            return true;
        }
        return canAccess(security.getSecurity(stack), player, security.getOwnerUUID(stack));
    }

    public static boolean canAccess(PlayerEntity player, BlockEntity tile) {
        if (!(tile instanceof ISecurityTile) || !((ISecurityTile) tile).hasSecurity()) {
            //If this tile does not have security allow access
            return true;
        }
        ISecurityTile security = (ISecurityTile) tile;
        if (MekanismUtils.isOp(player)) {
            return true;
        }
        return canAccess(security.getSecurity().getMode(), player, security.getSecurity().getOwnerUUID());
    }

    private static boolean canAccess(SecurityMode mode, PlayerEntity player, UUID owner) {
        // If protection is disabled, access is always granted
        if (!MekanismConfig.general.allowProtection.get()) {
            return true;
        }
        if (owner == null || player.getUuid().equals(owner)) {
            return true;
        }
        SecurityFrequency freq = getFrequency(owner);
        if (freq == null) {
            return true;
        }
        if (freq.isOverridden()) {
            mode = freq.getSecurityMode();
        }
        if (mode == SecurityMode.PUBLIC) {
            return true;
        } else if (mode == SecurityMode.TRUSTED) {
            return freq.getTrustedUUIDs().contains(player.getUuid());
        }
        return false;
    }

    public static SecurityFrequency getFrequency(UUID uuid) {
        if (uuid != null) {
            return FrequencyType.SECURITY.getManager(null).getFrequency(uuid);
        }
        return null;
    }

    public static void displayNoAccess(PlayerEntity player) {
        player.sendSystemMessage(MekanismLang.LOG_FORMAT.translateColored(EnumColor.DARK_BLUE, MekanismLang.MEKANISM, EnumColor.RED, MekanismLang.NO_ACCESS), Util.NIL_UUID);
    }

    public static SecurityMode getSecurity(ISecurityTile security, EnvType side) {
        if (!security.hasSecurity()) {
            return SecurityMode.PUBLIC;
        }
        if (side == EnvType.SERVER) {
            SecurityFrequency freq = security.getSecurity().getFreq();
            if (freq != null && freq.isOverridden()) {
                return freq.getSecurityMode();
            }
        } else if (side == EnvType.CLIENT) {
            SecurityData data = MekanismClient.clientSecurityMap.get(security.getSecurity().getOwnerUUID());
            if (data != null && data.override) {
                return data.mode;
            }
        }
        return security.getSecurity().getMode();
    }

    public static SecurityMode getSecurity(ItemStack stack, EnvType side) {
        ISecurityItem security = (ISecurityItem) stack.getItem();
        SecurityMode mode = security.getSecurity(stack);
        if (security.getOwnerUUID(stack) != null) {
            if (side == EnvType.SERVER) {
                SecurityFrequency freq = getFrequency(security.getOwnerUUID(stack));
                if (freq != null && freq.isOverridden()) {
                    mode = freq.getSecurityMode();
                }
            } else if (side == EnvType.CLIENT) {
                SecurityData data = MekanismClient.clientSecurityMap.get(security.getOwnerUUID(stack));
                if (data != null && data.override) {
                    mode = data.mode;
                }
            }
        }
        return mode;
    }

    public static boolean isOverridden(ItemStack stack, EnvType side) {
        ISecurityItem security = (ISecurityItem) stack.getItem();
        if (security.getOwnerUUID(stack) == null) {
            return false;
        }
        if (side == EnvType.SERVER) {
            SecurityFrequency freq = getFrequency(security.getOwnerUUID(stack));
            return freq != null && freq.isOverridden();
        }
        SecurityData data = MekanismClient.clientSecurityMap.get(security.getOwnerUUID(stack));
        return data != null && data.override;
    }

    public static boolean isOverridden(BlockEntity tile, EnvType side) {
        ISecurityTile security = (ISecurityTile) tile;
        if (!security.hasSecurity() || security.getSecurity().getOwnerUUID() == null) {
            return false;
        }
        if (side == EnvType.SERVER) {
            SecurityFrequency freq = getFrequency(security.getSecurity().getOwnerUUID());
            return freq != null && freq.isOverridden();
        }
        SecurityData data = MekanismClient.clientSecurityMap.get(security.getSecurity().getOwnerUUID());
        return data != null && data.override;
    }
}