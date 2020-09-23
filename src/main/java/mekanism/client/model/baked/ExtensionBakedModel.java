package mekanism.client.model.baked;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import mekanism._helpers.IModelData;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.function.BiPredicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.client.render.lib.QuadTransformation;
import mekanism.client.render.lib.QuadUtils;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraftforge.client.MinecraftForgeClient;

public class ExtensionBakedModel<T> implements BakedModel {

    protected final BakedModel original;

    private final LoadingCache<QuadsKey<T>, List<BakedQuad>> cache = CacheBuilder.newBuilder().build(new CacheLoader<QuadsKey<T>, List<BakedQuad>>() {
        @Override
        public List<BakedQuad> load(@Nonnull QuadsKey<T> key) {
            return createQuads(key);
        }
    });
    private final Map<List<Pair<BakedModel, RenderLayer>>, List<Pair<BakedModel, RenderLayer>>> cachedLayerRedirects = new Object2ObjectOpenHashMap<>();

    public ExtensionBakedModel(BakedModel original) {
        this.original = original;
    }

    @Nonnull
    @Override
    @Deprecated
    public List<BakedQuad> getQuads(BlockState state, Direction side, @Nonnull Random rand) {
        return original.getQuads(state, side, rand);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return original.useAmbientOcclusion();
    }

    @Override
    public boolean hasDepth() {
        return original.hasDepth();
    }

    @Override
    public boolean isSideLit() {
        return original.isSideLit();
    }

    @Override
    public boolean isBuiltin() {
        return original.isBuiltin();
    }

    public Sprite getSprite() {
        return original.getSprite();
    }

    @Nonnull
    @Override
    public ModelOverrideList getOverrides() {
        return original.getOverrides();
    }

    @Override
    public BakedModel handlePerspective(ModelTransformation.Mode cameraTransformType, MatrixStack mat) {
        return original.handlePerspective(cameraTransformType, mat);
    }

    @Override
    public ModelTransformation getTransformation() {
        return original.getTransformation();
    }

    @Override
    public boolean doesHandlePerspectives() {
        return original.doesHandlePerspectives();
    }

    protected QuadsKey<T> createKey(QuadsKey<T> key, IModelData data) {
        return key;
    }

    protected List<BakedQuad> createQuads(QuadsKey<T> key) {
        List<BakedQuad> ret = key.getQuads();
        if (key.getTransformation() != null) {
            ret = QuadUtils.transformBakedQuads(ret, key.getTransformation());
        }
        return ret;
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
        List<BakedQuad> quads = original.getQuads(state, side, rand, data);
        QuadsKey<T> key = createKey(new QuadsKey<>(state, side, rand, MinecraftForgeClient.getRenderLayer(), quads), data);
        if (key == null) {
            return quads;
        }
        return cache.getUnchecked(key);
    }



    @Override
    public boolean isLayered() {
        return original.isLayered();
    }

    @Nonnull
    @Override
    public List<Pair<BakedModel, RenderLayer>> getLayerModels(ItemStack stack, boolean fabulous) {
        //Cache the remappings so then the inner wrapped ones can cache their quads
        return cachedLayerRedirects.computeIfAbsent(original.getLayerModels(stack, fabulous), originalLayerModels -> {
            List<Pair<BakedModel, RenderLayer>> layerModels = new ArrayList<>();
            for (Pair<BakedModel, RenderLayer> layerModel : originalLayerModels) {
                layerModels.add(Pair.of(wrapModel(layerModel.getFirst()), layerModel.getSecond()));
            }
            return layerModels;
        });
    }

    protected ExtensionBakedModel<T> wrapModel(BakedModel model) {
        return new ExtensionBakedModel<>(model);
    }

    public static class LightedBakedModel extends TransformedBakedModel<Void> {

        public LightedBakedModel(BakedModel original) {
            super(original, QuadTransformation.filtered_fullbright);
        }

        @Override
        protected LightedBakedModel wrapModel(BakedModel model) {
            return new LightedBakedModel(model);
        }
    }

    public static class TransformedBakedModel<T> extends ExtensionBakedModel<T> {

        private final QuadTransformation transform;

        public TransformedBakedModel(BakedModel original, QuadTransformation transform) {
            super(original);
            this.transform = transform;
        }

        @Nonnull
        @Override
        @Deprecated
        public List<BakedQuad> getQuads(BlockState state, Direction side, @Nonnull Random rand) {
            return QuadUtils.transformBakedQuads(original.getQuads(state, side, rand), transform);
        }

        @Override
        public BakedModel handlePerspective(ModelTransformation.Mode cameraTransformType, MatrixStack mat) {
            // have the original model apply any perspective transforms onto the MatrixStack
            original.handlePerspective(cameraTransformType, mat);
            // return this model, as we want to draw the item variant quads ourselves
            return this;
        }

        @Override
        protected QuadsKey<T> createKey(QuadsKey<T> key, IModelData data) {
            return key.transform(transform);
        }

        @Override
        protected TransformedBakedModel<T> wrapModel(BakedModel model) {
            return new TransformedBakedModel<>(model, transform);
        }
    }

    public static class QuadsKey<T> {

        private final BlockState state;
        private final Direction side;
        private final Random random;
        private final RenderLayer layer;
        private final List<BakedQuad> quads;
        private QuadTransformation transformation;

        private T data;
        private int dataHash;
        private BiPredicate<T, T> equality;

        public QuadsKey(BlockState state, Direction side, Random random, RenderLayer layer, List<BakedQuad> quads) {
            this.state = state;
            this.side = side;
            this.random = random;
            this.layer = layer;
            this.quads = quads;
        }

        public QuadsKey<T> transform(QuadTransformation transformation) {
            this.transformation = transformation;
            return this;
        }

        public QuadsKey<T> data(T data, int dataHash, BiPredicate<T, T> equality) {
            this.data = data;
            this.dataHash = dataHash;
            this.equality = equality;
            return this;
        }

        public BlockState getBlockState() {
            return state;
        }

        public Direction getSide() {
            return side;
        }

        public Random getRandom() {
            return random;
        }

        public RenderLayer getLayer() {
            return layer;
        }

        public List<BakedQuad> getQuads() {
            return quads;
        }

        public QuadTransformation getTransformation() {
            return transformation;
        }

        public T getData() {
            return data;
        }

        @Override
        public int hashCode() {
            return Objects.hash(state, side, layer, transformation, dataHash);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof QuadsKey)) {
                return false;
            }
            QuadsKey<?> other = (QuadsKey<?>) obj;
            if (side != other.side || !state.equals(other.state) || layer != other.layer) {
                return false;
            }
            if (transformation != null && !transformation.equals(other.transformation)) {
                return false;
            }
            return data == null || equality.test(data, (T) other.getData());
        }
    }
}
