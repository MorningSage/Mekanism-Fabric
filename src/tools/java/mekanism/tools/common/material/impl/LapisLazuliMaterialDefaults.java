package mekanism.tools.common.material.impl;

import mekanism.common.tags.MekanismTags;
import mekanism.tools.common.material.BaseMekanismMaterial;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

import javax.annotation.Nonnull;

public class LapisLazuliMaterialDefaults extends BaseMekanismMaterial {

    @Override
    public int getShieldDurability() {
        return 582;
    }

    @Override
    public float getAxeDamage() {
        return 6;
    }

    @Override
    public float getAxeAtkSpeed() {
        return -3.1F;
    }

    @Override
    public float getPaxelDamage() {
        return 6;
    }

    @Override
    public int getPaxelHarvestLevel() {
        return 2;
    }

    @Override
    public int getPaxelMaxUses() {
        return 250;
    }

    @Override
    public float getPaxelEfficiency() {
        return 6;
    }

    @Override
    public int getDurability() {
        return 200;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return 5;
    }

    @Override
    public float getAttackDamage() {
        return 2;
    }

    @Override
    public int getMiningLevel() {
        return 2;
    }

    @Override
    public int getCommonEnchantability() {
        return 8;
    }

    @Override
    public float getToughness() {
        return 0;
    }

    @Override
    public int getDurability(@Nonnull EquipmentSlot slotType) {
        switch (slotType) {
            case FEET:
                return 169;
            case LEGS:
                return 195;
            case CHEST:
                return 208;
            case HEAD:
                return 143;
        }
        return 0;
    }

    @Override
    public int getProtectionAmount(@Nonnull EquipmentSlot slotType) {
        switch (slotType) {
            case FEET:
                return 2;
            case LEGS:
                return 6;
            case CHEST:
                return 5;
            case HEAD:
                return 2;
        }
        return 0;
    }

    @Nonnull
    @Override
    public String getRegistryPrefix() {
        return "lapis_lazuli";
    }

    @Override
    public int getPaxelEnchantability() {
        return 10;
    }

    @Nonnull
    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND;
    }

    @Nonnull
    @Override
    public Ingredient getCommonRepairMaterial() {
        return Ingredient.fromTag(MekanismTags.Items.GEMS_LAPIS);
    }

    @Override
    public float getKnockbackResistance() {
        return 0;
    }
}