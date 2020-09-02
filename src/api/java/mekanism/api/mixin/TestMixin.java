package mekanism.api.mixin;

import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.AbstractList;

@Mixin(PotionItem.class)
public abstract class TestMixin {
    @Redirect(
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/collection/DefaultedList;add(Ljava/lang/Object;)Z"
        ),
        method = "appendStacks"
    )
    public boolean appendStacks(DefaultedList list, Object object, ItemGroup group, DefaultedList<ItemStack> stacks) {
        return list.add(object);
    }
}
