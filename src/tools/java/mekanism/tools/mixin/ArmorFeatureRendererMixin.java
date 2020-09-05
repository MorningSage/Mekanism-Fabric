package mekanism.tools.mixin;

import mekanism.tools.common.IHasArmorModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;

import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;


@Mixin(ArmorFeatureRenderer.class)
@Environment(EnvType.CLIENT)
public abstract class ArmorFeatureRendererMixin<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {
    public ArmorFeatureRendererMixin(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @ModifyVariable(
        method = "renderArmor",
        at = @At("HEAD")
    )
    private A renderArmor(A bipedEntityModel, MatrixStack matrices, VertexConsumerProvider vertexConsumers, T livingEntity, EquipmentSlot equipmentSlot, int i) {
        ItemStack itemStack = livingEntity.getEquippedStack(equipmentSlot);

        if (itemStack.getItem() instanceof IHasArmorModel) {
            A customModel = ((IHasArmorModel) itemStack.getItem()).getArmorModel(livingEntity, itemStack, equipmentSlot, bipedEntityModel);
            if (customModel != null) return customModel;
        }

        return bipedEntityModel;
    }
}
