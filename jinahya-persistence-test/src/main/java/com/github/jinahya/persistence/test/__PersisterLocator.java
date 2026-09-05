package com.github.jinahya.persistence.test;

import java.util.function.Function;

/**
 * An interface for locating the persister class of an entity class.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __Persister
 * @see #STANDARD
 */
@FunctionalInterface
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public interface __PersisterLocator extends Function<Class<?>, Class<?>> {

    /**
     * The standard locator which locates, in order, a sibling class of an entity class, and a class enclosed by the
     * entity class; both of which should extend {@link __Persister}, and have a postfix of either {@code "Persister"}
     * or {@code "_Persister"}.
     *
     * @implNote Unlike {@link __RandomizerLocator#STANDARD} and {@link __InstantiatorLocator#STANDARD}, this locator
     *         does not look into the persister class of an enclosing class of an entity class.
     */
    __PersisterLocator STANDARD = __PersisterLocator::locateStandard;

    private static Class<?> locateStandard(final Class<?> target) {
        assert target != null;
        // [targetClassPersister], [targetClass_Persister],
        // then [targetClass$targetClassPersister], [targetClass$targetClass_Persister]
        return ___Utils.siblingClassForPostfix(
                target,
                __Persister.class,
                "Persister",
                "_Persister",
                "$" + target.getSimpleName() + "Persister",
                "$" + target.getSimpleName() + "_Persister"
        );
    }

    /**
     * Locates the class, which extends {@link __Persister}, for persisting the specified entity class.
     *
     * @param target the entity class whose persister class is located.
     * @return the persister class of the {@code target}; {@code null} when not found.
     */
    @Override
    Class<?> apply(Class<?> target);
}
