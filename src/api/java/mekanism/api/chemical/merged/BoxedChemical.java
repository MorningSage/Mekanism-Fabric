package mekanism.api.chemical.merged;

import mekanism.api.annotations.MethodsReturnNonnullByDefault;
import mekanism.api.MekanismAPI;
import mekanism.api.annotations.FieldsAreNonnullByDefault;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalType;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.text.IHasTextComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@FieldsAreNonnullByDefault
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BoxedChemical implements IHasTextComponent {

    //TODO: Make a subclass for the empty implementation?
    public static final BoxedChemical EMPTY = new BoxedChemical(ChemicalType.GAS, MekanismAPI.EMPTY_GAS);

    // ToDo: Test this as it's been modified quite a bit...
    // ToDo: A defaulted registry would be really nice here
    //@SuppressWarnings("RedundantCast")
    public static BoxedChemical read(PacketByteBuf buffer) {
        //Note: Casts are [maybe?] needed for compiling so it knows how to read it properly
        ChemicalType chemicalType = buffer.readEnumConstant(ChemicalType.class);
        Chemical<?> chemical;

        if (chemicalType == ChemicalType.GAS) {
            chemical = MekanismAPI.gasRegistry().get(buffer.readIdentifier());
            if (chemical == null) chemical = MekanismAPI.EMPTY_GAS;
        } else if (chemicalType == ChemicalType.INFUSION) {
            chemical = MekanismAPI.infuseTypeRegistry().get(buffer.readIdentifier());
            if (chemical == null) chemical = MekanismAPI.EMPTY_INFUSE_TYPE;
        } else if (chemicalType == ChemicalType.PIGMENT) {
            chemical = MekanismAPI.pigmentRegistry().get(buffer.readIdentifier());
            if (chemical == null) chemical = MekanismAPI.EMPTY_PIGMENT;
        } else if (chemicalType == ChemicalType.SLURRY) {
            chemical = MekanismAPI.slurryRegistry().get(buffer.readIdentifier());
            if (chemical == null) chemical = MekanismAPI.EMPTY_SLURRY;
        } else {
            throw new IllegalStateException("Unknown chemical type");
        }

        return new BoxedChemical(chemicalType, chemical);
    }

    public static BoxedChemical read(@Nullable CompoundTag nbt) {
        ChemicalType chemicalType = ChemicalType.fromNBT(nbt);
        Chemical<?> chemical = null;
        if (chemicalType == ChemicalType.GAS) {
            chemical = Gas.readFromNBT(nbt);
        } else if (chemicalType == ChemicalType.INFUSION) {
            chemical = InfuseType.readFromNBT(nbt);
        } else if (chemicalType == ChemicalType.PIGMENT) {
            chemical = Pigment.readFromNBT(nbt);
        } else if (chemicalType == ChemicalType.SLURRY) {
            chemical = Slurry.readFromNBT(nbt);
        }
        return chemicalType == null || chemical == null ? EMPTY : new BoxedChemical(chemicalType, chemical);
    }

    public static BoxedChemical box(Chemical<?> chemical) {
        return new BoxedChemical(ChemicalType.getTypeFor(chemical), chemical);
    }

    private final ChemicalType chemicalType;
    private final Chemical<?> chemical;

    protected BoxedChemical(ChemicalType chemicalType, Chemical<?> chemical) {
        this.chemicalType = chemicalType;
        this.chemical = chemical;
    }

    public boolean isEmpty() {
        return this == EMPTY || chemical.isEmptyType();
    }

    public ChemicalType getChemicalType() {
        return chemicalType;
    }

    public CompoundTag write(CompoundTag nbt) {
        chemicalType.write(nbt);
        chemical.write(nbt);
        return nbt;
    }

    // ToDo... what about EMPTY variants.  This is now broken since we don't store the IDs in the items like forge
    public void write(PacketByteBuf buffer) {
        buffer.writeEnumConstant(chemicalType);
        if (chemicalType == ChemicalType.GAS) {
            buffer.writeIdentifier(MekanismAPI.gasRegistry().getId((Gas) chemical));
        } else if (chemicalType == ChemicalType.INFUSION) {
            buffer.writeIdentifier(MekanismAPI.infuseTypeRegistry().getId((InfuseType) chemical));
        } else if (chemicalType == ChemicalType.PIGMENT) {
            buffer.writeIdentifier(MekanismAPI.pigmentRegistry().getId((Pigment) chemical));
        } else if (chemicalType == ChemicalType.SLURRY) {
            buffer.writeIdentifier(MekanismAPI.slurryRegistry().getId((Slurry) chemical));
        } else {
            throw new IllegalStateException("Unknown chemical type");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BoxedChemical other = (BoxedChemical) o;
        return chemicalType == other.chemicalType && chemical == other.chemical;
    }

    @Override
    public int hashCode() {
        return Objects.hash(chemicalType, chemical);
    }

    public Chemical<?> getChemical() {
        return chemical;
    }

    @Override
    public Text getTextComponent() {
        return chemical.getTextComponent();
    }
}