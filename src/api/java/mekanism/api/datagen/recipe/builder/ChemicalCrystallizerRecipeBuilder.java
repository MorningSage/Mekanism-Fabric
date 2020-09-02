package mekanism.api.datagen.recipe.builder;

import com.google.gson.JsonObject;
import mekanism.api.annotations.MethodsReturnNonnullByDefault;
import mekanism.api.JsonConstants;
import mekanism.api.SerializerHelper;
import mekanism.api.annotations.FieldsAreNonnullByDefault;
import mekanism.api.chemical.ChemicalType;
import mekanism.api.datagen.recipe.MekanismRecipeBuilder;
import mekanism.api.recipes.inputs.chemical.IChemicalStackIngredient;
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
public class ChemicalCrystallizerRecipeBuilder extends MekanismRecipeBuilder<ChemicalCrystallizerRecipeBuilder> {

    private final ChemicalType chemicalType;
    private final IChemicalStackIngredient<?, ?> input;
    private final ItemStack output;

    protected ChemicalCrystallizerRecipeBuilder(Identifier serializerName, IChemicalStackIngredient<?, ?> input, ItemStack output) {
        super(serializerName);
        this.input = input;
        this.chemicalType = ChemicalType.getTypeFor(input);
        this.output = output;
    }

    public static ChemicalCrystallizerRecipeBuilder crystallizing(IChemicalStackIngredient<?, ?> input, ItemStack output) {
        if (output.isEmpty()) {
            throw new IllegalArgumentException("This crystallizing recipe requires a non empty item output.");
        }
        return new ChemicalCrystallizerRecipeBuilder(mekSerializer("crystallizing"), input, output);
    }

    @Override
    protected ChemicalCrystallizerRecipeResult getResult(Identifier id) {
        return new ChemicalCrystallizerRecipeResult(id);
    }

    public void build(Consumer<RecipeJsonProvider> consumer) {
        build(consumer, Registry.ITEM.getId(output.getItem()));
    }

    public class ChemicalCrystallizerRecipeResult extends RecipeResult {

        protected ChemicalCrystallizerRecipeResult(Identifier id) {
            super(id);
        }

        @Override
        public void serialize(@Nonnull JsonObject json) {
            json.addProperty(JsonConstants.CHEMICAL_TYPE, chemicalType.asString());
            json.add(JsonConstants.INPUT, input.serialize());
            json.add(JsonConstants.OUTPUT, SerializerHelper.serializeItemStack(output));
        }
    }
}