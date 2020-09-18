package mekanism.mixin;

import mekanism._helpers.EntityHasPickableItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

import javax.annotation.Nullable;

@Mixin(MinecraftClient.class)
@Environment(EnvType.CLIENT)
public class MinecraftClientMixin {
    @Shadow @Nullable public HitResult crosshairTarget;

    @ModifyVariable(
        at = @At("STORE"),
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/util/hit/EntityHitResult;getEntity()Lnet/minecraft/entity/Entity;"
            )
        ),
        method = "doItemPick"
    )
    public ItemStack doItemPickEntity(ItemStack itemStack12) {
        if (this.crosshairTarget != null) {
            if (this.crosshairTarget.getType() == HitResult.Type.ENTITY && itemStack12.getItem() instanceof EntityHasPickableItem) {
                return ((EntityHasPickableItem) itemStack12.getItem()).getPickedResult(this.crosshairTarget);
            }
        }

        return itemStack12;
    }
}
