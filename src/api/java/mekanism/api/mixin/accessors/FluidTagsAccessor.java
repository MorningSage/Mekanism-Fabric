package mekanism.api.mixin.accessors;

import net.minecraft.fluid.Fluid;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FluidTags.class)
public interface FluidTagsAccessor {
    @Invoker
    static Tag.Identified<Fluid> register(String id) {
        throw new Error("Failed to register fluid tag");
    }
}
