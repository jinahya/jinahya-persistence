package com.github.jinahya.persistence.test.util;

import jakarta.persistence.EntityManager;
import org.jspecify.annotations.Nullable;

import java.lang.invoke.MethodHandles;
import java.util.Objects;
import java.util.Optional;

/**
 * Utilities for {@link __Persister}.
 * <p>
 * The {@code newPersistedInstanceOf} methods chain the three roles of this package: an entity instance is instantiated
 * and randomized by the {@link __Randomizer} found for the entity class, then handed to the {@link __Persister} found
 * for it.
 * <p>
 * A persister is found for an entity class by the {@link #locateStandard(Class) naming convention}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __Persister
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class __PersisterUtils {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    /**
     * Locates, by the naming convention, the persister class of the specified entity class.
     * <p>
     * The persister class is a sibling of the {@code target} -- declared in the same package, beside it -- which
     * extends {@link __Persister} and has a postfix of either {@code "Persister"} or {@code "_Persister"}. For a entity
     * class {@code Foo}, that is {@code FooPersister}, and then {@code Foo_Persister}.
     * <p>
     * The convention spans source sets: a {@code Foo} declared in {@code main} and a {@code FooPersister} declared in
     * {@code test} are the same package, and both are on the test classpath.
     *
     * @param target the entity class whose persister class is located.
     * @return the persister class of the {@code target}; {@code null} when not found.
     * @implNote A class which is not a sibling of the {@code target} is never located: neither a local nor an
     *         anonymous class, which can not carry the required name, nor a class nested inside the {@code target},
     *         which would have to be declared in the source of the {@code target} itself. A class nested in a entity
     *         class of {@code main} therefore has to be declared as a top-level class to be randomizable here.
     */
    @Nullable
    static Class<?> locateStandard(final Class<?> target) {
        assert target != null;
        return ___Utils.siblingClassForPostfixes(target, "Persister", "_Persister");
    }

    // ---------------------------------------------------------------------------------------------------------------------
    @SuppressWarnings({
            "unchecked"
    })
    private static <T> Optional<__Persister<T>> newPersisterInstanceOf(final Class<T> target) {
        assert target != null;
        final __Persister<?> persister =
                ___Utils.newLocatedInstance(target, __Persister.class, locateStandard(target));
        if (persister == null) {
            return Optional.empty();
        }
        // one is provided, so it is meant to be used; from here, anything wrong with it is a fault
        ___Utils.requireAccepting(target, persister.targetClass, persister);
        return Optional.of((__Persister<T>) persister);
    }

    /**
     * Returns a new persisted instance of the specified entity class, using the randomizer and the persister found for
     * it.
     *
     * @param entityManager an entity manager.
     * @param entityClass   the entity class to instantiate, randomize, and persist.
     * @param <T>           entity type parameter
     * @return a new persisted instance of the {@code entityClass}.
     * @throws NullPointerException     when either argument is {@code null}.
     * @throws IllegalArgumentException when no usable randomizer, or no usable persister, is found for the
     *                                  {@code entityClass}.
     * @see #locateStandard(Class)
     * @see __RandomizerUtils#locateStandard(Class)
     */
    public static <T> T newPersistedInstanceOf(final EntityManager entityManager, final Class<T> entityClass) {
        Objects.requireNonNull(entityManager, "entityManager is null");
        Objects.requireNonNull(entityClass, "entityClass is null");
        // the persister is resolved first: randomizing an instance which can not then be persisted is wasted work,
        // and, for the PODAM flavor, runs the located instantiator for nothing
        final var persisterInstance = newPersisterInstanceOf(entityClass)
                .orElseThrow(() -> new IllegalArgumentException("no persister instance for " + entityClass));
        logger.log(System.Logger.Level.TRACE, "persister instance: {0}", persisterInstance);
        final T entityInstance = __RandomizerUtils.newRandomizedInstanceOf(entityClass)
                .orElseThrow(() -> new IllegalArgumentException("no randomized instance for " + entityClass));
        logger.log(System.Logger.Level.TRACE, "entityInstance: {0}", entityInstance);
        return persisterInstance.apply(entityManager, entityInstance);
    }

    // ---------------------------------------------------------------------------------------------------------------------
    private __PersisterUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
