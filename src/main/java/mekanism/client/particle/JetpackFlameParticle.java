package mekanism.client.particle;

import javax.annotation.Nonnull;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;

public class JetpackFlameParticle extends FlameParticle {

    private JetpackFlameParticle(ClientWorld world, double posX, double posY, double posZ, double velX, double velY, double velZ) {
        super(world, posX, posY, posZ, velX, velY, velZ);
    }



    @Override
    public int getBrightnessForRender(float partialTick) {
        return 190 + (int) (20F * (1.0F - MinecraftClient.getInstance().gameSettings.gamma));
    }

    @Override
    public void renderParticle(@Nonnull IVertexBuilder vertexBuilder, @Nonnull ActiveRenderInfo renderInfo, float partialTicks) {
        if (age > 0) {
            super.renderParticle(vertexBuilder, renderInfo, partialTicks);
        }
    }

    public static class Factory implements IParticleFactory<BasicParticleType> {

        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle makeParticle(@Nonnull BasicParticleType type, @Nonnull ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            JetpackFlameParticle particle = new JetpackFlameParticle(world, x, y, z, xSpeed, ySpeed, zSpeed);
            particle.selectSpriteRandomly(this.spriteSet);
            return particle;
        }
    }
}