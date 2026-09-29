package com.github.jinahya.persistence.test.util;

import jakarta.persistence.EntityManager;

import java.util.function.BiFunction;

/**
 * An interface for persisting instances of a specific entity class.
 * <p>
 * This is the role which the {@linkplain EntityPersisterUtils naming convention} locates for an entity class, and which
 * {@link EntityPersisterUtils#newPersistedInstanceOf(EntityManager, Class)} applies. Implement it directly for a persister
 * which owes nothing to the machinery of {@link AbstractEntityPersister}; extend that class for the plain
 * {@link EntityManager#persist(Object) persist} it brings, and for the entity class it remembers, which is what lets a
 * located persister be checked against the class it was located for.
 *
 * @param <T> the type of the entity instances to persist.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see AbstractEntityPersister
 * @see EntityPersisterUtils#newPersistedInstanceOf(EntityManager, Class)
 */
@FunctionalInterface
public interface EntityPersister<T>
        extends BiFunction<EntityManager, T, T> {

    /**
     * Persists the specified entity instance, with the specified entity manager, and returns it.
     *
     * @param entityManager  the entity manager to persist the {@code entityInstance} with.
     * @param entityInstance the entity instance to persist.
     * @return the {@code entityInstance}; the very instance which was handed in, unless an implementation has reason to
     *         hand back another.
     * @apiNote An implementation persists, and does not flush: a caller which persists several instances
     *         flushes once, when it is done, rather than once per instance. An implementation which has to persist
     *         required associations first does so here, before the {@code entityInstance} itself.
     */
    @Override
    T apply(EntityManager entityManager, T entityInstance);
}
