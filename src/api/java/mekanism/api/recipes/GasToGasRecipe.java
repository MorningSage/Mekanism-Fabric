package mekanism.api.recipes;

import mekanism.api.annotations.MethodsReturnNonnullByDefault;
import mekanism.api.annotations.FieldsAreNonnullByDefault;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.recipes.chemical.ChemicalToChemicalRecipe;
import mekanism.api.recipes.inputs.chemical.GasStackIngredient;
import net.minecraft.util.Identifier;

import javax.annotation.ParametersAreNonnullByDefault;

@FieldsAreNonnullByDefault
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class GasToGasRecipe extends ChemicalToChemicalRecipe<Gas, GasStack, GasStackIngredient> {

    public GasToGasRecipe(ResourceLocation id, GasStackIngredient input, GasStack output) {
        super(id, input, output);
    }

    @Override
    public GasStack getOutput(GasStack input) {
        return output.copy();
    }
}