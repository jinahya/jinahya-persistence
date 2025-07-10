package com.github.jinahya.persistence.mapped.tests.util;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import jakarta.persistence.EntityManager;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.ManagedType;

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

public final class __JakartaPersistenceTestUtils {

    public static <X>
    void acceptEachAttributeName(@Nonnull final ManagedType<X> managedType,
                                 @Nonnull final Consumer<? super String> consumer) {
        Objects.requireNonNull(managedType, "managedType is null");
        Objects.requireNonNull(consumer, "consumer is null");
        managedType.getAttributes()
                .stream()
                .map(Attribute::getName)
                .forEach(consumer);
    }

    public static <X, C extends Collection<? super String>>
    C addAllAttributeNames(@Nonnull final ManagedType<X> managedType, @Nonnull final C collection) {
        Objects.requireNonNull(collection, "collection is null");
        acceptEachAttributeName(managedType, collection::add);
        return collection;
    }

    public static Optional<String> getColumnName(@Nonnull final Method method) {
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

    public static Optional<String> getColumnName(@Nonnull final Field field) {
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

    public static <R> R applyEntityManagerInTransaction(final EntityManager entityManager,
                                                        final Function<? super EntityManager, ? extends R> function,
                                                        final boolean rollback) {
        Objects.requireNonNull(entityManager, "entityManager is null");
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
            throw new RuntimeException("failed to apply " + function + " and rollback: " + rollback, e);
        }
    }

    public static void acceptEntityManagerInTransaction(final EntityManager entityManager,
                                                        final Consumer<? super EntityManager> consumer,
                                                        final boolean rollback) {
        Objects.requireNonNull(entityManager, "entityManager is null");
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

    public static <R> R applyConnection(final EntityManager entityManager,
                                        final Function<? super Connection, ? extends R> function,
                                        final boolean rollback) {
        Objects.requireNonNull(entityManager, "entityManager is null");
        Objects.requireNonNull(function, "function is null");
        final var transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            final var connection = entityManager.unwrap(Connection.class);
            final R result = function.apply(connection);
            if (rollback) {
                transaction.rollback();
            } else {
                transaction.commit();
            }
            return result;
        } catch (final Exception e) {
            transaction.rollback();
            throw new RuntimeException(e);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    public static long count(final EntityManager entityManager, final Class<?> entityClass) {
        final var builder = entityManager.getCriteriaBuilder();
        final var criteria = builder.createQuery(Long.class);
        final var root = criteria.from(entityClass);
        criteria.select(builder.count(root));
        final var typed = entityManager.createQuery(criteria);
        return typed.getSingleResult();
    }

    public static <R> R applyCount(final EntityManager entityManager, final Class<?> entityClass,
                                   final LongFunction<? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return function.apply(count(entityManager, entityClass));
    }

    public static <R> R applyCountAndRandomIndex(final EntityManager entityManager, final Class<?> entityClass,
                                                 final LongFunction<? extends LongFunction<? extends R>> function) {
        final var count = count(entityManager, entityClass);
        if (count == 0L) {
            return null;
        }
        assert count > 0L;
        final var index = ThreadLocalRandom.current().nextLong(count);
        return function.apply(count).apply(index);
    }

    public static void acceptCountAndRandomIndex(final EntityManager entityManager, final Class<?> entityClass,
                                                 final LongFunction<? extends LongConsumer> function) {
        applyCountAndRandomIndex(entityManager, entityClass, c -> i -> {
            function.apply(c).accept(i);
            return null;
        });
    }

    public static <T> Optional<T> selectRandom(final EntityManager entityManager, final Class<T> entityClass) {
        Objects.requireNonNull(entityManager, "entityManager is null");
        Objects.requireNonNull(entityClass, "entityClass is null");
        return applyCountAndRandomIndex(
                entityManager,
                entityClass,
                c -> i -> {
                    final var builder = entityManager.getCriteriaBuilder();
                    final var query = builder.createQuery(entityClass);
                    query.from(entityClass);
                    return Optional.of(
                            entityManager.createQuery(query)
                                    .setFirstResult(Math.toIntExact(i))
                                    .setMaxResults(1)
                                    .getSingleResult()
                    );
                }
        );
    }

    public static String entityName(final EntityManager entityManager, final Class<?> entityClass) {
        return entityManager.getMetamodel().entity(entityClass).getName();
    }

    // -----------------------------------------------------------------------------------------------------------------
    private __JakartaPersistenceTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
