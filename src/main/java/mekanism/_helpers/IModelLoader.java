package mekanism._helpers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.resource.ResourceManager;

import java.util.function.Predicate;

public interface IModelLoader<T extends IModelGeometry<T>> extends ISelectiveResourceReloadListener
{

    //@Override
    //default IResourceType getResourceType()
    //{
    //    return VanillaResourceType.MODELS;
    //}

    @Override
    void apply(ResourceManager resourceManager);

    @Override
    default void onResourceManagerReload(ResourceManager resourceManager, Predicate<IResourceType> resourcePredicate)
    {
        if (resourcePredicate.test(getResourceType()))
        {
            apply(resourceManager);
        }
    }

    T read(JsonDeserializationContext deserializationContext, JsonObject modelContents);
}
