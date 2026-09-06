package com.github.jinahya.persistence.test.util;

import java.util.function.Function;

/**
 * An interface for locating the persister class of an entity class.
 * <p>
 * A located class is expected to extend {@link __Persister} and to declare an accessible no-argument constructor; one
 * which does not is rejected, with a warning, by {@link __PersisterUtils}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __Persister
 * @see #STANDARD
 * @see __PersisterUtils#newPersistedInstanceOf(jakarta.persistence.EntityManager, Class, __PersisterLocator)
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
     * <p>
     * For an entity class {@code Foo}, that is, in order, {@code FooPersister}, {@code Foo_Persister},
     * {@code Foo$FooPersister}, and {@code Foo$Foo_Persister}.
     *
     * @implNote Unlike {@link __RandomizerLocator#STANDARD} and {@link __InstantiatorLocator#STANDARD}, this
     *         locator does not look into the persister class of an enclosing class of an entity class. A local class,
     *         or an anonymous class, can not be located either; specify a custom locator for those classes.
     */
    __PersisterLocator STANDARD = __PersisterLocator::locateStandard;

    // NullAway: this returns null when nothing is located, but the interface extends
    // Function<Class<?>, Class<?>>, whose apply() is modelled as @NonNull. Resolving it properly means
    // changing the contract (Optional, or throwing), which is a deliberate API decision, not an annotation.
    @SuppressWarnings("NullAway")
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

// ---------------------------------------------------------------------------------------------------------------------

    /**
     * Locates the class, which extends {@link __Persister}, for persisting the specified entity class.
     *
     * @param target the entity class whose persister class is located.
     * @return the persister class of the {@code target}; {@code null} when not found.
     */
    @Override
    Class<?> apply(Class<?> target);
}
