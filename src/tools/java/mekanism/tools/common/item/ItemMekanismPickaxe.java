package mekanism.tools.common.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import mekanism.common.lib.attribute.AttributeCache;
import mekanism.common.lib.attribute.IAttributeRefresher;
import mekanism.tools.common.IHasRepairType;
import mekanism.tools.common.ToolsLang;
import mekanism.tools.common.material.MaterialCreator;
import mekanism.tools.mixin.accessors.MiningToolItemAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.*;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemMekanismPickaxe extends PickaxeItem implements IHasRepairType, IAttributeRefresher {

    private final MaterialCreator material;
    private final AttributeCache attributeCache;

    public ItemMekanismPickaxe(MaterialCreator material, Item.Settings properties) {
        super(material, material.getPickaxeDamage(), material.getPickaxeAtkSpeed(), properties);
        this.material = material;
        this.attributeCache = new AttributeCache(this, material.attackDamage, material.pickaxeDamage, material.pickaxeAtkSpeed);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<Text> tooltip, @Nonnull TooltipContext flag) {
        tooltip.add(ToolsLang.HP.translate(stack.getMaxDamage() - stack.getDamage()));
    }

    public float getAttackDamage() {
        return material.getPickaxeDamage() + getMaterial().getAttackDamage();
    }

    /**
     * {@inheritDoc}
     *
     * @implNote Wrap {@link PickaxeItem#getMiningSpeedMultiplier(ItemStack, BlockState)} and  {@link MiningToolItem#getMiningSpeedMultiplier(ItemStack, BlockState)} to return
     * our efficiency level
     */
    @Override
    public float getMiningSpeedMultiplier(@Nonnull ItemStack stack, BlockState state) {
        Material material = state.getMaterial();
        if (material == Material.METAL || material == Material.REPAIR_STATION || material == Material.STONE || /*getToolTypes(stack).stream().anyMatch(state::isToolEffective) ||*/
            ((MiningToolItemAccessor) this).getEffectiveBlocks().contains(state.getBlock())) {
            return getMaterial().getMiningSpeedMultiplier();
        }
        return 1;
    }

    @Nonnull
    @Override
    public Ingredient getRepairMaterial() {
        return getMaterial().getRepairIngredient();
    }

    //@Override
    //public int getMaxDamage(ItemStack stack) {
    //    return getMaterial().getDurability();
    //}

    @Override
    public boolean isDamageable() {
        return getMaterial().getDurability() > 0;
    }

    //@Override
    //public int getHarvestLevel(@Nonnull ItemStack stack, @Nonnull ToolType tool, @Nullable PlayerEntity player, @Nullable BlockState blockState) {
    //    return tool == ToolType.PICKAXE ? getMaterial().getHarvestLevel() : super.getHarvestLevel(stack, tool, player, blockState);
    //}

    @Nonnull
    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(@Nonnull EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? attributeCache.getAttributes() : ImmutableMultimap.of();
    }

    @Override
    public void addToBuilder(ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder) {
        builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Tool modifier", getAttackDamage(), EntityAttributeModifier.Operation.ADDITION));
        builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Tool modifier", material.getPickaxeAtkSpeed(), EntityAttributeModifier.Operation.ADDITION));
    }
}