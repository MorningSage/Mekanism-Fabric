package mekanism.api.datagen.recipe.builder;

import com.google.gson.JsonObject;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
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

@FieldsAreNonnullByDefault
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ItemStackToItemStackRecipeBuilder extends MekanismRecipeBuilder<ItemStackToItemStackRecipeBuilder> {

    private final ItemStackIngredient input;
    private final ItemStack output;

    protected ItemStackToItemStackRecipeBuilder(ItemStackIngredient input, ItemStack output, Identifier serializerName) {
        super(serializerName);
        this.input = input;
        this.output = output;
    }

    public static ItemStackToItemStackRecipeBuilder crushing(ItemStackIngredient input, ItemStack output) {
        if (output.isEmpty()) {
            throw new IllegalArgumentException("This crushing recipe requires a non empty item output.");
        }
        return new ItemStackToItemStackRecipeBuilder(input, output, mekSerializer("crushing"));
    }

    public static ItemStackToItemStackRecipeBuilder enriching(ItemStackIngredient input, ItemStack output) {
        if (output.isEmpty()) {
            throw new IllegalArgumentException("This enriching recipe requires a non empty item output.");
        }
        return new ItemStackToItemStackRecipeBuilder(input, output, mekSerializer("enriching"));
    }

    public static ItemStackToItemStackRecipeBuilder smelting(ItemStackIngredient input, ItemStack output) {
        if (output.isEmpty()) {
            throw new IllegalArgumentException("This smelting recipe requires a non empty item output.");
        }
        return new ItemStackToItemStackRecipeBuilder(input, output, mekSerializer("smelting"));
    }

    @Override
    protected ItemStackToItemStackRecipeResult getResult(Identifier id) {
        return new ItemStackToItemStackRecipeResult(id);
    }

    public void build(Consumer<RecipeJsonProvider> consumer) {
        build(consumer, Registry.ITEM.getId(output.getItem()));
    }

    public class ItemStackToItemStackRecipeResult extends RecipeResult {

        protected ItemStackToItemStackRecipeResult(Identifier id) {
            super(id);
        }

        @Override
        public void serialize(@Nonnull JsonObject json) {
            json.add(JsonConstants.INPUT, input.serialize());
            json.add(JsonConstants.OUTPUT, SerializerHelper.serializeItemStack(output));
        }
    }
}