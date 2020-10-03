package mekanism.mixin.accessors;

import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerPlayNetworkHandler.class)
public interface ServerPlayNetworkHandlerAccessor {

    @Accessor("floatingTicks")
    void setFloatingTicks(int floatingTicks);
}
