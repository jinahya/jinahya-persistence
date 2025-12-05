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
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.ManagedType;
import jakarta.persistence.metamodel.Metamodel;
import jakarta.persistence.spi.PersistenceProviderResolverHolder;
import jakarta.validation.constraints.PositiveOrZero;
import org.junit.platform.commons.util.ReflectionUtils;

import java.beans.BeanInfo;
import java.beans.FeatureDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.System.Logger.Level;
import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.LongFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Utilities for the Jakarta Persistence API.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "unchecked",
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public final class ___JakartaPersistence_TestUtils {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // -----------------------------------------------------------------------------------------------------------------
    private static final Map<Class<? extends __MappedEntity<?>>, ManagedType<?>> MANAGED_TYPES = new HashMap<>();

    @Nonnull
    static <ENTITY extends __MappedEntity<?>>
    ManagedType<ENTITY> getManagedType(final @Nonnull EntityManagerFactory entityManagerFactory,
                                       final @Nonnull Class<ENTITY> entityClass) {
        Objects.requireNonNull(entityManagerFactory, "entityManagerFactory is null");
        Objects.requireNonNull(entityClass, "entityClass is null");
        return (ManagedType<ENTITY>) MANAGED_TYPES.computeIfAbsent(
                entityClass,
                k -> entityManagerFactory.getMetamodel().managedType(k)
        );
    }

    @Nonnull
    static <ENTITY extends __MappedEntity<?>>
    Annotation[] getAnnotations(final @Nonnull Class<ENTITY> entityClass,
                                final @Nonnull Attribute<? super ENTITY, ?> attribute) {
        final var entityMember = attribute.getJavaMember();
        if (entityMember instanceof Field field) {
            final var actualField = ReflectionUtils.findFields(
                    entityClass,
                    f -> f.getName().equals(field.getName()),
                    ReflectionUtils.HierarchyTraversalMode.BOTTOM_UP
            ).get(0);
            return actualField.getDeclaredAnnotations();
        } else if (entityMember instanceof Method method) {
            final var actualMethod = ReflectionUtils.findMethod(
                    entityClass,
                    method.getName()
            ).orElseThrow();
            return actualMethod.getDeclaredAnnotations();
        }
        throw new RuntimeException("unknown entity member type: " + entityMember);
    }

    /**
     * Returns an <em>unmodifiable</em> map of attributes and their annotations defined in the specified entity class.
     *
     * @param entityManagerFactory an entity manager factory.
     * @param entityClass          the entity class.
     * @param <ENTITY>             entity type parameter.
     * @return an <em>unmodifiable</em> map of attributes and their annotations defined in the specified entity class.
     */
    static <ENTITY extends __MappedEntity<?>>
    Map<Attribute<? super ENTITY, ?>, List<Annotation>> getAttributesAndAnnotationLists(
            final @Nonnull EntityManagerFactory entityManagerFactory,
            final @Nonnull Class<ENTITY> entityClass) {
        final var managedType = getManagedType(entityManagerFactory, entityClass);
        final var attributes = managedType.getAttributes();
        return attributes.stream()
                .map(a -> {
                    final var annotations = Arrays.asList(getAnnotations(entityClass, a));
                    return new AbstractMap.SimpleEntry<>(a, annotations);
                })
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    // ----------------------------------------------------------------------------------------------------------- types
    static void acceptEachEntityType(final @Nonnull EntityManagerFactory entityManagerFactory,
                                     final @Nonnull Consumer<? super EntityType<?>> consumer) {
        Objects.requireNonNull(entityManagerFactory, "entityManagerFactory is null");
        Objects.requireNonNull(consumer, "consumer is null");
        entityManagerFactory.getMetamodel().getEntities().forEach(consumer);
    }

    static <C extends Collection<? super EntityType<?>>>
    C addAllEntityTypes(final @Nonnull EntityManagerFactory entityManagerFactory, final @Nonnull C collection) {
        Objects.requireNonNull(collection, "collection is null");
        acceptEachEntityType(
                entityManagerFactory,
                collection::add
        );
        return collection;
    }

    static void acceptEachEntityJavaType(final @Nonnull EntityManagerFactory entityManagerFactory,
                                         final @Nonnull Consumer<? super Class<?>> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        acceptEachEntityType(
                entityManagerFactory,
                et -> consumer.accept(et.getJavaType())
        );
    }

    static <C extends Collection<? super Class<?>>>
    C addAllEntityJavaTypes(final @Nonnull EntityManagerFactory entityManagerFactory, final @Nonnull C collection) {
        Objects.requireNonNull(collection, "collection is null");
        acceptEachEntityJavaType(
                entityManagerFactory,
                collection::add
        );
        return collection;
    }

    // ------------------------------------------------------------------------------------------------------ tableNames
    static void acceptEachEntityTableName(final @Nonnull EntityManagerFactory entityManagerFactory,
                                          final @Nonnull Consumer<? super String> consumer) {
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
    C addAllEntityTableNames(final @Nonnull EntityManagerFactory entityManagerFactory, final @Nonnull C collection) {
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
    static <X> void acceptEachAttributeName(final @Nonnull ManagedType<X> managedType,
                                            final @Nonnull Consumer<? super String> consumer) {
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
    C addAllAttributeNames(final @Nonnull ManagedType<X> managedType, final @Nonnull C collection) {
        Objects.requireNonNull(collection, "collection is null");
        acceptEachAttributeName(
                managedType,
                collection::add
        );
        return collection;
    }

    // ----------------------------------------------------------------------------------------------------- columnNames
    static Optional<String> getAttributeColumnName(@Nonnull Class<?> beanClass,
                                                   final @Nonnull PropertyDescriptor propertyDescriptor) {
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

    private static void acceptEachAttributeColumName(final @Nonnull Metamodel metamodel,
                                                     final @Nonnull Class<?> entityClass,
                                                     final @Nonnull ManagedType<?> managedType,
                                                     final @Nonnull Consumer<? super String> consumer) {
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
                            acceptEachAttributeColumName(metamodel, type, managedType2, consumer);
                        } catch (final IllegalArgumentException iae) {
                            // ignore
                        }
                    }
                }
        );
    }

    public static <ENTITY extends __MappedEntity<?>>
    void acceptEntityColumName(final @Nonnull EntityManagerFactory entityManagerFactory,
                               final @Nonnull Class<ENTITY> entityClass,
                               final @Nonnull Consumer<? super String> consumer) {
        Objects.requireNonNull(entityManagerFactory, "entityManagerFactory is null");
        Objects.requireNonNull(entityClass, "entityClass is null");
        Objects.requireNonNull(consumer, "consumer is null");
        final var metamodel = entityManagerFactory.getMetamodel();
        final var entityType = metamodel.entity(entityClass); // IllegalArgumentException
        if (true) {
            acceptEachAttributeColumName(metamodel, entityClass, entityType, consumer);
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

    /**
     * Adds all attribute column names of the specified entity class to the specified collection.
     *
     * @param entityManagerFactory an entity manager factory.
     * @param entityClass          the entity class.
     * @param collection           the collection.
     * @param <ENTITY>             entity type parameter.
     * @param <C>                  collection type parameter.
     * @return given {@code collection}.
     */
    public static <ENTITY extends __MappedEntity<?>, C extends Collection<? super String>>
    C addAllEntityColumNames(final @Nonnull EntityManagerFactory entityManagerFactory,
                             final @Nonnull Class<ENTITY> entityClass,
                             final @Nonnull C collection) {
        Objects.requireNonNull(entityManagerFactory, "entityManagerFactory is null");
        Objects.requireNonNull(entityClass, "entityClass is null");
        Objects.requireNonNull(collection, "collection is null");
        if (___JakartaPersistence_TestUtils.isEclipseLink()) {
            return ____EclipseLinkTestUtils.addAllEntityColumnNames(
                    entityManagerFactory,
                    entityClass,
                    collection
            );
        } else if (___JakartaPersistence_TestUtils.isHibernate()) {
            return ____HibernateTestUtils.addAllEntityColumnNames(
                    entityManagerFactory,
                    entityClass,
                    collection
            );
        } else {
            throw new RuntimeException("unknown persistence provider");
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    static Optional<String> getColumnName(final @Nonnull Method method) {
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

    static Optional<String> getColumnName(final @Nonnull Field field) {
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
    public static <R> R applyEntityManagerInTransaction(
            final @Nonnull EntityManager entityManager,
            final @Nonnull Function<? super EntityManager, ? extends R> function,
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
                logger.log(Level.DEBUG, "rolling back...");
                transaction.rollback();
            } else {
                logger.log(Level.WARNING, "committing...");
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

// -----------------------------------------------------------------------------------------------------------------

    /**
     * Starts a resource-level transaction on the specified entity manager, and returns what supplied from the specified
     * supplier.
     *
     * @param entityManager the entity manager.
     * @param supplier      the supplier.
     * @param rollback      a flag for rolling-back; {@code true} for rolling-back, {@code false} for committing.
     * @param <R>           result type parameter.
     * @return the result of the specified supplier.
     * @see #applyEntityManagerInTransaction(EntityManager, Function, boolean)
     */
    public static <R> R getInTransaction(final @Nonnull EntityManager entityManager,
                                         final @Nonnull Supplier<? extends R> supplier,
                                         final boolean rollback) {
        Objects.requireNonNull(supplier, "supplier is null");
        return applyEntityManagerInTransaction(
                entityManager,
                em -> {
                    assert em == entityManager;
                    return supplier.get();
                },
                rollback
        );
    }

    /**
     * Starts a resource-level transaction on the specified entity manager, returns what supplied from the specified
     * supplier, and rolls back the transaction.
     *
     * @param entityManager the entity manager.
     * @param supplier      the supplier.
     * @param <R>           result type parameter.
     * @return the result of the specified supplier.
     * @apiNote This method invokes the {@link #getInTransaction(EntityManager, Supplier, boolean)} method with
     *         {@code entityManager}, {@code supplier}, and {@code true}.
     */
    public static <R> R getInTransactionAndRollback(final @Nonnull EntityManager entityManager,
                                                    final @Nonnull Supplier<? extends R> supplier) {
        return getInTransaction(
                entityManager,
                supplier,
                true
        );
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns the result of the specified function applied to a connection unwrapped from the specified entity
     * manager.
     *
     * @param entityManager the entity manager from which a connection is unwrapped.
     * @param function      the function.
     * @param <R>           result type parameter.
     * @return the result of the {@code function} applied to the connection unwrapped from the {@code entityManager}.
     */
    public static <R> R applyUnwrappedConnection(final @Nonnull EntityManager entityManager,
                                                 final @Nonnull Function<? super Connection, ? extends R> function) {
        Objects.requireNonNull(entityManager, "entityManager is null");
        Objects.requireNonNull(function, "function is null");
        try {
            final var connection = entityManager.unwrap(Connection.class);
            if (connection == null) {
                throw new RuntimeException("null unwrapped from " + entityManager);
            }
            logger.log(Level.TRACE, "unwrapped connection: {0}", connection);
            return function.apply(connection);
        } catch (final Exception e1) {
            logger.log(Level.TRACE, "failed to unwrap connection from " + entityManager, e1);
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

    public static <R> R applyConnection(final EntityManager entityManager,
                                        final Function<? super Connection, ? extends R> function,
                                        final boolean rollback) {
        Objects.requireNonNull(entityManager, "entityManager is null");
        Objects.requireNonNull(function, "function is null");
        return getInTransaction(
                entityManager,
                () -> applyUnwrappedConnection(entityManager, function),
                rollback
        );
    }

    public static <R> R applyConnectionAndRollback(final EntityManager entityManager,
                                                   final Function<? super Connection, ? extends R> function) {
        return applyConnection(entityManager, function, true);
    }

    // -----------------------------------------------------------------------------------------------------------------
    public static <R> R applyCriteriaBuilder(@Nonnull final EntityManager entityManager,
                                             @Nonnull final Function<
                                                     ? super CriteriaBuilder,
                                                     ? extends R
                                                     > function) {
        Objects.requireNonNull(entityManager, "entityManager is null");
        Objects.requireNonNull(function, "function is null");
        final var b = entityManager.getCriteriaBuilder();
        return function.apply(b);
    }

    public static <R> R applyCriteriaQuery(@Nonnull final EntityManager entityManager,
                                           @Nonnull final Class<R> resultClass,
                                           @Nonnull final Function<
                                                   ? super CriteriaBuilder,
                                                   ? extends Function<
                                                           ? super CriteriaQuery<R>,
                                                           ? extends R
                                                           >
                                                   > function) {
        Objects.requireNonNull(function, "function is null");
        return applyCriteriaBuilder(entityManager, b -> {
            final var q = b.createQuery(resultClass);
            return function.apply(b).apply(q);
        });
    }

    public static <R, T> R applyRoot(@Nonnull final EntityManager entityManager,
                                     @Nonnull final Class<R> resultClass,
                                     @Nonnull final Class<T> entityClass,
                                     @Nonnull final Function<
                                             ? super CriteriaBuilder,
                                             ? extends Function<
                                                     ? super CriteriaQuery<R>,
                                                     ? extends Function<
                                                             ? super Root<T>,
                                                             ? extends R
                                                             >
                                                     >
                                             > function) {
        Objects.requireNonNull(function, "function is null");
        return applyCriteriaQuery(entityManager, resultClass, b -> q -> {
            final var r = q.from(entityClass);
            return function.apply(b).apply(q).apply(r);
        });
    }

    // -----------------------------------------------------------------------------------------------------------------
    @PositiveOrZero
    static long count2(final @Nonnull EntityManager entityManager, final @Nonnull Class<?> entityClass) {
        Objects.requireNonNull(entityManager, "entityManager is null");
        Objects.requireNonNull(entityClass, "entityClass is null");
        return applyRoot(
                entityManager,
                Long.class,
                entityClass,
                b -> q -> r -> {
                    q.select(b.count(r));
                    return entityManager.createQuery(q).getSingleResult();
                }
        );
    }

    /**
     * Counts the number of entities in the table from which the specified entity class maps.
     *
     * @param entityManager an entity manager
     * @param entityClass   the entity class
     * @return the number of entities of {@code entityClass}.
     */
    @PositiveOrZero
    public static long count(final @Nonnull EntityManager entityManager, final @Nonnull Class<?> entityClass) {
        Objects.requireNonNull(entityManager, "entityManager is null");
        Objects.requireNonNull(entityClass, "entityClass is null");
        if (true) {
            return applyRoot(
                    entityManager,
                    Long.class,
                    entityClass,
                    b -> q -> r -> {
                        q.select(b.count(r));
                        return entityManager.createQuery(q).getSingleResult();
                    }
            );
        }
        final var builder = entityManager.getCriteriaBuilder();
        final var query = builder.createQuery(Long.class);
        final var root = query.from(entityClass);
        query.select(builder.count(root));
        return entityManager.createQuery(query).getSingleResult();
    }

    /**
     * Applies {@link #count(EntityManager, Class) count} of the specified entity class and a random index([0, count))
     * to the specified function, and returns the result.
     * <p>
     * {@snippet lang = "java":
     * applyCountAndRandomIndex(c -> i -> {
     *     assert c > 0L;
     *     assert i >= 0L;
     *     assert i < c;
     *     return null;
     * });
     *}
     *
     * @param entityManager an entity manager
     * @param entityClass   the entity class
     * @param function      the function.
     * @param <R>           result type parameter
     * @return the result of the {@code function}; {@code null} when the {@link #count(EntityManager, Class) count} of
     *         the {@code entityClass} is {@code zero}.
     */
    @Nullable
    public static <R> R applyCountAndRandomIndex(
            final @Nonnull EntityManager entityManager, final @Nonnull Class<?> entityClass,
            final @Nonnull LongFunction<? extends LongFunction<? extends R>> function) {
        final var count = count(entityManager, entityClass);
        logger.log(Level.DEBUG, "count of {0}: {1}", entityClass, count);
        if (count == 0L) {
            return null;
        }
        assert count > 0L;
        final var index = ThreadLocalRandom.current().nextLong(count);
        logger.log(Level.DEBUG, "random index: {0}", index);
        assert index >= 0L;
        assert index < count;
        return function.apply(count).apply(index);
    }

    /**
     * Applies the count, a random index([0, count]), and an instance of the specified entity class selected at random
     * index to the specified function, and returns the result.
     * <p>
     * {@snippet lang = "java":
     * applyCountRandomIndexAndEntity(c -> i -> e -> {
     *     assert c > 0L;
     *     assert i >= 0L;
     *     assert i < c;
     *     assert e != null;
     *     return null;
     * });
     *}
     *
     * @param entityManager an entity manager.
     * @param entityClass   the entity class.
     * @param function      the function.
     * @param <T>           entity type parameter
     * @param <R>           result type parameter
     * @return an optional of the result of the {@code function}; {@link Optional#empty() empty} when the
     *         {@link #count(EntityManager, Class) count} of the {@code entityClass} is {@code zero}.
     * @see #applyCountAndRandomIndex(EntityManager, Class, LongFunction)
     */
    @Nonnull
    public static <T, R> Optional<R> applyCountRandomIndexAndEntity(
            final @Nonnull EntityManager entityManager,
            final @Nonnull Class<T> entityClass,
            final @Nonnull LongFunction<? extends LongFunction<? extends Function<? super T, ? extends R>>> function) {
        Objects.requireNonNull(entityManager, "entityManager is null");
        Objects.requireNonNull(entityClass, "entityClass is null");
        Objects.requireNonNull(function, "function is null");
        return Optional.ofNullable(
                applyCountAndRandomIndex(
                        entityManager,
                        entityClass,
                        c -> i -> {
                            assert c > 0L;
                            assert i >= 0L;
                            assert i < c;
                            final var builder = entityManager.getCriteriaBuilder();
                            final var query = builder.createQuery(entityClass);
                            final var root = query.from(entityClass);
                            query.select(root);
                            final var entity = entityManager
                                    .createQuery(query)
                                    .setFirstResult(Math.toIntExact(i))
                                    .setMaxResults(1)
                                    .getSingleResult();
                            return function.apply(c).apply(i).apply(entity);
                        }
                )
        );
    }

    /**
     * Selects a random instance of the specified entity class.
     *
     * @param entityManager an entity manager
     * @param entityClass   the entity type
     * @param <T>           entity type parameter
     * @return an optional of the selected entity; {@link Optional#empty() empty} when the table is empty.
     */
    @Nonnull
    public static <T> Optional<T> selectRandom(final @Nonnull EntityManager entityManager,
                                               final @Nonnull Class<T> entityClass) {
        Objects.requireNonNull(entityManager, "entityManager is null");
        Objects.requireNonNull(entityClass, "entityClass is null");
        return applyCountRandomIndexAndEntity(
                entityManager,
                entityClass,
                c -> i -> e -> e
        );
    }

    // -----------------------------------------------------------------------------------------------------------------
    static String entityName(final @Nonnull EntityManager entityManager, final @Nonnull Class<?> entityClass) {
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
