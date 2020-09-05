package mekanism.client.render.obj;

import net.minecraft.client.renderer.model.*;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.ModelLoaderRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ContentsModelConfiguration implements IModelConfiguration {

    @Nullable
    @Override
    public IUnbakedModel getOwnerModel() {
        return null;
    }

    @Nonnull
    @Override
    public String getModelName() {
        return "transmitter_contents";
    }

    @Override
    public boolean isTexturePresent(@Nonnull String name) {
        return false;
    }

    @Nonnull
    @Override
    public RenderMaterial resolveTexture(@Nonnull String name) {
        return ModelLoaderRegistry.blockMaterial(name);
    }

    @Override
    public boolean isShadedInGui() {
        return false;
    }

    @Override
    public boolean isSideLit() {
        return false;
    }

    @Override
    public boolean useSmoothLighting() {
        return false;
    }

    @Nonnull
    @Override
    @Deprecated
    public ItemCameraTransforms getCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }

    @Nonnull
    @Override
    public IModelTransform getCombinedTransform() {
        return ModelRotation.X0_Y0;
    }
}