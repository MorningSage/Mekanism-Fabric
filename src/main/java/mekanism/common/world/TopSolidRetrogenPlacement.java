package mekanism.common.world;

import com.mojang.serialization.Codec;
import javax.annotation.Nonnull;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.decorator.NopeDecoratorConfig;
import net.minecraft.world.gen.decorator.TopSolidHeightmapDecorator;

public class TopSolidRetrogenPlacement extends TopSolidHeightmapDecorator {

    public TopSolidRetrogenPlacement(Codec<NopeDecoratorConfig> configFactory) {
        super(configFactory);
    }

    @Nonnull
    @Override
    protected Heightmap.Type getHeightmapType(@Nonnull NopeDecoratorConfig config) {
        //TODO - 1.16.2: Verify this
        return Heightmap.Type.OCEAN_FLOOR;
    }
}