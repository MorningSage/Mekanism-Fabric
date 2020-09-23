package mekanism._helpers;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * General interface for any model that can be baked, superset of vanilla {@link net.minecraft.client.render.model.UnbakedModel}.
 * Models can be baked to different vertex formats and with different state.
 */
public interface IModelGeometry<T extends IModelGeometry<T>>
{
    default Collection<? extends IModelGeometryPart> getParts() {
        return Collections.emptyList();
    }

    default Optional<? extends IModelGeometryPart> getPart(String name) {
        return Optional.empty();
    }

    BakedModel bake(IModelConfiguration owner, ModelLoader bakery, Function<SpriteIdentifier, Sprite> spriteGetter, ModelBakeSettings modelTransform, ModelOverrideList overrides, Identifier modelLocation);

    Collection<SpriteIdentifier> getTextures(IModelConfiguration owner, Function<Identifier, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors);
}
