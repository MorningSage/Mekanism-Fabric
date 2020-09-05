package mekanism.tools.common.material.impl;

import mekanism.common.tags.MekanismTags;
import mekanism.tools.common.material.BaseMekanismMaterial;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

import javax.annotation.Nonnull;

public class RefinedObsidianMaterialDefaults extends BaseMekanismMaterial {

    @Override
    public int getShieldDurability() {
        return 2_240;
    }

    @Override
    public float getAxeDamage() {
        return 2;
    }

    @Override
    public float getAxeAtkSpeed() {
        return -2;
    }

    @Override
    public float getPaxelDamage() {
        return 4;
    }

    @Override
    public int getPaxelHarvestLevel() {
        return 3;
    }

    @Override
    public int getPaxelMaxUses() {
        return 3_000;
    }

    @Override
    public float getPaxelEfficiency() {
        return 25;
    }

    @Override
    public int getDurability() {
        return 2_500;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return 20;
    }

    @Override
    public float getAttackDamage() {
        return 10;
    }

    @Override
    public int getMiningLevel() {
        return 3;
    }

    @Override
    public int getCommonEnchantability() {
        return 40;
    }

    @Override
    public boolean burnsInFire() {
        return false;
    }

    @Override
    public float getToughness() {
        return 4;
    }

    @Override
    public int getDurability(@Nonnull EquipmentSlot slotType) {
        switch (slotType) {
            case FEET:
                return 650;
            case LEGS:
                return 750;
            case CHEST:
                return 800;
            case HEAD:
                return 550;
        }
        return 0;
    }

    @Override
    public int getProtectionAmount(@Nonnull EquipmentSlot slotType) {
        switch (slotType) {
            case FEET:
                return 5;
            case LEGS:
                return 8;
            case CHEST:
                return 12;
            case HEAD:
                return 5;
        }
        return 0;
    }

    @Nonnull
    @Override
    public String getRegistryPrefix() {
        return "refined_obsidian";
    }

    @Override
    public int getPaxelEnchantability() {
        return 50;
    }

    @Nonnull
    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ITEM_ARMOR_EQUIP_IRON;
    }

    @Nonnull
    @Override
    public Ingredient getCommonRepairMaterial() {
        return Ingredient.fromTag(MekanismTags.Items.INGOTS_REFINED_OBSIDIAN);
    }

    @Override
    public float getKnockbackResistance() {
        return 0.1F;
    }
}