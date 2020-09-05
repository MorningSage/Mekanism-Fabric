package mekanism.common.lib.attribute;

import com.google.common.collect.ImmutableMultimap;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;

public interface IAttributeRefresher {

    void addToBuilder(ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder);
}