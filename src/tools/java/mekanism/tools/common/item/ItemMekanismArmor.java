package mekanism.tools.common.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import mekanism.common.config.value.CachedIntValue;
import mekanism.common.lib.attribute.AttributeCache;
import mekanism.common.lib.attribute.IAttributeRefresher;
import mekanism.tools.client.render.GlowArmor;
import mekanism.tools.common.IHasArmorModel;
import mekanism.tools.common.IHasPiglinSettings;
import mekanism.tools.common.IHasRepairType;
import mekanism.tools.common.ToolsLang;
import mekanism.tools.common.material.MaterialCreator;
import mekanism.tools.common.registries.ToolsItems;
import mekanism.tools.mixin.accessors.ArmorItemAccessor;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class ItemMekanismArmor extends ArmorItem implements IHasRepairType, IAttributeRefresher, IHasArmorModel, IHasPiglinSettings {

    private final MaterialCreator material;
    private final AttributeCache attributeCache;
    private final boolean makesPiglinsNeutral;

    public ItemMekanismArmor(MaterialCreator material, EquipmentSlot slot, Item.Settings properties, boolean makesPiglinsNeutral) {
        super(material, slot, properties);
        this.material = material;
        this.makesPiglinsNeutral = makesPiglinsNeutral;
        CachedIntValue armorConfig;
        if (slot == EquipmentSlot.FEET) {
            armorConfig = material.bootArmor;
        } else if (slot == EquipmentSlot.LEGS) {
            armorConfig = material.leggingArmor;
        } else if (slot == EquipmentSlot.CHEST) {
            armorConfig = material.chestplateArmor;
        } else if (slot == EquipmentSlot.HEAD) {
            armorConfig = material.helmetArmor;
        } else {
            throw new IllegalArgumentException("Invalid slot type for armor");
        }
        this.attributeCache = new AttributeCache(this, material.toughness, material.knockbackResistance, armorConfig);
    }

    @Override
    public void appendTooltip(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<Text> tooltip, @Nonnull TooltipContext flag) {
        tooltip.add(ToolsLang.HP.translate(stack.getMaxDamage() - stack.getDamage()));
    }

    @Override
    public BipedEntityModel getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, BipedEntityModel _default) {
        if (itemStack.getItem() == ToolsItems.REFINED_GLOWSTONE_HELMET.getItem() || itemStack.getItem() == ToolsItems.REFINED_GLOWSTONE_CHESTPLATE.getItem()
                || itemStack.getItem() == ToolsItems.REFINED_GLOWSTONE_LEGGINGS.getItem() || itemStack.getItem() == ToolsItems.REFINED_GLOWSTONE_BOOTS.getItem()) {
            return GlowArmor.getGlow(armorSlot);
        }

        return null;
    }


    @Override
    public boolean makesPiglinsNeutral(@Nonnull ItemStack stack, @Nonnull LivingEntity wearer) {
        return makesPiglinsNeutral;
    }

    @Nonnull
    @Override
    public Ingredient getRepairMaterial() {
        return getMaterial().getRepairIngredient();
    }

    @Override
    public int getProtection() {
        return getMaterial().getProtectionAmount(getSlotType());
    }

    @Override
    public float method_26353() {
        return getMaterial().getToughness();
    }

    public float getKnockbackResistance() {
        return getMaterial().getKnockbackResistance();
    }

    //@Override
    //public int getMaxDamage(ItemStack stack) {
    //    return material.getDurability(getSlotType());
    //}

    @Override
    public boolean isDamageable() {
        return material.getDurability(getSlotType()) > 0;
    }

    @Nonnull
    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(@Nonnull EquipmentSlot slot) {
        return slot == getSlotType() ? attributeCache.getAttributes() : ImmutableMultimap.of();
    }

    @Override
    public void addToBuilder(ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder) {
        UUID modifier = ArmorItemAccessor.getMODIFIERS()[getSlotType().getEntitySlotId()];
        builder.put(EntityAttributes.GENERIC_ARMOR, new EntityAttributeModifier(modifier, "Armor modifier", getProtection(), EntityAttributeModifier.Operation.ADDITION));
        builder.put(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, new EntityAttributeModifier(modifier, "Armor toughness", method_26353(), EntityAttributeModifier.Operation.ADDITION));
        builder.put(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, new EntityAttributeModifier(modifier, "Armor knockback resistance", getKnockbackResistance(), EntityAttributeModifier.Operation.ADDITION));
    }
}