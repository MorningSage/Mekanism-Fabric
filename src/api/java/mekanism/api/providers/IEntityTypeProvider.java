package mekanism.api.providers;

import net.minecraft.entity.EntityType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nonnull;

public interface IEntityTypeProvider extends IBaseProvider {

    @Nonnull
    EntityType<?> getEntityType();

    @Override
    default Identifier getRegistryName() {
        return Registry.ENTITY_TYPE.getId(getEntityType());
    }

    @Override
    default Text getTextComponent() {
        return getEntityType().getName();
    }

    @Override
    default String getTranslationKey() {
        return getEntityType().getTranslationKey();
    }
}