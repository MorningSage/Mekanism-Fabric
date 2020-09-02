package mekanism.api.chemical.slurry;

import mekanism.api.annotations.MethodsReturnNonnullByDefault;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.ChemicalBuilder;
import mekanism.api.mixin.accessors.ItemTagsAccessor;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SlurryBuilder extends ChemicalBuilder<Slurry, SlurryBuilder> {

    @Nullable
    private Tag<Item> oreTag;

    protected SlurryBuilder(Identifier texture) {
        super(texture);
    }

    public static SlurryBuilder clean() {
        return builder(new Identifier(MekanismAPI.MEKANISM_MODID, "slurry/clean"));
    }

    public static SlurryBuilder dirty() {
        return builder(new Identifier(MekanismAPI.MEKANISM_MODID, "slurry/dirty"));
    }

    public static SlurryBuilder builder(Identifier texture) {
        return new SlurryBuilder(Objects.requireNonNull(texture));
    }

    public SlurryBuilder ore(Identifier oreTagLocation) {
        return ore(ItemTagsAccessor.register(Objects.requireNonNull(oreTagLocation).toString()));
    }

    public SlurryBuilder ore(Tag<Item> oreTag) {
        this.oreTag = Objects.requireNonNull(oreTag);
        return this;
    }

    @Nullable
    public Tag<Item> getOreTag() {
        return oreTag;
    }
}