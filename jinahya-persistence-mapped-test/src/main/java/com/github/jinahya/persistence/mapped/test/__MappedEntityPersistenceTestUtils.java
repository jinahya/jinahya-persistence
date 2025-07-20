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
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.metamodel.Attribute;

import java.beans.BeanInfo;
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

    // -----------------------------------------------------------------------------------------------------------------
    static Optional<String> getAttributeColumnName(@Nonnull Class<?> beanClass,
                                                   @Nonnull final PropertyDescriptor propertyDescriptor) {
        Objects.requireNonNull(beanClass, "beanClass is null");
        Objects.requireNonNull(propertyDescriptor, "propertyDescriptor is null");
        {
            final var readMethod = propertyDescriptor.getReadMethod();
            if (readMethod != null) {
                final var result = ___JakartaPersistenceTestUtils.getColumnName(readMethod);
                if (result.isPresent()) {
                    return result;
                }
            }
        }
        {
            final var writeMethod = propertyDescriptor.getWriteMethod();
            if (writeMethod != null) {
                final var result = ___JakartaPersistenceTestUtils.getColumnName(writeMethod);
                if (result.isPresent()) {
                    return result;
                }
            }
        }
        final var field = ___JavaLangReflectTestUtils.findField(beanClass, propertyDescriptor.getName());
        if (field.isPresent()) {
            final var result = ___JakartaPersistenceTestUtils.getColumnName(field.get());
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
        ___JakartaPersistenceTestUtils.acceptEachAttributeName(entityType, an -> {
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
        acceptEachAttributeColumName(
                entityManagerFactory,
                entityClass,
                collection::add
        );
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
