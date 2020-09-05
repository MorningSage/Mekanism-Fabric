package mekanism.tools.mixin.accessors;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.ShieldEntityModel;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BuiltinModelItemRenderer.class)
@Environment(EnvType.CLIENT)
public interface BuiltinModelItemRendererAccessor {
    @Accessor("modelShield")
    ShieldEntityModel getModelShield();
}
