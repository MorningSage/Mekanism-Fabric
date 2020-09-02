package mekanism.api.chemical.infuse;

import mekanism.api.MekanismAPI;
import mekanism.api.NBTConstants;
import mekanism.api.annotations.MethodsReturnNonnullByDefault;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalTags;
import mekanism.api.chemical.ChemicalUtils;
import mekanism.api.providers.IInfuseTypeProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class InfuseType extends Chemical<InfuseType> implements IInfuseTypeProvider {

    public InfuseType(InfuseTypeBuilder builder) {
        super(builder, ChemicalTags.INFUSE_TYPE);
    }

    public static InfuseType readFromNBT(@Nullable CompoundTag nbtTags) {
        return ChemicalUtils.readChemicalFromNBT(nbtTags, MekanismAPI.EMPTY_INFUSE_TYPE, NBTConstants.INFUSE_TYPE_NAME, InfuseType::getFromRegistry);
    }

    public static InfuseType getFromRegistry(@Nullable Identifier name) {
        return ChemicalUtils.readChemicalFromRegistry(name, MekanismAPI.EMPTY_INFUSE_TYPE, MekanismAPI.infuseTypeRegistry());
    }

    @Override
    public String toString() {
        return "[InfuseType: " + getRegistryName() + "]";
    }

    @Override
    public CompoundTag write(CompoundTag nbtTags) {
        nbtTags.putString(NBTConstants.INFUSE_TYPE_NAME, getRegistryName().toString());
        return nbtTags;
    }

    @Override
    public final boolean isEmptyType() {
        return this == MekanismAPI.EMPTY_INFUSE_TYPE;
    }

    @Override
    protected String getDefaultTranslationKey() {
        return Util.createTranslationKey("infuse_type", getRegistryName());
    }
}