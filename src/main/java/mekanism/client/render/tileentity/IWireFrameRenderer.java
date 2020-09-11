package mekanism.client.render.tileentity;

import net.minecraft.client.util.math.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.tileentity.TileEntity;

public interface IWireFrameRenderer {

    void renderWireFrame(TileEntity tile, float partialTick, MatrixStack matrix, IVertexBuilder buffer, float red, float green, float blue, float alpha);
}