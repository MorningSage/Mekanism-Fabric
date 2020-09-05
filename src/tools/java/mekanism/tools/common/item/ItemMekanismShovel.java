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

public class ItemMekanismShovel extends ShovelItem implements IHasRepairType, IAttributeRefresher {

    private final MaterialCreator material;
    private final AttributeCache attributeCache;

    public ItemMekanismShovel(MaterialCreator material, Item.Settings properties) {
        super(material, material.getShovelDamage(), material.getShovelAtkSpeed(), properties);
        this.material = material;
        this.attributeCache = new AttributeCache(this, material.attackDamage, material.shovelDamage, material.shovelAtkSpeed);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<Text> tooltip, @Nonnull TooltipContext flag) {
        tooltip.add(ToolsLang.HP.translate(stack.getMaxDamage() - stack.getDamage()));
    }

    public float getAttackDamage() {
        return material.getShovelDamage() + getMaterial().getAttackDamage();
    }

    public int getHarvestLevel() {
        return getMaterial().getMiningLevel();
    }

    /**
     * {@inheritDoc}
     *
     * @implNote Wrap {@link MiningToolItem#getMiningSpeedMultiplier(ItemStack, BlockState)} to return our efficiency level
     */
    @Override
    public float getMiningSpeedMultiplier(@Nonnull ItemStack stack, BlockState state) {
        return /*getToolTypes(stack).stream().anyMatch(state::isToolEffective) ||*/ ((MiningToolItemAccessor) this).getEffectiveBlocks().contains(state.getBlock()) ? getMaterial().getMiningSpeedMultiplier() : 1;
    }

    /**
     * {@inheritDoc}
     *
     * @implNote Patches {@link ShovelItem} to return true for more than just snow
     */
    @Override
    public boolean isEffectiveOn(BlockState state) {
        // ToDo... this is obviously not right
        return /*state.getHarvestTool() == ToolType.SHOVEL ? getHarvestLevel() >= state.getHarvestLevel() :*/ ((MiningToolItemAccessor) this).getEffectiveBlocks().contains(state.getBlock()) || super.isEffectiveOn(state);
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
    //    return tool == ToolType.SHOVEL ? getHarvestLevel() : super.getHarvestLevel(stack, tool, player, blockState);
    //}

    @Nonnull
    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(@Nonnull EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? attributeCache.getAttributes() : ImmutableMultimap.of();
    }

    @Override
    public void addToBuilder(ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder) {
        builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Tool modifier", getAttackDamage(), EntityAttributeModifier.Operation.ADDITION));
        builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Tool modifier", material.getShovelAtkSpeed(), EntityAttributeModifier.Operation.ADDITION));
    }
}