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
import com.github.jinahya.persistence.mapped.__MappedEntityCriteria;
import jakarta.annotation.Nonnull;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.Shutdown;
import jakarta.enterprise.event.Startup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.lang.System.Logger.Level;
import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119", // Type parameter names should comply with a naming convention
        "java:S6813" // Field dependency injection should be avoided
})
abstract class __MappedEntityCriteria_Persistence_<
        CRITERIA extends __MappedEntityCriteria<ENTITY, ID>,
        ENTITY extends __MappedEntity<ID>,
        ID
        >
        extends ___MappedEntityTest_<ENTITY, ID> {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // -----------------------------------------------------------------------------------------------------------------
    protected __MappedEntityCriteria_Persistence_(final Class<CRITERIA> criteriaClass,
                                                  final Class<ENTITY> entityClass, final Class<ID> idClass) {
        super(entityClass, idClass);
        this.criteriaClass = Objects.requireNonNull(criteriaClass, "criteriaClass is null");
    }

    // -----------------------------------------------------------------------------------------------------------------
    @PostConstruct
    protected void doOnPostConstruct() {
        getEntityManagerFactory().getProperties().forEach((k, v) -> {
            logger.log(Level.DEBUG, "entityManagerFactory.property; {0}: {1}", k, v);
        });
        entityManager = getEntityManagerFactory().createEntityManager();
        logger.log(Level.DEBUG, "created: {0}", entityManager);
    }

    // https://stackoverflow.com/a/72628439/330457
    protected void onStartup(@Observes final Startup startup) {
        logger.log(Level.DEBUG, "onStartup{0})", startup);
    }

    @PreDestroy
    protected void onPreDestroy() {
        logger.log(Level.DEBUG, "closing {0}", entityManager);
        entityManager.close();
    }

    // https://stackoverflow.com/a/72628439/330457
    protected void onShutdown(@Observes final Shutdown shutdown) {
        logger.log(Level.DEBUG, "onShutdown({0})", shutdown);
    }

    // -------------------------------------------------------------------------------------------- entityManagerFactory

    /**
     * Returns an instance of {@link EntityManagerFactory}.
     *
     * @return an instance of {@link EntityManagerFactory}.
     */
    abstract EntityManagerFactory getEntityManagerFactory();

    /**
     * Applies an injected instance of {@link EntityManagerFactory} to the specified function, and returns the result.
     *
     * @param function the function.
     * @param <R>      result type parameter
     * @return the result of the {@code function}.
     * @see #acceptEntityManagerFactory(Consumer)
     */
    protected final <R> R applyEntityManagerFactory(
            @Nonnull final Function<? super EntityManagerFactory, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return function.apply(getEntityManagerFactory());
    }

    /**
     * Accepts an injected instance of {@link EntityManagerFactory} to the specified consumer.
     *
     * @param consumer the consumer.
     * @see #applyEntityManagerFactory(Function)
     */
    protected final void acceptEntityManagerFactory(@Nonnull final Consumer<? super EntityManagerFactory> consumer) {
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
     * @see EntityManagerFactory#getProperties()
     */
    protected final Map<String, Object> getEntityManagerFactoryProperties() {
        return applyEntityManagerFactory(
                EntityManagerFactory::getProperties
        );
    }

    // --------------------------------------------------------------------------------------------------- entityManager

    /**
     * Applies an injected instance of {@link EntityManager} to the specified function, and returns the result.
     *
     * @param function the function.
     * @param <R>      result type parameter
     * @return the result of the {@code function}.
     * @see #acceptEntityManager(Consumer)
     */
    protected final <R> R applyEntityManager(@Nonnull final Function<? super EntityManager, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        if (true) {
            return function.apply(entityManager);
        }
        return applyEntityManagerFactory(emf -> {
            try (final var entityManager = emf.createEntityManager()) {
                return function.apply(entityManager);
            }
        });
    }

    /**
     * Accepts an injected instance of {@link EntityManager} to the specified consumer.
     *
     * @param consumer the consumer.
     * @see #applyEntityManager(Function)
     */
    protected final void acceptEntityManager(@Nonnull final Consumer<? super EntityManager> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        applyEntityManager(em -> {
            consumer.accept(em);
            return null;
        });
    }

    @Deprecated(forRemoval = true)
    protected final <R> R applyEntityManagerInTransaction(
            @Nonnull final Function<? super EntityManager, ? extends R> function,
            final boolean rollback) {
        Objects.requireNonNull(function, "function is null");
        return applyEntityManager(em -> {
            return ___JakartaPersistence_TestUtils.applyEntityManagerInTransaction(em, function, rollback);
        });
    }

    @Deprecated(forRemoval = true)
    protected final void acceptEntityManagerInTransaction(@Nonnull final Consumer<? super EntityManager> consumer,
                                                          final boolean rollback) {
        Objects.requireNonNull(consumer, "consumer is null");
        applyEntityManagerInTransaction(
                em -> {
                    consumer.accept(em);
                    return null;
                },
                rollback
        );
    }

    /**
     * Returns the result of the specified function applied to an entity manager.
     *
     * @param function the function.
     * @param <R>      result type parameter.
     * @return the result of the {@code function}.
     * @see #acceptEntityManagerInTransactionAndRollback(Consumer)
     */
    protected final <R> R applyEntityManagerInTransactionAndRollback(
            @Nonnull final Function<? super EntityManager, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return applyEntityManagerInTransaction(function, true);
    }

    /**
     * Accepts an entity manager to the specified consumer.
     *
     * @param consumer the consumer.
     * @see #applyEntityManagerInTransactionAndRollback(Function)
     */
    protected final void acceptEntityManagerInTransactionAndRollback(
            @Nonnull final Consumer<? super EntityManager> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        applyEntityManagerInTransactionAndRollback(rm -> {
            consumer.accept(rm);
            return null;
        });
    }

    // -----------------------------------------------------------------------------------------------------------------
    protected final Class<CRITERIA> criteriaClass;

    // -----------------------------------------------------------------------------------------------------------------
    private EntityManager entityManager;
}
