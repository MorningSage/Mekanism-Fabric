package mekanism.mixin.accessors;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(World.class)
public interface WorldAccessor {
    @Invoker("isValid")
    static boolean isValid(BlockPos pos) {
        throw new Error("Failed to invoke isValid!");
    }
}
