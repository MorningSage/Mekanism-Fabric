package mekanism.common.registration.impl;

import mekanism.common.registration.WrappedDeferredRegister;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SoundEventDeferredRegister extends WrappedDeferredRegister<SoundEvent> {

    //We need to store the modid because the deferred register doesn't let you get the modid back out
    private final String modid;

    public SoundEventDeferredRegister(String modid) {
        super(modid, Registry.SOUND_EVENT);
        this.modid = modid;
    }

    public SoundEventRegistryObject<SoundEvent> register(String name) {
        return register(name, () -> new SoundEvent(new Identifier(modid, name)), SoundEventRegistryObject::new);
    }
}