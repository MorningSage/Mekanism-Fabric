package mekanism.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

public class MekanismRenderType extends RenderLayer {

    private static final RenderPhase.Alpha CUBOID_ALPHA = new RenderPhase.Alpha(0.1F);
    private static final RenderPhase.Transparency BLADE_TRANSPARENCY = new RenderPhase.Transparency("blade_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
    }, RenderSystem::disableBlend);

    public static final RenderLayer MEK_LIGHTNING = of("mek_lightning", VertexFormats.POSITION_COLOR, GL11.GL_QUADS, 256,
          false, true, RenderLayer.MultiPhaseParameters.builder()
                .writeMaskState(RenderPhase.ALL_MASK)
                .transparency(LIGHTNING_TRANSPARENCY)
                .shadeModel(RenderPhase.SMOOTH_SHADE_MODEL)
                .build(false)
    );

    //Ignored
    private MekanismRenderType(String name, VertexFormat format, int drawMode, int bufferSize, boolean useDelegate, boolean needsSorting, Runnable runnablePre, Runnable runnablePost) {
        super(name, format, drawMode, bufferSize, useDelegate, needsSorting, runnablePre, runnablePost);
    }

    public static RenderLayer mekStandard(Identifier resourceLocation) {
        RenderLayer.MultiPhaseParameters state = RenderLayer.MultiPhaseParameters.builder()
              .texture(new RenderPhase.Texture(resourceLocation, false, false))//Texture state
              .shadeModel(RenderPhase.SMOOTH_SHADE_MODEL)//shadeModel(GL11.GL_SMOOTH)
              .alpha(ZERO_ALPHA)//disableAlphaTest
              .transparency(TRANSLUCENT_TRANSPARENCY)//enableBlend/blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA)
              .build(true);
        return of("mek_standard", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, GL11.GL_QUADS, 256, true, false, state);
    }

    public static RenderLayer bladeRender(Identifier resourceLocation) {
        RenderLayer.MultiPhaseParameters state = RenderLayer.MultiPhaseParameters.builder()
              .texture(new RenderPhase.Texture(resourceLocation, false, false))//Texture state
              .shadeModel(RenderPhase.SMOOTH_SHADE_MODEL)
              .transparency(BLADE_TRANSPARENCY)
              .build(true);
        return of("mek_blade", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, GL11.GL_QUADS, 256, true, false, state);
    }

    public static RenderLayer renderFlame(Identifier resourceLocation) {
        RenderLayer.MultiPhaseParameters state = RenderLayer.MultiPhaseParameters.builder()
              .texture(new RenderPhase.Texture(resourceLocation, false, false))//Texture state
              .shadeModel(RenderPhase.SMOOTH_SHADE_MODEL)//shadeModel(GL11.GL_SMOOTH)
              .alpha(ZERO_ALPHA)//disableAlphaTest
              .transparency(TRANSLUCENT_TRANSPARENCY)//enableBlend/blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA)
              .lightmap(DISABLE_LIGHTMAP)//disableLighting
              .build(true);
        return of("mek_flame", VertexFormats.POSITION_COLOR_TEXTURE, GL11.GL_QUADS, 256, true, false, state);
    }

    public static RenderLayer getMekaSuit() {
        RenderLayer.MultiPhaseParameters state = RenderLayer.MultiPhaseParameters.builder()
              .texture(BLOCK_ATLAS_TEXTURE)
              .diffuseLighting(ENABLE_DIFFUSE_LIGHTING)
              .shadeModel(RenderPhase.SMOOTH_SHADE_MODEL)
              .alpha(HALF_ALPHA)
              .lightmap(ENABLE_LIGHTMAP)
              .build(true);
        return of("mekasuit", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, 7, 131_072, true, true, state);
    }

    public static RenderLayer renderSPS(Identifier resourceLocation) {
        RenderLayer.MultiPhaseParameters state = RenderLayer.MultiPhaseParameters.builder()
              .texture(new RenderPhase.Texture(resourceLocation, false, false))//Texture state
              .shadeModel(RenderPhase.SMOOTH_SHADE_MODEL)//shadeModel(GL11.GL_SMOOTH)
              .transparency(LIGHTNING_TRANSPARENCY)//enableBlend/blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA)
              .lightmap(DISABLE_LIGHTMAP)
              .alpha(CUBOID_ALPHA)
              .build(true);
        return of("mek_sps", VertexFormats.POSITION_COLOR_TEXTURE, GL11.GL_QUADS, 256, true, false, state);
    }
}