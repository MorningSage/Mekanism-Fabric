package mekanism.common.registration.impl;

import java.util.function.Supplier;
import mekanism.common.registration.WrappedDeferredRegister;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;

public class IRecipeSerializerDeferredRegister extends WrappedDeferredRegister<RecipeSerializer<?>> {

    public IRecipeSerializerDeferredRegister(String modid) {
        super(modid, ForgeRegistries.RECIPE_SERIALIZERS);
    }

    public <RECIPE extends Recipe<?>> IRecipeSerializerRegistryObject<RECIPE> register(String name, Supplier<RecipeSerializer<RECIPE>> sup) {
        return register(name, sup, IRecipeSerializerRegistryObject::new);
    }
}