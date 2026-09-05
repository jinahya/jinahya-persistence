package com.github.jinahya.persistence.test.util;

import java.util.Optional;
import java.util.function.Function;

/**
 * An interface for locating the instantiator class of a target class.
 * <p>
 * A located class is expected to extend {@link __Instantiator} and to declare an accessible no-argument constructor;
 * one which does not is rejected, with a warning, by {@link __InstantiatorUtils}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __Instantiator
 * @see #STANDARD
 * @see __InstantiatorUtils#newInstantiatedInstanceOf(Class, __InstantiatorLocator)
 */
@FunctionalInterface
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public interface __InstantiatorLocator extends Function<Class<?>, Class<?>> {

    /**
     * The standard locator which locates, in order, the instantiator class enclosed by the instantiator class of the
     * enclosing class of a target class, a sibling class of the target class, and a class enclosed by the target class;
     * all of which should extend {@link __Instantiator}, and have a postfix of either {@code "Instantiator"} or
     * {@code "_Instantiator"}.
     * <p>
     * For a target class {@code Foo}, that is, in order, {@code FooInstantiator}, {@code Foo_Instantiator},
     * {@code Foo$FooInstantiator}, and {@code Foo$Foo_Instantiator}, with the enclosing-class form probed first when
     * {@code Foo} is itself a nested class.
     *
     * @implNote A local class, or an anonymous class, can not be located by this locator, for no class can be declared
     *         with a name required by the convention; specify a custom locator for those classes.
     */
    __InstantiatorLocator STANDARD = __InstantiatorLocator::locateStandard;

    private static Class<?> locateStandard(final Class<?> target) {
        assert target != null;
        // if enclosed,
        // try to find [enclosingClass_Instantiator$targetClass_Instantiator]
        {
            final Class<?> enclosedInstantiatorClass =
                    Optional.ofNullable(target.getEnclosingClass())
                            .map(__InstantiatorLocator::locateStandard)
                            .map(c -> ___Utils.siblingClassForPostfix(
                                    c,
                                    __Instantiator.class,
                                    "$" + target.getSimpleName() + "Instantiator",
                                    "$" + target.getSimpleName() + "_Instantiator"
                            ))
                            .orElse(null);
            if (enclosedInstantiatorClass != null) {
                return enclosedInstantiatorClass;
            }
        }
        // [targetClassInstantiator], [targetClass_Instantiator],
        // then [targetClass$targetClassInstantiator], [targetClass$targetClass_Instantiator]
        return ___Utils.siblingClassForPostfix(
                target,
                __Instantiator.class,
                "Instantiator",
                "_Instantiator",
                "$" + target.getSimpleName() + "Instantiator",
                "$" + target.getSimpleName() + "_Instantiator"
        );
    }

// ---------------------------------------------------------------------------------------------------------------------

    /**
     * Locates the class, which extends {@link __Instantiator}, for instantiating the specified target class.
     *
     * @param target the target class whose instantiator class is located.
     * @return the instantiator class of the {@code target}; {@code null} when not found.
     */
    @Override
    Class<?> apply(Class<?> target);
}
