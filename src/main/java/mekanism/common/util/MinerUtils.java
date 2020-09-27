package mekanism.common.util;

import java.util.Collections;
import java.util.List;
import mekanism.common.Mekanism;
import mekanism.common.registries.MekanismItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class MinerUtils {

    public static List<ItemStack> getDrops(ServerWorld world, BlockPos pos, boolean silk, BlockPos minerPosition) {
        BlockState state = world.getBlockState(pos);
        if (state.getMaterial() == Material.AIR) {
            return Collections.emptyList();
        }
        ItemStack stack = MekanismItems.ATOMIC_DISASSEMBLER.getItemStack();
        if (silk) {
            stack.addEnchantment(Enchantments.SILK_TOUCH, 1);
        }
        //TODO - 1.16.2: Test
        LootContext.Builder lootContextBuilder = new LootContext.Builder(world)
            .random(world.random)
            .parameter(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos))
            .parameter(LootContextParameters.TOOL, stack)
            .optionalParameter(LootContextParameters.THIS_ENTITY, Mekanism.proxy.getDummyPlayer(world, minerPosition).get())
            .optionalParameter(LootContextParameters.BLOCK_ENTITY, MekanismUtils.getTileEntity(world, pos));
        return state.getDroppedStacks(lootContextBuilder);
    }
}