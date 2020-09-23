package mekanism.client;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import mekanism.api.text.EnumColor;
import mekanism.client.render.RenderTickHandler;
import mekanism.client.sound.SoundHandler;
import mekanism.common.CommonProxy;
import mekanism.common.MekanismLang;
import mekanism.common.base.HolidayManager;
import mekanism.common.config.MekanismConfig;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.network.NetworkEvent.Context;

/**
 * Client proxy for the Mekanism mod.
 *
 * @author AidanBrady
 */
public class ClientProxy extends CommonProxy {

    private void doSparkle(BlockEntity tile, SparkleAnimation anim) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        //If player is within 40 blocks (1,600 = 40^2), show the status message/sparkles
        if (tile.getPos().getSquaredDistance(player.getBlockPos()) <= 1_600) {
            if (MekanismConfig.client.enableMultiblockFormationParticles.get()) {
                anim.run();
            } else {
                player.sendMessage(MekanismLang.MULTIBLOCK_FORMED_CHAT.translateColored(EnumColor.INDIGO), true);
            }
        }
    }

    @Override
    public void doMultiblockSparkle(BlockEntity tile, BlockPos renderLoc, int length, int width, int height) {
        doSparkle(tile, new SparkleAnimation(tile, renderLoc, length, width, height));
    }

    @Override
    public void init() {
        super.init();
        MinecraftForge.EVENT_BUS.register(new ClientTickHandler());
        MinecraftForge.EVENT_BUS.register(new RenderTickHandler());
        MinecraftForge.EVENT_BUS.register(SoundHandler.class);
        new MekanismKeyHandler();
        HolidayManager.init();
    }

    @Override
    public double getReach(PlayerEntity player) {
        return MinecraftClient.getInstance().playerController == null ? 8 : MinecraftClient.getInstance().playerController.getBlockReachDistance();
    }

    @Override
    public boolean isPaused() {
        if (MinecraftClient.getInstance().isInSingleplayer() && !MinecraftClient.getInstance().getIntegratedServer().getPublic()) {
            return MinecraftClient.getInstance().isPaused();
        }
        return false;
    }

    @Override
    public PlayerEntity getPlayer(Supplier<Context> context) {
        if (context.get().getDirection().getReceptionSide().isServer()) {
            return super.getPlayer(context);
        }
        return Minecraft.getInstance().player;
    }

    @Nullable
    @Override
    public World tryGetMainWorld() {
        return MinecraftClient.getInstance().world;
    }
}