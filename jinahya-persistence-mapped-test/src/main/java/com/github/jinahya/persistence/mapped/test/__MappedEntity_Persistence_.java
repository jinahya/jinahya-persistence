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
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.Shutdown;
import jakarta.enterprise.event.Startup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Table;
import org.junit.jupiter.api.DisplayName;
import org.junit.platform.commons.util.AnnotationUtils;

import java.lang.System.Logger.Level;
import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119", // Type parameter names should comply with a naming convention
        "java:S6813" // Field dependency injection should be avoided
})
abstract class __MappedEntity_Persistence_<ENTITY extends __MappedEntity<ID>, ID>
        extends ___MappedEntityTest_<ENTITY, ID> {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // -----------------------------------------------------------------------------------------------------------------
    __MappedEntity_Persistence_(final Class<ENTITY> entityClass, final Class<ID> idClass) {
        super(entityClass, idClass);
        tableName = AnnotationUtils.findAnnotation(entityClass, jakarta.persistence.Table.class, false)
                .map(Table::name)
                .filter(v -> !v.isBlank())
                .orElseThrow();
    }

    // -----------------------------------------------------------------------------------------------------------------
    @PostConstruct
    protected void doOnPostConstruct() {
        getEntityManagerFactory().getProperties().forEach((k, v) -> {
            logger.log(Level.DEBUG, "entityManagerFactory.property; {0}: {1}", k, v);
        });
        tableCatalog = __PersistenceUnit_TestUtils.getJinahyaTableCatalog(getEntityManagerFactory()).orElseThrow();
        tableSchema = __PersistenceUnit_TestUtils.getJinahyaTableSchema(getEntityManagerFactory()).orElseThrow();
        tableTypes = __PersistenceUnit_TestUtils
                .getJinahyaTableTypes(getEntityManagerFactory())
                .map(List::of)
                .orElseGet(List::of);
        entityManager = getEntityManagerFactory().createEntityManager();
        logger.log(Level.DEBUG, "created: {0}", entityManager);
    }

    // https://stackoverflow.com/a/72628439/330457
    protected void onStartup(@Observes final Startup startup) {
    }

    @PreDestroy
    protected void onPreDestroy() {
        logger.log(Level.DEBUG, "closing {0}", entityManager);
        entityManager.close();
    }

    // https://stackoverflow.com/a/72628439/330457
    protected void onShutdown(@Observes final Shutdown shutdown) {
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Persists {@link #newRandomizedEntityInstance() a new randomized instance} of {@link #entityClass}.
     *
     * @see #newRandomizedEntityInstance()
     * @see #__persistEntityInstance(__MappedEntity)
     */
    @DisplayName("persist a random entity instance")
//    @Test
    protected void __persistEntityInstance() {
        acceptEntityManagerInTransactionAndRollback(em -> {
            final var entityInstance = __MappedEntity_PersisterUtils.newPersistedInstanceOf(em, entityClass);
            em.persist(entityInstance);
            em.flush();
            __persistEntityInstance(entityInstance);
        });
    }

    /**
     * Notifies, by the {@link #__persistEntityInstance()} method, that the specified entity instance has been
     * persisted.
     *
     * @param entityInstance the entity instance.
     */
    protected void __persistEntityInstance(@Nonnull final ENTITY entityInstance) {
        Objects.requireNonNull(entityInstance, "entityInstance is null");
        ___JakartaValidation_TestUtils.requireValid(entityInstance);
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

    // ------------------------------------------------------------------------------------------------------ connection

    /**
     * Returns the result of the specified function applied to a connection unwrapped from an entity manager.
     *
     * @param function the function.
     * @param rollback a flag for rolling-back; {@code true} for rollback; {@code false} otherwise.
     * @param <R>      result type parameter
     * @return the result of the {@code function}.
     * @deprecated Use {@link #applyConnectionInTransactionAndRollback(Function)} method.
     */
    @Deprecated(forRemoval = true)
    protected final <R> R applyConnectionInTransaction(
            @Nonnull final Function<? super Connection, ? extends R> function,
            final boolean rollback) {
        Objects.requireNonNull(function, "function is null");
        return applyEntityManagerInTransaction(
                em -> ___JakartaPersistence_TestUtils.applyConnection(em, function),
                rollback
        );
    }

    /**
     * Accepts a connection, unwrapped from an entity manager, to the specified consumer.
     *
     * @param consumer the consumer.
     * @param rollback a flag for rolling-back; {@code true} for rollback; {@code false} otherwise.
     * @deprecated Use {@link #acceptConnectionInTransactionAndRollback(Consumer)} method.
     */
    @Deprecated(forRemoval = true)
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

    /**
     * Returns the result of the specified function applied, in transaction, to a connection unwrapped from an entity
     * manager, and rolls back.
     *
     * @param function the function.
     * @param <R>      result type parameter
     * @return the result of the {@code function}.
     * @see #acceptConnectionInTransactionAndRollback(Consumer)
     */
    protected final <R> R applyConnectionInTransactionAndRollback(
            @Nonnull final Function<? super Connection, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return applyConnectionInTransaction(
                function,
                true
        );
    }

    /**
     * Accepts a connection, unwrapped from an entity manager, in transaction, to the specified consumer, and rolls
     * back.
     *
     * @param consumer the consumer.
     * @see #applyConnectionInTransactionAndRollback(Function)
     */
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

    // ------------------------------------------------------------------------------------------------------- tableName
    protected String getTableName() {
        return tableName;
    }

    // ---------------------------------------------------------------------------------------------------- tableCatalog
    protected String getTableCatalog() {
        return tableCatalog;
    }

    // ----------------------------------------------------------------------------------------------------- tableSchema
    protected String getTableSchema() {
        return tableSchema;
    }

    // ------------------------------------------------------------------------------------------------------ tableTypes
    protected List<String> getTableTypes() {
        return tableTypes;
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The value of {@code @Table(name =)} of the {@link #entityClass}.
     */
    private final String tableName;

    // -----------------------------------------------------------------------------------------------------------------
    private String tableCatalog;

    private String tableSchema;

    private List<String> tableTypes;

    // -----------------------------------------------------------------------------------------------------------------
    private EntityManager entityManager;
}
