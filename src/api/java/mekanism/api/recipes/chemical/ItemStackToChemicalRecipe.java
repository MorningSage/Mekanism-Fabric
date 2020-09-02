package mekanism.api.recipes.chemical;

import mekanism.api.annotations.MethodsReturnNonnullByDefault;
import mekanism.api.annotations.FieldsAreNonnullByDefault;
import mekanism.api.annotations.NonNull;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.inputs.ItemStackIngredient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Predicate;

@FieldsAreNonnullByDefault
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class ItemStackToChemicalRecipe<CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>> extends MekanismRecipe implements
      Predicate<@NonNull ItemStack> {

    protected final ItemStackIngredient input;
    protected final STACK output;

    public ItemStackToChemicalRecipe(Identifier id, ItemStackIngredient input, STACK output) {
        super(id);
        this.input = input;
        this.output = output;
    }

    @Override
    public boolean test(ItemStack itemStack) {
        return input.test(itemStack);
    }

    public ItemStackIngredient getInput() {
        return input;
    }

    @Contract(value = "_ -> new", pure = true)
    public abstract STACK getOutput(ItemStack input);

    public STACK getOutputDefinition() {
        return output;
    }

    @Override
    public void write(PacketByteBuf buffer) {
        input.write(buffer);
        output.writeToPacket(buffer);
    }
}