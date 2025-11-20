package com.github.jinahya.persistence.metamodel;

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
import jakarta.persistence.metamodel.Attribute;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class JinahyaAttributeUtils {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Applies the {@link Attribute#getJavaMember() javaMember} of either a method or a field, of the specified
     * attribute to the specified function, and returns the result.
     * {@snippet lang = "java":
     * applyJavaMember(
     *         attribute,
     *         (Method m) -> (Field f) -> {
     *             // either m or f is not null, exclusively
     *             if (m != null ) {
     *                 assert f == null;
     *             } else {
     *                 assert f != null;
     *             }
     *             return null;
     *         }
     * );
     *}
     *
     * @param attribute the attribute.
     * @param function  the function.
     * @param <R>       result type parameter
     * @return the result of the {@code function}.
     */
    public static <R> R applyJavaMember(
            final @Nonnull Attribute<?, ?> attribute,
            final @Nonnull Function<? super Method, ? extends Function<? super Field, ? extends R>> function) {
        Objects.requireNonNull(attribute, "attribute is null");
        Objects.requireNonNull(function, "function is null");
        final var javaMember = attribute.getJavaMember();
        if (javaMember instanceof Method method) {
            return function.apply(method).apply(null);
        } else if (javaMember instanceof Field field) {
            return function.apply(null).apply(field);
        }
        throw new RuntimeException(
                """
                        unknown java member type
                        ; attribute: %1$s
                        ; java member: %2$s"""
                        .formatted(attribute, javaMember)
        );
    }

    /**
     * Returns an instance of the specified annotation class on the specified attribute's java member.
     *
     * @param attribute       the attribute.
     * @param annotationClass the annotation class.
     * @param <A>             annotation type parameter
     * @return an instance of the {@code annotation} on the {@code attribute}'s java member; {@code null} when not
     *         found.
     */
    public static <A extends Annotation> @Nullable A getJavaMemberAnnotation(
            final @Nonnull Attribute<?, ?> attribute, final @Nonnull Class<A> annotationClass) {
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

    /**
     * Returns the value of the specified attribute of the specified entity.
     *
     * @param entity    the entity.
     * @param attribute the attribute of the {@code entity}.
     * @param <Y>       attribute type parameter
     * @return the value of the {@code attribute} of the {@code entity}.
     */
    public static <Y> Y getAttributeValue(final @Nonnull Object entity,
                                          final @Nonnull Attribute<?, ? extends Y> attribute) {
        Objects.requireNonNull(entity, "entity is null");
        return applyJavaMember(
                attribute,
                m -> f -> {
                    if (m != null) {
                        if (m.canAccess(entity)) {
                            m.setAccessible(true);
                        }
                        try {
                            return (Y) m.invoke(entity);
                        } catch (final ReflectiveOperationException roe) {
                            throw new RuntimeException(
                                    """
                                            failed to get value
                                            "; entity: %1$s
                                            "; attribute: %2$s
                                            "; method: %3$s"""
                                            .formatted(entity, attribute, m),
                                    roe
                            );
                        }
                    }
                    assert f != null;
                    if (!f.canAccess(entity)) {
                        f.setAccessible(true);
                    }
                    try {
                        return (Y) f.get(entity);
                    } catch (final ReflectiveOperationException roe) {
                        throw new RuntimeException(
                                """
                                        failed to get value
                                        "; entity: %1$s
                                        "; attribute: %2$s
                                        "; field: %3$s"""
                                        .formatted(entity, attribute, f),
                                roe
                        );
                    }
                }
        );
    }

    private static final Map<Attribute<?, ?>, Method> SETTERS = new ConcurrentHashMap<>();

    private static Method getSetter(final @Nonnull Class<?> clazz, final @Nonnull Attribute<?, ?> attribute) {
        return SETTERS.computeIfAbsent(
                attribute,
                k -> {
                    final BeanInfo beanInfo;
                    try {
                        beanInfo = Introspector.getBeanInfo(clazz);
                    } catch (final IntrospectionException ie) {
                        throw new RuntimeException("failed to get bean info of " + clazz, ie);
                    }
                    return Arrays.stream(beanInfo.getPropertyDescriptors())
                            .filter(d -> {
                                return d.getName().equals(k.getName()) &&
                                       k.getJavaType().isAssignableFrom(d.getPropertyType());
                            })
                            .map(PropertyDescriptor::getWriteMethod)
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("no setter found for " + attribute));
                }
        );
    }

    public static <T> T setAttributeValue(final @Nonnull Object entity,
                                          final @Nonnull Attribute<?, ? extends T> attribute,
                                          final @Nullable Object value) {
        Objects.requireNonNull(entity, "entity is null");
        return applyJavaMember(
                attribute,
                m -> f -> {
                    if (m != null) {
                        assert m.getName().startsWith("get");
                        final var setter = getSetter(entity.getClass(), attribute);
                        if (!setter.canAccess(entity)) {
                            setter.setAccessible(true);
                        }
                        try {
                            return (T) setter.invoke(entity);
                        } catch (final ReflectiveOperationException roe) {
                            throw new RuntimeException(
                                    """
                                            failed to set value
                                            ; entity: %1$s
                                            ; attribute: %2$s
                                            ; value: %3$s
                                            ; method: %4$s"""
                                            .formatted(value, attribute, entity, setter),
                                    roe
                            );
                        }
                    }
                    assert f != null;
                    if (!f.canAccess(entity)) {
                        f.setAccessible(true);
                    }
                    try {
                        f.set(entity, value);
                    } catch (final ReflectiveOperationException roe) {
                        throw new RuntimeException(
                                """
                                        failed to set value
                                        ; entity: %1$s
                                        ; attribute: %2$s
                                        ; value: %3$s
                                        ; field: %4$s"""
                                        .formatted(entity, attribute, value, f),
                                roe
                        );
                    }
                    return null;
                }
        );
    }

    // -----------------------------------------------------------------------------------------------------------------
    private JinahyaAttributeUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
