package mekanism.client.jei.interfaces;

import javax.annotation.Nullable;

import me.shedaniel.rei.plugin.DefaultPlugin;
import mekanism.client.gui.element.GuiElement;
import mekanism.common.Mekanism;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.client.gui.Element;
import net.minecraft.util.Identifier;

public interface IJEIRecipeArea<ELEMENT extends GuiElement> extends Element {

    /**
     * @return null if not an active recipe area, otherwise the category
     */
    @Nullable
    Identifier[] getRecipeCategories();

    default boolean isActive() {
        return true;
    }

    ELEMENT jeiCategories(@Nullable Identifier... recipeCategories);

    default ELEMENT jeiCategory(TileEntityMekanism tile) {
        return jeiCategories(tile.getBlockType().getRegistryName());
    }

    default ELEMENT jeiCrafting() {
        if (Mekanism.hooks.JEILoaded) {
            return jeiCategories(DefaultPlugin.CRAFTING);
        }
        return jeiCategories((Identifier) null);
    }

    default boolean isMouseOverJEIArea(double mouseX, double mouseY) {
        return isMouseOver(mouseX, mouseY);
    }
}