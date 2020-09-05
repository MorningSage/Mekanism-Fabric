package mekanism.api.datagen.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.gson.JsonSyntaxException;
import mekanism.api._helpers_pls_remove.CraftingHelper;
import mekanism.api._helpers_pls_remove.ICondition;
import mekanism.api.annotations.MethodsReturnNonnullByDefault;
import mekanism.api.JsonConstants;
import mekanism.api.MekanismAPI;
import mekanism.api.annotations.FieldsAreNonnullByDefault;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.CriterionMerger;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

//TODO: We may also want to validate inputs, currently we are not validating our input ingredients as being valid, and are just validating the other parameters
@FieldsAreNonnullByDefault
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class MekanismRecipeBuilder<BUILDER extends MekanismRecipeBuilder<BUILDER>> {

    protected static Identifier mekSerializer(String name) {
        return new Identifier(MekanismAPI.MEKANISM_MODID, name);
    }

    protected final List<ICondition> conditions = new ArrayList<>();
    protected final Advancement.Task advancementBuilder = Advancement.Task.create();
    protected final Identifier serializerName;

    protected MekanismRecipeBuilder(Identifier serializerName) {
        this.serializerName = serializerName;
    }

    public BUILDER addCriterion(RecipeCriterion criterion) {
        return addCriterion(criterion.name, criterion.criterion);
    }

    public BUILDER addCriterion(String name, CriterionConditions criterion) {
        advancementBuilder.criterion(name, criterion);
        return (BUILDER) this;
    }

    public BUILDER addCondition(ICondition condition) {
        conditions.add(condition);
        return (BUILDER) this;
    }

    protected boolean hasCriteria() {
        return !advancementBuilder.getCriteria().isEmpty();
    }

    protected abstract RecipeResult getResult(Identifier id);

    protected void validate(Identifier id) {
    }

    public void build(Consumer<RecipeJsonProvider> consumer, Identifier id) {
        validate(id);
        if (hasCriteria()) {
            //If there is a way to "unlock" this recipe then add an advancement with the criteria
            advancementBuilder.parent(new Identifier("recipes/root")).criterion("has_the_recipe", RecipeUnlockedCriterion.create(id))
                  .rewards(AdvancementRewards.Builder.recipe(id)).criteriaMerger(CriterionMerger.OR);
        }
        consumer.accept(getResult(id));
    }

    protected abstract class RecipeResult implements RecipeJsonProvider {

        private final Identifier id;

        public RecipeResult(Identifier id) {
            this.id = id;
        }

        @Override
        public JsonObject toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(JsonConstants.TYPE, serializerName.toString());
            if (!conditions.isEmpty()) {
                JsonArray conditionsArray = new JsonArray();
                for (ICondition condition : conditions) {
                    conditionsArray.add(CraftingHelper.serialize(condition));
                }
                jsonObject.add(JsonConstants.CONDITIONS, conditionsArray);
            }
            this.serialize(jsonObject);
            return jsonObject;
        }

        // Why was this still Nonnull when we know it could be null?
        //@Nonnull
        @Nullable
        @Override
        public RecipeSerializer<?> getSerializer() {
            //Note: This may be null if something is screwed up but this method isn't actually used so it shouldn't matter
            // and in fact it will probably be null if only the API is included. But again, as we manually just use
            // the serializer's name this should not effect us
            return Registry.RECIPE_SERIALIZER.get(serializerName);
        }

        @Nonnull
        @Override
        public Identifier getRecipeId() {
            return this.id;
        }

        @Nullable
        @Override
        public JsonObject toAdvancementJson() {
            return hasCriteria() ? advancementBuilder.toJson() : null;
        }

        @Nullable
        @Override
        public Identifier getAdvancementId() {
            return new Identifier(id.getNamespace(), "recipes/" + id.getPath());
        }
    }
}