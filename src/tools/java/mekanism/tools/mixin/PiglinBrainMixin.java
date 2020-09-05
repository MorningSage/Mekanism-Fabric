package mekanism.tools.mixin;

import mekanism.tools.common.IHasPiglinSettings;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;

@Mixin(PiglinBrain.class)
public class PiglinBrainMixin {

    @Inject(
        at = @At(
            value = "INVOKE_ASSIGN",
            target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"
        ),
        method = "wearsGoldArmor",
        locals = LocalCapture.CAPTURE_FAILHARD,
        cancellable = true
    )
    private static void wearsGoldArmor(LivingEntity entity, CallbackInfoReturnable<Boolean> callbackInfo, Iterable<ItemStack> iterable, Iterator<ItemStack> var2, ItemStack itemStack, Item item) {
        if (item instanceof IHasPiglinSettings && ((IHasPiglinSettings) item).makesPiglinsNeutral(itemStack, entity)) {
            callbackInfo.setReturnValue(true);
        }
    }
}
