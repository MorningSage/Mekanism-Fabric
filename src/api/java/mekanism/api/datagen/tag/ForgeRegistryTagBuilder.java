package mekanism.api.datagen.tag;

import mekanism.api.mixin.accessors.TagBuilderAccessor;
import net.minecraft.tag.Tag;

//Based off of TagsProvider.Builder
public class ForgeRegistryTagBuilder<TYPE /*extends IForgeRegistryEntry<TYPE>*/> {

    private final Tag.Builder builder;
    private final String modID;

    public ForgeRegistryTagBuilder(Tag.Builder builder, String modID) {
        this.builder = builder;
        this.modID = modID;
    }

    //// Because we can't use Forge classes, you can't use this feature.
    //@Deprecated
    //public ForgeRegistryTagBuilder<TYPE> add(TYPE element) {
    //    //this.builder.add(element.getRegistryName(), modID);
    //    return this;
    //}

    //// Because we can't use Forge classes, you can't use this feature.
    //@SafeVarargs
    //public final ForgeRegistryTagBuilder<TYPE> add(TYPE... elements) {
    //    for (TYPE element : elements) {
    //        add(element);
    //    }
    //    return this;
    //}

    public ForgeRegistryTagBuilder<TYPE> add(Tag.Identified<TYPE> tag) {
        this.builder.add(tag.getId(), modID);
        return this;
    }

    @SafeVarargs
    public final ForgeRegistryTagBuilder<TYPE> add(Tag.Identified<TYPE>... tags) {
        for (Tag.Identified<TYPE> tag : tags) {
            add(tag);
        }
        return this;
    }

    public ForgeRegistryTagBuilder<TYPE> add(Tag.Entry tag) {
        builder.add(tag, modID);
        return this;
    }

    public ForgeRegistryTagBuilder<TYPE> replace() {
        return replace(true);
    }

    public ForgeRegistryTagBuilder<TYPE> replace(boolean value) {
        ((TagBuilderAccessor) builder).replace(value);
        return this;
    }

    // ToDo: Optional tags not supported at this time
    //public ForgeRegistryTagBuilder<TYPE> addOptional(final Identifier... locations) {
    //    return addOptional(Arrays.asList(locations));
    //}
//
    //public ForgeRegistryTagBuilder<TYPE> addOptional(final Collection<Identifier> locations) {
    //    return add(ForgeHooks.makeOptionalTag(true, locations));
    //}
//
    //public ForgeRegistryTagBuilder<TYPE> addOptionalTag(final Identifier... locations) {
    //    return addOptionalTag(Arrays.asList(locations));
    //}
//
    //public ForgeRegistryTagBuilder<TYPE> addOptionalTag(final Collection<Identifier> locations) {
    //    return add(ForgeHooks.makeOptionalTag(false, locations));
    //}
}