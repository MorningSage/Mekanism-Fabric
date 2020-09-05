package mekanism.api._helpers_pls_remove;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class NBTIngredient extends Ingredient {
    private final ItemStack stack;
    protected NBTIngredient(ItemStack stack) {
        super(Stream.of(new Ingredient.StackEntry(stack)));
        this.stack = stack;
    }

    @Override
    public boolean test(@Nullable ItemStack input) {
        if (input == null) return false;

        //Can't use areItemStacksEqualUsingNBTShareTag because it compares stack size as well
        return this.stack.getItem() == input.getItem() && this.stack.getDamage() == input.getDamage() && this.stack.areShareTagsEqual(input);
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", CraftingHelper.getID(Serializer.INSTANCE).toString());
        json.addProperty("item", stack.getItem().getRegistryName().toString());
        json.addProperty("count", stack.getCount());
        if (stack.hasTag())
            json.addProperty("nbt", stack.getTag().toString());
        return json;
    }

    public static class Serializer implements IIngredientSerializer<NBTIngredient> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public NBTIngredient parse(PacketByteBuf buffer) {
            return new NBTIngredient(buffer.readItemStack());
        }

        @Override
        public NBTIngredient parse(JsonObject json) {
            return new NBTIngredient(CraftingHelper.getItemStack(json, true));
        }

        @Override
        public void write(PacketBuffer buffer, NBTIngredient ingredient) {
            buffer.writeItemStack(ingredient.stack);
        }
    }
}
