package mekanism.common.registration.impl;

import javax.annotation.Nonnull;
import mekanism.common.registration.WrappedRegistryObject;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraftforge.fml.RegistryObject;

public class PlacementRegistryObject<CONFIG extends DecoratorConfig, PLACEMENT extends Decorator<CONFIG>> extends WrappedRegistryObject<PLACEMENT> {

    public PlacementRegistryObject(RegistryObject<PLACEMENT> registryObject) {
        super(registryObject);
    }

    @Nonnull
    public PLACEMENT getPlacement() {
        return get();
    }

    @Nonnull
    public ConfiguredDecorator<CONFIG> getConfigured(CONFIG placementConfig) {
        return getPlacement().configure(placementConfig);
    }
}