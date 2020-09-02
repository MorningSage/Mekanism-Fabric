package mekanism.api.recipes.inputs.chemical;

import com.google.gson.JsonElement;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.providers.IPigmentProvider;
import mekanism.api.recipes.inputs.chemical.ChemicalStackIngredient.MultiIngredient;
import mekanism.api.recipes.inputs.chemical.ChemicalStackIngredient.SingleIngredient;
import mekanism.api.recipes.inputs.chemical.ChemicalStackIngredient.TaggedIngredient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.tag.Tag;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface PigmentStackIngredient extends IChemicalStackIngredient<Pigment, PigmentStack> {

    static PigmentStackIngredient from(@Nonnull PigmentStack instance) {
        return from(instance.getType(), instance.getAmount());
    }

    static PigmentStackIngredient from(@Nonnull IPigmentProvider pigment, long amount) {
        return new Single(pigment.getStack(amount));
    }

    static PigmentStackIngredient from(@Nonnull Tag<Pigment> tag, long amount) {
        return new Tagged(tag, amount);
    }

    static PigmentStackIngredient read(PacketByteBuf buffer) {
        return ChemicalIngredientDeserializer.PIGMENT.read(buffer);
    }

    static PigmentStackIngredient deserialize(@Nullable JsonElement json) {
        return ChemicalIngredientDeserializer.PIGMENT.deserialize(json);
    }

    static PigmentStackIngredient createMulti(PigmentStackIngredient... ingredients) {
        return ChemicalIngredientDeserializer.PIGMENT.createMulti(ingredients);
    }

    @Override
    default ChemicalIngredientInfo<Pigment, PigmentStack> getIngredientInfo() {
        return ChemicalIngredientInfo.PIGMENT;
    }

    class Single extends SingleIngredient<Pigment, PigmentStack> implements PigmentStackIngredient {

        protected Single(@Nonnull PigmentStack stack) {
            super(stack);
        }
    }

    class Tagged extends TaggedIngredient<Pigment, PigmentStack> implements PigmentStackIngredient {

        protected Tagged(@Nonnull Tag<Pigment> tag, long amount) {
            super(tag, amount);
        }
    }

    class Multi extends MultiIngredient<Pigment, PigmentStack, PigmentStackIngredient> implements PigmentStackIngredient {

        protected Multi(@Nonnull PigmentStackIngredient... ingredients) {
            super(ingredients);
        }
    }
}