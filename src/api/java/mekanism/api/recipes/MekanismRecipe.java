package mekanism.api.recipes;

import mekanism.api.inventory.IgnoredIInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

//TODO: Make implementations override equals and hashcode?
public abstract class MekanismRecipe implements Recipe<IgnoredIInventory> {

    private final Identifier id;

    protected MekanismRecipe(Identifier id) {
        this.id = id;
    }

    /**
     * Writes this recipe to a PacketBuffer.
     *
     * @param buffer The buffer to write to.
     */
    public abstract void write(PacketByteBuf buffer);

    @Nonnull
    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public boolean matches(@Nonnull IgnoredIInventory inv, @Nonnull World world) {
        return true;
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        //Note: If we make this non dynamic, we can make it show in vanilla's crafting book and also then obey the recipe locking.
        // For now none of that works/makes sense in our concept so don't lock it
        return true;
    }

    @Nonnull
    @Override
    public ItemStack craft(@Nonnull IgnoredIInventory inv) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Nonnull
    @Override
    public ItemStack getOutput() {
        return ItemStack.EMPTY;
    }
}