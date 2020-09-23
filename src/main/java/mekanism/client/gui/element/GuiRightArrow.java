package mekanism.client.gui.element;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.jei.interfaces.IJEIRecipeArea;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.util.Identifier;

public class GuiRightArrow extends GuiTextureOnlyElement implements IJEIRecipeArea<GuiRightArrow> {

    private static final Identifier ARROW = MekanismUtils.getResource(ResourceType.GUI, "right_arrow.png");

    private Identifier[] recipeCategories;

    public GuiRightArrow(IGuiWrapper gui, int x, int y) {
        super(ARROW, gui, x, y, 22, 15);
    }

    @Nonnull
    @Override
    public GuiRightArrow jeiCategories(@Nullable Identifier... recipeCategories) {
        this.recipeCategories = recipeCategories;
        return this;
    }

    @Nullable
    @Override
    public Identifier[] getRecipeCategories() {
        return recipeCategories;
    }
}