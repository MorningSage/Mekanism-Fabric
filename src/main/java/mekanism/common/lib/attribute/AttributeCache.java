package mekanism.common.lib.attribute;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.Multimap;
import mekanism.common.config.value.CachedPrimitiveValue;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;

public class AttributeCache {

    private final IAttributeRefresher attributeRefresher;
    private Multimap<EntityAttribute, EntityAttributeModifier> attributes;

    public AttributeCache(IAttributeRefresher attributeRefresher, CachedPrimitiveValue<?>... configValues) {
        this.attributeRefresher = attributeRefresher;
        Runnable refreshListener = this::refreshAttributes;
        for (CachedPrimitiveValue<?> configValue : configValues) {
            configValue.addInvalidationListener(refreshListener);
        }
        refreshAttributes();
    }

    private void refreshAttributes() {
        Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        attributeRefresher.addToBuilder(builder);
        this.attributes = builder.build();
    }

    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributes() {
        return attributes;
    }
}