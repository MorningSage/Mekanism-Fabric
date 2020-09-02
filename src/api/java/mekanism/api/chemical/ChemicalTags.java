package mekanism.api.chemical;

import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.slurry.Slurry;
import net.minecraft.block.Block;
import net.minecraft.tag.*;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map.Entry;

public class ChemicalTags<CHEMICAL extends Chemical<CHEMICAL>> {

    public static final ChemicalTags<Gas> GAS = new ChemicalTags<>();
    public static final ChemicalTags<InfuseType> INFUSE_TYPE = new ChemicalTags<>();
    public static final ChemicalTags<Pigment> PIGMENT = new ChemicalTags<>();
    public static final ChemicalTags<Slurry> SLURRY = new ChemicalTags<>();

    private final RequiredTagList<CHEMICAL> collection = new RequiredTagList<>();

    private ChemicalTags() {

    }

    public void setCollection(TagGroup<CHEMICAL> collectionIn) {
        collection.func_232935_a_(collectionIn);
    }

    public TagGroup<CHEMICAL> getCollection() {
        return collection.getGroup();
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

    public static Tag.Identified<Gas> gasTag(Identifier resourceLocation) {
        return chemicalTag(resourceLocation, GAS);
    }

    public static Tag.Identified<InfuseType> infusionTag(Identifier resourceLocation) {
        return chemicalTag(resourceLocation, INFUSE_TYPE);
    }

    public static Tag.Identified<Pigment> pigmentTag(Identifier resourceLocation) {
        return chemicalTag(resourceLocation, PIGMENT);
    }

    public static Tag.Identified<Slurry> slurryTag(Identifier resourceLocation) {
        return chemicalTag(resourceLocation, SLURRY);
    }

    public static <CHEMICAL extends Chemical<CHEMICAL>> Tag.Identified<CHEMICAL> chemicalTag(Identifier resourceLocation, ChemicalTags<CHEMICAL> chemicalTags) {
        return chemicalTags.collection.add(resourceLocation.toString());
    }
}