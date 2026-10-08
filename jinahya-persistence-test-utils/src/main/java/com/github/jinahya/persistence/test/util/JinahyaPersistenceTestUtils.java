package com.github.jinahya.persistence.test.util;

import jakarta.persistence.EntityManager;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

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
     */
    public static <T> long selectCount(final EntityManager entityManager, final Class<T> entityClass) {
        Objects.requireNonNull(entityManager, "entityManager is null");
        Objects.requireNonNull(entityClass, "entityClass is null");
        final var builder = entityManager.getCriteriaBuilder();
        final var query = builder.createQuery(Long.class);
        final var root = query.from(entityClass);
        query.select(builder.count(root));
        return entityManager.createQuery(query).getSingleResult();
    }

    /**
     * Selects a randomly chosen instance of the specified entity class.
     *
     * @param entityManager an entity manager.
     * @param entityClass   the entity class whose instance is selected.
     * @param <T>           entity type parameter
     * @return an optional of a randomly chosen instance of the {@code entityClass}; {@code empty} when there is none.
     * @throws NullPointerException when either argument is {@code null}.
     * @implNote The offset is chosen from the first {@link Integer#MAX_VALUE} instances only, for
     *         {@link jakarta.persistence.TypedQuery#setFirstResult(int)} takes an {@code int}.
     */
    public static <T> Optional<T> selectRandom(final EntityManager entityManager, final Class<T> entityClass) {
        final var count = selectCount(entityManager, entityClass);
        if (count == 0L) {
            return Optional.empty();
        }
        final var builder = entityManager.getCriteriaBuilder();
        final var query = builder.createQuery(entityClass);
        final var root = query.from(entityClass);
        query.select(root);
        return entityManager.createQuery(query)
                .setFirstResult(ThreadLocalRandom.current().nextInt((int) Math.min(count, Integer.MAX_VALUE)))
                .setMaxResults(1)
                .getResultList()
                .stream()
                .findFirst();
    }

    // -----------------------------------------------------------------------------------------------------------------
    private JinahyaPersistenceTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
