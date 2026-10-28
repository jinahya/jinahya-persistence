package com.github.jinahya.persistence.test.util;

import com.github.jinahya.persistence.util.JinahyaEntityManagerUtils;
import jakarta.persistence.EntityManager;

import java.util.Optional;

/**
 * Utilities for testing persistence.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public final class JinahyaPersistenceTestUtils {

    /**
     * Selects the number of all instances of the specified entity class.
     *
     * @param entityManager an entity manager.
     * @param entityClass   the entity class whose instances are counted.
     * @param <T>           entity type parameter
     * @return the number of all instances of the {@code entityClass}.
     * @throws NullPointerException when either argument is {@code null}.
     * @deprecated Counting is not specific to tests; use
     *         {@link JinahyaEntityManagerUtils#selectEntityCount(EntityManager, Class)}, which this now merely calls.
     */
    @Deprecated(forRemoval = true)
    public static <T> long selectCount(final EntityManager entityManager, final Class<T> entityClass) {
        return JinahyaEntityManagerUtils.selectEntityCount(entityManager, entityClass);
    }

    /**
     * Selects a randomly chosen instance of the specified entity class.
     *
     * @param entityManager an entity manager.
     * @param entityClass   the entity class whose instance is selected.
     * @param <T>           entity type parameter
     * @return an optional of a randomly chosen instance of the {@code entityClass}; {@code empty} when there is none.
     * @throws NullPointerException when either argument is {@code null}.
     * @deprecated Use {@link JinahyaEntityManagerTestUtils#selectRandomEntity(EntityManager, Class)}, which this now merely
     *         calls.
     */
    @Deprecated(forRemoval = true)
    public static <T> Optional<T> selectRandom(final EntityManager entityManager, final Class<T> entityClass) {
        return JinahyaEntityManagerTestUtils.selectRandomEntity(entityManager, entityClass);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private JinahyaPersistenceTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
