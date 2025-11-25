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
import org.junit.jupiter.api.DisplayName;
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
        "java:S100", // Method names should comply with a naming convention
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
        entityTableName = AnnotationUtils.findAnnotation(entityClass, Table.class, false)
                .map(Table::name)
                .filter(v -> !v.isBlank())
                .orElseThrow();
    }

    // -----------------------------------------------------------------------------------------------------------------
    @PostConstruct
    protected void doOnPostConstruct() {
        jinahyaTableCatalog =
                __PersistenceUnit_TestUtils.getJinahyaTableCatalog(getEntityManagerFactory()).orElse(null);
        jinahyaTableSchema =
                __PersistenceUnit_TestUtils.getJinahyaTableSchema(getEntityManagerFactory()).orElse(null);
        jinahyaTableTypes =
                __PersistenceUnit_TestUtils.getJinahyaTableTypes(getEntityManagerFactory()).orElse(null);
        logger.log(Level.DEBUG, "jinahyaTableCatalog: {0}", jinahyaTableCatalog);
        logger.log(Level.DEBUG, "jinahyaTableSchema: {0}", jinahyaTableSchema);
        logger.log(Level.DEBUG, "jinahyaTableTypes: {0}", Arrays.toString(jinahyaTableTypes));
    }

    // https://stackoverflow.com/a/72628439/330457
    protected void onStartup(@Observes final Startup startup) {
        logger.log(Level.DEBUG, "onStartup{0})", startup);
    }

    @PreDestroy
    protected void onPreDestroy() {
        if (cachedEntityManager != null) {
            logger.log(Level.DEBUG, "closing cached entity manager: {0}", cachedEntityManager);
            cachedEntityManager.close();
            cachedEntityManager = null;
        }
    }

    // https://stackoverflow.com/a/72628439/330457
    protected void onShutdown(@Observes final Shutdown shutdown) {
        logger.log(Level.DEBUG, "onShutdown({0})", shutdown);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Persists a new (randomized) instance.
     *
     * @see __Disable_PersistEntityInstance_Test
     * @see __MappedEntity_PersisterUtils#newPersistedInstanceOf(EntityManager, Class)
     */
    @DisplayName("persist a new (randomized) entity instance")
    @Test
    final void __persistEntityInstance() {
        {
            final var clazz = getClass();
            final var disabled = AnnotationUtils.findAnnotation(
                    clazz,
                    __Disable_PersistEntityInstance_Test.class
            );
            assumeThat(disabled)
                    .as("%s on %s", __Disable_PersistEntityInstance_Test.class, clazz)
                    .isEmpty();
        }
        applyEntityManagerInTransactionAndRollback(em -> {
            final var persisted = __MappedEntity_PersisterUtils.newPersistedInstanceOf(em, entityClass);
            __persistEntityInstance(em, persisted);
            __persistEntityInstance(persisted);
            return null;
        });
    }

    /**
     * Gets notified, from the {@link #__persistEntityInstance()} method, with the specified entity instance which has
     * been persisted to the specified entity manager.
     *
     * @param entityManager the entity manager to which the entity instance has been persisted.
     * @param persisted     the entity instance persisted to the {@code entityManager}.
     */
    protected void __persistEntityInstance(final EntityManager entityManager, final ENTITY persisted) {
    }

    /**
     * Gets notified, from the {@link #__persistEntityInstance()} method, with the specified entity instance which has
     * been persisted.
     *
     * @param persisted the entity instance persisted.
     * @deprecated use {@link #__persistEntityInstance(EntityManager, __MappedEntity)}
     */
    @Deprecated(forRemoval = true)
    protected void __persistEntityInstance(final ENTITY persisted) {
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
            final @Nonnull Function<? super EntityManagerFactory, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return function.apply(getEntityManagerFactory());
    }

    /**
     * Accepts an injected instance of {@link EntityManagerFactory} to the specified consumer.
     *
     * @param consumer the consumer.
     * @see #applyEntityManagerFactory(Function)
     */
    protected final void acceptEntityManagerFactory(final @Nonnull Consumer<? super EntityManagerFactory> consumer) {
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
    private EntityManager cachedEntityManager() {
        var em = cachedEntityManager;
        if (em == null || !em.isOpen()) {
            em = cachedEntityManager = getEntityManagerFactory().createEntityManager();
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
    protected final <R> R applyEntityManager(final @Nonnull Function<? super EntityManager, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        {
            final var clazz = getClass();
            final var flag = AnnotationUtils.findAnnotation(clazz, __Use_Cached_EntityManager.class);
            if (flag.isPresent()) {
                return function.apply(cachedEntityManager());
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
            final @Nonnull Function<? super EntityManager, ? extends R> function,
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
            final @Nonnull Function<? super EntityManager, ? extends R> function) {
        return applyEntityManagerInTransaction(function, true);
    }

    @Nullable
    protected <R> R applyCountAndRandomIndex(
            final @Nonnull LongFunction<? extends LongFunction<? extends R>> function) {
        return applyEntityManager(em -> ___JakartaPersistence_TestUtils.applyCountAndRandomIndex(
                em,
                entityClass,
                function
        ));
    }

    @Deprecated(forRemoval = true)
    protected void acceptCountAndRandomIndex(final @Nonnull LongFunction<? extends LongConsumer> function) {
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
    protected final <R> R applyConnection(final @Nonnull Function<? super Connection, ? extends R> function,
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
            final @Nonnull Function<? super Connection, ? extends R> function) {
        return applyConnection(function, true);
    }

    // ------------------------------------------------------------------------------------------------------- tableName

    /**
     * Returns the value of {@link Table#name()} of the {@link #entityClass}.
     *
     * @return the value of {@link Table#name()} of the {@link #entityClass}.
     */
    protected String getEntityTableName() {
        return entityTableName;
    }

    // ---------------------------------------------------------------------------------------------------- tableCatalog
    @Nullable
    protected String getJinahyaTableCatalog() {
        return jinahyaTableCatalog;
    }

    // ----------------------------------------------------------------------------------------------------- tableSchema
    @Nullable
    protected String getJinahyaTableSchema() {
        return jinahyaTableSchema;
    }

    // ------------------------------------------------------------------------------------------------------ tableTypes
    @Nullable
    protected String[] tableTypes() {
        return Optional.ofNullable(jinahyaTableTypes)
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
    private final String entityTableName;

    // -----------------------------------------------------------------------------------------------------------------
    @Nullable
    private String jinahyaTableCatalog;

    @Nullable
    private String jinahyaTableSchema;

    @Nullable
    private String[] jinahyaTableTypes;

    // -----------------------------------------------------------------------------------------------------------------
    private EntityManager cachedEntityManager;
}
