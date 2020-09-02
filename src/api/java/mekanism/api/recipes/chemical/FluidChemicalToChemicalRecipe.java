package mekanism.api.recipes.chemical;

import mekanism.api._helpers_pls_remove.FluidStack;
import mekanism.api.annotations.MethodsReturnNonnullByDefault;
import mekanism.api.annotations.FieldsAreNonnullByDefault;
import mekanism.api.annotations.NonNull;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.inputs.FluidStackIngredient;
import mekanism.api.recipes.inputs.chemical.IChemicalStackIngredient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.BiPredicate;

@FieldsAreNonnullByDefault
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class FluidChemicalToChemicalRecipe<CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>,
      INGREDIENT extends IChemicalStackIngredient<CHEMICAL, STACK>> extends MekanismRecipe implements BiPredicate<@NonNull FluidStack, @NonNull STACK> {

    private final FluidStackIngredient fluidInput;
    private final INGREDIENT chemicalInput;
    protected final STACK output;

    public FluidChemicalToChemicalRecipe(Identifier id, FluidStackIngredient fluidInput, INGREDIENT chemicalInput, STACK output) {
        super(id);
        this.fluidInput = fluidInput;
        this.chemicalInput = chemicalInput;
        this.output = output;
    }

    @Override
    public boolean test(FluidStack fluidStack, STACK chemicalStack) {
        return fluidInput.test(fluidStack) && chemicalInput.test(chemicalStack);
    }

    public FluidStackIngredient getFluidInput() {
        return fluidInput;
    }

    public INGREDIENT getChemicalInput() {
        return chemicalInput;
    }

    public STACK getOutputRepresentation() {
        return output;
    }

    @Contract(value = "_, _ -> new", pure = true)
    public abstract STACK getOutput(FluidStack fluidStack, STACK chemicalStack);

    @Override
    public void write(PacketByteBuf buffer) {
        fluidInput.write(buffer);
        chemicalInput.write(buffer);
        output.writeToPacket(buffer);
    }
}