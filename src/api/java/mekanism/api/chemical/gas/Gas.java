package mekanism.api.chemical.gas;

import mekanism.api.MekanismAPI;
import mekanism.api.NBTConstants;
import mekanism.api.annotations.MethodsReturnNonnullByDefault;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalTags;
import mekanism.api.chemical.ChemicalUtils;
import mekanism.api.providers.IGasProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Gas - a class used to set specific properties of gases when used or seen in-game.
 *
 * @author aidancbrady
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class Gas extends Chemical<Gas> implements IGasProvider {

    public Gas(GasBuilder builder) {
        super(builder, ChemicalTags.GAS);
    }

    /**
     * Returns the Gas stored in the defined tag compound.
     *
     * @param nbtTags - tag compound to get the Gas from
     *
     * @return Gas stored in the tag compound
     */
    public static Gas readFromNBT(@Nullable CompoundTag nbtTags) {
        return ChemicalUtils.readChemicalFromNBT(nbtTags, MekanismAPI.EMPTY_GAS, NBTConstants.GAS_NAME, Gas::getFromRegistry);
    }

    public static Gas getFromRegistry(@Nullable Identifier name) {
        return ChemicalUtils.readChemicalFromRegistry(name, MekanismAPI.EMPTY_GAS, MekanismAPI.gasRegistry());
    }

    @Override
    public CompoundTag write(CompoundTag nbtTags) {
        nbtTags.putString(NBTConstants.GAS_NAME, getRegistryName().toString());
        return nbtTags;
    }

    @Override
    public String toString() {
        return "[Gas: " + getRegistryName() + "]";
    }

    @Override
    public final boolean isEmptyType() {
        return this == MekanismAPI.EMPTY_GAS;
    }

    @Override
    protected String getDefaultTranslationKey() {
        return Util.createTranslationKey("gas", getRegistryName());
    }
}