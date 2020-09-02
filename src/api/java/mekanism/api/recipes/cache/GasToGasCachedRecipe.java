package mekanism.api.recipes.cache;

import mekanism.api.annotations.FieldsAreNonnullByDefault;
import mekanism.api.annotations.NonNull;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.recipes.GasToGasRecipe;
import mekanism.api.recipes.cache.chemical.ChemicalToChemicalCachedRecipe;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.chemical.GasStackIngredient;
import mekanism.api.recipes.outputs.IOutputHandler;

import javax.annotation.ParametersAreNonnullByDefault;

@FieldsAreNonnullByDefault
@ParametersAreNonnullByDefault
public class GasToGasCachedRecipe extends ChemicalToChemicalCachedRecipe<Gas, GasStack, GasStackIngredient, GasToGasRecipe> {

    public GasToGasCachedRecipe(GasToGasRecipe recipe, IInputHandler<@NonNull GasStack> inputHandler, IOutputHandler<@NonNull GasStack> outputHandler) {
        super(recipe, inputHandler, outputHandler);
    }
}