package mekanism.common.entity;

import java.util.Optional;
import javax.annotation.Nonnull;
import mekanism.api.NBTConstants;
import mekanism.common.config.MekanismConfig;
import mekanism.common.item.gear.ItemFlamethrower;
import mekanism.common.item.gear.ItemFlamethrower.FlamethrowerMode;
import mekanism.common.lib.math.Pos3D;
import mekanism.common.registries.MekanismEntityTypes;
import mekanism.common.util.NBTUtils;
import mekanism.common.util.StackUtils;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.TNTBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceContext.BlockMode;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.WorldEvents;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityFlame extends ProjectileEntity implements IEntityAdditionalSpawnData {

    public static final int LIFESPAN = 80;
    private static final int DAMAGE = 10;

    private FlamethrowerMode mode = FlamethrowerMode.COMBAT;

    public EntityFlame(EntityType<EntityFlame> type, World world) {
        super(type, world);
    }

    public EntityFlame(PlayerEntity player) {
        this(MekanismEntityTypes.FLAME.getEntityType(), player.world);
        Pos3D playerPos = new Pos3D(player.getPosX(), player.getPosYEye() - 0.1, player.getPosZ());
        Pos3D flameVec = new Pos3D(1, 1, 1);

        Vector3d lookVec = player.getLookVec();
        flameVec = flameVec.multiply(lookVec).rotateYaw(6);

        Pos3D mergedVec = playerPos.translate(flameVec);
        setPosition(mergedVec.x, mergedVec.y, mergedVec.z);
        setShooter(player);
        mode = ((ItemFlamethrower) player.inventory.getCurrentItem().getItem()).getMode(player.inventory.getCurrentItem());
        func_234612_a_(player, player.rotationPitch, player.rotationYaw, 0, 0.5F, 1);
    }

    @Override
    public void baseTick() {
        if (!isAlive()) {
            return;
        }
        ticksExisted++;

        prevPosX = getPosX();
        prevPosY = getPosY();
        prevPosZ = getPosZ();

        prevRotationPitch = rotationPitch;
        prevRotationYaw = rotationYaw;

        Vector3d motion = getMotion();
        setRawPosition(getPosX() + motion.getX(), getPosY() + motion.getY(), getPosZ() + motion.getZ());

        setPosition(getPosX(), getPosY(), getPosZ());

        calculateVector();
        if (ticksExisted > LIFESPAN) {
            remove();
        }
    }

    private void calculateVector() {
        Vector3d localVec = new Vector3d(getPosX(), getPosY(), getPosZ());
        Vector3d motion = getMotion();
        Vector3d motionVec = new Vector3d(getPosX() + motion.getX() * 2, getPosY() + motion.getY() * 2, getPosZ() + motion.getZ() * 2);
        BlockRayTraceResult blockRayTrace = world.rayTraceBlocks(new RayTraceContext(localVec, motionVec, BlockMode.COLLIDER, FluidMode.ANY, this));
        localVec = new Vector3d(getPosX(), getPosY(), getPosZ());
        motionVec = new Vector3d(getPosX() + motion.getX(), getPosY() + motion.getY(), getPosZ() + motion.getZ());
        if (blockRayTrace.getType() != Type.MISS) {
            motionVec = blockRayTrace.getHitVec();
        }
        EntityRayTraceResult entityResult = ProjectileHelper.rayTraceEntities(world, this, localVec, motionVec,
              getBoundingBox().expand(getMotion()).grow(1.0D, 1.0D, 1.0D), EntityPredicates.NOT_SPECTATING);
        onImpact(entityResult == null ? blockRayTrace : entityResult);
    }

    @Override
    protected void onEntityHit(EntityRayTraceResult entityResult) {
        Entity entity = entityResult.getEntity();
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity.getEntity();
            Entity owner = func_234616_v_();
            if (player.abilities.disableDamage || owner instanceof PlayerEntity && !((PlayerEntity) owner).canAttackPlayer(player)) {
                return;
            }
        }
        if (!entity.getEntity().isImmuneToFire()) {
            if (entity.getEntity() instanceof ItemEntity && mode == FlamethrowerMode.HEAT) {
                if (entity.getEntity().ticksExisted > 100 && !smeltItem((ItemEntity) entity.getEntity())) {
                    burn(entity.getEntity());
                }
            } else {
                burn(entity.getEntity());
            }
        }
        remove();
    }

    @Override
    protected void func_230299_a_(@Nonnull BlockRayTraceResult blockRayTrace) {
        super.func_230299_a_(blockRayTrace);
        BlockPos hitPos = blockRayTrace.getPos();
        Direction hitSide = blockRayTrace.getFace();
        boolean hitFluid = !world.getFluidState(hitPos).isEmpty();
        if (!world.isRemote && MekanismConfig.general.aestheticWorldDamage.get() && !hitFluid) {
            if (mode == FlamethrowerMode.HEAT) {
                smeltBlock(hitPos);
            } else if (mode == FlamethrowerMode.INFERNO) {
                Entity owner = func_234616_v_();
                PlayerEntity shooter = owner instanceof PlayerEntity ? (PlayerEntity) owner : null;
                BlockPos sidePos = hitPos.offset(hitSide);
                BlockState hitState = world.getBlockState(hitPos);
                if (AbstractFireBlock.canLightBlock(world, sidePos)) {
                    world.setBlockState(sidePos, AbstractFireBlock.getFireForPlacement(world, sidePos));
                } else if (CampfireBlock.canBeLit(hitState)) {
                    world.setBlockState(hitPos, hitState.with(BlockStateProperties.LIT, true));
                } else if (hitState.isFlammable(world, hitPos, hitSide)) {
                    hitState.catchFire(world, hitPos, hitSide, shooter);
                    if (hitState.getBlock() instanceof TNTBlock) {
                        world.removeBlock(hitPos, false);
                    }
                }
            }
        }
        if (hitFluid) {
            spawnParticlesAt(getPosition());
            playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 1.0F, 1.0F);
        }
        remove();
    }

    private boolean smeltItem(ItemEntity item) {
        Optional<FurnaceRecipe> recipe = world.getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(item.getItem()), world);
        if (recipe.isPresent()) {
            ItemStack result = recipe.get().getRecipeOutput();
            item.setItem(StackUtils.size(result, item.getItem().getCount()));
            item.ticksExisted = 0;
            spawnParticlesAt(item.getPosition());
            playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 1.0F, 1.0F);
            return true;
        }
        return false;
    }

    private boolean smeltBlock(BlockPos blockPos) {
        if (world.isAirBlock(blockPos)) {
            return false;
        }
        ItemStack stack = new ItemStack(world.getBlockState(blockPos).getBlock());
        if (stack.isEmpty()) {
            return false;
        }
        Optional<FurnaceRecipe> recipe;
        try {
            recipe = world.getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(stack), world);
        } catch (Exception e) {
            return false;
        }
        if (recipe.isPresent()) {
            if (!world.isRemote) {
                BlockState state = world.getBlockState(blockPos);
                ItemStack result = recipe.get().getRecipeOutput();
                if (result.getItem() instanceof BlockItem) {
                    world.setBlockState(blockPos, Block.getBlockFromItem(result.getItem().getItem()).getDefaultState());
                } else {
                    world.removeBlock(blockPos, false);
                    ItemEntity item = new ItemEntity(world, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, result.copy());
                    item.setMotion(0, 0, 0);
                    world.addEntity(item);
                }
                world.playEvent(WorldEvents.BREAK_BLOCK_EFFECTS, blockPos, Block.getStateId(state));
            }
            spawnParticlesAt(blockPos.add(0.5, 0.5, 0.5));
            return true;
        }
        return false;
    }

    private void burn(Entity entity) {
        entity.setFire(20);
        entity.attackEntityFrom(DamageSource.causeThrownDamage(this, func_234616_v_()), DAMAGE);
    }

    private void spawnParticlesAt(BlockPos pos) {
        for (int i = 0; i < 10; i++) {
            world.addParticle(ParticleTypes.SMOKE, pos.getX() + (rand.nextFloat() - 0.5), pos.getY() + (rand.nextFloat() - 0.5),
                  pos.getZ() + (rand.nextFloat() - 0.5), 0, 0, 0);
        }
    }

    @Override
    protected void registerData() {
    }

    @Override
    public void readAdditional(@Nonnull CompoundNBT nbtTags) {
        super.readAdditional(nbtTags);
        NBTUtils.setEnumIfPresent(nbtTags, NBTConstants.MODE, FlamethrowerMode::byIndexStatic, mode -> this.mode = mode);
    }

    @Override
    public void writeAdditional(@Nonnull CompoundNBT nbtTags) {
        super.writeAdditional(nbtTags);
        nbtTags.putInt(NBTConstants.MODE, mode.ordinal());
    }

    @Nonnull
    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void writeSpawnData(PacketBuffer dataStream) {
        dataStream.writeEnumValue(mode);
    }

    @Override
    public void readSpawnData(PacketBuffer dataStream) {
        mode = dataStream.readEnumValue(FlamethrowerMode.class);
    }
}