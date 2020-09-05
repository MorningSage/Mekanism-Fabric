package mekanism.tools.mixin;

import mekanism.tools.common.item.ItemMekanismShield;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ShieldItem;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    private boolean redirectPassed = false;

    @Redirect(
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/item/Items;SHIELD:Lnet/minecraft/item/Item;"
        ),
        method = "damageShield"
    )
    protected void damageShield(float amount) {
        redirectPassed = true;

        if (this.get.getItem() instanceof ItemMekanismShield) {
            return
        }
    }
}
