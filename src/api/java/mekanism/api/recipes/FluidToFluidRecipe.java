package mekanism.api.recipes;

import mekanism.api.annotations.MethodsReturnNonnullByDefault;
import mekanism.api.annotations.FieldsAreNonnullByDefault;
import mekanism.api.annotations.NonNull;
import mekanism.api.recipes.inputs.FluidStackIngredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Identifier;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Contract;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Predicate;

@FieldsAreNonnullByDefault
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class FluidToFluidRecipe extends MekanismRecipe implements Predicate<@NonNull FluidStack> {

    private final FluidStackIngredient input;
    private final FluidStack output;

    public FluidToFluidRecipe(ResourceLocation id, FluidStackIngredient input, FluidStack output) {
        super(id);
        this.input = input;
        this.output = output;
    }

    @Override
    public boolean test(FluidStack fluidStack) {
        return this.input.test(fluidStack);
    }

    public FluidStackIngredient getInput() {
        return input;
    }

    public FluidStack getOutputRepresentation() {
        return output;
    }

    @Contract(value = "_->new", pure = true)
    public FluidStack getOutput(FluidStack input) {
        return this.output.copy();
    }

    @Override
    public void write(PacketBuffer buffer) {
        input.write(buffer);
        output.writeToPacket(buffer);
    }
}