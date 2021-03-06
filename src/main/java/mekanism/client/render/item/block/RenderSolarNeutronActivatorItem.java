package mekanism.client.render.item.block;

import net.minecraft.client.util.math.MatrixStack;
import javax.annotation.Nonnull;
import mekanism.client.model.ModelSolarNeutronActivator;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;

public class RenderSolarNeutronActivatorItem extends ItemStackTileEntityRenderer {

    private static final ModelSolarNeutronActivator solarNeutronActivator = new ModelSolarNeutronActivator();

    @Override
    public void func_239207_a_(@Nonnull ItemStack stack, @Nonnull TransformType transformType, @Nonnull MatrixStack matrix, @Nonnull IRenderTypeBuffer renderer, int light, int overlayLight) {
        matrix.push();
        matrix.translate(0.5, 0.5, 0.5);
        matrix.rotate(Vector3f.ZP.rotationDegrees(180));
        matrix.translate(0, -0.55, 0);
        solarNeutronActivator.render(matrix, renderer, light, overlayLight, stack.hasEffect());
        matrix.pop();
    }
}