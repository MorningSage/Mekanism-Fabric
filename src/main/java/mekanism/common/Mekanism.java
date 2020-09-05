package mekanism.common;

import com.mojang.authlib.GameProfile;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import mekanism.api.Coord4D;
import mekanism.api.MekanismAPI;
import mekanism.api.NBTConstants;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.common.base.IModule;
import mekanism.common.base.KeySync;
import mekanism.common.base.MekFakePlayer;
import mekanism.common.base.PlayerState;
import mekanism.common.base.TagCache;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.command.CommandMek;
import mekanism.common.command.builders.BuildCommand;
import mekanism.common.command.builders.Builders.BoilerBuilder;
import mekanism.common.command.builders.Builders.EvaporationBuilder;
import mekanism.common.command.builders.Builders.MatrixBuilder;
import mekanism.common.command.builders.Builders.SPSBuilder;
import mekanism.common.command.builders.Builders.TankBuilder;
import mekanism.common.config.MekanismConfig;
import mekanism.common.config.MekanismModConfig;
import mekanism.common.content.boiler.BoilerMultiblockData;
import mekanism.common.content.boiler.BoilerValidator;
import mekanism.common.content.evaporation.EvaporationMultiblockData;
import mekanism.common.content.evaporation.EvaporationValidator;
import mekanism.common.content.gear.Modules;
import mekanism.common.content.matrix.MatrixMultiblockData;
import mekanism.common.content.matrix.MatrixValidator;
import mekanism.common.content.network.BoxedChemicalNetwork.ChemicalTransferEvent;
import mekanism.common.content.network.EnergyNetwork.EnergyTransferEvent;
import mekanism.common.content.network.FluidNetwork.FluidTransferEvent;
import mekanism.common.content.sps.SPSCache;
import mekanism.common.content.sps.SPSMultiblockData;
import mekanism.common.content.sps.SPSValidator;
import mekanism.common.content.tank.TankCache;
import mekanism.common.content.tank.TankMultiblockData;
import mekanism.common.content.tank.TankValidator;
import mekanism.common.content.transporter.PathfinderCache;
import mekanism.common.content.transporter.TransporterManager;
import mekanism.common.entity.EntityRobit;
import mekanism.common.integration.MekanismHooks;
import mekanism.common.inventory.container.sync.dynamic.SyncMapper;
import mekanism.common.lib.Version;
import mekanism.common.lib.frequency.FrequencyManager;
import mekanism.common.lib.frequency.FrequencyType;
import mekanism.common.lib.multiblock.MultiblockCache;
import mekanism.common.lib.multiblock.MultiblockManager;
import mekanism.common.lib.radiation.RadiationManager;
import mekanism.common.lib.transmitter.TransmitterNetworkRegistry;
import mekanism.common.network.PacketHandler;
import mekanism.common.network.PacketTransmitterUpdate;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.recipe.bin.BinInsertRecipe;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismContainerTypes;
import mekanism.common.registries.MekanismEntityTypes;
import mekanism.common.registries.MekanismFeatures;
import mekanism.common.registries.MekanismFluids;
import mekanism.common.registries.MekanismGases;
import mekanism.common.registries.MekanismInfuseTypes;
import mekanism.common.registries.MekanismItems;
import mekanism.common.registries.MekanismParticleTypes;
import mekanism.common.registries.MekanismPigments;
import mekanism.common.registries.MekanismPlacements;
import mekanism.common.registries.MekanismRecipeSerializers;
import mekanism.common.registries.MekanismSlurries;
import mekanism.common.registries.MekanismSounds;
import mekanism.common.registries.MekanismTileEntityTypes;
import mekanism.common.tags.MekanismTagManager;
import mekanism.common.world.GenHandler;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraft.tags.NetworkTagManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Mekanism.MODID)
public class Mekanism {

    public static final String MODID = MekanismAPI.MEKANISM_MODID;
    public static final String MOD_NAME = "Mekanism";
    public static final String LOG_TAG = '[' + MOD_NAME + ']';
    public static final PlayerState playerState = new PlayerState();
    /**
     * Mekanism Packet Pipeline
     */
    public static final PacketHandler packetHandler = new PacketHandler();
    /**
     * Mekanism logger instance
     */
    public static final Logger logger = LogManager.getLogger(MOD_NAME);
    /**
     * Mekanism proxy instance
     */
    //TODO: Remove need for having a proxy as it is the legacy way of doing things
    public static CommonProxy proxy = DistExecutor.safeRunForDist(() -> CommonProxy::createClientProxy, () -> CommonProxy::new);

    /**
     * Mekanism mod instance
     */
    public static Mekanism instance;
    /**
     * Mekanism hooks instance
     */
    public static final MekanismHooks hooks = new MekanismHooks();
    /**
     * Mekanism version number
     */
    public final Version versionNumber;
    /**
     * MultiblockManagers for various structures
     */
    public static final MultiblockManager<TankMultiblockData> tankManager = new MultiblockManager<>("dynamicTank", TankCache::new, TankValidator::new);
    public static final MultiblockManager<MatrixMultiblockData> matrixManager = new MultiblockManager<>("inductionMatrix", MultiblockCache::new, MatrixValidator::new);
    public static final MultiblockManager<BoilerMultiblockData> boilerManager = new MultiblockManager<>("thermoelectricBoiler", MultiblockCache::new, BoilerValidator::new);
    public static final MultiblockManager<EvaporationMultiblockData> evaporationManager = new MultiblockManager<>("evaporation", MultiblockCache::new, EvaporationValidator::new);
    public static final MultiblockManager<SPSMultiblockData> spsManager = new MultiblockManager<>("sps", SPSCache::new, SPSValidator::new);
    /**
     * RadiationManager for handling radiation across all dimensions
     */
    public static final RadiationManager radiationManager = new RadiationManager();
    /**
     * Mekanism creative tab
     */
    public static final CreativeTabMekanism tabMekanism = new CreativeTabMekanism();
    /**
     * List of Mekanism modules loaded
     */
    public static final List<IModule> modulesLoaded = new ArrayList<>();
    /**
     * The server's world tick handler.
     */
    public static final CommonWorldTickHandler worldTickHandler = new CommonWorldTickHandler();
    /**
     * The GameProfile used by the dummy Mekanism player
     */
    public static final GameProfile gameProfile = new GameProfile(UUID.nameUUIDFromBytes("mekanism.common".getBytes()), Mekanism.LOG_TAG);
    public static final KeySync keyMap = new KeySync();
    public static final Set<Coord4D> activeVibrators = new ObjectOpenHashSet<>();

    private MekanismTagManager mekanismTagManager;
    private ReloadListener recipeCacheManager;

    public Mekanism() {
        instance = this;
        MekanismConfig.registerConfigs(ModLoadingContext.get());

        MinecraftForge.EVENT_BUS.addListener(this::onEnergyTransferred);
        MinecraftForge.EVENT_BUS.addListener(this::onChemicalTransferred);
        MinecraftForge.EVENT_BUS.addListener(this::onLiquidTransferred);
        MinecraftForge.EVENT_BUS.addListener(this::chunkSave);
        MinecraftForge.EVENT_BUS.addListener(this::onChunkDataLoad);
        MinecraftForge.EVENT_BUS.addListener(this::onWorldLoad);
        MinecraftForge.EVENT_BUS.addListener(this::onWorldUnload);
        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
        MinecraftForge.EVENT_BUS.addListener(this::serverStopped);
        MinecraftForge.EVENT_BUS.addListener(this::addReloadListeners);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::addReloadListenersLowest);
        MinecraftForge.EVENT_BUS.addListener(BinInsertRecipe::onCrafting);
        MinecraftForge.EVENT_BUS.addListener(this::onTagsReload);
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::onConfigLoad);
        modEventBus.addListener(this::imcQueue);
        MekanismItems.ITEMS.register(modEventBus);
        MekanismBlocks.BLOCKS.register(modEventBus);
        MekanismFluids.FLUIDS.register(modEventBus);
        MekanismContainerTypes.CONTAINER_TYPES.register(modEventBus);
        MekanismEntityTypes.ENTITY_TYPES.register(modEventBus);
        MekanismTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus);
        MekanismSounds.SOUND_EVENTS.register(modEventBus);
        MekanismParticleTypes.PARTICLE_TYPES.register(modEventBus);
        MekanismPlacements.PLACEMENTS.register(modEventBus);
        MekanismFeatures.FEATURES.register(modEventBus);
        MekanismRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
        MekanismGases.GASES.createAndRegister(modEventBus, "gas");
        MekanismInfuseTypes.INFUSE_TYPES.createAndRegister(modEventBus, "infuse_type");
        MekanismPigments.PIGMENTS.createAndRegister(modEventBus, "pigment");
        MekanismSlurries.SLURRIES.createAndRegister(modEventBus, "slurry");
        modEventBus.addGenericListener(Gas.class, this::registerGases);
        modEventBus.addGenericListener(InfuseType.class, this::registerInfuseTypes);
        modEventBus.addGenericListener(Pigment.class, this::registerPigments);
        modEventBus.addGenericListener(Slurry.class, this::registerSlurries);
        modEventBus.addGenericListener(IRecipeSerializer.class, this::registerRecipeSerializers);
        //Set our version number to match the mods.toml file, which matches the one in our build.gradle
        versionNumber = new Version(ModLoadingContext.get().getActiveContainer().getModInfo().getVersion());
    }

    //Register the empty chemicals
    private void registerGases(RegistryEvent.Register<Gas> event) {
        event.getRegistry().register(MekanismAPI.EMPTY_GAS);
    }

    private void registerInfuseTypes(RegistryEvent.Register<InfuseType> event) {
        event.getRegistry().register(MekanismAPI.EMPTY_INFUSE_TYPE);
    }

    private void registerPigments(RegistryEvent.Register<Pigment> event) {
        event.getRegistry().register(MekanismAPI.EMPTY_PIGMENT);
    }

    private void registerSlurries(RegistryEvent.Register<Slurry> event) {
        event.getRegistry().register(MekanismAPI.EMPTY_SLURRY);
    }

    private void registerRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> event) {
        MekanismRecipeType.registerRecipeTypes(event.getRegistry());
    }

    public static ResourceLocation rl(String path) {
        return new ResourceLocation(Mekanism.MODID, path);
    }

    private void setTagManager(MekanismTagManager manager) {
        if (mekanismTagManager == null) {
            mekanismTagManager = manager;
        } else {
            logger.warn("Mekanism Tag Manager has already been set.");
        }
    }

    public MekanismTagManager getTagManager() {
        return mekanismTagManager;
    }

    private void setRecipeCacheManager(ReloadListener manager) {
        if (recipeCacheManager == null) {
            recipeCacheManager = manager;
        } else {
            logger.warn("Recipe cache manager has already been set.");
        }
    }

    public ReloadListener getRecipeCacheManager() {
        return recipeCacheManager;
    }

    private void onTagsReload(TagsUpdatedEvent event) {
        TagCache.resetTagCaches();
    }

    private void addReloadListeners(AddReloadListenerEvent event) {
        boolean added = false;
        IResourceManager resourceManager = event.getDataPackRegistries().getResourceManager();
        if (resourceManager instanceof SimpleReloadableResourceManager) {
            //Note: We "hack" it so that our tag manager gets registered directly after the normal tag manager
            // to ensure that it is before the recipe manager and that the custom tags can be properly resolved
            //TODO: It would make sense to eventually make a PR to forge to make custom tags easier to do
            SimpleReloadableResourceManager manager = (SimpleReloadableResourceManager) resourceManager;
            for (int i = 0; i < manager.reloadListeners.size(); i++) {
                IFutureReloadListener listener = manager.reloadListeners.get(i);
                if (listener instanceof NetworkTagManager) {
                    manager.reloadListeners.add(i + 1, getTagManager());
                    manager.initTaskQueue.add(i + 1, getTagManager());
                    added = true;
                    break;
                }
            }
        }
        if (!added) {
            //Fallback to trying to just add it even though this is probably too late to do so properly
            event.addListener(getTagManager());
        }
    }

    private void addReloadListenersLowest(AddReloadListenerEvent event) {
        //Note: We register reload listeners here which we want to make sure run after CraftTweaker or any other mods that may modify recipes
        event.addListener(getRecipeCacheManager());
    }

    private void registerCommands(RegisterCommandsEvent event) {
        BuildCommand.register("boiler", new BoilerBuilder());
        BuildCommand.register("matrix", new MatrixBuilder());
        BuildCommand.register("tank", new TankBuilder());
        BuildCommand.register("evaporation", new EvaporationBuilder());
        BuildCommand.register("sps", new SPSBuilder());
        event.getDispatcher().register(CommandMek.register());
    }

    private void serverStopped(FMLServerStoppedEvent event) {
        //Clear all cache data, wait until server stopper though so that we make sure saving can use any data it needs
        playerState.clear();
        activeVibrators.clear();
        worldTickHandler.resetRegenChunks();
        FrequencyType.clear();
        BoilerMultiblockData.hotMap.clear();

        //Reset consistent managers
        radiationManager.reset();
        MultiblockManager.reset();
        FrequencyManager.reset();
        TransporterManager.reset();
        PathfinderCache.reset();
        TransmitterNetworkRegistry.reset();
    }

    private void imcQueue(InterModEnqueueEvent event) {
        hooks.sendIMCMessages(event);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        hooks.hookCommonSetup();
        Capabilities.registerCapabilities();
        setTagManager(new MekanismTagManager());
        setRecipeCacheManager(new ReloadListener());

        DeferredWorkQueue.runLater(() -> {
            //Register the mod's world generators
            GenHandler.setupWorldGeneration();
            //Collect sync mapper scan data
            SyncMapper.collectScanData();
            //Entity attribute assignments
            GlobalEntityTypeAttributes.put(MekanismEntityTypes.ROBIT.get(), EntityRobit.getDefaultAttributes().create());
        });

        //Register player tracker
        MinecraftForge.EVENT_BUS.register(new CommonPlayerTracker());
        MinecraftForge.EVENT_BUS.register(new CommonPlayerTickHandler());

        MinecraftForge.EVENT_BUS.register(radiationManager);

        //Set up module container tooltips
        Modules.processSupportedContainers();

        //Initialization notification
        logger.info("Version {} initializing...", versionNumber);

        //Register with TransmitterNetworkRegistry
        TransmitterNetworkRegistry.initiate();

        //Packet registrations
        packetHandler.initialize();

        //Load proxy
        proxy.init();

        //Fake player info
        logger.info("Fake player readout: UUID = {}, name = {}", gameProfile.getId(), gameProfile.getName());

        //Completion notification
        logger.info("Loading complete.");

        //Success message
        logger.info("Mod loaded.");
    }

    private void onEnergyTransferred(EnergyTransferEvent event) {
        packetHandler.sendToReceivers(new PacketTransmitterUpdate(event.network), event.network);
    }

    private void onChemicalTransferred(ChemicalTransferEvent event) {
        packetHandler.sendToReceivers(new PacketTransmitterUpdate(event.network, event.transferType), event.network);
    }

    private void onLiquidTransferred(FluidTransferEvent event) {
        packetHandler.sendToReceivers(new PacketTransmitterUpdate(event.network, event.fluidType), event.network);
    }

    private void chunkSave(ChunkDataEvent.Save event) {
        if (event.getWorld() != null && !event.getWorld().isRemote()) {
            //TODO - 1.17: Make both this and load write to the main tag instead of the level sub tag. For now we are using the level tag
            // in both spots to have proper backwards compatibility with earlier mek release versions from 1.16
            CompoundNBT levelTag = event.getData().getCompound(NBTConstants.CHUNK_DATA_LEVEL);
            levelTag.putInt(NBTConstants.WORLD_GEN_VERSION, MekanismConfig.world.userGenVersion.get());
        }
    }

    private synchronized void onChunkDataLoad(ChunkDataEvent.Load event) {
        IWorld world = event.getWorld();
        if (world instanceof World && !world.isRemote() && MekanismConfig.world.enableRegeneration.get()) {
            CompoundNBT levelTag = event.getData().getCompound(NBTConstants.CHUNK_DATA_LEVEL);
            if (levelTag.getInt(NBTConstants.WORLD_GEN_VERSION) < MekanismConfig.world.userGenVersion.get()) {
                worldTickHandler.addRegenChunk(((World) world).func_234923_W_(), event.getChunk().getPos());
            }
        }
    }

    private void onConfigLoad(ModConfig.ModConfigEvent configEvent) {
        //Note: We listen to both the initial load and the reload, so as to make sure that we fix any accidentally
        // cached values from calls before the initial loading
        ModConfig config = configEvent.getConfig();
        //Make sure it is for the same modid as us
        if (config.getModId().equals(MODID) && config instanceof MekanismModConfig) {
            ((MekanismModConfig) config).clearCache();
        }
    }

    private void onWorldLoad(WorldEvent.Load event) {
        playerState.init(event.getWorld());
    }

    private void onWorldUnload(WorldEvent.Unload event) {
        // Make sure the global fake player drops its reference to the World
        // when the server shuts down
        if (event.getWorld() instanceof ServerWorld) {
            MekFakePlayer.releaseInstance(event.getWorld());
        }
    }
}