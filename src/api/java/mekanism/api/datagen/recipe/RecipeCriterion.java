package mekanism.api.datagen.recipe;

import mekanism.api.annotations.FieldsAreNonnullByDefault;
import net.minecraft.advancement.criterion.CriterionConditions;

import javax.annotation.ParametersAreNonnullByDefault;

@FieldsAreNonnullByDefault
@ParametersAreNonnullByDefault
public class RecipeCriterion {

    public final String name;
    public final CriterionConditions criterion;

    private RecipeCriterion(String name, CriterionConditions criterion) {
        this.name = name;
        this.criterion = criterion;
    }

    public static RecipeCriterion of(String name, CriterionConditions criterion) {
        return new RecipeCriterion(name, criterion);
    }
}