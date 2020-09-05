package mekanism.tools.common.material;

import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;

import javax.annotation.Nonnull;

interface IArmorMaterialHelper extends ArmorMaterial {

    int getArmorEnchantability();

    /**
     * Wrap the IArmorMaterial methods that have the same deobf name as IItemTier to regular variants so that they can both be resolved as implemented when
     * reobfuscating.
     *
     * @apiNote Both {@link #getArmorEnchantability()} and {@link IItemTierHelper#getItemEnchantability()} are wrapped back into a single method in {@link
     * BaseMekanismMaterial}
     */
    @Override
    default int getEnchantability() {
        return getArmorEnchantability();
    }

    @Nonnull
    Ingredient getArmorRepairMaterial();

    /**
     * Wrap the IArmorMaterial methods that have the same deobf name as IItemTier to regular variants so that they can both be resolved as implemented when
     * reobfuscating.
     *
     * @apiNote Both {@link #getArmorEnchantability()} and {@link IItemTierHelper#getItemRepairMaterial()} are wrapped back into a single method in {@link
     * BaseMekanismMaterial}
     */
    @Nonnull
    @Override
    default Ingredient getRepairIngredient() {
        return getArmorRepairMaterial();
    }
}