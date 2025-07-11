package com.github.jinahya.persistence.mapped.tests;

import com.github.jinahya.persistence.mapped.__MappedEntity;
import com.github.jinahya.persistence.mapped.tests.util.__JakartaPersistenceTestUtils;
import com.github.jinahya.persistence.mapped.tests.util.__JavaLangReflectUtils;
import com.github.jinahya.persistence.mapped.tests.util.__JavaSqlUtils;
import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.metamodel.Attribute;

import java.beans.BeanInfo;
import java.beans.FeatureDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S125", // Sections of code should not be commented out
})
public class __MappedEntityPersistenceTestUtils {

//    public static <ENTITY extends __MappedEntity<ENTITY, ?>>
//    void acceptEachDatabaseColumnName(@Nonnull final Connection connection,
//                                      @Nonnull final Class<ENTITY> entityClass,
//                                      @Nonnull final Consumer<? super String> consumer) {
//        Objects.requireNonNull(connection, "connection is null");
//        Objects.requireNonNull(entityClass, "entityClass is null");
//        Objects.requireNonNull(consumer, "consumer is null");
//        __MappedEntityTestUtils.acceptTableInfo(entityClass, c -> s -> t -> {
//            try {
//                __JavaSqlUtils.acceptEachColumnName(connection, c, s, t, consumer);
//            } catch (final SQLException sqle) {
//                throw new RuntimeException(
//                        "failed to get table column names" +
//                        "; entity class: " + entityClass +
//                        "; catalog: " + c +
//                        "; schema: " + s +
//                        "; table: " + t,
//                        sqle
//                );
//            }
//        });
//    }
//
//    public static <ENTITY extends __MappedEntity<ENTITY, ?>, C extends Collection<? super String>>
//    C addAllDatabaseColumnName(@Nonnull final Connection connection, @Nonnull final Class<ENTITY> entityClass,
//                               @Nonnull C collection) {
//        Objects.requireNonNull(connection, "connection is null");
//        Objects.requireNonNull(entityClass, "entityClass is null");
//        Objects.requireNonNull(collection, "collection is null");
//        acceptEachDatabaseColumnName(
//                connection,
//                entityClass, collection::add);
//        return collection;
//    }

    // -----------------------------------------------------------------------------------------------------------------
    static Optional<String> getAttributeColumnName(@Nonnull Class<?> beanClass,
                                                   @Nonnull final PropertyDescriptor propertyDescriptor) {
        Objects.requireNonNull(beanClass, "beanClass is null");
        Objects.requireNonNull(propertyDescriptor, "propertyDescriptor is null");
        {
            final var readMethod = propertyDescriptor.getReadMethod();
            if (readMethod != null) {
                final var result = __JakartaPersistenceTestUtils.getColumnName(readMethod);
                if (result.isPresent()) {
                    return result;
                }
            }
        }
        {
            final var writeMethod = propertyDescriptor.getWriteMethod();
            if (writeMethod != null) {
                final var result = __JakartaPersistenceTestUtils.getColumnName(writeMethod);
                if (result.isPresent()) {
                    return result;
                }
            }
        }
        final var field = __JavaLangReflectUtils.findField(beanClass, propertyDescriptor.getName());
        if (field.isPresent()) {
            final var result = __JakartaPersistenceTestUtils.getColumnName(field.get());
            if (result.isPresent()) {
                return result;
            }
        }
        return Optional.empty();
    }

    public static <ENTITY extends __MappedEntity<ENTITY, ?>>
    void acceptEachAttributeColumName(@Nonnull final EntityManagerFactory entityManagerFactory,
                                      @Nonnull final Class<ENTITY> entityClass,
                                      @Nonnull final Consumer<? super String> consumer) {
        Objects.requireNonNull(entityManagerFactory, "entityManagerFactory is null");
        Objects.requireNonNull(entityClass, "entityClass is null");
        Objects.requireNonNull(consumer, "consumer is null");
        final var entityType = entityManagerFactory.getMetamodel().entity(entityClass);
        final BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(entityClass);
        } catch (final IntrospectionException ie) {
            throw new RuntimeException("failed to introspect " + entityClass, ie);
        }
        final var propertyDescriptors = Arrays.stream(beanInfo.getPropertyDescriptors())
                .collect(Collectors.toMap(FeatureDescriptor::getName, Function.identity()));
        __JakartaPersistenceTestUtils.acceptEachAttributeName(entityType, an -> {
            final var propertyDescriptor = propertyDescriptors.get(an);
            if (propertyDescriptor != null) {
                getAttributeColumnName(entityClass, propertyDescriptor).ifPresent(consumer);
            }
        });
    }

    public static <ENTITY extends __MappedEntity<ENTITY, ?>, C extends Collection<? super String>>
    C addAllAttributeColumNames(@Nonnull final EntityManagerFactory entityManagerFactory,
                                @Nonnull final Class<ENTITY> entityClass,
                                @Nonnull final C collection) {
        Objects.requireNonNull(entityManagerFactory, "entityManagerFactory is null");
        Objects.requireNonNull(entityClass, "entityClass is null");
        Objects.requireNonNull(collection, "collection is null");
        acceptEachAttributeColumName(entityManagerFactory, entityClass, collection::add);
        return collection;
    }

    // -----------------------------------------------------------------------------------------------------------------
    public static <ENTITY extends __MappedEntity<ENTITY, ?>>
    void acceptEachAttributeName(@Nonnull final EntityManagerFactory entityManagerFactory,
                                 @Nonnull final Class<ENTITY> entityClass,
                                 @Nonnull final Consumer<? super String> consumer) {
        Objects.requireNonNull(entityManagerFactory, "entityManagerFactory is null");
        Objects.requireNonNull(entityClass, "entityClass is null");
        Objects.requireNonNull(consumer, "consumer is null");
        final var metamodel = entityManagerFactory.getMetamodel();
        final var managedType = metamodel.managedType(entityClass);
        managedType.getAttributes()
                .stream()
                .map(Attribute::getName)
                .forEach(consumer);
    }

    public static <ENTITY extends __MappedEntity<ENTITY, ?>, C extends Collection<? super String>>
    C addAllAttributeNames(@Nonnull final EntityManagerFactory entityManagerFactory,
                           @Nonnull final Class<ENTITY> entityClass, @Nonnull final C collection) {
        Objects.requireNonNull(entityManagerFactory, "entityManagerFactory is null");
        Objects.requireNonNull(entityClass, "entityClass is null");
        Objects.requireNonNull(collection, "collection is null");
        acceptEachAttributeName(entityManagerFactory, entityClass, collection::add);
        return collection;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private __MappedEntityPersistenceTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
