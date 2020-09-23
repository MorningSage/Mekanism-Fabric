package mekanism.common.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import java.util.Optional;
import java.util.function.Consumer;

public final class CapabilityUtils {

    @Nonnull
    public static <T> Optional<T> getCapability(@Nullable ICapabilityProvider provider, @Nullable Capability<T> cap, @Nullable Direction side) {
        if (provider == null || cap == null) {
            return Optional.empty();
        }
        return provider.getCapability(cap, side);
    }

    /**
     * Helper to add listeners that don't care about the data type to lazy optionals. This makes it so when we have {@code LazyOptional<?>} we can add a listener to it
     * without having to deal with the fact that one is "capture of ?" and the listener is "?".
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void addListener(@Nonnull Optional<?> lazyOptional, @Nonnull Consumer listener) {
        lazyOptional.addListener(listener);
    }
}