package mekanism.api.datagen.recipe.builder;

import com.google.gson.JsonObject;
import mekanism.api.annotations.MethodsReturnNonnullByDefault;
import mekanism.api.JsonConstants;
import mekanism.api.SerializerHelper;
import mekanism.api.annotations.FieldsAreNonnullByDefault;
import mekanism.api.datagen.recipe.MekanismRecipeBuilder;
import mekanism.api.recipes.inputs.ItemStackIngredient;
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
public class CombinerRecipeBuilder extends MekanismRecipeBuilder<CombinerRecipeBuilder> {

    private final ItemStackIngredient mainInput;
    private final ItemStackIngredient extraInput;
    private final ItemStack output;

    protected CombinerRecipeBuilder(ItemStackIngredient mainInput, ItemStackIngredient extraInput, ItemStack output) {
        super(mekSerializer("combining"));
        this.mainInput = mainInput;
        this.extraInput = extraInput;
        this.output = output;
    }

    public static CombinerRecipeBuilder combining(ItemStackIngredient mainInput, ItemStackIngredient extraInput, ItemStack output) {
        if (output.isEmpty()) {
            throw new IllegalArgumentException("This combining recipe requires a non empty item output.");
        }
        return new CombinerRecipeBuilder(mainInput, extraInput, output);
    }

    @Override
    protected CombinerRecipeResult getResult(Identifier id) {
        return new CombinerRecipeResult(id);
    }

    public void build(Consumer<RecipeJsonProvider> consumer) {
        build(consumer, Registry.ITEM.getId(output.getItem()));
    }

    public class CombinerRecipeResult extends RecipeResult {

        protected CombinerRecipeResult(Identifier id) {
            super(id);
        }

        @Override
        public void serialize(@Nonnull JsonObject json) {
            json.add(JsonConstants.MAIN_INPUT, mainInput.serialize());
            json.add(JsonConstants.EXTRA_INPUT, extraInput.serialize());
            json.add(JsonConstants.OUTPUT, SerializerHelper.serializeItemStack(output));
        }
    }
}