package com.github.jinahya.persistence.mapped.test;

/*-
 * #%L
 * jinahya-persistence-mapped-test
 * %%
 * Copyright (C) 2024 - 2025 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.github.jinahya.persistence.mapped.__MappedEntity;
import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119", // Type parameter names should comply with a naming convention
        "java:S6813" // Field dependency injection should be avoided
})
abstract class ___MappedEntityPersistenceTest<ENTITY extends __MappedEntity<ENTITY, ID>, ID>
        extends ___MappedEntityTest<ENTITY, ID> {

    // -----------------------------------------------------------------------------------------------------------------
    ___MappedEntityPersistenceTest(final Class<ENTITY> entityClass, final Class<ID> idClass) {
        super(entityClass, idClass);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Persists {@link #newRandomizedEntityInstance() a new randomized instance} of {@link #entityClass}.
     *
     * @see #newRandomizedEntityInstance()
     * @see #persistingEntityInstance(__MappedEntity)
     * @see #persistedEntityInstance(__MappedEntity)
     */
    @Test
    protected void persistEntityInstance() {
        acceptEntityManagerInTransactionAndRollback(em -> {
            final var entityInstance = newRandomizedEntityInstance();
            persistingEntityInstance(entityInstance);
            em.persist(entityInstance);
            em.flush();
            persistedEntityInstance(entityInstance);
        });
    }

    /**
     * Notifies that the specified entity instance is about to be persisted.
     *
     * @param entityInstance the entity instance.
     */
    protected void persistingEntityInstance(final ENTITY entityInstance) {
        Objects.requireNonNull(entityInstance, "entityInstance is null");
    }

    /**
     * Notifies that the specified entity instance has been persisted.
     *
     * @param entityInstance the entity instance.
     */
    protected void persistedEntityInstance(final ENTITY entityInstance) {
        Objects.requireNonNull(entityInstance, "entityInstance is null");
    }

    // -------------------------------------------------------------------------------------------- entityManagerFactory

    /**
     * Applies an injected instance of {@link EntityManagerFactory} to the specified function, and returns the result.
     *
     * @param function the function.
     * @param <R>      result type parameter
     * @return the result of the {@code function}.
     * @see #acceptEntityManagerFactory(Consumer)
     */
    protected final <R> R applyEntityManagerFactory(
            final Function<? super EntityManagerFactory, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return function.apply(getEntityManagerFactory());
    }

    /**
     * Accepts an injected instance of {@link EntityManagerFactory} to the specified consumer.
     *
     * @param consumer the consumer.
     * @see #applyEntityManagerFactory(Function)
     */
    protected final void acceptEntityManagerFactory(final Consumer<? super EntityManagerFactory> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        applyEntityManagerFactory(emf -> {
            consumer.accept(emf);
            return null;
        });
    }

    /**
     * Applies an unmodified map of properties of the injected instance of {@link EntityManagerFactory}.
     *
     * @return an unmodified map of properties of the injected instance of {@link EntityManagerFactory}.
     */
    protected final Map<String, Object> getEntityManagerProperties() {
        return Collections.unmodifiableMap(
                applyEntityManagerFactory(
                        EntityManagerFactory::getProperties
                )
        );
    }

    // -------=------------------------------------------------------------------------------------------- entityManager

    /**
     * Applies an injected instance of {@link EntityManager} to the specified function, and returns the result.
     *
     * @param function the function.
     * @param <R>      result type parameter
     * @return the result of the {@code function}.
     * @see #acceptEntityManager(Consumer)
     */
    protected final <R> R applyEntityManager(final Function<? super EntityManager, ? extends R> function) {
        return function.apply(getEntityManager());
    }

    /**
     * Accepts an injected instance of {@link EntityManager} to the specified consumer.
     *
     * @param consumer the consumer.
     * @see #applyEntityManager(Function)
     */
    protected final void acceptEntityManager(final Consumer<? super EntityManager> consumer) {
        applyEntityManager(em -> {
            consumer.accept(em);
            return null;
        });
    }

    protected final <R> R applyEntityManagerInTransaction(final Function<? super EntityManager, ? extends R> function,
                                                          final boolean rollback) {
        return applyEntityManager(em -> {
            return ___JakartaPersistenceTestUtils.applyEntityManagerInTransaction(em, function, rollback);
        });
    }

    protected final void acceptEntityManagerInTransaction(final Consumer<? super EntityManager> consumer,
                                                          final boolean rollback) {
        applyEntityManagerInTransaction(
                em -> {
                    consumer.accept(em);
                    return null;
                },
                rollback
        );
    }

    protected final <R> R applyEntityManagerInTransactionAndRollback(
            final Function<? super EntityManager, ? extends R> function) {
        return applyEntityManagerInTransaction(function, true);
    }

    protected final void acceptEntityManagerInTransactionAndRollback(
            final Consumer<? super EntityManager> consumer) {
        applyEntityManagerInTransactionAndRollback(rm -> {
            consumer.accept(rm);
            return null;
        });
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Applies a connection, unwrapped from the specified {@link #applyEntityManager(Function) entity manager}, to the
     * specified function, and returns the result.
     *
     * @param function the function.
     * @param rollback {@code true} for rollback; {@code false} otherwise.
     * @param <R>      result type parameter
     * @return the result of the {@code function}.
     */
    protected final <R> R applyConnectionInTransaction(
            @Nonnull final Function<? super Connection, ? extends R> function,
            final boolean rollback) {
        Objects.requireNonNull(function, "function is null");
        return applyEntityManagerInTransaction(
                em -> ___JakartaPersistenceTestUtils.applyConnection(em, function),
                rollback
        );
    }

    protected final void acceptConnectionInTransaction(@Nonnull final Consumer<? super Connection> consumer,
                                                       final boolean rollback) {
        Objects.requireNonNull(consumer, "consumer is null");
        applyConnectionInTransaction(
                c -> {
                    consumer.accept(c);
                    return null;
                },
                rollback
        );
    }

    protected final <R> R applyConnectionInTransactionAndRollback(
            @Nonnull final Function<? super Connection, ? extends R> function) {
        return applyConnectionInTransaction(function, true);
    }

    protected final void acceptConnectionInTransactionAndRollback(
            @Nonnull final Consumer<? super Connection> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        applyConnectionInTransactionAndRollback(
                c -> {
                    consumer.accept(c);
                    return null;
                }
        );
    }

    // -----------------------------------------------------------------------------------------------------------------
    abstract EntityManagerFactory getEntityManagerFactory();

    abstract EntityManager getEntityManager();
}
