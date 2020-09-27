package mekanism.common.item.gear;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.chemical.ChemicalTankBuilder;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.api.chemical.gas.IGasHandler.IMekanismGasHandler;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.common.MekanismLang;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.ItemCapabilityWrapper;
import mekanism.common.capabilities.chemical.item.RateLimitGasHandler;
import mekanism.common.config.MekanismConfig;
import mekanism.common.item.interfaces.IGasItem;
import mekanism.common.registries.MekanismGases;
import mekanism.common.util.ChemicalUtil;
import mekanism.common.util.StorageUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class ItemCanteen extends Item implements IGasItem {

    public ItemCanteen(Settings properties) {
        super(properties.rarity(Rarity.UNCOMMON).maxCount(1)/*.setNoRepair()*/);
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return false;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<Text> tooltip, @Nonnull TooltipContext flag) {
        StorageUtils.addStoredGas(stack, tooltip, true, false, MekanismLang.EMPTY);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return StorageUtils.getDurabilityForDisplay(stack);
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return ChemicalUtil.getRGBDurabilityForDisplay(stack);
    }

    @Override
    public void appendStacks(@Nonnull ItemGroup group, @Nonnull DefaultedList<ItemStack> items) {
        super.appendStacks(group, items);
        if (isIn(group)) {
            items.add(ChemicalUtil.getFilledVariant(new ItemStack(this), MekanismConfig.gear.canteenMaxStorage.get(), MekanismGases.NUTRITIONAL_PASTE));
        }
    }

    @Nonnull
    @Override
    public ItemStack finishUsing(@Nonnull ItemStack stack, @Nonnull World world, @Nonnull LivingEntity entityLiving) {
        if (!world.isClient && entityLiving instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entityLiving;
            long needed = Math.min(20 - player.getHungerManager().getFoodLevel(), getGas(stack).getAmount() / MekanismConfig.general.nutritionalPasteMBPerFood.get());
            if (needed > 0) {
                player.getHungerManager().add((int) needed, MekanismConfig.general.nutritionalPasteSaturation.get());
                useGas(stack, needed * MekanismConfig.general.nutritionalPasteMBPerFood.get());
            }
        }
        return stack;
    }

    @Override
    public int getMaxUseTime(@Nonnull ItemStack stack) {
        return 32;
    }

    @Nonnull
    @Override
    public UseAction getUseAction(@Nonnull ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
        return new ItemCapabilityWrapper(stack, RateLimitGasHandler.create(MekanismConfig.gear.canteenTransferRate, MekanismConfig.gear.canteenMaxStorage,
            ChemicalTankBuilder.GAS.alwaysTrueBi, ChemicalTankBuilder.GAS.alwaysTrueBi, gas -> gas == MekanismGases.NUTRITIONAL_PASTE.getChemical()));
    }

    private GasStack getGas(ItemStack stack) {
        Optional<IGasHandler> capability = stack.getCapability(Capabilities.GAS_HANDLER_CAPABILITY).resolve();
        if (capability.isPresent()) {
            IGasHandler gasHandlerItem = capability.get();
            if (gasHandlerItem instanceof IMekanismGasHandler) {
                IGasTank gasTank = ((IMekanismGasHandler) gasHandlerItem).getChemicalTank(0, null);
                if (gasTank != null) {
                    return gasTank.getStack();
                }
            }
            return gasHandlerItem.getChemicalInTank(0);
        }
        return GasStack.EMPTY;
    }

    @Nonnull
    @Override
    public TypedActionResult<ItemStack> use(@Nonnull World worldIn, PlayerEntity playerIn, @Nonnull Hand handIn) {
        if (!playerIn.isCreative() && playerIn.canConsume(false) && getGas(playerIn.getStackInHand(handIn)).getAmount() >= 50) {
            playerIn.setCurrentHand(handIn);
        }
        return TypedActionResult.pass(playerIn.getStackInHand(handIn));
    }
}
