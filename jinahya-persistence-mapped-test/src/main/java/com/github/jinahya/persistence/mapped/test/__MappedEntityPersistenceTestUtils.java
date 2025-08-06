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
import jakarta.persistence.metamodel.ManagedType;
import jakarta.persistence.metamodel.Metamodel;

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

    static void acceptEachAttributeColumName2(@Nonnull final EntityManagerFactory entityManagerFactory,
                                              @Nonnull final Class<?> clazz,
                                              @Nonnull final Consumer<? super String> consumer) {
        Objects.requireNonNull(entityManagerFactory, "entityManagerFactory is null");
        Objects.requireNonNull(clazz, "clazz is null");
        Objects.requireNonNull(consumer, "consumer is null");
        final var entityType = entityManagerFactory.getMetamodel().entity(clazz);
        final BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(clazz);
        } catch (final IntrospectionException ie) {
            throw new RuntimeException("failed to introspect " + clazz, ie);
        }
        final var propertyDescriptors = Arrays.stream(beanInfo.getPropertyDescriptors())
                .collect(Collectors.toMap(FeatureDescriptor::getName, Function.identity()));
        ___JakartaPersistenceTestUtils.acceptEachAttributeName(
                entityType,
                an -> {
                    final var propertyDescriptor = propertyDescriptors.get(an);
                    if (propertyDescriptor != null) {
                        getAttributeColumnName(clazz, propertyDescriptor).ifPresent(consumer);
                    }
                }
        );
    }

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
        ___JakartaPersistenceTestUtils.acceptEachAttributeName(
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
        ___JakartaPersistenceTestUtils.acceptEachAttributeName(
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
            if (___JakartaPersistenceTestUtils.isEclipseLink()) {
                collection.addAll(___EclipseLinkTestUtils.getEntityColumnNames(entityManagerFactory, entityClass));
            } else if (___JakartaPersistenceTestUtils.isHibernate()) {
                collection.addAll(___HibernateTestUtils.getEntityColumnNames(entityManagerFactory, entityClass));
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

    // -----------------------------------------------------------------------------------------------------------------
    public static <ENTITY extends __MappedEntity<?>>
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

    public static <ENTITY extends __MappedEntity<?>, C extends Collection<? super String>>
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
