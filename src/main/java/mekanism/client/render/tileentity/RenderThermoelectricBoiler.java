package mekanism.client.render.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import javax.annotation.ParametersAreNonnullByDefault;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.MekanismRenderer.Model3D;
import mekanism.client.render.ModelRenderer;
import mekanism.client.render.data.FluidRenderData;
import mekanism.client.render.data.GasRenderData;
import mekanism.common.base.ProfilerConstants;
import mekanism.common.tile.multiblock.TileEntityBoilerCasing;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.math.BlockPos;

@ParametersAreNonnullByDefault
public class RenderThermoelectricBoiler extends MekanismTileEntityRenderer<TileEntityBoilerCasing> {

    public RenderThermoelectricBoiler(TileEntityRendererDispatcher renderer) {
        super(renderer);
    }

    @Override
    protected void render(TileEntityBoilerCasing tile, float partialTick, MatrixStack matrix, IRenderTypeBuffer renderer, int light, int overlayLight, IProfiler profiler) {
        if (tile.isMaster && tile.getMultiblock().isFormed() && tile.getMultiblock().renderLocation != null && tile.getMultiblock().upperRenderLocation != null) {
            BlockPos pos = tile.getPos();
            IVertexBuilder buffer = null;
            if (!tile.getMultiblock().waterTank.isEmpty()) {
                int height = tile.getMultiblock().upperRenderLocation.getY() - 1 - tile.getMultiblock().renderLocation.getY();
                if (height >= 1) {
                    FluidRenderData data = new FluidRenderData(tile.getMultiblock().waterTank.getFluid());
                    data.location = tile.getMultiblock().renderLocation;
                    data.height = height;
                    data.length = tile.getMultiblock().length();
                    data.width = tile.getMultiblock().width();
                    int glow = data.calculateGlowLight(LightTexture.packLight(0, 15));
                    matrix.push();
                    matrix.translate(data.location.getX() - pos.getX(), data.location.getY() - pos.getY(), data.location.getZ() - pos.getZ());
                    buffer = renderer.getBuffer(Atlases.getTranslucentCullBlockType());
                    MekanismRenderer.renderObject(ModelRenderer.getModel(data, tile.getMultiblock().prevWaterScale), matrix, buffer,
                          data.getColorARGB(tile.getMultiblock().prevWaterScale), glow, overlayLight);
                    matrix.pop();

                    MekanismRenderer.renderValves(matrix, buffer, tile.getMultiblock().valves, data, pos, glow, overlayLight);
                }
            }
            if (!tile.getMultiblock().steamTank.isEmpty()) {
                int height = tile.getMultiblock().renderLocation.getY() + tile.getMultiblock().height() - 2 - tile.getMultiblock().upperRenderLocation.getY();
                if (height >= 1) {
                    GasRenderData data = new GasRenderData(tile.getMultiblock().steamTank.getStack());
                    data.location = tile.getMultiblock().upperRenderLocation;
                    data.height = height;
                    data.length = tile.getMultiblock().length();
                    data.width = tile.getMultiblock().width();
                    if (buffer == null) {
                        buffer = renderer.getBuffer(Atlases.getTranslucentCullBlockType());
                    }
                    int glow = data.calculateGlowLight(LightTexture.packLight(0, 15));
                    matrix.push();
                    matrix.translate(data.location.getX() - pos.getX(), data.location.getY() - pos.getY(), data.location.getZ() - pos.getZ());
                    Model3D gasModel = ModelRenderer.getModel(data, 1);
                    MekanismRenderer.renderObject(gasModel, matrix, buffer, data.getColorARGB(tile.getMultiblock().prevSteamScale), glow, overlayLight);
                    matrix.pop();
                }
            }
        }
    }

    @Override
    protected String getProfilerSection() {
        return ProfilerConstants.THERMOELECTRIC_BOILER;
    }

    @Override
    public boolean isGlobalRenderer(TileEntityBoilerCasing tile) {
        return tile.isMaster && tile.getMultiblock().isFormed() && tile.getMultiblock().renderLocation != null && tile.getMultiblock().upperRenderLocation != null;
    }
}