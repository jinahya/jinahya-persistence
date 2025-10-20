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
import jakarta.annotation.Nullable;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.Shutdown;
import jakarta.enterprise.event.Startup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Table;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.ManagedType;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.AnnotationUtils;

import java.lang.System.Logger.Level;
import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.LongConsumer;
import java.util.function.LongFunction;

import static org.assertj.core.api.Assumptions.assumeThat;

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
        {
            getEntityManagerFactory().getProperties().forEach((k, v) -> {
                logger.log(Level.DEBUG, "entityManagerFactory.property; {0}: {1}", k, v);
            });
        }
        {
            tableCatalog = __PersistenceUnit_TestUtils.getJinahyaTableCatalog(getEntityManagerFactory()).orElse(null);
            tableSchema = __PersistenceUnit_TestUtils.getJinahyaTableSchema(getEntityManagerFactory()).orElse(null);
            tableTypes = __PersistenceUnit_TestUtils.getJinahyaTableTypes(getEntityManagerFactory()).orElse(null);
        }
        {
//            entityManager = getEntityManagerFactory().createEntityManager();
//            logger.log(Level.DEBUG, "created: {0}", entityManager);
        }
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

    // -----------------------------------------------------------------------------------------------------------------
    @Test
    final void __persistRandomInstance() {
        {
            final var clazz = getClass();
            final var disabled = AnnotationUtils.findAnnotation(
                    clazz, __Disable_PersistEntityInstance_Test.class
            );
            assumeThat(disabled)
                    .as("%s on %s", __Disable_PersistEntityInstance_Test.class, clazz)
                    .isEmpty();
        }
        final var persisted = applyEntityManagerInTransaction(
                em -> {
                    return __MappedEntity_PersisterUtils.newPersistedInstanceOf(em, entityClass);
                },
                true
        );
        logger.log(Level.DEBUG, "persisted: {0}", persisted);
    }

    // ----------------------------------------------------------------------------------------------- super.entityClass
    protected final EntityType<ENTITY> entityType() {
        if (entityType == null) {
            entityType = getEntityManagerFactory().getMetamodel().entity(entityClass);
        }
        return entityType;
    }

    protected final String entityName() {
        return entityType().getName();
    }

    protected final ManagedType<ENTITY> managedType() {
        if (managedType == null) {
            managedType = getEntityManagerFactory().getMetamodel().managedType(entityClass);
        }
        return managedType;
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
     * Applies an unmodified map of the injected instance of {@link EntityManagerFactory}'s properties.
     *
     * @return an unmodified map of the injected instance of {@link EntityManagerFactory}'s properties.
     * @see EntityManagerFactory#getProperties()
     */
    protected final Map<String, Object> getEntityManagerFactoryProperties() {
        return applyEntityManagerFactory(
                EntityManagerFactory::getProperties
        );
    }

    // --------------------------------------------------------------------------------------------------- entityManager
    private EntityManager entityManager() {
        var em = entityManager;
        if (em == null || !em.isOpen()) {
            em = entityManager = getEntityManagerFactory().createEntityManager();
        }
        return em;
    }

    /**
     * Applies an instance of {@link EntityManager} to the specified function, and returns the result.
     *
     * @param function the function.
     * @param <R>      result type parameter
     * @return the result of the {@code function} applied to an entity manager.
     */
    protected final <R> R applyEntityManager(@Nonnull final Function<? super EntityManager, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        {
            final var clazz = getClass();
            final var flag = AnnotationUtils.findAnnotation(clazz, __Use_Cached_EntityManager.class);
            if (flag.isPresent()) {
                return function.apply(entityManager());
            }
        }
        // too slow!
        return applyEntityManagerFactory(emf -> {
            try (final var em = emf.createEntityManager()) {
                return function.apply(em);
            }
        });
    }

    /**
     * Applies an instance of {@link EntityManager}, as joined to a transaction, to the specified function, and returns
     * the result.
     *
     * @param function the function.
     * @param <R>      result type parameter
     * @return the result of the {@code function} applied to an entity manager.
     */
    protected final <R> R applyEntityManagerInTransaction(
            @Nonnull final Function<? super EntityManager, ? extends R> function,
            final boolean rollback) {
        Objects.requireNonNull(function, "function is null");
        return applyEntityManager(em -> {
            return ___JakartaPersistence_TestUtils.applyEntityManagerInTransaction(
                    em,
                    function,
                    rollback
            );
        });
    }

    /**
     * Applies an instance of {@link EntityManager}, as joined to a transaction, to the specified function, and returns
     * the result.
     *
     * @param function the function.
     * @param <R>      result type parameter
     * @return the result of the {@code function} applied to an entity manager.
     * @apiNote This method invokes the {@link #applyEntityManagerInTransaction(Function, boolean)} method with
     *         {@code function} and {@code true}.
     */
    protected final <R> R applyEntityManagerInTransactionAndRollback(
            @Nonnull final Function<? super EntityManager, ? extends R> function) {
        return applyEntityManagerInTransaction(function, true);
    }

    @Nullable
    protected <R> R applyCountAndRandomIndex(
            @Nonnull final LongFunction<? extends LongFunction<? extends R>> function) {
        return applyEntityManager(em -> ___JakartaPersistence_TestUtils.applyCountAndIndex(
                em,
                entityClass,
                function
        ));
    }

    @Deprecated(forRemoval = true)
    protected void acceptCountAndRandomIndex(@Nonnull final LongFunction<? extends LongConsumer> function) {
        applyCountAndRandomIndex(c -> i -> {
            function.apply(c).accept(i);
            return null;
        });
    }

    protected <R> R applyCriteria(
            final Function<
                    ? super CriteriaBuilder, ? extends Function<
                    ? super CriteriaQuery<ENTITY>, ? extends Function<
                    ? super Root<ENTITY>, ? extends R>>> function) {
        return applyEntityManager(em -> {
            final var builder = em.getCriteriaBuilder();
            final var query = builder.createQuery(entityClass);
            final var root = query.from(entityClass);
            return function.apply(builder).apply(query).apply(root);
        });
    }

    // ------------------------------------------------------------------------------------------------------ connection

    /**
     * Returns the result of the specified function applied to a connection unwrapped from an entity manager.
     *
     * @param function the function.
     * @param rollback a flag for rolling-back; {@code true} for rolling-back; {@code false} for committing.
     * @param <R>      result type parameter
     * @return the result of the {@code function}.
     * @see #applyEntityManagerInTransaction(Function, boolean)
     */
    protected final <R> R applyConnection(@Nonnull final Function<? super Connection, ? extends R> function,
                                          final boolean rollback) {
        Objects.requireNonNull(function, "function is null");
        return applyEntityManagerInTransaction(
                em -> ___JakartaPersistence_TestUtils.applyUnwrappedConnection(em, function),
                rollback
        );
    }

    /**
     * Returns the result of the specified function applied to a connection unwrapped from an entity manager.
     *
     * @param function the function.
     * @param <R>      result type parameter
     * @return the result of the {@code function}.
     * @apiNote This method invokes the {@link #applyConnection(Function, boolean)} method with {@code function}
     *         and {@code true}.
     */
    protected final <R> R applyConnectionAndRollback(
            @Nonnull final Function<? super Connection, ? extends R> function) {
        return applyConnection(function, true);
    }

    // ------------------------------------------------------------------------------------------------------- tableName
    protected String tableName() {
        return tableName;
    }

    // ---------------------------------------------------------------------------------------------------- tableCatalog
    @Nullable
    protected String tableCatalog() {
        return tableCatalog;
    }

    // ----------------------------------------------------------------------------------------------------- tableSchema
    @Nullable
    protected String tableSchema() {
        return tableSchema;
    }

    // ------------------------------------------------------------------------------------------------------ tableTypes
    @Nullable
    protected String[] tableTypes() {
        return Optional.ofNullable(tableTypes)
                .map(v -> Arrays.copyOf(v, v.length))
                .orElse(null)
                ;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private EntityType<ENTITY> entityType;

    private ManagedType<ENTITY> managedType;

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The value of {@code @Table(name =)} of the {@link #entityClass}.
     */
    private final String tableName;

    // -----------------------------------------------------------------------------------------------------------------
    @Nullable
    private String tableCatalog;

    @Nullable
    private String tableSchema;

    @Nullable
    private String[] tableTypes;

    // -----------------------------------------------------------------------------------------------------------------
    private EntityManager entityManager;
}
