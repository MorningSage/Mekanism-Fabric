package mekanism.common.registration.impl;

import javax.annotation.Nonnull;
import mekanism.common.registration.WrappedRegistryObject;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraftforge.fml.RegistryObject;

public class ContainerTypeRegistryObject<CONTAINER extends ScreenHandler> extends WrappedRegistryObject<ScreenHandlerType<CONTAINER>> {

    public ContainerTypeRegistryObject(RegistryObject<ScreenHandlerType<CONTAINER>> registryObject) {
        super(registryObject);
    }

    @Nonnull
    public ScreenHandlerType<CONTAINER> getContainerType() {
        return get();
    }

    //Internal use only overwrite the registry object
    ContainerTypeRegistryObject<CONTAINER> setRegistryObject(RegistryObject<ScreenHandlerType<CONTAINER>> registryObject) {
        this.registryObject = registryObject;
        return this;
    }
}