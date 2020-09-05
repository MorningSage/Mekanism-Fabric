package mekanism.common.registries;

import mekanism.common.Mekanism;
import mekanism.common.particle.LaserParticleData;
import mekanism.common.particle.LaserParticleType;
import mekanism.common.registration.impl.ParticleTypeDeferredRegister;
import mekanism.common.registration.impl.ParticleTypeRegistryObject;
import net.minecraft.particles.BasicParticleType;

public class MekanismParticleTypes {

    public static final ParticleTypeDeferredRegister PARTICLE_TYPES = new ParticleTypeDeferredRegister(Mekanism.MODID);

    public static final ParticleTypeRegistryObject<LaserParticleData> LASER = PARTICLE_TYPES.register("laser", LaserParticleType::new);
    public static final ParticleTypeRegistryObject<BasicParticleType> JETPACK_FLAME = PARTICLE_TYPES.registerBasicParticle("jetpack_flame");
    public static final ParticleTypeRegistryObject<BasicParticleType> JETPACK_SMOKE = PARTICLE_TYPES.registerBasicParticle("jetpack_smoke");
    public static final ParticleTypeRegistryObject<BasicParticleType> SCUBA_BUBBLE = PARTICLE_TYPES.registerBasicParticle("scuba_bubble");
    public static final ParticleTypeRegistryObject<BasicParticleType> RADIATION = PARTICLE_TYPES.registerBasicParticle("radiation");
}