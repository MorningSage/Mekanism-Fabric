package mekanism.api;

import mekanism.api.chemical.gas.EmptyGas;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.infuse.EmptyInfuseType;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.pigment.EmptyPigment;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.slurry.EmptySlurry;
import mekanism.api.chemical.slurry.Slurry;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

public class MekanismAPI {

    /**
     * The version of the api classes - may not always match the mod's version
     */
    public static final String API_VERSION = "10.0.10";
    public static final String MEKANISM_MODID = "mekanism";
    /**
     * Mekanism debug mode
     */
    public static boolean debug = false;

    public static final Logger logger = LogManager.getLogger(MEKANISM_MODID + "_api");

    private static Registry<Gas> GAS_REGISTRY;
    private static Registry<InfuseType> INFUSE_TYPE_REGISTRY;
    private static Registry<Pigment> PIGMENT_REGISTRY;
    private static Registry<Slurry> SLURRY_REGISTRY;

    //Note: None of the empty variants support registry replacement
    @Nonnull
    public static final Gas EMPTY_GAS = new EmptyGas();
    @Nonnull
    public static final InfuseType EMPTY_INFUSE_TYPE = new EmptyInfuseType();
    @Nonnull
    public static final Pigment EMPTY_PIGMENT = new EmptyPigment();
    @Nonnull
    public static final Slurry EMPTY_SLURRY = new EmptySlurry();

    @Nonnull
    public static Registry<Gas> gasRegistry() {
        if (GAS_REGISTRY == null) {
            GAS_REGISTRY = FabricRegistryBuilder
                .createSimple(Gas.class, new Identifier(MekanismAPI.MEKANISM_MODID, "gases"))
                .attribute(RegistryAttribute.SYNCED)
                .attribute(RegistryAttribute.MODDED)
                .attribute(RegistryAttribute.PERSISTED)
            .buildAndRegister();
        }
        return GAS_REGISTRY;
    }

    @Nonnull
    public static Registry<InfuseType> infuseTypeRegistry() {
        if (INFUSE_TYPE_REGISTRY == null) {
            INFUSE_TYPE_REGISTRY = FabricRegistryBuilder
                .createSimple(InfuseType.class, new Identifier(MekanismAPI.MEKANISM_MODID, "infuse_types"))
                .attribute(RegistryAttribute.SYNCED)
                .attribute(RegistryAttribute.MODDED)
                .attribute(RegistryAttribute.PERSISTED)
                .buildAndRegister();
        }
        return INFUSE_TYPE_REGISTRY;
    }

    @Nonnull
    public static Registry<Pigment> pigmentRegistry() {
        if (PIGMENT_REGISTRY == null) {
            PIGMENT_REGISTRY = FabricRegistryBuilder
                .createSimple(Pigment.class, new Identifier(MekanismAPI.MEKANISM_MODID, "pigments"))
                .attribute(RegistryAttribute.SYNCED)
                .attribute(RegistryAttribute.MODDED)
                .attribute(RegistryAttribute.PERSISTED)
                .buildAndRegister();
        }
        return PIGMENT_REGISTRY;
    }

    @Nonnull
    public static Registry<Slurry> slurryRegistry() {
        if (SLURRY_REGISTRY == null) {
            SLURRY_REGISTRY = FabricRegistryBuilder
                .createSimple(Slurry.class, new Identifier(MekanismAPI.MEKANISM_MODID, "slurry"))
                .attribute(RegistryAttribute.SYNCED)
                .attribute(RegistryAttribute.MODDED)
                .attribute(RegistryAttribute.PERSISTED)
                .buildAndRegister();
        }
        return SLURRY_REGISTRY;
    }
}