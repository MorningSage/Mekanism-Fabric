package mekanism.tools.mixin;

import mekanism.tools.common.IHasPreferredEquipmentSlot;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public class MobEntityMixin {

    @Inject(
        at = @At("HEAD"),
        method = "getPreferredEquipmentSlot",
        cancellable = true
    )
    private static void getPreferredEquipmentSlot(ItemStack stack, CallbackInfoReturnable<EquipmentSlot> callbackInfo) {
        if (stack.getItem() instanceof IHasPreferredEquipmentSlot) {
            callbackInfo.setReturnValue(((IHasPreferredEquipmentSlot) stack.getItem()).getPreferredEquipmentSlot(stack, null));
        }
    }
}
