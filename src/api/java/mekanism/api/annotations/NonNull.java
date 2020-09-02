package mekanism.api.annotations;

import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifier;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link Nonnull}, but for all the things (like type params)
 */
@TypeQualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_PARAMETER, ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.PARAMETER, ElementType.TYPE, ElementType.TYPE_USE})
@Nonnull
public @interface NonNull {}
