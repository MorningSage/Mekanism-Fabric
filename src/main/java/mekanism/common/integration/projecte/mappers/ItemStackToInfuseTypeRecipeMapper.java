package mekanism.common.integration.projecte.mappers;/*package mekanism.common.integration.projecte.mappers;

import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.recipes.ItemStackToInfuseTypeRecipe;
import mekanism.common.integration.projecte.IngredientHelper;
import mekanism.common.recipe.MekanismRecipeType;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.mapper.recipe.IRecipeTypeMapper;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;

@RecipeTypeMapper
public class ItemStackToInfuseTypeRecipeMapper implements IRecipeTypeMapper {

    @Override
    public String getName() {
        return "MekItemStackToInfuseType";
    }

    @Override
    public String getDescription() {
        return "Maps Mekanism item stack to infuse type conversion recipes.";
    }

    @Override
    public boolean canHandle(IRecipeType<?> recipeType) {
        return recipeType == MekanismRecipeType.INFUSION_CONVERSION;
    }

    @Override
    public boolean handleRecipe(IMappingCollector<NormalizedSimpleStack, Long> mapper, IRecipe<?> iRecipe) {
        if (!(iRecipe instanceof ItemStackToInfuseTypeRecipe)) {
            //Double check that we have a type of recipe we know how to handle
            return false;
        }
        boolean handled = false;
        ItemStackToInfuseTypeRecipe recipe = (ItemStackToInfuseTypeRecipe) iRecipe;
        for (ItemStack representation : recipe.getInput().getRepresentations()) {
            InfusionStack output = recipe.getOutput(representation);
            if (!output.isEmpty()) {
                IngredientHelper ingredientHelper = new IngredientHelper(mapper);
                ingredientHelper.put(representation);
                if (ingredientHelper.addAsConversion(output)) {
                    handled = true;
                }
            }
        }
        return handled;
    }
}*/