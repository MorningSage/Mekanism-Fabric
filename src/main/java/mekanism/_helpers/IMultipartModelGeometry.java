package mekanism._helpers;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public interface IMultipartModelGeometry<T extends IMultipartModelGeometry<T>> extends ISimpleModelGeometry<T> {
    @Override
    Collection<? extends IModelGeometryPart> getParts();

    Optional<? extends IModelGeometryPart> getPart(String name);

    @Override
    default void addQuads(IModelConfiguration owner, IModelBuilder<?> modelBuilder, ModelLoader bakery, Function<SpriteIdentifier, Sprite> spriteGetter, ModelBakeSettings modelTransform, Identifier modelLocation)
    {
        getParts().stream().filter(part -> owner.getPartVisibility(part))
            .forEach(part -> part.addQuads(owner, modelBuilder, bakery, spriteGetter, modelTransform, modelLocation));
    }

    @Override
    default Collection<SpriteIdentifier> getTextures(IModelConfiguration owner, Function<Identifier, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors)
    {
        Set<SpriteIdentifier> combined = Sets.newHashSet();
        for (IModelGeometryPart part : getParts())
            combined.addAll(part.getTextures(owner, modelGetter, missingTextureErrors));
        return combined;
    }
}
