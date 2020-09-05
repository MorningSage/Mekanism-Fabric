package mekanism.api.recipes;

import mekanism.api.annotations.MethodsReturnNonnullByDefault;
import mekanism.api.annotations.FieldsAreNonnullByDefault;
import mekanism.api.annotations.NonNull;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.ChemicalType;
import mekanism.api.chemical.merged.BoxedChemicalStack;
import mekanism.api.recipes.inputs.chemical.IChemicalStackIngredient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

@FieldsAreNonnullByDefault
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class ChemicalCrystallizerRecipe extends MekanismRecipe implements Predicate<@NonNull BoxedChemicalStack> {

    private final ChemicalType chemicalType;
    private final IChemicalStackIngredient<?, ?> input;
    private final ItemStack output;

    public ChemicalCrystallizerRecipe(Identifier id, IChemicalStackIngredient<?, ?> input, ItemStack output) {
        super(id);
        this.input = input;
        this.chemicalType = ChemicalType.getTypeFor(input);
        this.output = output.copy();
    }

    @Contract(value = "_ -> new", pure = true)
    public ItemStack getOutput(BoxedChemicalStack input) {
        return output.copy();
    }

    public List<ItemStack> getOutputDefinition() {
        return output.isEmpty() ? Collections.emptyList() : Collections.singletonList(output);
    }

    @Override
    public boolean test(BoxedChemicalStack chemicalStack) {
        return chemicalType == chemicalStack.getChemicalType() && testInternal(chemicalStack.getChemicalStack());
    }

    public boolean test(ChemicalStack<?> stack) {
        return chemicalType == ChemicalType.getTypeFor(stack) && testInternal(stack);
    }

    private <CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>> boolean testInternal(STACK stack) {
        return ((IChemicalStackIngredient<CHEMICAL, STACK>) input).test(stack);
    }

    public IChemicalStackIngredient<?, ?> getInput() {
        return input;
    }

    @Override
    public void write(PacketByteBuf buffer) {
        buffer.writeEnumConstant(chemicalType);
        input.write(buffer);
        buffer.writeItemStack(output);
    }
}