package com.github.jinahya.persistence.test.util;

import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityManager;

import java.lang.invoke.MethodHandles;
import java.util.Objects;
import java.util.Optional;

/**
 * Utilities for {@link __Persister}.
 * <p>
 * The {@code newPersistedInstanceOf} methods chain the three roles of this package: an entity instance is instantiated
 * and randomized by the {@link __Randomizer} located for the entity class, then handed to the {@link __Persister}
 * located for it.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __Persister
 * @see __PersisterLocator
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class __PersisterUtils {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

// ---------------------------------------------------------------------------------------------------------------------
    @SuppressWarnings({
            "unchecked"
    })
    private static <T> Optional<__Persister<T>> newPersisterInstanceOf(final Class<T> target,
                                                                       final __PersisterLocator locator) {
        assert target != null;
        assert locator != null;
        return Optional.ofNullable(locator.apply(target))
                .filter(__Persister.class::isAssignableFrom)
                .map(___Utils::newInstance)
                .filter(i -> ___Utils.canConsume(target, ((__Persister<?>) i).entityClass, i))
                .map(i -> (__Persister<T>) i);
    }

    /**
     * Returns a new persisted instance of the specified entity class, using a randomizer and a persister located by
     * specified locators.
     *
     * @param entityManager     an entity manager.
     * @param entityClass       the entity class to instantiate, randomize, and persist.
     * @param persisterLocator  the locator for locating the persister class of the {@code entityClass}.
     * @param randomizerLocator the locator for locating the randomizer class of the {@code entityClass}.
     * @param <T>               entity type parameter
     * @return a new persisted instance of the {@code entityClass}.
     * @throws NullPointerException     when any argument is {@code null}.
     * @throws IllegalArgumentException when the {@code randomizerLocator} yields no usable randomizer, or the
     *                                  {@code persisterLocator} yields no usable persister, for the
     *                                  {@code entityClass}.
     * @apiNote The instance is persisted, but not flushed; a caller which needs generated values, or constraint
     *         violations, has to flush the {@code entityManager} itself.
     * @see __PersisterLocator#STANDARD
     * @see __RandomizerLocator#STANDARD
     */
    @Nonnull
    public static <T> T newPersistedInstanceOf(final @Nonnull EntityManager entityManager,
                                               final @Nonnull Class<T> entityClass,
                                               final @Nonnull __PersisterLocator persisterLocator,
                                               final @Nonnull __RandomizerLocator randomizerLocator) {
        Objects.requireNonNull(entityManager, "entityManager is null");
        Objects.requireNonNull(entityClass, "entityClass is null");
        Objects.requireNonNull(persisterLocator, "persisterLocator is null");
        Objects.requireNonNull(randomizerLocator, "randomizerLocator is null");
        final T entityInstance = __RandomizerUtils.newRandomizedInstanceOf(entityClass, randomizerLocator)
                .orElseThrow(() -> new IllegalArgumentException("no randomized instance for " + entityClass));
        logger.log(System.Logger.Level.TRACE, "entityInstance: {0}", entityInstance);
        final var persisterInstance = newPersisterInstanceOf(entityClass, persisterLocator)
                .orElseThrow(() -> new IllegalArgumentException("no persister instance for " + entityClass));
        logger.log(System.Logger.Level.TRACE, "persister instance: {0}", persisterInstance);
        return persisterInstance.apply(entityManager, entityInstance);
    }

    /**
     * Returns a new persisted instance of the specified entity class, using a persister located by the specified
     * locator, and a randomizer located by the {@link __RandomizerLocator#STANDARD standard} randomizer locator.
     *
     * @param entityManager    an entity manager.
     * @param entityClass      the entity class to instantiate, randomize, and persist.
     * @param persisterLocator the locator for locating the persister class of the {@code entityClass}.
     * @param <T>              entity type parameter
     * @return a new persisted instance of the {@code entityClass}.
     * @throws NullPointerException     when any argument is {@code null}.
     * @throws IllegalArgumentException when no usable randomizer, or no usable persister, is found for the
     *                                  {@code entityClass}.
     * @see #newPersistedInstanceOf(EntityManager, Class, __PersisterLocator, __RandomizerLocator)
     * @see __RandomizerLocator#STANDARD
     */
    @Nonnull
    public static <T> T newPersistedInstanceOf(final @Nonnull EntityManager entityManager,
                                               final @Nonnull Class<T> entityClass,
                                               final @Nonnull __PersisterLocator persisterLocator) {
        return newPersistedInstanceOf(
                entityManager,
                entityClass,
                persisterLocator,
                __RandomizerLocator.STANDARD
        );
    }

    /**
     * Returns a new persisted instance of the specified entity class.
     *
     * @param entityManager an entity manager.
     * @param entityClass   the entity class to instantiate, randomize, and persist.
     * @param <T>           entity type parameter
     * @return a new persisted instance of the {@code entityClass}.
     * @throws NullPointerException     when either argument is {@code null}.
     * @throws IllegalArgumentException when no usable randomizer, or no usable persister, is found for the
     *                                  {@code entityClass}.
     * @see #newPersistedInstanceOf(EntityManager, Class, __PersisterLocator, __RandomizerLocator)
     * @see __PersisterLocator#STANDARD
     * @see __RandomizerLocator#STANDARD
     */
    @Nonnull
    public static <T> T newPersistedInstanceOf(final @Nonnull EntityManager entityManager,
                                               final @Nonnull Class<T> entityClass) {
        return newPersistedInstanceOf(
                entityManager,
                entityClass,
                __PersisterLocator.STANDARD
        );
    }

// ---------------------------------------------------------------------------------------------------------------------
    private __PersisterUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
