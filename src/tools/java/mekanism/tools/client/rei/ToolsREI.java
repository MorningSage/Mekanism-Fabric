package mekanism.tools.client.rei;

import com.google.common.collect.ImmutableList;
import me.shedaniel.rei.api.RecipeCategory;
import me.shedaniel.rei.api.RecipeHelper;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import me.shedaniel.rei.plugin.autocrafting.DefaultRecipeBookHandler;
import mekanism.api.providers.IItemProvider;
import mekanism.tools.common.IHasRepairType;
import mekanism.tools.common.MekanismTools;
import mekanism.tools.common.registries.ToolsItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;

@Environment(EnvType.CLIENT)
public class ToolsREI implements REIPluginV0 {

    @Override
    public Identifier getPluginIdentifier() {
        return MekanismTools.rl("rei_plugin");
    }

    @Override
    public void registerOthers(RecipeHelper recipeHelper) {
        // ToDo: REI does not have Anvil recipes.  Should we add them anyway?
        //recipeHelper.registerAutoCraftingHandler(new AnvilRecipeHandler());
    }

    //@Override
    //public void registerRecipes(IRecipeRegistration registry) {
    //    IVanillaRecipeFactory factory = registry.getVanillaRecipeFactory();
    //    //Add the Anvil repair recipes to JEI for all the different mekanism.tools and armors in Mekanism Tools
    //    for (IItemProvider toolsItem : ToolsItems.ITEMS.getAllItems()) {
    //        //Based off of how JEI adds for Vanilla items
    //        ItemStack damaged2 = toolsItem.getItemStack();
    //        damaged2.setDamage(damaged2.getMaxDamage() * 3 / 4);
    //        ItemStack damaged3 = toolsItem.getItemStack();
    //        damaged3.setDamage(damaged3.getMaxDamage() * 2 / 4);
//
    //        //Two damaged items combine to undamaged
    //        registry.addRecipes(ImmutableList.of(factory.createAnvilRecipe(damaged2, Collections.singletonList(damaged2), Collections.singletonList(damaged3))), VanillaRecipeCategoryUid.ANVIL);
//
    //        Item item = toolsItem.getItem();
    //        if (item instanceof IHasRepairType) {
    //            ItemStack[] repairStacks = ((IHasRepairType) item).getRepairMaterial().getMatchingStacks();
    //            //Damaged item + the repair material
    //            if (repairStacks.length > 0) {
    //                //While this is damaged1 it is down here as we don't need to bother creating the reference if we don't have a repair material
    //                ItemStack damaged1 = toolsItem.getItemStack();
    //                damaged1.setDamage(damaged1.getMaxDamage());
    //                registry.addRecipes(ImmutableList.of(factory.createAnvilRecipe(damaged1, Arrays.asList(repairStacks), Collections.singletonList(damaged2))), VanillaRecipeCategoryUid.ANVIL);
    //            }
    //        }
    //    }
    //}
}