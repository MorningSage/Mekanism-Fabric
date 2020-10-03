package mekanism.mixin.accessors;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Invoker("onStatusEffectUpgraded")
    void onStatusEffectUpgraded(StatusEffectInstance effect, boolean reapplyEffect);
}
