package com.github.jinahya.persistence.test;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation for specifying the randomizer class of the annotated class.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __Randomizer
 * @see __RandomizerUtils
 * @apiNote This annotation requires the annotated class to reference its randomizer class, and is, hence, applicable
 *         only when both classes reside in a same source set. For an entity class in the {@code main} source set,
 *         whose randomizer class resides in the {@code test} source set, specify a custom
 *         {@link __RandomizerLocator locator} to
 *         {@link __RandomizerUtils#newRandomizedInstanceOf(Class, __RandomizerLocator)}.
 * @implNote This annotation is intentionally not {@link java.lang.annotation.Inherited @Inherited}; an inherited value
 *         would point to the
 *         randomizer of a superclass, whose {@link __Randomizer#targetClass targetClass} can not produce instances of
 *         the annotated subclass, while suppressing the lookup of the subclass' own randomizer.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S1452" // Generic wildcard types should not be used in return types
})
public @interface __RandomizerClass {

    /**
     * Returns the randomizer class, which extends {@link __Randomizer}, of the annotated class.
     *
     * @return the randomizer class of the annotated class.
     */
    //    Class<? extends __Randomizer<?>> value();
    Class<?> value();
}
