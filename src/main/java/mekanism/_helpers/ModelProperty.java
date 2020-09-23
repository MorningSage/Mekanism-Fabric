package mekanism._helpers;

import com.google.common.base.Predicates;

import java.util.function.Predicate;

public class ModelProperty<T> implements Predicate<T> {

    private final Predicate<T> pred;

    public ModelProperty() {
        this(Predicates.alwaysTrue());
    }

    public ModelProperty(Predicate<T> pred) {
        this.pred = pred;
    }

    @Override
    public boolean test(T t) {
        return pred.test(t);
    }
}
