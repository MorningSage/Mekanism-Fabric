package mekanism.api.recipes;

import mekanism.api.annotations.MethodsReturnNonnullByDefault;
import mekanism.api.annotations.FieldsAreNonnullByDefault;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.recipes.chemical.FluidChemicalToChemicalRecipe;
import mekanism.api.recipes.inputs.FluidStackIngredient;
import mekanism.api.recipes.inputs.chemical.SlurryStackIngredient;
import net.minecraft.util.Identifier;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.ParametersAreNonnullByDefault;

@FieldsAreNonnullByDefault
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class FluidSlurryToSlurryRecipe extends FluidChemicalToChemicalRecipe<Slurry, SlurryStack, SlurryStackIngredient> {

    public FluidSlurryToSlurryRecipe(ResourceLocation id, FluidStackIngredient fluidInput, SlurryStackIngredient slurryInput, SlurryStack output) {
        super(id, fluidInput, slurryInput, output);
    }

    @Override
    public SlurryStack getOutput(FluidStack fluidStack, SlurryStack slurryStack) {
        return output.copy();
    }
}