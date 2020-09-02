package mekanism.api.datagen.recipe.builder;

import com.google.gson.JsonObject;
import mekanism.api.annotations.MethodsReturnNonnullByDefault;
import mekanism.api.JsonConstants;
import mekanism.api.SerializerHelper;
import mekanism.api.annotations.FieldsAreNonnullByDefault;
import mekanism.api.datagen.recipe.MekanismRecipeBuilder;
import mekanism.api.recipes.inputs.ItemStackIngredient;
import mekanism.api.recipes.inputs.chemical.GasStackIngredient;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

@FieldsAreNonnullByDefault
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class NucleosynthesizingRecipeBuilder extends MekanismRecipeBuilder<NucleosynthesizingRecipeBuilder> {

    private final ItemStackIngredient itemInput;
    private final GasStackIngredient gasInput;
    private final ItemStack output;
    private final int duration;

    protected NucleosynthesizingRecipeBuilder(ItemStackIngredient itemInput, GasStackIngredient gasInput, ItemStack output, int duration) {
        super(mekSerializer("nucleosynthesizing"));
        this.itemInput = itemInput;
        this.gasInput = gasInput;
        this.output = output;
        this.duration = duration;
    }

    public static NucleosynthesizingRecipeBuilder nucleosynthesizing(ItemStackIngredient itemInput, GasStackIngredient gasInput, ItemStack output, int duration) {
        if (output.isEmpty()) {
            throw new IllegalArgumentException("This nucleosynthesizing recipe requires a non empty item output.");
        }
        if (duration <= 0) {
            throw new IllegalArgumentException("This nucleosynthesizing recipe must have a positive duration.");
        }
        return new NucleosynthesizingRecipeBuilder(itemInput, gasInput, output, duration);
    }

    @Override
    protected NucleosynthesizingRecipeResult getResult(Identifier id) {
        return new NucleosynthesizingRecipeResult(id);
    }

    public void build(Consumer<RecipeJsonProvider> consumer) {
        build(consumer, Registry.ITEM.getId(output.getItem()));
    }

    public class NucleosynthesizingRecipeResult extends RecipeResult {

        protected NucleosynthesizingRecipeResult(Identifier id) {
            super(id);
        }

        @Override
        public void serialize(@Nonnull JsonObject json) {
            json.add(JsonConstants.ITEM_INPUT, itemInput.serialize());
            json.add(JsonConstants.GAS_INPUT, gasInput.serialize());
            json.add(JsonConstants.OUTPUT, SerializerHelper.serializeItemStack(output));
            json.addProperty(JsonConstants.DURATION, duration);
        }
    }
}