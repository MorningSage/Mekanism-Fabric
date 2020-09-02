package mekanism.api.chemical;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import mekanism.api.NBTConstants;
import mekanism.api._helpers_pls_remove.NBT;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.recipes.inputs.chemical.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.StringIdentifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Predicate;

//TODO: Make the chemicals know their own chemical type
public enum ChemicalType implements StringIdentifiable {
    GAS("gas", c -> c instanceof Gas),
    INFUSION("infuse_type", c -> c instanceof InfuseType),
    PIGMENT("pigment", c -> c instanceof Pigment),
    SLURRY("slurry", c -> c instanceof Slurry);

    private static final Map<String, ChemicalType> nameToType = new Object2ObjectOpenHashMap<>();

    static {
        for (ChemicalType type : values()) {
            nameToType.put(type.asString(), type);
        }
    }

    private final Predicate<Chemical<?>> instanceCheck;
    private final String name;

    ChemicalType(String name, Predicate<Chemical<?>> instanceCheck) {
        this.name = name;
        this.instanceCheck = instanceCheck;
    }

    @Nonnull
    @Override
    public String asString() {
        return name;
    }

    public boolean isInstance(Chemical<?> chemical) {
        return instanceCheck.test(chemical);
    }

    public void write(@Nonnull CompoundTag nbt) {
        nbt.putString(NBTConstants.CHEMICAL_TYPE, asString());
    }

    @Nullable
    public static ChemicalType fromString(String name) {
        return nameToType.get(name);
    }

    @Nullable
    public static ChemicalType fromNBT(@Nullable CompoundTag nbt) {
        if (nbt != null && nbt.contains(NBTConstants.CHEMICAL_TYPE, NBT.STRING)) {
            return fromString(nbt.getString(NBTConstants.CHEMICAL_TYPE));
        }
        return null;
    }

    public static ChemicalType getTypeFor(Chemical<?> chemical) {
        if (chemical instanceof Gas) {
            return GAS;
        } else if (chemical instanceof InfuseType) {
            return INFUSION;
        } else if (chemical instanceof Pigment) {
            return PIGMENT;
        } else if (chemical instanceof Slurry) {
            return SLURRY;
        }
        throw new IllegalStateException("Unknown chemical type");
    }

    public static ChemicalType getTypeFor(ChemicalStack<?> stack) {
        return getTypeFor(stack.getType());
    }

    public static ChemicalType getTypeFor(IChemicalStackIngredient<?, ?> ingredient) {
        if (ingredient instanceof GasStackIngredient) {
            return GAS;
        } else if (ingredient instanceof InfusionStackIngredient) {
            return INFUSION;
        } else if (ingredient instanceof PigmentStackIngredient) {
            return PIGMENT;
        } else if (ingredient instanceof SlurryStackIngredient) {
            return SLURRY;
        }
        throw new IllegalStateException("Unknown chemical ingredient type");
    }
}