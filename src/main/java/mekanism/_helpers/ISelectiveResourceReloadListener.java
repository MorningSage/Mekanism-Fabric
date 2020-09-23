package mekanism._helpers;

import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloadListener;

import java.util.function.Predicate;

public interface ISelectiveResourceReloadListener extends SynchronousResourceReloadListener
{
    @Override
    default void apply(ResourceManager resourceManager)
    {
        // For compatibility, call the selective version from the non-selective function
        onResourceManagerReload(resourceManager, SelectiveReloadStateHandler.INSTANCE.get());
    }

    /**
     * A version of onResourceManager that selectively chooses {@link IResourceType}s
     * to reload.
     * When using this, the given predicate should be called to ensure the relevant resources should
     * be reloaded at this time.
     *
     * @param resourceManager the resource manager being reloaded
     * @param resourcePredicate predicate to test whether any given resource type should be reloaded
     */
    void onResourceManagerReload(ResourceManager resourceManager, Predicate<IResourceType> resourcePredicate);
}
