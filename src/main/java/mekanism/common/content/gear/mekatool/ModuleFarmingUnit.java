package mekanism.common.content.gear.mekatool;

import java.util.Objects;
import mekanism.api.Action;
import mekanism.api._helpers_pls_remove.BlockFlags;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.inventory.AutomationType;
import mekanism.api.math.FloatingLong;
import mekanism.api.text.IHasTextComponent;
import mekanism.common.Mekanism;
import mekanism.common.MekanismLang;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.gear.ModuleConfigItem;
import mekanism.common.content.gear.ModuleConfigItem.EnumData;
import mekanism.common.network.PacketLightningRender;
import mekanism.common.network.PacketLightningRender.LightningPreset;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.StorageUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

public class ModuleFarmingUnit extends ModuleMekaTool {

    private ModuleConfigItem<FarmingRadius> farmingRadius;

    @Override
    public void init() {
        super.init();
        addConfigItem(farmingRadius = new ModuleConfigItem<>(this, "farming_radius", MekanismLang.MODULE_FARMING_RADIUS, new EnumData<>(FarmingRadius.class, getInstalledCount() + 1), FarmingRadius.LOW));
    }

    @Override
    public ActionResult onItemUse(ItemUsageContext context) {
        return MekanismUtils.performActions(
              //First try to use the disassembler as an axe
              stripLogsAOE(context),
              //Then as a shovel
              //Fire a generic use event, if we are allowed to use the tool return zero otherwise return -1
              // This is to mirror how onHoeUse returns of 0 if allowed, -1 if not allowed, and 1 if processing happened in the event
              () -> tillAOE(context, ToolType.SHOVEL, SoundEvents.ITEM_SHOVEL_FLATTEN, MekanismConfig.gear.mekaToolEnergyUsageShovel.get()),
              //Finally as a hoe
              () -> tillAOE(context, ToolType.HOE, SoundEvents.ITEM_HOE_TILL, MekanismConfig.gear.mekaToolEnergyUsageHoe.get())
        );
    }

    public enum FarmingRadius implements IHasTextComponent {
        OFF(0),
        LOW(1),
        MED(3),
        HIGH(5),
        ULTRA(7);

        private final int radius;
        private final Text label;

        FarmingRadius(int radius) {
            this.radius = radius;
            this.label = new LiteralText(Integer.toString(radius));
        }

        @Override
        public Text getTextComponent() {
            return label;
        }

        public int getRadius() {
            return radius;
        }
    }

    private ActionResult tillAOE(ItemUsageContext context, ToolType toolType, SoundEvent sound, FloatingLong energyUsage) {
        PlayerEntity player = context.getPlayer();
        if (player == null || player.isSneaking()) {
            //Skip if we don't have a player or they are sneaking
            return ActionResult.PASS;
        }
        Direction sideHit = context.getSide();
        if (sideHit == Direction.DOWN) {
            //Don't allow tilling a block from underneath
            return ActionResult.PASS;
        }
        int diameter = farmingRadius.get().getRadius();
        if (diameter == 0) {
            //If we don't have any blocks we are going to want to do, then skip it
            return ActionResult.PASS;
        }
        Hand hand = context.getHand();
        ItemStack stack = player.getStackInHand(hand);
        IEnergyContainer energyContainer = StorageUtils.getEnergyContainer(stack, 0);
        if (energyContainer == null) {
            return ActionResult.FAIL;
        }
        FloatingLong energy = energyContainer.getEnergy();
        if (energy.smallerThan(energyUsage)) {
            //Fail if we don't have enough energy or using the item failed
            return ActionResult.FAIL;
        }
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState tilledState = world.getBlockState(pos).getToolModifiedState(world, pos, player, stack, toolType);
        if (tilledState == null) {
            //Skip tilling the blocks if the one we clicked cannot be tilled
            return ActionResult.PASS;
        }
        BlockPos abovePos = pos.up();
        BlockState aboveState = world.getBlockState(abovePos);
        //Check to make sure the block above is not opaque
        if (aboveState.isOpaqueCube(world, abovePos)) {
            //If the block above our source is opaque, just skip tiling in general
            return ActionResult.PASS;
        }
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        //Processing did not happen, so we need to process it
        world.setBlockState(pos, tilledState, BlockFlags.DEFAULT_AND_RERENDER);
        Material aboveMaterial = aboveState.getMaterial();
        if (aboveMaterial == Material.PLANTS || aboveMaterial == Material.TALL_PLANTS) {
            world.destroyBlock(abovePos, true);
        }
        world.playSound(null, pos, sound, SoundCategory.BLOCKS, 1.0F, 1.0F);
        FloatingLong energyUsed = energyUsage.copy();
        int radius = (diameter - 1) / 2;
        for (BlockPos newPos : BlockPos.iterate(pos.add(-radius, 0, -radius), pos.add(radius, 0, radius))) {
            if (pos.equals(newPos)) {
                //Skip the source position as it is free and we manually handled it before the loop
                continue;
            } else if (energyUsed.add(energyUsage).greaterThan(energy)) {
                break;
            }
            BlockState stateAbove = world.getBlockState(newPos.up());
            //Check to make sure the block above is not opaque and that the result we would get from tilling the other block is
            // the same as the one we got on the initial block we interacted with
            if (!stateAbove.isOpaqueCube(world, newPos.up()) && tilledState == world.getBlockState(newPos).getToolModifiedState(world, newPos, player, stack, toolType)) {
                //Some of the below methods don't behave properly when the BlockPos is mutable, so now that we are onto ones where it may actually
                // matter we make sure to get an immutable instance of newPos
                newPos = newPos.toImmutable();
                //Add energy cost
                energyUsed = energyUsed.plusEqual(energyUsage);
                //Replace the block. Note it just directly sets it (in the same way that HoeItem/ShovelItem do)
                world.setBlockState(newPos, tilledState, BlockFlags.DEFAULT_AND_RERENDER);
                aboveMaterial = stateAbove.getMaterial();
                if (aboveMaterial == Material.PLANTS || aboveMaterial == Material.TALL_PLANTS) {
                    //If the block above the one we tilled is a plant, then we try to remove it
                    world.destroyBlock(newPos.up(), true);
                }
                world.playSound(null, newPos, sound, SoundCategory.BLOCKS, 1.0F, 1.0F);
                Mekanism.packetHandler.sendToAllTracking(new PacketLightningRender(LightningPreset.TOOL_AOE, Objects.hash(pos, newPos),
                      Vector3d.copyCenteredWithVerticalOffset(pos, 0.94), Vector3d.copyCenteredWithVerticalOffset(newPos, 0.94), 10), world, pos);
            }
        }
        energyContainer.extract(energyUsed, Action.EXECUTE, AutomationType.MANUAL);
        return ActionResult.SUCCESS;
    }

    private ActionResult stripLogsAOE(ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();
        if (player == null || player.isSneaking()) {
            //Skip if we don't have a player or they are sneaking
            return ActionResult.PASS;
        }
        int diameter = farmingRadius.get().getRadius();
        if (diameter == 0) {
            //If we don't have any blocks we are going to want to do, then skip it
            return ActionResult.PASS;
        }
        Hand hand = context.getHand();
        ItemStack stack = player.getStackInHand(hand);
        IEnergyContainer energyContainer = StorageUtils.getEnergyContainer(stack, 0);
        if (energyContainer == null) {
            return ActionResult.FAIL;
        }
        FloatingLong energy = energyContainer.getEnergy();
        FloatingLong energyUsage = MekanismConfig.gear.mekaToolEnergyUsageAxe.get();
        if (energy.smallerThan(energyUsage)) {
            //Fail if we don't have enough energy or using the item failed
            return ActionResult.FAIL;
        }
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState clickedState = world.getBlockState(pos);
        BlockState strippedState = clickedState.getToolModifiedState(world, pos, player, stack, ToolType.AXE);
        if (strippedState == null) {
            //Skip stripping the blocks if the one we clicked cannot be stripped
            return ActionResult.PASS;
        } else if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        Axis axis = clickedState.get(RotatedPillarBlock.AXIS);
        //Process the block we interacted with initially and play the sound
        world.setBlockState(pos, strippedState, BlockFlags.DEFAULT_AND_RERENDER);
        world.playSound(null, pos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0F, 1.0F);
        Direction side = context.getSide();
        FloatingLong energyUsed = energyUsage.copy();
        Vector3d offset = Vector3d.copy(side.getDirectionVec()).scale(0.44);
        for (BlockPos newPos : getStrippingArea(pos, side, (diameter - 1) / 2)) {
            if (pos.equals(newPos)) {
                //Skip the source position as it is free and we manually handled it before the loop
                continue;
            } else if (energyUsed.add(energyUsage).greaterThan(energy)) {
                break;
            }
            //Check to make that the result we would get from stripping the other block is the same as the one we got on the initial block we interacted with
            // Also make sure that it is on the same axis as the block we initially clicked
            BlockState state = world.getBlockState(newPos);
            if (strippedState == state.getToolModifiedState(world, newPos, player, stack, ToolType.AXE) && axis == state.get(RotatedPillarBlock.AXIS)) {
                //Some of the below methods don't behave properly when the BlockPos is mutable, so now that we are onto ones where it may actually
                // matter we make sure to get an immutable instance of newPos
                newPos = newPos.toImmutable();
                //Add energy cost
                energyUsed = energyUsed.plusEqual(energyUsage);
                //Replace the block. Note it just directly sets it (in the same way that AxeItem does).
                world.setBlockState(newPos, strippedState, BlockFlags.DEFAULT_AND_RERENDER);
                world.playSound(null, pos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0F, 1.0F);
                Mekanism.packetHandler.sendToAllTracking(new PacketLightningRender(LightningPreset.TOOL_AOE, Objects.hash(pos, newPos),
                      Vector3d.copyCentered(pos).add(offset), Vector3d.copyCentered(newPos).add(offset), 10), world, pos);
            }
        }
        energyContainer.extract(energyUsed, Action.EXECUTE, AutomationType.MANUAL);
        return ActionResult.SUCCESS;
    }

    private static Iterable<BlockPos> getStrippingArea(BlockPos pos, Direction direction, int radius) {
        Box box;
        switch (direction) {
            case EAST:
            case WEST:
                box = new Box(pos.getX(), pos.getY() - radius, pos.getZ() - radius, pos.getX(), pos.getY() + radius, pos.getZ() + radius);
                break;
            case UP:
            case DOWN:
                box = new Box(pos.getX() - radius, pos.getY(), pos.getZ() - radius, pos.getX() + radius, pos.getY(), pos.getZ() + radius);
                break;
            case SOUTH:
            case NORTH:
                box = new Box(pos.getX() - radius, pos.getY() - radius, pos.getZ(), pos.getX() + radius, pos.getY() + radius, pos.getZ());
                break;
            default:
                return BlockPos.iterate(BlockPos.ORIGIN, BlockPos.ORIGIN);
        }
        return BlockPos.iterate(new BlockPos(box.minX, box.minY, box.minZ), new BlockPos(box.maxX, box.maxY, box.maxZ));
    }
}