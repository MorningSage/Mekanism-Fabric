package mekanism.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HandledScreen.class)
@Environment(EnvType.CLIENT)
public interface HandledScreenAccessor {
    @Accessor("x")
    int getGuiLeft();

    @Accessor("y")
    int getGuiTop();

    @Accessor("titleX")
    int getXSize();

    @Accessor("titleY")
    int getYSize();
}
