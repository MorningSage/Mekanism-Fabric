package mekanism.api.chemical;

import mekanism.api.MekanismAPI;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.slurry.Slurry;
import net.minecraft.block.Block;
import net.minecraft.tag.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Supplier;

public class ChemicalTags<CHEMICAL extends Chemical<CHEMICAL>> {

    public static final ChemicalTags<Gas> GAS = new ChemicalTags<>(new Identifier(MekanismAPI.MEKANISM_MODID, "gas"), MekanismAPI::gasRegistry);
    public static final ChemicalTags<InfuseType> INFUSE_TYPE = new ChemicalTags<>(new Identifier(MekanismAPI.MEKANISM_MODID, "infuse_type"), MekanismAPI::infuseTypeRegistry);
    public static final ChemicalTags<Pigment> PIGMENT = new ChemicalTags<>(new Identifier(MekanismAPI.MEKANISM_MODID, "pigment"), MekanismAPI::pigmentRegistry);
    public static final ChemicalTags<Slurry> SLURRY = new ChemicalTags<>(new Identifier(MekanismAPI.MEKANISM_MODID, "slurry"), MekanismAPI::slurryRegistry);

    private final Supplier<Registry<CHEMICAL>> registrySupplier;
    private final Identifier registryName;

    private ChemicalTags(Identifier registryName, Supplier<Registry<CHEMICAL>> registrySupplier) {
        this.registrySupplier = registrySupplier;
        this.registryName = registryName;
    }

    public TagGroup<CHEMICAL> getCollection() {
        Registry<CHEMICAL> registry = registrySupplier.get();
        if (registry == null) {
            return (TagGroup<CHEMICAL>) ServerTagManagerHolder.getTagManager().getCustomTypeCollection(registryName);
        }
        return ServerTagManagerHolder.getTagManager().getCustomTypeCollection(registry);
    }

    public Identifier lookupTag(Tag<CHEMICAL> tag) {
        //Manual and slightly modified implementation of TagCollection#func_232975_b_ to have better reverse lookup handling
        TagGroup<CHEMICAL> collection = getCollection();
        Identifier resourceLocation = collection.getTagId(tag);
        if (resourceLocation == null) {
            //If we failed to get the resource location, try manually looking it up by a "matching" entry
            // as the objects are different and neither Tag nor NamedTag override equals and hashCode
            List<CHEMICAL> chemicals = tag.values();
            for (Entry<Identifier, Tag<CHEMICAL>> entry : collection.getTags().entrySet()) {
                if (chemicals.equals(entry.getValue().values())) {
                    resourceLocation = entry.getKey();
                    break;
                }
            }
        }
        if (resourceLocation == null) {
            throw new IllegalStateException("Unrecognized tag");
        }
        return resourceLocation;
    }

    public Tag.Identified<CHEMICAL> tag(Identifier name) {
        Registry<CHEMICAL> registry = registrySupplier.get();
        if (registry == null) {
            return ForgeTagHandler.makeWrapperTag(registryName, name);
        }
        return ForgeTagHandler.makeWrapperTag(registry, name);
    }

    public IOptionalNamedTag<CHEMICAL> optionalTag(Identifier name) {
        return optionalTag(name, null);
    }

    public IOptionalNamedTag<CHEMICAL> optionalTag(Identifier name, @Nullable Supplier<Set<CHEMICAL>> defaults) {
        Registry<CHEMICAL> registry = registrySupplier.get();
        if (registry == null) {
            return ForgeTagHandler.createOptionalTag(registryName, name, defaults);
        }
        return ForgeTagHandler.createOptionalTag(registry, name, defaults);
    }
}