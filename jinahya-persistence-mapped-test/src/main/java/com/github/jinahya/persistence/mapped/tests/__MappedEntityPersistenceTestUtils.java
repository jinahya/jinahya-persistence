package com.github.jinahya.persistence.mapped.tests;

import com.github.jinahya.persistence.mapped.__MappedEntity;
import com.github.jinahya.persistence.mapped.tests.util.__JakartaPersistenceTestUtils;
import com.github.jinahya.persistence.mapped.tests.util.__JavaLangReflectUtils;
import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityManagerFactory;

import java.beans.FeatureDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
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

    static Optional<String> getColumnName(@Nonnull Class<?> beanClass,
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
    void acceptEachColumName(@Nonnull final EntityManagerFactory entityManagerFactory,
                             @Nonnull final Class<ENTITY> entityClass,
                             @Nonnull final Consumer<? super String> consumer)
            throws IntrospectionException {
        Objects.requireNonNull(entityManagerFactory, "entityManagerFactory is null");
        Objects.requireNonNull(entityClass, "entityClass is null");
        Objects.requireNonNull(consumer, "consumer is null");
        final var entityType = entityManagerFactory.getMetamodel().entity(entityClass);
        final var beanInfo = Introspector.getBeanInfo(entityClass);
        final var propertyDescriptors = Arrays.stream(beanInfo.getPropertyDescriptors())
                .collect(Collectors.toMap(FeatureDescriptor::getName, Function.identity()));
        __JakartaPersistenceTestUtils.acceptEachAttributeName(entityType, an -> {
            final var propertyDescriptor = propertyDescriptors.get(an);
            if (propertyDescriptor != null) {
                getColumnName(entityClass, propertyDescriptor).ifPresent(consumer);
            }
        });
    }

    public static <ENTITY extends __MappedEntity<ENTITY, ?>, C extends Collection<? super String>>
    C addAllColumName(@Nonnull final EntityManagerFactory entityManagerFactory,
                      @Nonnull final Class<ENTITY> entityClass,
                      @Nonnull final C collection)
            throws IntrospectionException {
        Objects.requireNonNull(entityManagerFactory, "entityManagerFactory is null");
        Objects.requireNonNull(entityClass, "entityClass is null");
        Objects.requireNonNull(collection, "collection is null");
        acceptEachColumName(entityManagerFactory, entityClass, collection::add);
        return collection;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private __MappedEntityPersistenceTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
