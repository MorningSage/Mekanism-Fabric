package mekanism.common.world;

import com.google.common.collect.ImmutableList;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import mekanism.api.providers.IBlockProvider;
import mekanism.common.config.MekanismConfig;
import mekanism.common.config.WorldConfig.OreConfig;
import mekanism.common.config.WorldConfig.SaltConfig;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismFeatures;
import mekanism.common.registries.MekanismPlacements;
import mekanism.common.resource.OreType;
import mekanism.common.util.EnumUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.UniformIntDistribution;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.decorator.NopeDecoratorConfig;
import net.minecraft.world.gen.feature.*;

public class GenHandler {

    private static final Map<OreType, ConfiguredFeature<?, ?>> ORES = new EnumMap<>(OreType.class);
    private static final Map<OreType, ConfiguredFeature<?, ?>> ORE_RETROGENS = new EnumMap<>(OreType.class);

    private static ConfiguredFeature<?, ?> SALT_FEATURE;
    private static ConfiguredFeature<?, ?> SALT_RETROGEN_FEATURE;

    public static void setupWorldGeneration() {
        for (OreType type : EnumUtils.ORE_TYPES) {
            ORES.put(type, getOreFeature(MekanismBlocks.ORES.get(type), MekanismConfig.world.ores.get(type), Feature.ORE));
        }
        SALT_FEATURE = getSaltFeature(MekanismBlocks.SALT_BLOCK, MekanismConfig.world.salt, ConfiguredFeatures.Decorators.TOP_SOLID_HEIGHTMAP);
        //Retrogen features
        if (MekanismConfig.world.enableRegeneration.get()) {
            for (OreType type : EnumUtils.ORE_TYPES) {
                ORE_RETROGENS.put(type, getOreFeature(MekanismBlocks.ORES.get(type), MekanismConfig.world.ores.get(type), MekanismFeatures.ORE_RETROGEN.getFeature()));
            }
            SALT_RETROGEN_FEATURE = getSaltFeature(MekanismBlocks.SALT_BLOCK, MekanismConfig.world.salt,
                MekanismPlacements.TOP_SOLID_RETROGEN.getConfigured(DecoratorConfig.DEFAULT));
        }
    }

    public static void onBiomeLoad(BiomeLoadingEvent event) {
        if (isValidBiome(event.getCategory())) {
            BiomeGenerationSettingsBuilder generation = event.getGeneration();
            //Add ores
            for (ConfiguredFeature<?, ?> feature : ORES.values()) {
                addFeature(generation, feature);
            }
            //TODO - 1.16.2: Look at uses of DefaultBiomeFeatures#func_243754_n as I think we maybe should only be adding
            // things like salt to specific biome types??
            //Add salt
            addFeature(generation, SALT_FEATURE);
        }
    }

    private static boolean isValidBiome(Biome.Category biomeCategory) {
        //If this does weird things to unclassified biomes (Category.NONE), then we should also mark that biome as invalid
        return biomeCategory != Category.THEEND && biomeCategory != Category.NETHER;
    }

    private static void addFeature(BiomeGenerationSettingsBuilder generation, @Nullable ConfiguredFeature<?, ?> feature) {
        if (feature != null) {
            generation.func_242513_a(GenerationStep.Feature.UNDERGROUND_ORES, feature);
        }
    }

    @Nullable
    private static ConfiguredFeature<?, ?> getOreFeature(IBlockProvider blockProvider, OreConfig oreConfig, Feature<OreFeatureConfig> feature) {
        if (oreConfig.shouldGenerate.get()) {
            return feature.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, blockProvider.getBlock().getDefaultState(), oreConfig.maxVeinSize.get()))
                .method_30377(oreConfig.maxHeight.get())
                .spreadHorizontally()
                .repeat(oreConfig.perChunk.get());
        }
        return null;
    }

    @Nullable
    private static ConfiguredFeature<?, ?> getSaltFeature(IBlockProvider blockProvider, SaltConfig saltConfig, ConfiguredDecorator<NopeDecoratorConfig> placement) {
        if (saltConfig.shouldGenerate.get()) {
            BlockState state = blockProvider.getBlock().getDefaultState();
            return Feature.DISK.configure(new DiskFeatureConfig(state, UniformIntDistribution.of(saltConfig.baseRadius.get(), saltConfig.spread.get()),
                saltConfig.ySize.get(), ImmutableList.of(Blocks.DIRT.getDefaultState(), Blocks.CLAY.getDefaultState(), state)))
                .decorate(placement.spreadHorizontally()).repeat(saltConfig.perChunk.get());
        }
        return null;
    }

    /**
     * @return True if some retro-generation happened, false otherwise
     */
    public static boolean generate(ServerWorld world, Random random, int chunkX, int chunkZ) {
        BlockPos blockPos = new BlockPos(chunkX * 16, 0, chunkZ * 16);
        Biome biome = world.getBiome(blockPos);
        boolean generated = false;
        if (isValidBiome(biome.getCategory()) && world.isChunkLoaded(chunkX, chunkZ)) {
            for (ConfiguredFeature<?, ?> feature : ORE_RETROGENS.values()) {
                generated |= placeFeature(feature, world, random, blockPos);
            }
            generated |= placeFeature(SALT_RETROGEN_FEATURE, world, random, blockPos);
        }
        return generated;
    }

    private static boolean placeFeature(@Nullable ConfiguredFeature<?, ?> feature, ServerWorld world, Random random, BlockPos blockPos) {
        if (feature != null) {
            //TODO - 1.16.2: Test this
            feature.generate(world, world.getChunkManager().getChunkGenerator(), random, blockPos);
            return true;
        }
        return false;
    }
}