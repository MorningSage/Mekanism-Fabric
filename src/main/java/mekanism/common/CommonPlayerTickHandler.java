package mekanism.common;

import javax.annotation.Nullable;
import mekanism.api.Action;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.inventory.AutomationType;
import mekanism.api.math.FloatingLong;
import mekanism.common.base.KeySync;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.gear.IModuleContainerItem;
import mekanism.common.content.gear.Modules;
import mekanism.common.content.gear.mekasuit.ModuleJetpackUnit;
import mekanism.common.content.gear.mekasuit.ModuleLocomotiveBoostingUnit;
import mekanism.common.content.gear.mekasuit.ModuleMekaSuit.ModuleGravitationalModulatingUnit;
import mekanism.common.content.gear.mekasuit.ModuleMekaSuit.ModuleHydraulicPropulsionUnit;
import mekanism.common.content.gear.mekasuit.ModuleMekaSuit.ModuleInhalationPurificationUnit;
import mekanism.common.entity.EntityFlame;
import mekanism.common.item.gear.ItemFlamethrower;
import mekanism.common.item.gear.ItemFreeRunners;
import mekanism.common.item.gear.ItemFreeRunners.FreeRunnerMode;
import mekanism.common.item.gear.ItemJetpack;
import mekanism.common.item.gear.ItemJetpack.JetpackMode;
import mekanism.common.item.gear.ItemMekaSuitArmor;
import mekanism.common.item.gear.ItemScubaMask;
import mekanism.common.item.gear.ItemScubaTank;
import mekanism.common.registries.MekanismGases;
import mekanism.common.util.ChemicalUtil;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.StorageUtils;
import mekanism.mixin.accessors.ServerPlayNetworkHandlerAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CommonPlayerTickHandler {

    public static boolean isOnGround(PlayerEntity player) {
        int x = MathHelper.floor(player.getX());
        int y = MathHelper.floor(player.getY() - 0.01);
        int z = MathHelper.floor(player.getZ());
        BlockPos pos = new BlockPos(x, y, z);
        BlockState s = player.world.getBlockState(pos);
        VoxelShape shape = s.getOutlineShape(player.world, pos);
        if (shape.isEmpty()) {
            return false;
        }
        Box playerBox = player.getBoundingBox();
        return !s.isAir(player.world, pos) && playerBox.offset(0, -0.01, 0).intersects(shape.getBoundingBox().offset(pos));

    }

    public static boolean isScubaMaskOn(PlayerEntity player) {
        ItemStack tank = player.getEquippedStack(EquipmentSlot.CHEST);
        ItemStack mask = player.getEquippedStack(EquipmentSlot.HEAD);
        return !tank.isEmpty() && !mask.isEmpty() && tank.getItem() instanceof ItemScubaTank && mask.getItem() instanceof ItemScubaMask && ChemicalUtil.hasGas(tank) &&
               ((ItemScubaTank) tank.getItem()).getFlowing(tank);
    }

    public static boolean isFlamethrowerOn(PlayerEntity player) {
        if (Mekanism.playerState.isFlamethrowerOn(player)) {
            ItemStack currentItem = player.inventory.getMainHandStack();
            return !currentItem.isEmpty() && currentItem.getItem() instanceof ItemFlamethrower;
        }
        return false;
    }

    public static float getStepBoost(PlayerEntity player) {
        ItemStack stack = player.getEquippedStack(EquipmentSlot.FEET);
        if (!stack.isEmpty() && !player.isSneaking()) {
            if (stack.getItem() instanceof ItemFreeRunners) {
                ItemFreeRunners freeRunners = (ItemFreeRunners) stack.getItem();
                if (freeRunners.getMode(stack) == FreeRunnerMode.NORMAL) {
                    return 0.5F;
                }
            }
            ModuleHydraulicPropulsionUnit module = Modules.load(stack, Modules.HYDRAULIC_PROPULSION_UNIT);
            if (module != null && module.isEnabled()) {
                return module.getStepHeight();
            }
        }
        return 0;
    }

    public void onTick(PlayerTickEvent event) {
        if (event.phase == Phase.END && event.side.isServer()) {
            tickEnd(event.player);
        }
    }

    private void tickEnd(PlayerEntity player) {
        Mekanism.playerState.updateStepAssist(player);
        if (player instanceof ServerPlayerEntity) {
            Mekanism.radiationManager.tickServer((ServerPlayerEntity) player);
        }

        if (isFlamethrowerOn(player)) {
            player.world.spawnEntity(new EntityFlame(player));
            if (MekanismUtils.isPlayingMode(player)) {
                ItemStack currentItem = player.inventory.getMainHandStack();
                ((ItemFlamethrower) currentItem.getItem()).useGas(currentItem, 1);
            }
        }

        if (isJetpackOn(player)) {
            ItemStack stack = player.getEquippedStack(EquipmentSlot.CHEST);
            JetpackMode mode = getJetpackMode(stack);
            Vec3d motion = player.getVelocity();
            if (mode == JetpackMode.NORMAL) {
                player.setVelocity(motion.getX(), Math.min(motion.getY() + 0.15D, 0.5D), motion.getZ());
            } else if (mode == JetpackMode.HOVER) {
                boolean ascending = Mekanism.keyMap.has(player.getUuid(), KeySync.ASCEND);
                boolean descending = Mekanism.keyMap.has(player.getUuid(), KeySync.DESCEND);
                if ((!ascending && !descending) || (ascending && descending)) {
                    if (motion.getY() > 0) {
                        player.setVelocity(motion.getX(), Math.max(motion.getY() - 0.15D, 0), motion.getZ());
                    } else if (motion.getY() < 0) {
                        if (!isOnGround(player)) {
                            player.setVelocity(motion.getX(), Math.min(motion.getY() + 0.15D, 0), motion.getZ());
                        }
                    }
                } else if (ascending) {
                    player.setVelocity(motion.getX(), Math.min(motion.getY() + 0.15D, 0.2D), motion.getZ());
                } else if (!isOnGround(player)) {
                    player.setVelocity(motion.getX(), Math.max(motion.getY() - 0.15D, -0.2D), motion.getZ());
                }
            }
            player.fallDistance = 0.0F;
            if (player instanceof ServerPlayerEntity) {
                ((ServerPlayNetworkHandlerAccessor) ((ServerPlayerEntity) player).networkHandler).setFloatingTicks(0);
            }
            if (stack.getItem() instanceof ItemJetpack) {
                ((ItemJetpack) stack.getItem()).useGas(stack, 1);
            } else {
                ((ItemMekaSuitArmor) stack.getItem()).useGas(stack, MekanismGases.HYDROGEN.get(), 1);
            }
        }

        if (isScubaMaskOn(player)) {
            ItemStack stack = player.getEquippedStack(EquipmentSlot.CHEST);
            ItemScubaTank tank = (ItemScubaTank) stack.getItem();
            final int max = 300;
            tank.useGas(stack, 1);
            GasStack received = tank.useGas(stack, max - player.getAir());
            if (!received.isEmpty()) {
                player.setAir(player.getAir() + (int) received.getAmount());
            }
            if (player.getAir() == max) {
                for (StatusEffectInstance effect : player.getStatusEffects()) {
                    for (int i = 0; i < 9; i++) {
                        effect.update(player, () -> MekanismUtils.onChangedPotionEffect(player, effect, true));
                    }
                }
            }
        }

        Mekanism.playerState.updateFlightInfo(player);
    }

    public static boolean isJetpackOn(PlayerEntity player) {
        if (MekanismUtils.isPlayingMode(player)) {
            ItemStack chest = player.getEquippedStack(EquipmentSlot.CHEST);
            if (!chest.isEmpty()) {
                JetpackMode mode = getJetpackMode(chest);
                if (mode == JetpackMode.NORMAL) {
                    return Mekanism.keyMap.has(player.getUuid(), KeySync.ASCEND);
                } else if (mode == JetpackMode.HOVER) {
                    boolean ascending = Mekanism.keyMap.has(player.getUuid(), KeySync.ASCEND);
                    boolean descending = Mekanism.keyMap.has(player.getUuid(), KeySync.DESCEND);
                    if (!ascending || descending) {
                        return !isOnGround(player);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isGravitationalModulationReady(PlayerEntity player) {
        ModuleGravitationalModulatingUnit module = Modules.load(player.getEquippedStack(EquipmentSlot.CHEST), Modules.GRAVITATIONAL_MODULATING_UNIT);
        FloatingLong usage = MekanismConfig.gear.mekaSuitEnergyUsageGravitationalModulation.get();
        return MekanismUtils.isPlayingMode(player) && module != null && module.isEnabled() && module.getContainerEnergy().greaterOrEqual(usage);
    }

    public static boolean isGravitationalModulationOn(PlayerEntity player) {
        return isGravitationalModulationReady(player) && player.abilities.flying;
    }

    /** Will return null if jetpack mode is not active */
    private static JetpackMode getJetpackMode(ItemStack stack) {
        if (stack.getItem() instanceof ItemJetpack && ChemicalUtil.hasGas(stack)) {
            return ((ItemJetpack) stack.getItem()).getMode(stack);
        } else if (stack.getItem() instanceof IModuleContainerItem && ChemicalUtil.hasChemical(stack, MekanismGases.HYDROGEN.get())) {
            ModuleJetpackUnit module = Modules.load(stack, Modules.JETPACK_UNIT);
            if (module != null && module.isEnabled()) {
                return module.getMode();
            }
        }
        return null;
    }

    public void onEntityAttacked(LivingAttackEvent event) {
        LivingEntity base = event.getEntityLiving();
        //Gas Mask checks
        if (event.getSource() == DamageSource.MAGIC) {
            ItemStack headStack = base.getEquippedStack(EquipmentSlot.HEAD);
            if (!headStack.isEmpty() && headStack.getItem() instanceof ItemScubaMask) {
                ItemStack chestStack = base.getEquippedStack(EquipmentSlot.CHEST);
                if (!chestStack.isEmpty()) {
                    if (chestStack.getItem() instanceof ItemScubaTank && ((ItemScubaTank) chestStack.getItem()).getFlowing(chestStack) &&
                        ChemicalUtil.hasGas(chestStack)) {
                        event.setCanceled(true);
                        return;
                    }
                    ModuleInhalationPurificationUnit module = Modules.load(chestStack, Modules.INHALATION_PURIFICATION_UNIT);
                    if (module != null && module.isEnabled() && module.getContainerEnergy().greaterOrEqual(MekanismConfig.gear.mekaSuitEnergyUsageMagicPrevent.get())) {
                        module.useEnergy(base, MekanismConfig.gear.mekaSuitEnergyUsageMagicPrevent.get());
                        event.setCanceled(true);
                        return;
                    }
                }
            }
        }
        //Free runner checks
        //Note: We have this here in addition to listening to LivingHurt, so as if we can fully block the damage
        // then we don't play the hurt effect/sound, as cancelling LivingHurtEvent still causes that to happen
        if (event.getSource() == DamageSource.FALL) {
            IEnergyContainer energyContainer = getFallAbsorptionEnergyContainer(base);
            if (energyContainer != null) {
                FloatingLong energyRequirement = MekanismConfig.gear.freeRunnerFallEnergyCost.get().multiply(event.getAmount());
                FloatingLong simulatedExtract = energyContainer.extract(energyRequirement, Action.SIMULATE, AutomationType.MANUAL);
                if (simulatedExtract.equals(energyRequirement)) {
                    //If we could fully negate the damage cancel the event
                    energyContainer.extract(energyRequirement, Action.EXECUTE, AutomationType.MANUAL);
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (event.getSource() == DamageSource.FALL) {
            IEnergyContainer energyContainer = getFallAbsorptionEnergyContainer(event.getEntityLiving());
            if (energyContainer != null) {
                FloatingLong energyRequirement = MekanismConfig.gear.freeRunnerFallEnergyCost.get().multiply(event.getAmount());
                FloatingLong extracted = energyContainer.extract(energyRequirement, Action.EXECUTE, AutomationType.MANUAL);
                if (!extracted.isZero()) {
                    //If we managed to remove any power, then we want to lower (or negate) the amount of fall damage
                    FloatingLong remainder = energyRequirement.subtract(extracted);
                    if (remainder.isZero()) {
                        //If we used all the power we required, then cancel the event
                        event.setCanceled(true);
                    } else {
                        float newDamage = remainder.divide(MekanismConfig.gear.freeRunnerFallEnergyCost.get()).floatValue();
                        if (newDamage == 0) {
                            //If we ended up being close enough that it rounds down to zero, just cancel it anyways
                            event.setCanceled(true);
                        } else {
                            //Otherwise reduce the damage
                            event.setAmount(newDamage);
                        }
                    }
                }
            }
        } else {
            if (event.getEntityLiving() instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) event.getEntityLiving();
                float ratioAbsorbed = 0;
                for (ItemStack stack : player.inventory.armorInventory) {
                    if (stack.getItem() instanceof ItemMekaSuitArmor) {
                        ratioAbsorbed += ((ItemMekaSuitArmor) stack.getItem()).getDamageAbsorbed(stack, event.getSource(), event.getAmount());
                    }
                }
                float damageRemaining = event.getAmount() * Math.max(0, 1 - ratioAbsorbed);
                if (damageRemaining <= 0) {
                    event.setCanceled(true);
                } else {
                    event.setAmount(damageRemaining);
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingJump(LivingJumpEvent event) {
        if (event.getEntityLiving() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();
            ModuleHydraulicPropulsionUnit module = Modules.load(player.getItemStackFromSlot(EquipmentSlotType.FEET), Modules.HYDRAULIC_PROPULSION_UNIT);
            if (module != null && module.isEnabled() && Mekanism.keyMap.has(player.getUniqueID(), KeySync.BOOST)) {
                FloatingLong usage = MekanismConfig.gear.mekaSuitBaseJumpEnergyUsage.get().multiply(module.getBoost() / 0.1F);
                if (module.getContainerEnergy().greaterOrEqual(usage)) {
                    float boost = module.getBoost();
                    // if we're sprinting with the boost module, limit the height
                    ModuleLocomotiveBoostingUnit boostModule = Modules.load(player.getItemStackFromSlot(EquipmentSlotType.LEGS), Modules.LOCOMOTIVE_BOOSTING_UNIT);
                    if (boostModule != null && boostModule.isEnabled() && boostModule.canFunction(player)) {
                        boost = (float) Math.sqrt(boost);
                    }
                    player.setMotion(player.getMotion().add(0, boost, 0));
                    module.useEnergy(player, usage);
                }
            }
        }
    }

    /**
     * @return null if free runners are not being worn or they don't have an energy container for some reason
     */
    @Nullable
    private IEnergyContainer getFallAbsorptionEnergyContainer(LivingEntity base) {
        ItemStack feetStack = base.getItemStackFromSlot(EquipmentSlotType.FEET);
        if (!feetStack.isEmpty()) {
            if (feetStack.getItem() instanceof ItemFreeRunners) {
                ItemFreeRunners boots = (ItemFreeRunners) feetStack.getItem();
                if (boots.getMode(feetStack) == FreeRunnerMode.NORMAL) {
                    return StorageUtils.getEnergyContainer(feetStack, 0);
                }
            } else if (feetStack.getItem() instanceof ItemMekaSuitArmor) {
                return StorageUtils.getEnergyContainer(feetStack, 0);
            }
        }
        return null;
    }
}