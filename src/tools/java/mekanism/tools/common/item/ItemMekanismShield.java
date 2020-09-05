package mekanism.tools.common.item;

import mekanism.tools.common.IHasBuiltinModelItemRenderer;
import mekanism.tools.common.IHasPreferredEquipmentSlot;
import mekanism.tools.common.IHasRepairType;
import mekanism.tools.common.ToolsLang;
import mekanism.tools.common.material.BaseMekanismMaterial;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

public class ItemMekanismShield extends ShieldItem implements IHasRepairType, IHasPreferredEquipmentSlot, IHasBuiltinModelItemRenderer<ItemMekanismShield> {

    private final BaseMekanismMaterial material;
    private Supplier<Callable<BuiltinModelItemRenderer>> builtinModelItemRenderer = null;

    public ItemMekanismShield(BaseMekanismMaterial material, Item.Settings properties) {
        super(properties.maxDamage(material.getShieldDurability()));
        this.material = material;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<Text> tooltip, @Nonnull TooltipContext flag) {
        super.appendTooltip(stack, world, tooltip, flag); //Add the banner type description
        tooltip.add(ToolsLang.HP.translate(stack.getMaxDamage() - stack.getDamage()));
    }

    @Nonnull
    @Override
    public Ingredient getRepairMaterial() {
        return material.getRepairIngredient();
    }

    //@Override
    //public int getMaxDamage(ItemStack stack) {
    //    return material.getShieldDurability();
    //}

    @Override
    public boolean isDamageable() {
        return material.getShieldDurability() > 0;
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return getRepairMaterial().test(ingredient);
    }

    @Override
    public EquipmentSlot getPreferredEquipmentSlot(ItemStack stack, @Nullable LivingEntity entity) {
        return EquipmentSlot.OFFHAND;
    }

    //@Override
    //public boolean isShield(ItemStack stack, @Nullable LivingEntity entity) {
    //    //Has to override this because default impl in IForgeItem checks for exact equality with the shield item instead of instanceof
    //    return true;
    //}

    @Override
    public int getEnchantability() {
        return material.getEnchantability();
    }

    @Override
    public Supplier<Callable<BuiltinModelItemRenderer>> getBuiltinModelItemRenderer() {
        return this.builtinModelItemRenderer;
    }

    @Override
    public ItemMekanismShield setBuiltinModelItemRenderer(Supplier<Callable<BuiltinModelItemRenderer>> builtinModelItemRenderer) {
        this.builtinModelItemRenderer = builtinModelItemRenderer;
        return this;
    }
}