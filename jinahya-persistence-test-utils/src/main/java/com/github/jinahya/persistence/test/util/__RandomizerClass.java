package com.github.jinahya.persistence.test.util;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation for specifying the randomizer class of the annotated class.
 * <p>
 * This annotation takes precedence over the naming convention applied by
 * {@link __RandomizerLocator#STANDARD the standard locator}; use it for a class whose randomizer can not, or should
 * not, be named after it.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @apiNote This annotation requires the annotated class to reference its randomizer class, and is, hence,
 *         applicable only when both classes reside in a same source set. For an entity class in the {@code main} source
 *         set, whose randomizer class resides in the {@code test} source set, specify a custom
 *         {@link __RandomizerLocator locator} to
 *         {@link __RandomizerUtils#newRandomizedInstanceOf(Class, __RandomizerLocator)}.
 * @implNote This annotation is intentionally not {@link java.lang.annotation.Inherited @Inherited}; an
 *         inherited value would point to the randomizer of a superclass, whose
 *         {@link __Randomizer#targetClass targetClass} can not produce instances of the annotated subclass, while
 *         suppressing the lookup of the subclass' own randomizer.
 * @see __Randomizer
 * @see __RandomizerLocator#STANDARD
 * @see __RandomizerUtils
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
     * @implNote The return type is {@link Class}{@code <?>}, rather than
     *         {@code Class<? extends __Randomizer<?>>}, so that the annotation stays usable where the randomizer class
     *         is not visible as such. A value which does not extend {@link __Randomizer} is, therefore, caught at run
     *         time: it is logged, at {@link System.Logger.Level#WARNING WARNING}, and ignored, after which the naming
     *         convention is applied as if the class had not been annotated.
     */
    //    Class<? extends __Randomizer<?>> value();
    Class<?> value();
}
