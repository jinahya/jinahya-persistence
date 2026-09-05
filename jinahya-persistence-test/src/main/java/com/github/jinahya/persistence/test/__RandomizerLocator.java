package com.github.jinahya.persistence.test;

import java.util.Optional;
import java.util.function.Function;

/**
 * An interface for locating the randomizer class of a target class.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __Randomizer
 * @see #STANDARD
 */
@FunctionalInterface
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public interface __RandomizerLocator extends Function<Class<?>, Class<?>> {

    /**
     * The standard locator which locates, in order, the {@link __RandomizerClass#value() value} of a
     * {@link __RandomizerClass @__RandomizerClass} annotation declared on a target class, the randomizer class enclosed
     * by the randomizer class of the enclosing class of the target class, a sibling class of the target class, and a
     * class enclosed by the target class; all but the annotated value should extend {@link __Randomizer}, and have a
     * postfix of either {@code "Randomizer"} or {@code "_Randomizer"}.
     *
     * @implNote A local class, or an anonymous class, can not be located by this locator, for no class can be declared
     *         with a name required by the convention; specify a custom locator for those classes.
     */
    __RandomizerLocator STANDARD = __RandomizerLocator::locateStandard;

    private static Class<?> locateStandard(final Class<?> target) {
        assert target != null;
        {
            final var annotation = target.getDeclaredAnnotation(__RandomizerClass.class);
            if (annotation != null) {
                final var randomizerClass = ___Utils.classForSupertype(annotation.value(), __Randomizer.class);
                if (randomizerClass != null) {
                    return randomizerClass;
                }
            }
        }
        // if enclosed,
        // try to find [enclosingClass_Randomizer$targetClass_Randomizer]
        {
            final Class<?> enclosedRandomizerClass =
                    Optional.ofNullable(target.getEnclosingClass())
                            .map(__RandomizerLocator::locateStandard)
                            .map(c -> ___Utils.siblingClassForPostfix(
                                    c,
                                    __Randomizer.class,
                                    "$" + target.getSimpleName() + "Randomizer",
                                    "$" + target.getSimpleName() + "_Randomizer"
                            ))
                            .orElse(null);
            if (enclosedRandomizerClass != null) {
                return enclosedRandomizerClass;
            }
        }
        // [targetClassRandomizer], [targetClass_Randomizer],
        // then [targetClass$targetClassRandomizer], [targetClass$targetClass_Randomizer]
        return ___Utils.siblingClassForPostfix(
                target,
                __Randomizer.class,
                "Randomizer",
                "_Randomizer",
                "$" + target.getSimpleName() + "Randomizer",
                "$" + target.getSimpleName() + "_Randomizer"
        );
    }

    /**
     * Locates the class, which extends {@link __Randomizer}, for randomizing the specified target class.
     *
     * @param target the target class whose randomizer class is located.
     * @return the randomizer class of the {@code target}; {@code null} when not found.
     */
    @Override
    Class<?> apply(Class<?> target);
}
