package mekanism.common;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nonnull;
import mekanism.common.network.PacketClearRecipeCache;
import mekanism.common.recipe.MekanismRecipeType;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.profiler.Profiler;

public class ReloadListener implements ResourceReloadListener {

    @Nonnull
    @Override
    public CompletableFuture<Void> reload(@Nonnull Synchronizer stage, @Nonnull ResourceManager resourceManager, @Nonnull Profiler preparationsProfiler,
          @Nonnull Profiler reloadProfiler, @Nonnull Executor backgroundExecutor, @Nonnull Executor gameExecutor) {
        return CompletableFuture.runAsync(() -> {
            MekanismRecipeType.clearCache();
            Mekanism.packetHandler.sendToAllIfLoaded(new PacketClearRecipeCache());
            CommonWorldTickHandler.flushTagAndRecipeCaches = true;
        }, gameExecutor).thenCompose(stage::whenPrepared);
    }
}