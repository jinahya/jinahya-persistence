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

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.EntityManager;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.ManagedType;
import jakarta.validation.constraints.PositiveOrZero;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.LongConsumer;
import java.util.function.LongFunction;

@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class ___JakartaPersistenceTestUtils {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Accepts each attribute name of the specified managed type to the specified consumer.
     *
     * @param managedType the managed type.
     * @param consumer    the consumer.
     * @param <X>         managed type parameter.
     */
    public static <X> void acceptEachAttributeName(@Nonnull final ManagedType<X> managedType,
                                                   @Nonnull final Consumer<? super String> consumer) {
        Objects.requireNonNull(managedType, "managedType is null");
        Objects.requireNonNull(consumer, "consumer is null");
        managedType.getAttributes()
                .stream()
                .map(Attribute::getName)
                .forEach(consumer);
    }

    /**
     * Adds all attribute names of the specified managed type to the specified collection.
     *
     * @param managedType the managed type.
     * @param collection  the collection.
     * @param <X>         managed type parameter
     * @param <C>         collection type parameter
     * @return given {@code collection}.
     */
    public static <X, C extends Collection<? super String>>
    C addAllAttributeNames(@Nonnull final ManagedType<X> managedType, @Nonnull final C collection) {
        Objects.requireNonNull(collection, "collection is null");
        acceptEachAttributeName(managedType, collection::add);
        return collection;
    }

    // -----------------------------------------------------------------------------------------------------------------
    static Optional<String> getColumnName(@Nonnull final Method method) {
        Objects.requireNonNull(method, "method is null");
        {
            final var column = method.getAnnotation(Column.class);
            if (column != null) {
                final var name = column.name();
                if (!name.isBlank()) {
                    return Optional.of(name);
                }
            }
        }
        {
            final var column = method.getAnnotation(JoinColumn.class);
            if (column != null) {
                final var name = column.name();
                if (!name.isBlank()) {
                    return Optional.of(name);
                }
            }
        }
        return Optional.empty();
    }

    static Optional<String> getColumnName(@Nonnull final Field field) {
        Objects.requireNonNull(field, "field is null");
        {
            final var column = field.getAnnotation(Column.class);
            if (column != null) {
                final var name = column.name();
                if (!name.isBlank()) {
                    return Optional.of(name);
                }
            }
        }
        {
            final var column = field.getAnnotation(JoinColumn.class);
            if (column != null) {
                final var name = column.name();
                if (!name.isBlank()) {
                    return Optional.of(name);
                }
            }
        }
        return Optional.empty();
    }

    // -----------------------------------------------------------------------------------------------------------------
    public static <R> R applyEntityManagerInTransaction(final EntityManager entityManager,
                                                        final Function<? super EntityManager, ? extends R> function,
                                                        final boolean rollback) {
        Objects.requireNonNull(entityManager, "entityManager is null");
        if (entityManager.isJoinedToTransaction()) {
            throw new IllegalArgumentException("entityManager is already joined to a transaction");
        }
        Objects.requireNonNull(function, "function is null");
        final var transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            final R result = function.apply(entityManager);
            if (rollback) {
                transaction.rollback();
            } else {
                transaction.commit();
            }
            return result;
        } catch (final Exception e) {
            transaction.rollback();
            throw new RuntimeException(
                    "failed to apply, in transaction" +
                    "; entityManager: " + entityManager +
                    "; function: " + function +
                    "; rollback: " + rollback,
                    e
            );
        }
    }

    public static void acceptEntityManagerInTransaction(final EntityManager entityManager,
                                                        final Consumer<? super EntityManager> consumer,
                                                        final boolean rollback) {
        Objects.requireNonNull(consumer, "consumer is null");
        applyEntityManagerInTransaction(
                entityManager,
                em -> {
                    consumer.accept(em);
                    return null;
                },
                rollback
        );
    }

    // -----------------------------------------------------------------------------------------------------------------
    public static <R> R applyUnwrappedConnection(final EntityManager entityManager,
                                                 final Function<? super Connection, ? extends R> function) {
        Objects.requireNonNull(entityManager, "entityManager is null");
        Objects.requireNonNull(function, "function is null");
        try {
            return function.apply(entityManager.unwrap(Connection.class));
        } catch (final Exception e1) {
            try {
                return ___OrgHibernateOrmTestUtils.applyConnection(
                        entityManager,
                        function
                );
            } catch (final Exception e2) {
                throw new RuntimeException("failed to unwrap connection from " + entityManager, e2);
            }
        }
    }

    public static <R> R applyConnection(final EntityManager entityManager,
                                        final Function<? super Connection, ? extends R> function) {
        Objects.requireNonNull(entityManager, "entityManager is null");
        Objects.requireNonNull(function, "function is null");
        return applyUnwrappedConnection(entityManager, function);
    }

    public static void acceptConnection(final EntityManager entityManager,
                                        final Consumer<? super Connection> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        applyConnection(
                entityManager,
                c -> {
                    consumer.accept(c);
                    return null;
                }
        );
    }

    // -----------------------------------------------------------------------------------------------------------------
    @PositiveOrZero
    public static long count(@Nonnull final EntityManager entityManager, @Nonnull final Class<?> entityClass) {
        Objects.requireNonNull(entityManager, "entityManager is null");
        Objects.requireNonNull(entityClass, "entityClass is null");
        final var builder = entityManager.getCriteriaBuilder();
        final var criteria = builder.createQuery(Long.class);
        final var root = criteria.from(entityClass);
        criteria.select(builder.count(root));
        final var typed = entityManager.createQuery(criteria);
        return typed.getSingleResult();
    }

    public static <R> R applyCount(@Nonnull final EntityManager entityManager, @Nonnull final Class<?> entityClass,
                                   @Nonnull final LongFunction<? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return function.apply(
                count(entityManager, entityClass)
        );
    }

    /**
     * Applies {@link #count(EntityManager, Class) count} of the specified entity class and a random index([0, count -
     * 1]) to the specified function, and returns the result.
     *
     * @param entityManager an entity manager
     * @param entityClass   the entity class
     * @param function      the function.
     * @param <R>           result type parameter
     * @return the result of the {@code function}; {@code null} when the {@link #count(EntityManager, Class) count} of
     *         the entity class is {@code zero}.
     */
    @Nullable
    public static <R> R applyCountAndRandomIndex(
            @Nonnull final EntityManager entityManager, @Nonnull final Class<?> entityClass,
            @Nonnull final LongFunction<? extends LongFunction<? extends R>> function) {
        final var count = count(entityManager, entityClass);
        logger.log(System.Logger.Level.DEBUG, "count of {0}: {1}", entityClass, count);
        if (count == 0L) {
            return null;
        }
        assert count > 0L;
        final var index = ThreadLocalRandom.current().nextLong(count);
        logger.log(System.Logger.Level.DEBUG, "index: {0}", entityClass, index);
        return function.apply(count).apply(index);
    }

    public static void acceptCountAndRandomIndex(@Nonnull final EntityManager entityManager,
                                                 @Nonnull final Class<?> entityClass,
                                                 @Nonnull final LongFunction<? extends LongConsumer> function) {
        applyCountAndRandomIndex(entityManager, entityClass, c -> i -> {
            function.apply(c).accept(i);
            return null;
        });
    }

    /**
     * Selects an entity of the specified type at a random index.
     *
     * @param entityManager an entity manager
     * @param entityClass   the entity type
     * @param <T>           entity type parameter
     * @return an optional of the selected entity; {@link Optional#empty() empty} when no entity found.
     */
    @Nonnull
    public static <T> Optional<T> selectRandom(@Nonnull final EntityManager entityManager,
                                               @Nonnull final Class<T> entityClass) {
        Objects.requireNonNull(entityManager, "entityManager is null");
        Objects.requireNonNull(entityClass, "entityClass is null");
        return Optional.ofNullable(
                applyCountAndRandomIndex(
                        entityManager,
                        entityClass,
                        c -> i -> {
                            final var builder = entityManager.getCriteriaBuilder();
                            final var query = builder.createQuery(entityClass);
                            query.from(entityClass);
                            return entityManager
                                    .createQuery(query)
                                    .setFirstResult(Math.toIntExact(i))
                                    .setMaxResults(1)
                                    .getSingleResult();
                        }
                )
        );
    }

    // -----------------------------------------------------------------------------------------------------------------
    public static String entityName(final EntityManager entityManager, final Class<?> entityClass) {
        return entityManager.getMetamodel().entity(entityClass).getName();
    }

    // -----------------------------------------------------------------------------------------------------------------
    private ___JakartaPersistenceTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
