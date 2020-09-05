package mekanism.common.content.gear.mekasuit;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import mekanism.api.Action;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.inventory.AutomationType;
import mekanism.api.math.FloatingLong;
import mekanism.api.text.IHasTextComponent;
import mekanism.common.MekanismLang;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.gear.HUDElement;
import mekanism.common.content.gear.HUDElement.HUDColor;
import mekanism.common.content.gear.Module;
import mekanism.common.content.gear.ModuleConfigItem;
import mekanism.common.content.gear.ModuleConfigItem.BooleanData;
import mekanism.common.content.gear.ModuleConfigItem.EnumData;
import mekanism.common.content.gear.Modules;
import mekanism.common.content.gear.mekasuit.ModuleLocomotiveBoostingUnit.SprintBoost;
import mekanism.common.item.gear.ItemMekaSuitArmor;
import mekanism.common.lib.radiation.RadiationManager;
import mekanism.common.lib.radiation.capability.IRadiationEntity;
import mekanism.common.registries.MekanismGases;
import mekanism.common.registries.MekanismItems;
import mekanism.common.util.CapabilityUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.common.util.StorageUtils;
import mekanism.common.util.UnitDisplayUtils;
import mekanism.common.util.UnitDisplayUtils.RadiationUnit;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public abstract class ModuleMekaSuit extends Module {

    public static class ModuleElectrolyticBreathingUnit extends ModuleMekaSuit {

        @Override
        public void tickServer(PlayerEntity player) {
            int productionRate = 0;
            //Check if the mask is under water
            //Note: Being in water is checked first to ensure that if it is raining and the player is in water
            // they get the full strength production
            double maskHeight = player.getPosYEye() - 0.15;
            BlockPos headPos = new BlockPos(player.getPosX(), maskHeight, player.getPosZ());
            FluidState fluidstate = player.getEntityWorld().getFluidState(headPos);
            if (fluidstate.isTagged(FluidTags.WATER) && maskHeight <= headPos.getY() + fluidstate.getActualHeight(player.getEntityWorld(), headPos)) {
                //If the position the bottom of the mask is in is water set the production rate to our max rate
                productionRate = getMaxRate();
            } else if (player.isInRain()) {
                //If the player is not in water but is in rain set the production at half power
                productionRate = getMaxRate() / 2;
            }
            if (productionRate > 0) {
                FloatingLong usage = MekanismConfig.general.FROM_H2.get().multiply(2);
                int maxRate = Math.min(productionRate, getContainerEnergy().divide(usage).intValue());
                long hydrogenUsed = 0;
                GasStack hydrogenStack = MekanismGases.HYDROGEN.getStack(maxRate * 2L);
                ItemStack chestStack = player.getItemStackFromSlot(EquipmentSlotType.CHEST);
                Optional<IGasHandler> chestCapability = MekanismUtils.toOptional(chestStack.getCapability(Capabilities.GAS_HANDLER_CAPABILITY));
                if (checkChestPlate(chestStack) && chestCapability.isPresent()) {
                    hydrogenUsed = maxRate * 2L - chestCapability.get().insertChemical(hydrogenStack, Action.EXECUTE).getAmount();
                    hydrogenStack.shrink(hydrogenUsed);
                }
                ItemStack handStack = player.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
                Optional<IGasHandler> handCapability = MekanismUtils.toOptional(handStack.getCapability(Capabilities.GAS_HANDLER_CAPABILITY));
                if (handCapability.isPresent()) {
                    hydrogenUsed = maxRate * 2L - handCapability.get().insertChemical(hydrogenStack, Action.EXECUTE).getAmount();
                }
                int oxygenUsed = Math.min(maxRate, player.getMaxAir() - player.getAir());
                long used = Math.max((int) Math.ceil(hydrogenUsed / 2D), oxygenUsed);
                useEnergy(player, usage.multiply(used));
                player.setAir(player.getAir() + oxygenUsed);
            }
        }

        /**
         * Checks whether the given chestplate should be filled with hydrogen, if it can store hydrogen. Does not check whether the chestplate can store hydrogen.
         *
         * @param chestPlate the chestplate to check
         *
         * @return whether the given chestplate should be filled with hydrogen.
         */
        private boolean checkChestPlate(ItemStack chestPlate) {
            if (chestPlate.getItem() == MekanismItems.MEKASUIT_BODYARMOR.get()) {
                return Modules.load(chestPlate, Modules.JETPACK_UNIT) != null;
            }
            return true;
        }

        private int getMaxRate() {
            return (int) Math.pow(2, getInstalledCount());
        }
    }

    public static class ModuleInhalationPurificationUnit extends ModuleMekaSuit {

        private ModuleConfigItem<Boolean> beneficialEffects;
        private ModuleConfigItem<Boolean> neutralEffects;
        private ModuleConfigItem<Boolean> harmfulEffects;

        @Override
        public void init() {
            super.init();
            addConfigItem(beneficialEffects = new ModuleConfigItem<>(this, "beneficial_effects", MekanismLang.MODULE_PURIFICATION_BENEFICIAL, new BooleanData(), false));
            addConfigItem(neutralEffects = new ModuleConfigItem<>(this, "neutral_effects", MekanismLang.MODULE_PURIFICATION_NEUTRAL, new BooleanData(), true));
            addConfigItem(harmfulEffects = new ModuleConfigItem<>(this, "harmful_effects", MekanismLang.MODULE_PURIFICATION_HARMFUL, new BooleanData(), true));
        }

        @Override
        public void tickServer(PlayerEntity player) {
            for (EffectInstance effect : player.getActivePotionEffects()) {
                EffectType effectType = effect.getPotion().getEffectType();
                if (!canHandle(effectType)) {
                    continue;
                } else if (getContainerEnergy().smallerThan(MekanismConfig.gear.mekaSuitEnergyUsagePotionTick.get())) {
                    break;
                }
                useEnergy(player, MekanismConfig.gear.mekaSuitEnergyUsagePotionTick.get());
                for (int i = 0; i < 9; i++) {
                    effect.tick(player, () -> MekanismUtils.onChangedPotionEffect(player, effect, true));
                }
            }
        }

        private boolean canHandle(EffectType effectType) {
            switch (effectType) {
                case BENEFICIAL:
                    return beneficialEffects.get();
                case HARMFUL:
                    return harmfulEffects.get();
                case NEUTRAL:
                    return neutralEffects.get();
            }
            return false;
        }
    }

    public static class ModuleVisionEnhancementUnit extends ModuleMekaSuit {

        private static final ResourceLocation icon = MekanismUtils.getResource(ResourceType.GUI_HUD, "vision_enhancement_unit.png");

        @Override
        public void tickServer(PlayerEntity player) {
            super.tickServer(player);
            useEnergy(player, MekanismConfig.gear.mekaSuitEnergyUsageVisionEnhancement.get());
        }

        @Override
        public void addHUDElements(List<HUDElement> list) {
            list.add(HUDElement.enabled(icon, isEnabled()));
        }

        @Override
        public void changeMode(@Nonnull PlayerEntity player, @Nonnull ItemStack stack, int shift, boolean displayChangeMessage) {
            toggleEnabled(player, MekanismLang.MODULE_VISION_ENHANCEMENT.translate());
        }
    }

    public static class ModuleRadiationShieldingUnit extends ModuleMekaSuit {}

    public static class ModuleGravitationalModulatingUnit extends ModuleMekaSuit {

        private static final ResourceLocation icon = MekanismUtils.getResource(ResourceType.GUI_HUD, "gravitational_modulation_unit.png");

        // we share with locomotive boosting unit
        private ModuleConfigItem<SprintBoost> speedBoost;

        @Override
        public void init() {
            super.init();
            addConfigItem(speedBoost = new ModuleConfigItem<>(this, "speed_boost", MekanismLang.MODULE_SPEED_BOOST, new EnumData<>(SprintBoost.class), SprintBoost.LOW));
        }

        @Override
        public void addHUDElements(List<HUDElement> list) {
            list.add(HUDElement.enabled(icon, isEnabled()));
        }

        @Override
        public void changeMode(@Nonnull PlayerEntity player, @Nonnull ItemStack stack, int shift, boolean displayChangeMessage) {
            toggleEnabled(player, MekanismLang.MODULE_GRAVITATIONAL_MODULATION.translate());
        }

        public float getBoost() {
            return speedBoost.get().getBoost();
        }
    }

    public static class ModuleHydraulicAbsorptionUnit extends ModuleMekaSuit {}

    public static class ModuleHydraulicPropulsionUnit extends ModuleMekaSuit {

        private ModuleConfigItem<JumpBoost> jumpBoost;
        private ModuleConfigItem<StepAssist> stepAssist;

        @Override
        public void init() {
            super.init();
            addConfigItem(jumpBoost = new ModuleConfigItem<>(this, "jump_boost", MekanismLang.MODULE_JUMP_BOOST, new EnumData<>(JumpBoost.class, getInstalledCount() + 1), JumpBoost.LOW));
            addConfigItem(stepAssist = new ModuleConfigItem<>(this, "step_assist", MekanismLang.MODULE_STEP_ASSIST, new EnumData<>(StepAssist.class, getInstalledCount() + 1), StepAssist.LOW));
        }

        public float getBoost() {
            return jumpBoost.get().getBoost();
        }

        public float getStepHeight() {
            return stepAssist.get().getHeight();
        }

        public enum JumpBoost implements IHasTextComponent {
            OFF(0),
            LOW(0.5F),
            MED(1),
            HIGH(3),
            ULTRA(5);

            private final float boost;
            private final ITextComponent label;

            JumpBoost(float boost) {
                this.boost = boost;
                this.label = new StringTextComponent(Float.toString(boost));
            }

            @Override
            public ITextComponent getTextComponent() {
                return label;
            }

            public float getBoost() {
                return boost;
            }
        }

        public enum StepAssist implements IHasTextComponent {
            OFF(0),
            LOW(0.5F),
            MED(1),
            HIGH(1.5F),
            ULTRA(2);

            private final float height;
            private final ITextComponent label;

            StepAssist(float height) {
                this.height = height;
                this.label = new StringTextComponent(Float.toString(height));
            }

            @Override
            public ITextComponent getTextComponent() {
                return label;
            }

            public float getHeight() {
                return height;
            }
        }
    }

    public static class ModuleSolarRechargingUnit extends ModuleMekaSuit {

        @Override
        public void tickServer(PlayerEntity player) {
            super.tickServer(player);
            IEnergyContainer energyContainer = StorageUtils.getEnergyContainer(getContainer(), 0);
            if (energyContainer != null && !energyContainer.getNeeded().isZero() && player.world.isDaytime() &&
                player.world.canSeeSky(new BlockPos(player.getPositionVec())) && !player.world.isRaining() && player.world.func_230315_m_().hasSkyLight()) {
                FloatingLong rate = MekanismConfig.gear.mekaSuitSolarRechargingRate.get().multiply(getInstalledCount());
                energyContainer.insert(rate, Action.EXECUTE, AutomationType.MANUAL);
            }
        }
    }

    public static class ModuleNutritionalInjectionUnit extends ModuleMekaSuit {

        private static final ResourceLocation icon = MekanismUtils.getResource(ResourceType.GUI_HUD, "nutritional_injection_unit.png");

        @Override
        public void tickServer(PlayerEntity player) {
            super.tickServer(player);
            FloatingLong usage = MekanismConfig.gear.mekaSuitEnergyUsageNutritionalInjection.get();
            if (MekanismUtils.isPlayingMode(player) && player.canEat(false) && getContainerEnergy().greaterOrEqual(usage)) {
                ItemMekaSuitArmor item = (ItemMekaSuitArmor) getContainer().getItem();
                long toFeed = Math.min(1, item.getContainedGas(getContainer(), MekanismGases.NUTRITIONAL_PASTE.get()).getAmount() / MekanismConfig.general.nutritionalPasteMBPerFood.get());
                if (toFeed > 0) {
                    useEnergy(player, usage.multiply(toFeed));
                    item.useGas(getContainer(), MekanismGases.NUTRITIONAL_PASTE.get(), toFeed * MekanismConfig.general.nutritionalPasteMBPerFood.get());
                    player.getFoodStats().addStats(1, MekanismConfig.general.nutritionalPasteSaturation.get());
                }
            }
        }

        @Override
        public void addHUDElements(List<HUDElement> list) {
            if (!isEnabled()) {
                return;
            }
            GasStack stored = ((ItemMekaSuitArmor) getContainer().getItem()).getContainedGas(getContainer(), MekanismGases.NUTRITIONAL_PASTE.get());
            double ratio = StorageUtils.getRatio(stored.getAmount(), MekanismConfig.gear.mekaSuitNutritionalMaxStorage.getAsLong());
            list.add(HUDElement.percent(icon, ratio));
        }
    }

    public static class ModuleDosimeterUnit extends ModuleMekaSuit {

        private static final ResourceLocation icon = MekanismUtils.getResource(ResourceType.GUI_HUD, "dosimeter.png");

        @Override
        public void addHUDElements(List<HUDElement> list) {
            if (!isEnabled()) {
                return;
            }
            Optional<IRadiationEntity> capability = MekanismUtils.toOptional(CapabilityUtils.getCapability(Minecraft.getInstance().player,
                  Capabilities.RADIATION_ENTITY_CAPABILITY, null));
            if (capability.isPresent()) {
                double radiation = capability.get().getRadiation();
                HUDElement e = HUDElement.of(icon, UnitDisplayUtils.getDisplayShort(radiation, RadiationUnit.SV, 2));
                HUDColor color = radiation < RadiationManager.MIN_MAGNITUDE ? HUDColor.REGULAR : (radiation < 0.1 ? HUDColor.WARNING : HUDColor.DANGER);
                list.add(e.color(color));
            }
        }
    }
}
