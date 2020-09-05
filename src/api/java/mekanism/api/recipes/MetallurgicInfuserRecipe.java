package mekanism.api.recipes;

import mekanism.api.annotations.MethodsReturnNonnullByDefault;
import mekanism.api.annotations.FieldsAreNonnullByDefault;
import mekanism.api.annotations.NonNull;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.recipes.inputs.ItemStackIngredient;
import mekanism.api.recipes.inputs.chemical.InfusionStackIngredient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;
import java.util.function.BiPredicate;

@FieldsAreNonnullByDefault
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class MetallurgicInfuserRecipe extends MekanismRecipe implements BiPredicate<InfusionStack, ItemStack> {

    private final ItemStackIngredient itemInput;
    private final InfusionStackIngredient infusionInput;
    private final ItemStack output;

    public MetallurgicInfuserRecipe(Identifier id, ItemStackIngredient itemInput, InfusionStackIngredient infusionInput, ItemStack output) {
        super(id);
        this.itemInput = itemInput;
        this.infusionInput = infusionInput;
        this.output = output.copy();
    }

    @Override
    public boolean test(InfusionStack infusionContainer, ItemStack itemStack) {
        return infusionInput.test(infusionContainer) && itemInput.test(itemStack);
    }

    public List<@NonNull ItemStack> getOutputDefinition() {
        return output.isEmpty() ? Collections.emptyList() : Collections.singletonList(output);
    }

    @Contract(value = "_, _ -> new", pure = true)
    public ItemStack getOutput(InfusionStack inputInfuse, ItemStack inputItem) {
        return output.copy();
    }

    public InfusionStackIngredient getInfusionInput() {
        return this.infusionInput;
    }

    public ItemStackIngredient getItemInput() {
        return this.itemInput;
    }

    @Override
    public void write(PacketByteBuf buffer) {
        itemInput.write(buffer);
        infusionInput.write(buffer);
        buffer.writeItemStack(output);
    }
}