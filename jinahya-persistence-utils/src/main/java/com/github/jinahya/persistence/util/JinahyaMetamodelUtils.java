package com.github.jinahya.persistence.util;

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
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.ManagedType;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Stream;

@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class JinahyaMetamodelUtils {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // -----------------------------------------------------------------------------------------------------------------
    private static final Map<Class<?>, ManagedType<?>> MANAGED_TYPES = new ConcurrentHashMap<>();

    public static <X> ManagedType<X> getManagedType(
            final @Nonnull Class<X> typeClass,
            final @Nonnull Stream<? extends EntityManagerFactory> entityManagerFactoryStream) {
        Objects.requireNonNull(typeClass, "typeClass is null");
        Objects.requireNonNull(entityManagerFactoryStream, "entityManagerFactoryStream is null");
        @SuppressWarnings({"unchecked"})
        final var managedType = (ManagedType<X>) MANAGED_TYPES.computeIfAbsent(
                typeClass,
                k -> entityManagerFactoryStream
                        .map(EntityManagerFactory::getMetamodel)
                        .map(m -> {
                            try {
                                return m.entity(typeClass);
                            } catch (final IllegalArgumentException iae) {
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .findFirst()
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "no entity type found for entity class: " + typeClass)
                        ));
        return managedType;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private static final Map<Class<?>, EntityType<?>> ENTITY_TYPES = new ConcurrentHashMap<>();

    public static <X> EntityType<X> getEntityType(
            final @Nonnull Class<X> entityClass,
            final @Nonnull Stream<? extends EntityManagerFactory> entityManagerFactoryStream) {
        Objects.requireNonNull(entityClass, "entityClass is null");
        Objects.requireNonNull(entityManagerFactoryStream, "entityManagerFactoryStream is null");
        @SuppressWarnings({"unchecked"})
        final var entityType = (EntityType<X>) ENTITY_TYPES.computeIfAbsent(
                entityClass,
                k -> {
                    {
                        final var managedType = MANAGED_TYPES.get(entityClass);
                        if (managedType instanceof EntityType<?> et && et.getJavaType() == entityClass) {
                            return et;
                        }
                    }
                    return entityManagerFactoryStream
                            .map(EntityManagerFactory::getMetamodel)
                            .map(m -> {
                                try {
                                    return m.entity(entityClass);
                                } catch (final IllegalArgumentException iae) {
                                    return null;
                                }
                            })
                            .filter(Objects::nonNull)
                            .findFirst()
                            .orElseThrow(
                                    () -> new IllegalArgumentException(
                                            "no entity type found for entity class: " + entityClass)
                            );
                }
        );
        return entityType;
    }

    // -----------------------------------------------------------------------------------------------------------------
    public static <T, R> R applyJavaMember(
            final @Nonnull Attribute<?, ? extends T> attribute,
            final @Nonnull Function<? super Method, ? extends Function<? super Field, ? extends R>> function) {
        Objects.requireNonNull(attribute, "attribute is null");
        Objects.requireNonNull(function, "function is null");
        final var member = attribute.getJavaMember();
        if (member instanceof Method method) {
            return function.apply(method).apply(null);
        } else if (member instanceof Field field) {
            return function.apply(null).apply(field);
        }
        throw new RuntimeException(
                """
                        unhandled [java member](%1$s) \
                        of [attribute](%2$s)"""
                        .formatted(member, attribute));
    }

    public static <T, A extends Annotation> @Nullable A getJavaMemberAnnotation(
            final @Nonnull Attribute<?, ? extends T> attribute, final @Nonnull Class<A> annotationClass) {
        Objects.requireNonNull(annotationClass, "annotationClass is null");
        return applyJavaMember(
                attribute,
                m -> f -> {
                    if (m != null) {
                        return m.getAnnotation(annotationClass);
                    }
                    assert f != null;
                    return f.getAnnotation(annotationClass);
                }
        );
    }

    public static <X, T> T getAttributeValue(final @Nonnull X entity,
                                             final @Nonnull Attribute<? extends X, ? extends T> attribute) {
        Objects.requireNonNull(entity, "entity is null");
        return applyJavaMember(
                attribute,
                m -> f -> {
                    if (m != null) {
                        if (m.canAccess(entity)) {
                            m.setAccessible(true);
                        }
                        try {
                            return (T) m.invoke(entity);
                        } catch (final ReflectiveOperationException roe) {
                            throw new RuntimeException(
                                    """
                                            failed to get value of \
                                            [java member of method](%1$s) \
                                            of [attribute](%2$s) \
                                            on [entity](%3$s)"""
                                            .formatted(m, attribute, entity),
                                    roe
                            );
                        }
                    }
                    assert f != null;
                    if (!f.canAccess(entity)) {
                        f.setAccessible(true);
                    }
                    try {
                        return (T) f.get(entity);
                    } catch (final ReflectiveOperationException roe) {
                        throw new RuntimeException(
                                """
                                        failed to get value of \
                                        [java member of field](%1$s) \
                                        of [attribute](%2$s) \
                                        on [entity](%3$s)"""
                                        .formatted(f, attribute, entity),
                                roe
                        );
                    }
                }
        );
    }

    // -----------------------------------------------------------------------------------------------------------------
    private JinahyaMetamodelUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
