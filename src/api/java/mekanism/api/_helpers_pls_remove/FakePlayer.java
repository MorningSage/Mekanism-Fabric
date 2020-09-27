package mekanism.api._helpers_pls_remove;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.ClientSettingsC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stat;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;
import java.util.UUID;

//Preliminary, simple Fake Player class
public class FakePlayer extends ServerPlayerEntity {
    public FakePlayer(ServerWorld world, GameProfile name) {
        super(world.getServer(), world, name, new ServerPlayerInteractionManager(world));
    }

    @Override public Vec3d getPos() { return new Vec3d(0, 0, 0); }
    @Override public BlockPos getBlockPos() { return BlockPos.ORIGIN; }
    @Override public void sendMessage(Text chatComponent, boolean actionBar) {}
    @Override public void sendSystemMessage(Text component, UUID senderUUID) {}
    @Override public void increaseStat(Stat<?> stat, int amount) {}
    @Override public boolean isInvulnerableTo(DamageSource source){ return true; }
    @Override public boolean shouldDamagePlayer(PlayerEntity player) { return false; }
    @Override public void onDeath(DamageSource source){}
    @Override public void tick(){}
    @Override public void setClientSettings(ClientSettingsC2SPacket packet) {}
    @Override @Nullable public MinecraftServer getServer() { return this.server; }
}