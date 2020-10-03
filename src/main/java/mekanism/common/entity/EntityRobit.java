package mekanism.common.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mekanism._helpers.EntityHasPickableItem;
import mekanism.api.Action;
import mekanism.api.Coord4D;
import mekanism.api.DataHandlerUtils;
import mekanism.api.NBTConstants;
import mekanism.api._helpers_pls_remove.NBTFlags;
import mekanism.api.annotations.NonNull;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.energy.IMekanismStrictEnergyHandler;
import mekanism.api.energy.IStrictEnergyHandler;
import mekanism.api.inventory.AutomationType;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.inventory.IMekanismInventory;
import mekanism.api.math.FloatingLong;
import mekanism.api.recipes.ItemStackToItemStackRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.ICachedRecipeHolder;
import mekanism.api.recipes.cache.ItemStackToItemStackCachedRecipe;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.InputHelper;
import mekanism.api.recipes.outputs.IOutputHandler;
import mekanism.api.recipes.outputs.OutputHelper;
import mekanism.common.CommonWorldTickHandler;
import mekanism.common.MekanismLang;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import mekanism.common.config.MekanismConfig;
import mekanism.common.entity.ai.RobitAIFollow;
import mekanism.common.entity.ai.RobitAIPickup;
import mekanism.common.inventory.container.ContainerProvider;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.entity.robit.MainRobitContainer;
import mekanism.common.inventory.container.sync.SyncableFloatingLong;
import mekanism.common.inventory.container.sync.SyncableInt;
import mekanism.common.inventory.slot.BasicInventorySlot;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.InputInventorySlot;
import mekanism.common.inventory.slot.OutputInventorySlot;
import mekanism.common.item.ItemConfigurator;
import mekanism.common.item.ItemRobit;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.registries.MekanismContainerTypes;
import mekanism.common.registries.MekanismDamageSource;
import mekanism.common.registries.MekanismEntityTypes;
import mekanism.common.registries.MekanismItems;
import mekanism.common.tile.TileEntityChargepad;
import mekanism.common.tile.interfaces.ISustainedInventory;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.NBTUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.items.ItemHandlerHelper;

//TODO: When Galacticraft gets ported make it so the robit can "breath" without a mask
public class EntityRobit extends PathAwareEntity implements IMekanismInventory, ISustainedInventory, ICachedRecipeHolder<ItemStackToItemStackRecipe>,
      IMekanismStrictEnergyHandler, EntityHasPickableItem {

    private static final TrackedData<String> OWNER_UUID = DataTracker.registerData(EntityRobit.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<String> OWNER_NAME = DataTracker.registerData(EntityRobit.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Boolean> FOLLOW = DataTracker.registerData(EntityRobit.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> DROP_PICKUP = DataTracker.registerData(EntityRobit.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final FloatingLong MAX_ENERGY = FloatingLong.createConst(100_000);
    private static final FloatingLong DISTANCE_MULTIPLIER = FloatingLong.createConst(1.5);
    public Coord4D homeLocation;
    public boolean texTick;
    private int progress;
    //TODO: Note the robit smelts at double normal speed, we may want to make this configurable
    //TODO: Allow for upgrades in the robit?
    private final int ticksRequired = 100;

    private CachedRecipe<ItemStackToItemStackRecipe> cachedRecipe = null;

    private final IInputHandler<@NonNull ItemStack> inputHandler;
    private final IOutputHandler<@NonNull ItemStack> outputHandler;

    @Nonnull
    private final List<IInventorySlot> inventorySlots;
    @Nonnull
    private final List<IInventorySlot> mainContainerSlots;
    @Nonnull
    private final List<IInventorySlot> smeltingContainerSlots;
    @Nonnull
    private final List<IInventorySlot> inventoryContainerSlots;
    private final EnergyInventorySlot energySlot;
    private final InputInventorySlot smeltingInputSlot;
    private final OutputInventorySlot smeltingOutputSlot;
    private final List<IEnergyContainer> energyContainers;
    private final BasicEnergyContainer energyContainer;

    public EntityRobit(EntityType<EntityRobit> type, World world) {
        super(type, world);
        getNavigation().setCanSwim(false);
        setCustomNameVisible(true);
        energyContainers = Collections.singletonList(energyContainer = BasicEnergyContainer.input(MAX_ENERGY, this));

        inventorySlots = new ArrayList<>();
        inventoryContainerSlots = new ArrayList<>();
        for (int slotY = 0; slotY < 3; slotY++) {
            for (int slotX = 0; slotX < 9; slotX++) {
                IInventorySlot slot = BasicInventorySlot.at(this, 8 + slotX * 18, 18 + slotY * 18);
                inventorySlots.add(slot);
                inventoryContainerSlots.add(slot);
            }
        }
        inventorySlots.add(energySlot = EnergyInventorySlot.fillOrConvert(energyContainer, this::getEntityWorld, this, 153, 17));
        inventorySlots.add(smeltingInputSlot = InputInventorySlot.at(item -> containsRecipe(recipe -> recipe.getInput().testType(item)), this, 51, 35));
        //TODO: Previously used FurnaceResultSlot, check if we need to replicate any special logic it had (like if it had xp logic or something)
        // Yes we probably do want this to allow for experience. Though maybe we should allow for experience for all our recipes/smelting recipes? V10
        inventorySlots.add(smeltingOutputSlot = OutputInventorySlot.at(this, 116, 35));

        mainContainerSlots = Collections.singletonList(energySlot);
        smeltingContainerSlots = Arrays.asList(smeltingInputSlot, smeltingOutputSlot);

        inputHandler = InputHelper.getInputHandler(smeltingInputSlot);
        outputHandler = OutputHelper.getOutputHandler(smeltingOutputSlot);
    }

    public EntityRobit(World world, double x, double y, double z) {
        this(MekanismEntityTypes.ROBIT.getEntityType(), world);
        setPos(x, y, z);
        prevX = x;
        prevY = y;
        prevZ = z;
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        goalSelector.add(1, new RobitAIPickup(this, 1));
        goalSelector.add(2, new RobitAIFollow(this, 1, 4, 2));
        goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 8));
        goalSelector.add(3, new LookAroundGoal(this));
        goalSelector.add(4, new SwimGoal(this));
    }

    public static DefaultAttributeContainer.Builder getDefaultAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 1.0D).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3F);
    }

    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) {
        return false;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        dataTracker.startTracking(OWNER_UUID, "");
        dataTracker.startTracking(OWNER_NAME, "");
        dataTracker.startTracking(FOLLOW, false);
        dataTracker.startTracking(DROP_PICKUP, false);
    }

    private FloatingLong getRoundedTravelEnergy() {
        return DISTANCE_MULTIPLIER.multiply(Math.sqrt(squaredDistanceTo(prevX, prevY, prevZ)));
    }

    @Override
    public void baseTick() {
        if (!world.isClient) {
            if (getFollowing() && getOwner() != null && squaredDistanceTo(getOwner()) > 4 && !getNavigation().isIdle() && !energyContainer.isEmpty()) {
                energyContainer.extract(getRoundedTravelEnergy(), Action.EXECUTE, AutomationType.INTERNAL);
            }
        }

        super.baseTick();

        if (!world.isClient) {
            if (getDropPickup()) {
                collectItems();
            }
            if (homeLocation == null) {
                remove();
                return;
            }

            if (age % 20 == 0) {
                // ToDo: Is this the best way to do this in Fabric?
                World serverWorld = world.getServer().getWorld(homeLocation.dimension); /*ServerLifecycleHooks.getCurrentServer().getWorld(homeLocation.dimension)*/;
                BlockPos homePos = homeLocation.getPos();
                if (serverWorld.canSetBlock(homePos)) {
                    if (MekanismUtils.getTileEntity(TileEntityChargepad.class, serverWorld, homePos) == null) {
                        drop();
                        remove();
                    }
                }
            }

            if (energyContainer.isEmpty() && !isOnChargepad()) {
                goHome();
            }

            energySlot.fillContainerOrConvert();
            cachedRecipe = getUpdatedCache(0);
            if (cachedRecipe != null) {
                cachedRecipe.process();
            }
        }
    }

    private void collectItems() {
        List<ItemEntity> items = world.getEntitiesWithinAABB(ItemEntity.class, getBoundingBox().expand(1.5, 1.5, 1.5));
        if (!items.isEmpty()) {
            for (ItemEntity item : items) {
                if (item.cannotPickup() || item.getStack().getItem() instanceof ItemRobit || !item.isAlive()) {
                    continue;
                }
                for (IInventorySlot slot : inventoryContainerSlots) {
                    if (slot.isEmpty()) {
                        slot.setStack(item.getStack());
                        sendPickup(item, item.getStack().getCount());
                        item.remove();
                        playSound(SoundEvents.ENTITY_ITEM_PICKUP, 1, ((random.nextFloat() - random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                        break;
                    }
                    ItemStack itemStack = slot.getStack();
                    int maxSize = slot.getLimit(itemStack);
                    if (ItemHandlerHelper.canItemStacksStack(itemStack, item.getStack()) && itemStack.getCount() < maxSize) {
                        int needed = maxSize - itemStack.getCount();
                        int toAdd = Math.min(needed, item.getStack().getCount());
                        MekanismUtils.logMismatchedStackSize(slot.growStack(toAdd, Action.EXECUTE), toAdd);
                        item.getStack().decrement(toAdd);
                        sendPickup(item, toAdd);
                        if (item.getStack().isEmpty()) {
                            item.remove();
                        }
                        playSound(SoundEvents.ENTITY_ITEM_PICKUP, 1, ((random.nextFloat() - random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                        break;
                    }
                }
            }
        }
    }

    public void goHome() {
        if (world.isClient()) {
            return;
        }
        setFollowing(false);
        if (world.getRegistryKey() == homeLocation.dimension) {
            setPositionAndUpdate(homeLocation.getX() + 0.5, homeLocation.getY() + 0.3, homeLocation.getZ() + 0.5);
        } else {
            ServerWorld newWorld = ((ServerWorld) world).getServer().getWorld(homeLocation.dimension);
            if (newWorld != null) {
                changeDimension(newWorld, new ITeleporter() {
                    @Override
                    public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
                        Entity repositionedEntity = repositionEntity.apply(false);
                        if (repositionedEntity != null) {
                            repositionedEntity.setPositionAndUpdate(homeLocation.getX() + 0.5, homeLocation.getY() + 0.3, homeLocation.getZ() + 0.5);
                        }
                        return repositionedEntity;
                    }
                });
            }
        }
        setVelocity(0, 0, 0);
    }

    private boolean isOnChargepad() {
        return MekanismUtils.getTileEntity(TileEntityChargepad.class, world, getBlockPos()) != null;
    }

    @Nonnull
    @Override
    public ActionResult interactAt(PlayerEntity player, @Nonnull Vec3d vec, @Nonnull Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (player.isSneaking()) {
            if (!stack.isEmpty() && stack.getItem() instanceof ItemConfigurator) {
                if (!world.isClient) {
                    drop();
                }
                remove();
                player.swingHand(hand);
                return ActionResult.SUCCESS;
            }
        } else {
            if (!world.isClient) {
                NetworkHooks.openGui((ServerPlayerEntity) player, new ContainerProvider(MekanismLang.ROBIT, (i, inv, p) -> new MainRobitContainer(i, inv, this)),
                      buf -> buf.writeVarInt(getEntityId()));
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    private ItemStack getItemVariant() {
        ItemStack stack = MekanismItems.ROBIT.getItemStack();
        Optional<IStrictEnergyHandler> capability = stack.getCapabil0ity(Capabilities.STRICT_ENERGY_CAPABILITY).resolve();
        if (capability.isPresent()) {
            IStrictEnergyHandler energyHandlerItem = capability.get();
            if (energyHandlerItem.getEnergyContainerCount() > 0) {
                energyHandlerItem.setEnergy(0, energyContainer.getEnergy());
            }
        }
        ItemRobit item = (ItemRobit) stack.getItem();
        item.setInventory(((ISustainedInventory) this).getInventory(), stack);
        item.setName(stack, getName());
        return stack;
    }

    public void drop() {
        //TODO: Move this to loot table?
        ItemEntity entityItem = new ItemEntity(world, getX(), getY() + 0.3, getZ(), getItemVariant());
        entityItem.setVelocity(0, random.nextGaussian() * 0.05F + 0.2F, 0);
        world.spawnEntity(entityItem);
    }

    public double getScaledProgress() {
        return getOperatingTicks() / (double) ticksRequired;
    }

    public int getOperatingTicks() {
        return progress;
    }

    @Override
    public int getSavedOperatingTicks(int cacheIndex) {
        return getOperatingTicks();
    }

    @Override
    public void writeCustomDataToTag(@Nonnull CompoundTag nbtTags) {
        super.writeCustomDataToTag(nbtTags);
        if (getOwnerUUID() != null) {
            nbtTags.putUuid(NBTConstants.OWNER_UUID, getOwnerUUID());
        }
        nbtTags.putBoolean(NBTConstants.FOLLOW, getFollowing());
        nbtTags.putBoolean(NBTConstants.PICKUP_DROPS, getDropPickup());
        if (homeLocation != null) {
            homeLocation.write(nbtTags);
        }
        nbtTags.put(NBTConstants.ITEMS, DataHandlerUtils.writeContainers(getInventorySlots(null)));
        nbtTags.put(NBTConstants.ENERGY_CONTAINERS, DataHandlerUtils.writeContainers(getEnergyContainers(null)));
        nbtTags.putInt(NBTConstants.PROGRESS, getOperatingTicks());
    }

    @Override
    public void readCustomDataFromTag(@Nonnull CompoundTag nbtTags) {
        super.readCustomDataFromTag(nbtTags);
        NBTUtils.setUUIDIfPresent(nbtTags, NBTConstants.OWNER_UUID, this::setOwnerUUID);
        setFollowing(nbtTags.getBoolean(NBTConstants.FOLLOW));
        setDropPickup(nbtTags.getBoolean(NBTConstants.PICKUP_DROPS));
        homeLocation = Coord4D.read(nbtTags);
        DataHandlerUtils.readContainers(getInventorySlots(null), nbtTags.getList(NBTConstants.ITEMS, NBTFlags.COMPOUND));
        DataHandlerUtils.readContainers(getEnergyContainers(null), nbtTags.getList(NBTConstants.ENERGY_CONTAINERS, NBTFlags.COMPOUND));
        progress = nbtTags.getInt(NBTConstants.PROGRESS);
    }

    @Override
    public boolean isInvulnerableTo(@Nonnull DamageSource source) {
        return source == MekanismDamageSource.RADIATION || super.isInvulnerableTo(source);
    }

    @Override
    protected void applyDamage(@Nonnull DamageSource damageSource, float amount) {
        amount = ForgeHooks.onLivingHurt(this, damageSource, amount);
        if (amount <= 0) {
            return;
        }
        amount = applyArmorToDamage(damageSource, amount);
        amount = applyEnchantmentsToDamage(damageSource, amount);
        float j = getHealth();

        energyContainer.extract(FloatingLong.create(1_000 * amount), Action.EXECUTE, AutomationType.INTERNAL);
        getDamageTracker().onDamage(damageSource, j, amount);
    }

    @Override
    protected void updatePostDeath() {
    }

    public void setHome(Coord4D home) {
        homeLocation = home;
    }

    @Override
    public boolean isPushable() {
        return !energyContainer.isEmpty();
    }

    public PlayerEntity getOwner() {
        return world.getPlayerByUuid(getOwnerUUID());
    }

    public String getOwnerName() {
        return dataTracker.get(OWNER_NAME);
    }

    public UUID getOwnerUUID() {
        return UUID.fromString(dataTracker.get(OWNER_UUID));
    }

    public void setOwnerUUID(UUID uuid) {
        dataTracker.set(OWNER_UUID, uuid.toString());
        dataTracker.set(OWNER_NAME, MekanismUtils.getLastKnownUsername(uuid));
    }

    public boolean getFollowing() {
        return dataTracker.get(FOLLOW);
    }

    public void setFollowing(boolean follow) {
        dataTracker.set(FOLLOW, follow);
    }

    public boolean getDropPickup() {
        return dataTracker.get(DROP_PICKUP);
    }

    public void setDropPickup(boolean pickup) {
        dataTracker.set(DROP_PICKUP, pickup);
    }

    @Override
    public void setInventory(ListTag nbtTags, Object... data) {
        if (nbtTags != null && !nbtTags.isEmpty()) {
            DataHandlerUtils.readContainers(getInventorySlots(null), nbtTags);
        }
    }

    @Override
    public ListTag getInventory(Object... data) {
        return DataHandlerUtils.writeContainers(getInventorySlots(null));
    }

    @Nonnull
    @Override
    public List<IInventorySlot> getInventorySlots(@Nullable Direction side) {
        return hasInventory() ? inventorySlots : Collections.emptyList();
    }

    @Nonnull
    @Override
    public List<IEnergyContainer> getEnergyContainers(@Nullable Direction side) {
        return canHandleEnergy() ? energyContainers : Collections.emptyList();
    }

    @Override
    public void onContentsChanged() {
        //TODO: Do we need to save the things? Probably, if not remove the call to here from createNewCachedRecipe
    }

    @Nonnull
    public List<IInventorySlot> getContainerInventorySlots(@Nonnull ScreenHandlerType<?> containerType) {
        if (!hasInventory()) {
            return Collections.emptyList();
        } else if (containerType == MekanismContainerTypes.INVENTORY_ROBIT.getContainerType()) {
            return inventoryContainerSlots;
        } else if (containerType == MekanismContainerTypes.MAIN_ROBIT.getContainerType()) {
            return mainContainerSlots;
        } else if (containerType == MekanismContainerTypes.SMELTING_ROBIT.getContainerType()) {
            return smeltingContainerSlots;
        }
        return Collections.emptyList();
    }

    @Nonnull
    public MekanismRecipeType<ItemStackToItemStackRecipe> getRecipeType() {
        return MekanismRecipeType.SMELTING;
    }

    public boolean containsRecipe(@Nonnull Predicate<ItemStackToItemStackRecipe> matchCriteria) {
        return getRecipeType().contains(getEntityWorld(), matchCriteria);
    }

    @Nullable
    public ItemStackToItemStackRecipe findFirstRecipe(@Nonnull Predicate<ItemStackToItemStackRecipe> matchCriteria) {
        return getRecipeType().findFirst(getEntityWorld(), matchCriteria);
    }

    @Nullable
    @Override
    public CachedRecipe<ItemStackToItemStackRecipe> getCachedRecipe(int cacheIndex) {
        return cachedRecipe;
    }

    @Nullable
    @Override
    public ItemStackToItemStackRecipe getRecipe(int cacheIndex) {
        ItemStack stack = inputHandler.getInput();
        return stack.isEmpty() ? null : findFirstRecipe(recipe -> recipe.test(stack));
    }

    public IEnergyContainer getEnergyContainer() {
        return energyContainer;
    }

    @Override
    public boolean invalidateCache() {
        return CommonWorldTickHandler.flushTagAndRecipeCaches;
    }

    @Override
    public ItemStack getPickedResult(HitResult target) {
        return getItemVariant();
    }

    @Nullable
    @Override
    public CachedRecipe<ItemStackToItemStackRecipe> createNewCachedRecipe(@Nonnull ItemStackToItemStackRecipe recipe, int cacheIndex) {
        //TODO: Make a robit specific smelting energy usage config
        return new ItemStackToItemStackCachedRecipe(recipe, inputHandler, outputHandler)
              .setEnergyRequirements(MekanismConfig.usage.energizedSmelter, energyContainer)
              .setRequiredTicks(() -> ticksRequired)
              .setOnFinish(this::onContentsChanged)
              .setOperatingTicksChanged(operatingTicks -> progress = operatingTicks);
    }

    public void addContainerTrackers(@Nonnull ScreenHandlerType<?> containerType, MekanismContainer container) {
        if (containerType == MekanismContainerTypes.MAIN_ROBIT.getContainerType()) {
            container.track(SyncableFloatingLong.create(energyContainer::getEnergy, energyContainer::setEnergy));
        } else if (containerType == MekanismContainerTypes.SMELTING_ROBIT.getContainerType()) {
            container.track(SyncableInt.create(() -> progress, value -> progress = value));
        }
    }

    public ScreenHandlerContext getWorldPosCallable() {
        return new ScreenHandlerContext() {
            @Nonnull
            @Override
            public <T> Optional<T> run(@Nonnull BiFunction<World, BlockPos, T> worldBlockPosTBiFunction) {
                //Note: We use an anonymous class implementation rather than using IWorldPosCallable.of, so that if the robit moves
                // this uses the proper updated position
                return Optional.of(worldBlockPosTBiFunction.apply(getEntityWorld(), getPosition()));
            }
        };
    }
}