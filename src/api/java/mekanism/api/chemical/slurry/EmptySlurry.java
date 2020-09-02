package mekanism.api.chemical.slurry;

import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Set;

public final class EmptySlurry extends Slurry {

    public EmptySlurry() {
        super(SlurryBuilder.clean().hidden());
        // ToDo...
        //setRegistryName(new Identifier(MekanismAPI.MEKANISM_MODID, "empty_slurry"));
    }

    @Override
    public boolean isIn(@Nonnull Tag<Slurry> tags) {
        //Empty slurry is in no tags
        return false;
    }

    @Nonnull
    @Override
    public Set<Identifier> getTags() {
        return Collections.emptySet();
    }
}