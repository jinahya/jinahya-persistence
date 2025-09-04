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
import jakarta.persistence.Column;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.ManagedType;
import jakarta.persistence.metamodel.Metamodel;
import jakarta.persistence.spi.PersistenceProviderResolverHolder;
import jakarta.validation.constraints.PositiveOrZero;

import java.beans.BeanInfo;
import java.beans.FeatureDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.System.Logger.Level;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.LongConsumer;
import java.util.function.LongFunction;
import java.util.stream.Collectors;

/**
 * Utilities for the Jakarta Persistence API.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
final class ___JakartaPersistence_TestUtils {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // ----------------------------------------------------------------------------------------------------------- types
    static void acceptEachEntityType(@Nonnull final EntityManagerFactory entityManagerFactory,
                                     @Nonnull final Consumer<? super EntityType<?>> consumer) {
        Objects.requireNonNull(entityManagerFactory, "entityManagerFactory is null");
        Objects.requireNonNull(consumer, "consumer is null");
        entityManagerFactory.getMetamodel().getEntities().forEach(consumer);
    }

    static <C extends Collection<? super EntityType<?>>>
    C addAllEntityTypes(@Nonnull final EntityManagerFactory entityManagerFactory, @Nonnull final C collection) {
        Objects.requireNonNull(collection, "collection is null");
        acceptEachEntityType(
                entityManagerFactory,
                collection::add
        );
        return collection;
    }

    static void acceptEachEntityJavaType(@Nonnull final EntityManagerFactory entityManagerFactory,
                                         @Nonnull final Consumer<? super Class<?>> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        acceptEachEntityType(
                entityManagerFactory,
                et -> consumer.accept(et.getJavaType())
        );
    }

    static <C extends Collection<? super Class<?>>>
    C addAllEntityJavaTypes(@Nonnull final EntityManagerFactory entityManagerFactory, @Nonnull final C collection) {
        Objects.requireNonNull(collection, "collection is null");
        acceptEachEntityJavaType(
                entityManagerFactory,
                collection::add
        );
        return collection;
    }

    static void acceptEachEntityTableName(@Nonnull final EntityManagerFactory entityManagerFactory,
                                          @Nonnull final Consumer<? super String> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        acceptEachEntityJavaType(
                entityManagerFactory,
                jt -> {
                    final var tableName = ___JavaLangReflect_TestUtils.findAnnotation(jt, Table.class)
                            .map(Table::name)
                            .map(String::strip)
                            .filter(v -> !v.isBlank())
                            .orElse(null);
                    if (tableName == null) {
                        logger.log(Level.DEBUG, "no table name found for {0}", jt);
                        return;
                    }
                    consumer.accept(tableName);
                }
        );
    }

    static <C extends Collection<? super String>>
    C addAllEntityTableNames(@Nonnull final EntityManagerFactory entityManagerFactory, @Nonnull final C collection) {
        Objects.requireNonNull(collection, "collection is null");
        acceptEachEntityTableName(
                entityManagerFactory,
                collection::add
        );
        return collection;
    }

    // ------------------------------------------------------------------------------------------------------ attributes

    /**
     * Accepts each attribute name of the specified managed type to the specified consumer.
     *
     * @param managedType the managed type.
     * @param consumer    the consumer.
     * @param <X>         managed type parameter.
     */
    static <X> void acceptEachAttributeName(@Nonnull final ManagedType<X> managedType,
                                            @Nonnull final Consumer<? super String> consumer) {
        Objects.requireNonNull(managedType, "managedType is null");
        Objects.requireNonNull(consumer, "consumer is null");
        final var attributes = managedType.getAttributes();
        final var declaredAttributes = managedType.getDeclaredAttributes();
        managedType.getAttributes()
//        managedType.getDeclaredAttributes()
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
    static <X, C extends Collection<? super String>>
    C addAllAttributeNames(@Nonnull final ManagedType<X> managedType, @Nonnull final C collection) {
        Objects.requireNonNull(collection, "collection is null");
        acceptEachAttributeName(
                managedType,
                collection::add
        );
        return collection;
    }

    // -----------------------------------------------------------------------------------------------------------------
    static Optional<String> getAttributeColumnName(@Nonnull Class<?> beanClass,
                                                   @Nonnull final PropertyDescriptor propertyDescriptor) {
        Objects.requireNonNull(beanClass, "beanClass is null");
        Objects.requireNonNull(propertyDescriptor, "propertyDescriptor is null");
        {
            final var readMethod = propertyDescriptor.getReadMethod();
            if (readMethod != null) {
                final var result = ___JakartaPersistence_TestUtils.getColumnName(readMethod);
                if (result.isPresent()) {
                    return result;
                }
            }
        }
        {
            final var writeMethod = propertyDescriptor.getWriteMethod();
            if (writeMethod != null) {
                final var result = ___JakartaPersistence_TestUtils.getColumnName(writeMethod);
                if (result.isPresent()) {
                    return result;
                }
            }
        }
        final var field = ___JavaLangReflect_TestUtils.findField(beanClass, propertyDescriptor.getName());
        if (field.isPresent()) {
            final var result = ___JakartaPersistence_TestUtils.getColumnName(field.get());
            if (result.isPresent()) {
                return result;
            }
        }
        return Optional.empty();
    }

//    static void acceptEachAttributeColumName2(@Nonnull final EntityManagerFactory entityManagerFactory,
//                                              @Nonnull final Class<?> clazz,
//                                              @Nonnull final Consumer<? super String> consumer) {
//        Objects.requireNonNull(entityManagerFactory, "entityManagerFactory is null");
//        Objects.requireNonNull(clazz, "clazz is null");
//        Objects.requireNonNull(consumer, "consumer is null");
//        final var entityType = entityManagerFactory.getMetamodel().entity(clazz);
//        final BeanInfo beanInfo;
//        try {
//            beanInfo = Introspector.getBeanInfo(clazz);
//        } catch (final IntrospectionException ie) {
//            throw new RuntimeException("failed to introspect " + clazz, ie);
//        }
//        final var propertyDescriptors = Arrays.stream(beanInfo.getPropertyDescriptors())
//                .collect(Collectors.toMap(FeatureDescriptor::getName, Function.identity()));
//        ___JakartaPersistence_TestUtils.acceptEachAttributeName(
//                entityType,
//                an -> {
//                    final var propertyDescriptor = propertyDescriptors.get(an);
//                    if (propertyDescriptor != null) {
//                        getAttributeColumnName(clazz, propertyDescriptor).ifPresent(consumer);
//                    }
//                }
//        );
//    }

    private static void acceptEachAttributeColumName2(@Nonnull final Metamodel metamodel,
                                                      @Nonnull final Class<?> entityClass,
                                                      @Nonnull final ManagedType<?> managedType,
                                                      @Nonnull final Consumer<? super String> consumer) {
        Objects.requireNonNull(metamodel, "metamodel is null");
        Objects.requireNonNull(entityClass, "entityClass is null");
        Objects.requireNonNull(managedType, "managedType is null");
        Objects.requireNonNull(consumer, "consumer is null");
        final BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(entityClass);
        } catch (final IntrospectionException ie) {
            throw new RuntimeException("failed to introspect " + entityClass, ie);
        }
        final var propertyDescriptors = Arrays.stream(beanInfo.getPropertyDescriptors())
                .collect(Collectors.toMap(FeatureDescriptor::getName, Function.identity()));
        ___JakartaPersistence_TestUtils.acceptEachAttributeName(
                managedType,
                an -> {
                    final var propertyDescriptor = propertyDescriptors.get(an);
                    if (propertyDescriptor != null) {
                        getAttributeColumnName(entityClass, propertyDescriptor).ifPresent(consumer);
                        // Embeddable
                        final var type = propertyDescriptor.getPropertyType();
                        try {
                            final var managedType2 = metamodel.managedType(type);
                            acceptEachAttributeColumName2(metamodel, type, managedType2, consumer);
                        } catch (final IllegalArgumentException iae) {
                            // ignore
                        }
                    }
                }
        );
    }

    public static <ENTITY extends __MappedEntity<?>>
    void acceptEntityColumName(@Nonnull final EntityManagerFactory entityManagerFactory,
                               @Nonnull final Class<ENTITY> entityClass,
                               @Nonnull final Consumer<? super String> consumer) {
        Objects.requireNonNull(entityManagerFactory, "entityManagerFactory is null");
        Objects.requireNonNull(entityClass, "entityClass is null");
        Objects.requireNonNull(consumer, "consumer is null");
        final var metamodel = entityManagerFactory.getMetamodel();
        final var entityType = metamodel.entity(entityClass); // IllegalArgumentException
        if (true) {
            acceptEachAttributeColumName2(metamodel, entityClass, entityType, consumer);
        }
        final BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(entityClass);
        } catch (final IntrospectionException ie) {
            throw new RuntimeException("failed to introspect " + entityClass, ie);
        }
        final var propertyDescriptors = Arrays.stream(beanInfo.getPropertyDescriptors())
                .collect(Collectors.toMap(FeatureDescriptor::getName, Function.identity()));
        ___JakartaPersistence_TestUtils.acceptEachAttributeName(
                entityType,
                an -> {
                    final var propertyDescriptor = propertyDescriptors.get(an);
                    if (propertyDescriptor != null) {
                        getAttributeColumnName(entityClass, propertyDescriptor).ifPresent(consumer);
                    }
                }
        );
    }

    public static <ENTITY extends __MappedEntity<?>, C extends Collection<? super String>>
    C addAllEntityColumNames(@Nonnull final EntityManagerFactory entityManagerFactory,
                             @Nonnull final Class<ENTITY> entityClass,
                             @Nonnull final C collection) {
        Objects.requireNonNull(entityManagerFactory, "entityManagerFactory is null");
        Objects.requireNonNull(entityClass, "entityClass is null");
        Objects.requireNonNull(collection, "collection is null");

        if (true) {
            if (___JakartaPersistence_TestUtils.isEclipseLink()) {
                collection.addAll(____EclipseLinkTestUtils.getEntityColumnNames(entityManagerFactory, entityClass));
            } else if (___JakartaPersistence_TestUtils.isHibernate()) {
                collection.addAll(____HibernateTestUtils.getEntityColumnNames(entityManagerFactory, entityClass));
            }
            return collection;
        }
        acceptEntityColumName(
                entityManagerFactory,
                entityClass,
                collection::add
        );
        return collection;
    }

//    // -----------------------------------------------------------------------------------------------------------------
//    public static <ENTITY extends __MappedEntity<?>>
//    void acceptEachAttributeName(@Nonnull final EntityManagerFactory entityManagerFactory,
//                                 @Nonnull final Class<ENTITY> entityClass,
//                                 @Nonnull final Consumer<? super String> consumer) {
//        Objects.requireNonNull(entityManagerFactory, "entityManagerFactory is null");
//        Objects.requireNonNull(entityClass, "entityClass is null");
//        Objects.requireNonNull(consumer, "consumer is null");
//        final var metamodel = entityManagerFactory.getMetamodel();
//        final var managedType = metamodel.managedType(entityClass);
//        managedType.getAttributes()
//                .stream()
//                .map(Attribute::getName)
//                .forEach(consumer);
//    }
//
//    public static <ENTITY extends __MappedEntity<?>, C extends Collection<? super String>>
//    C addAllAttributeNames(@Nonnull final EntityManagerFactory entityManagerFactory,
//                           @Nonnull final Class<ENTITY> entityClass, @Nonnull final C collection) {
//        Objects.requireNonNull(entityManagerFactory, "entityManagerFactory is null");
//        Objects.requireNonNull(entityClass, "entityClass is null");
//        Objects.requireNonNull(collection, "collection is null");
//        acceptEachAttributeName(entityManagerFactory, entityClass, collection::add);
//        return collection;
//    }

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
    static <R> R applyEntityManagerInTransaction(final EntityManager entityManager,
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

    static void acceptEntityManagerInTransaction(final EntityManager entityManager,
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
    static <R> R applyUnwrappedConnection(final EntityManager entityManager,
                                          final Function<? super Connection, ? extends R> function) {
        Objects.requireNonNull(entityManager, "entityManager is null");
        Objects.requireNonNull(function, "function is null");
        try {
            final var connection = entityManager.unwrap(Connection.class);
            if (connection == null) {
                throw new RuntimeException("null unwrapped from " + entityManager);
            }
            return function.apply(connection);
        } catch (final Exception e1) {
            logger.log(Level.DEBUG, "failed to unwrap connection from " + entityManager, e1);
            try {
                return ____HibernateTestUtils.applyConnection(
                        entityManager,
                        function
                );
            } catch (final Exception e2) {
                throw new RuntimeException("failed to unwrap connection from " + entityManager, e2);
            }
        }
    }

    static <R> R applyConnection(final EntityManager entityManager,
                                 final Function<? super Connection, ? extends R> function) {
        Objects.requireNonNull(entityManager, "entityManager is null");
        Objects.requireNonNull(function, "function is null");
        return applyUnwrappedConnection(
                entityManager,
                function
        );
    }

    static void acceptConnection(final EntityManager entityManager,
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

    static <R> R applyConnectionInTransaction(final EntityManager entityManager,
                                              final Function<? super Connection, ? extends R> function) {
        Objects.requireNonNull(entityManager, "entityManager is null");
        Objects.requireNonNull(function, "function is null");
        return applyEntityManagerInTransaction(
                entityManager,
                em -> applyUnwrappedConnection(
                        em,
                        function
                ),
                true
        );
    }

    static void acceptConnectionInTransaction(final EntityManager entityManager,
                                              final Consumer<? super Connection> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        applyConnectionInTransaction(
                entityManager,
                c -> {
                    consumer.accept(c);
                    return null;
                }
        );
    }

    // -----------------------------------------------------------------------------------------------------------------
    @PositiveOrZero
    static long count(@Nonnull final EntityManager entityManager, @Nonnull final Class<?> entityClass) {
        Objects.requireNonNull(entityManager, "entityManager is null");
        Objects.requireNonNull(entityClass, "entityClass is null");
        final var builder = entityManager.getCriteriaBuilder();
        final var criteria = builder.createQuery(Long.class);
        final var root = criteria.from(entityClass);
        criteria.select(builder.count(root));
        final var typed = entityManager.createQuery(criteria);
        return typed.getSingleResult();
    }

    static <R> R applyCount(@Nonnull final EntityManager entityManager, @Nonnull final Class<?> entityClass,
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
    static <R> R applyCountAndRandomIndex(
            @Nonnull final EntityManager entityManager, @Nonnull final Class<?> entityClass,
            @Nonnull final LongFunction<? extends LongFunction<? extends R>> function) {
        final var count = count(entityManager, entityClass);
        logger.log(Level.DEBUG, "count of {0}: {1}", entityClass, count);
        if (count == 0L) {
            return null;
        }
        assert count > 0L;
        final var index = ThreadLocalRandom.current().nextLong(count);
        logger.log(Level.DEBUG, "random index: {0}", index);
        return function.apply(count).apply(index);
    }

    static void acceptCountAndRandomIndex(@Nonnull final EntityManager entityManager,
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
    static <T> Optional<T> selectRandom(@Nonnull final EntityManager entityManager,
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
                            final var root = query.from(entityClass);
                            query.select(root);
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
    static String entityName(final EntityManager entityManager, final Class<?> entityClass) {
        return entityManager.getMetamodel().entity(entityClass).getName();
    }

    // -----------------------------------------------------------------------------------------------------------------
    static boolean isEclipseLink() {
        return PersistenceProviderResolverHolder
                .getPersistenceProviderResolver()
                .getPersistenceProviders()
                .stream()
                .anyMatch(pp -> pp.getClass().getName().startsWith("org.eclipse.persistence"));
    }

    static boolean isHibernate() {
        return PersistenceProviderResolverHolder
                .getPersistenceProviderResolver()
                .getPersistenceProviders()
                .stream()
                .anyMatch(pp -> pp.getClass().getName().startsWith("org.hibernate"));
    }

    // -----------------------------------------------------------------------------------------------------------------
    private ___JakartaPersistence_TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
