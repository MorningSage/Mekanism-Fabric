package mekanism.tools.mixin.accessors;

import net.minecraft.block.Material;
import net.minecraft.item.AxeItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(AxeItem.class)
public interface AxeItemAccessor {
    @Accessor("field_23139")
    Set<Material> getField_23139();
}
