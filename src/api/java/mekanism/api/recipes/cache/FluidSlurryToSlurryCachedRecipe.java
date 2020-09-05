package mekanism.api.recipes.cache;

import mekanism.api._helpers_pls_remove.FluidStack;
import mekanism.api.annotations.FieldsAreNonnullByDefault;
import mekanism.api.annotations.NonNull;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.recipes.FluidSlurryToSlurryRecipe;
import mekanism.api.recipes.cache.chemical.FluidChemicalToChemicalCachedRecipe;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.chemical.SlurryStackIngredient;
import mekanism.api.recipes.outputs.IOutputHandler;

import javax.annotation.ParametersAreNonnullByDefault;

@FieldsAreNonnullByDefault
@ParametersAreNonnullByDefault
public class FluidSlurryToSlurryCachedRecipe extends FluidChemicalToChemicalCachedRecipe<Slurry, SlurryStack, SlurryStackIngredient, FluidSlurryToSlurryRecipe> {

    public FluidSlurryToSlurryCachedRecipe(FluidSlurryToSlurryRecipe recipe, IInputHandler<@NonNull FluidStack> fluidInputHandler,
          IInputHandler<@NonNull SlurryStack> slurryInputHandler, IOutputHandler<@NonNull SlurryStack> outputHandler) {
        super(recipe, fluidInputHandler, slurryInputHandler, outputHandler);
    }
}