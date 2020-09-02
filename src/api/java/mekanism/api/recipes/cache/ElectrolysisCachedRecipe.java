package mekanism.api.recipes.cache;

import mekanism.api._helpers_pls_remove.FluidStack;
import mekanism.api.annotations.FieldsAreNonnullByDefault;
import mekanism.api.annotations.NonNull;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.recipes.ElectrolysisRecipe;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.outputs.IOutputHandler;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.ParametersAreNonnullByDefault;

@FieldsAreNonnullByDefault
@ParametersAreNonnullByDefault
public class ElectrolysisCachedRecipe extends CachedRecipe<ElectrolysisRecipe> {

    private final IOutputHandler<@NonNull Pair<GasStack, GasStack>> outputHandler;
    private final IInputHandler<@NonNull FluidStack> inputHandler;

    public ElectrolysisCachedRecipe(ElectrolysisRecipe recipe, IInputHandler<@NonNull FluidStack> inputHandler, IOutputHandler<@NonNull Pair<GasStack, GasStack>> outputHandler) {
        super(recipe);
        this.inputHandler = inputHandler;
        this.outputHandler = outputHandler;
    }

    @Override
    protected int getOperationsThisTick(int currentMax) {
        currentMax = super.getOperationsThisTick(currentMax);
        if (currentMax <= 0) {
            //If our parent checks show we can't operate then return so
            return currentMax;
        }
        FluidStack recipeFluid = inputHandler.getRecipeInput(recipe.getInput());
        //Test to make sure we can even perform a single operation. This is akin to !recipe.test(inputFluid)
        if (recipeFluid.isEmpty()) {
            return -1;
        }
        //Calculate the current max based on the fluid input
        currentMax = inputHandler.operationsCanSupport(recipe.getInput(), currentMax);
        if (currentMax <= 0) {
            //If our input can't handle it return that we should be resetting
            return -1;
        }
        //Calculate the max based on the space in the output
        return outputHandler.operationsRoomFor(recipe.getOutput(recipeFluid), currentMax);
    }

    @Override
    public boolean isInputValid() {
        return recipe.test(inputHandler.getInput());
    }

    @Override
    protected void finishProcessing(int operations) {
        //TODO - Performance: Eventually we should look into caching this stuff from when getOperationsThisTick was called?
        FluidStack recipeFluid = inputHandler.getRecipeInput(recipe.getInput());
        if (recipeFluid.isEmpty()) {
            //Something went wrong, this if should never really be true if we got to finishProcessing
            return;
        }
        inputHandler.use(recipeFluid, operations);
        outputHandler.handleOutput(recipe.getOutput(recipeFluid), operations);
    }
}