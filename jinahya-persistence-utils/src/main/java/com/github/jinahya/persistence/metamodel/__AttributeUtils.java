package com.github.jinahya.persistence.metamodel;

/*-
 * #%L
 * jinahya-persistence-utils
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

import jakarta.persistence.metamodel.Attribute;
import org.jspecify.annotations.Nullable;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * A utility class for {@link Attribute}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @apiNote An {@link Attribute} is mapped either to a {@link Method} or to a {@link Field}, and the methods
 *         here hide that distinction: they read the {@link Attribute#getJavaMember() javaMember}, make it accessible
 *         when required, and work with it reflectively.
 * @see Attribute#getJavaMember()
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class __AttributeUtils {

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
    // NullAway does not propagate @Nullable through the nested wildcard type arguments below; the two
    // `apply(null)` calls are exactly what the annotated signature already declares.
    @SuppressWarnings("NullAway")
    public static <R> R applyJavaMember(
            final Attribute<?, ?> attribute,
            final Function<? super @Nullable Method, ? extends Function<? super @Nullable Field, ? extends R>> function) {
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
            final Attribute<?, ?> attribute, final Class<A> annotationClass) {
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
    public static <Y> Y getAttributeValue(final Object entity,
                                          final Attribute<?, ? extends Y> attribute) {
        Objects.requireNonNull(entity, "entity is null");
        return applyJavaMember(
                attribute,
                m -> f -> {
                    if (m != null) {
                        if (!m.canAccess(entity)) {
                            m.setAccessible(true);
                        }
                        try {
                            return (Y) m.invoke(entity);
                        } catch (final ReflectiveOperationException roe) {
                            throw new RuntimeException(
                                    """
                                            failed to get value
                                            ; entity: %1$s
                                            ; attribute: %2$s
                                            ; method: %3$s"""
                                            .formatted(entity.getClass().getName(), attribute.getName(), m),
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
                                        ; entity: %1$s
                                        ; attribute: %2$s
                                        ; field: %3$s"""
                                        .formatted(entity.getClass().getName(), attribute.getName(), f),
                                roe
                        );
                    }
                }
        );
    }

    /**
     * The key a write method is cached under.
     *
     * @param clazz     the runtime class the method was resolved against.
     * @param attribute the attribute it writes.
     * @implNote The class is part of the key on purpose. One {@link Attribute} of a {@code @MappedSuperclass}
     *         is shared by every subclass which inherits it, so caching by attribute alone let the first subclass to be
     *         written decide the write method for all of them &mdash; and a subclass which overrides the setter yields
     *         a {@link Method} whose declaring class its siblings are not instances of.
     */
    private record SetterKey(Class<?> clazz, Attribute<?, ?> attribute) {

    }

    private static final Map<SetterKey, Method> SETTERS = new ConcurrentHashMap<>();

    /**
     * Returns the write method, of the specified class, for the specified attribute, caching it against both.
     *
     * @param clazz     the class to introspect.
     * @param attribute the attribute whose write method is returned.
     * @return the write method for the {@code attribute}.
     * @throws RuntimeException when the {@code clazz} cannot be introspected, or when no write method matches the
     *                          {@code attribute}.
     * @implNote {@link Introspector} reports only {@code public} write methods, while Jakarta Persistence 3.2
     *         &sect;2.2 permits a {@code protected} property accessor; a declared-method scan up the hierarchy covers
     *         that before giving up. This is the one lookup here still memoized, deliberately: unlike a metamodel
     *         lookup, introspecting a bean and then scanning its hierarchy is not cheap, and the key pairs a class
     *         with an attribute of that class, so the map is bounded by the mapped model rather than by traffic. The
     *         cost is that the entries, and so the classes they name, live as long as the class holding this map
     *         &mdash; which is only visible to a container that unloads the persistence classes without unloading
     *         this one.
     * @see Introspector#getBeanInfo(Class)
     */
    private static Method getSetter(final Class<?> clazz, final Attribute<?, ?> attribute) {
        return SETTERS.computeIfAbsent(
                new SetterKey(clazz, attribute),
                k -> {
                    final BeanInfo beanInfo;
                    try {
                        beanInfo = Introspector.getBeanInfo(k.clazz());
                    } catch (final IntrospectionException ie) {
                        throw new RuntimeException("failed to get bean info of " + k.clazz(), ie);
                    }
                    final var introspected = Arrays.stream(beanInfo.getPropertyDescriptors())
                            .filter(d -> d.getName().equals(k.attribute().getName()))
                            // an indexed property reports a null propertyType
                            .filter(d -> d.getPropertyType() != null
                                         && k.attribute().getJavaType().isAssignableFrom(d.getPropertyType()))
                            // a read-only property has no write method; skip it rather than mapping to null,
                            // which findFirst() would reject with a NullPointerException
                            .map(PropertyDescriptor::getWriteMethod)
                            .filter(Objects::nonNull)
                            .findFirst();
                    if (introspected.isPresent()) {
                        return introspected.get();
                    }
                    return findDeclaredSetter(k.clazz(), k.attribute()).orElseThrow(
                            () -> new RuntimeException(
                                    "no setter found for " + k.attribute() + " on " + k.clazz())
                    );
                }
        );
    }

    /**
     * Finds a write method for the specified attribute among the methods the specified class, or any of its supertypes,
     * declares &mdash; whatever their visibility.
     *
     * @param clazz     the class to start from.
     * @param attribute the attribute whose write method is looked for.
     * @return an optional of the write method; {@link Optional#empty() empty} when none is declared.
     */
    private static Optional<Method> findDeclaredSetter(final Class<?> clazz, final Attribute<?, ?> attribute) {
        final var attributeName = attribute.getName();
        if (attributeName.isEmpty()) {
            return Optional.empty();
        }
        final var name = "set" + Character.toUpperCase(attributeName.charAt(0)) + attributeName.substring(1);
        for (var c = clazz; c != null && c != Object.class; c = c.getSuperclass()) {
            for (final var method : c.getDeclaredMethods()) {
                if (method.getName().equals(name)
                    && method.getParameterCount() == 1
                    && method.getParameterTypes()[0].isAssignableFrom(attribute.getJavaType())) {
                    return Optional.of(method);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Sets the value of the specified attribute, of the specified entity, to the specified value.
     *
     * @param entity    the entity whose attribute value is set.
     * @param attribute the attribute of the {@code entity}.
     * @param value     the new value for the {@code attribute}; may be {@code null}.
     * @param <T>       attribute type parameter
     * @return {@code null} when the {@code attribute} is mapped to a {@link Field}; the result of the write method,
     *         otherwise.
     * @throws RuntimeException when the value cannot be set reflectively.
     * @see #getAttributeValue(Object, Attribute)
     */
    // NullAway cannot infer a @Nullable type argument for the generic call below from the lambda body;
    // the enclosing method is declared @Nullable and documents the null result.
    @SuppressWarnings("NullAway")
    public static <T> @Nullable T setAttributeValue(final Object entity,
                                                    final Attribute<?, ? extends T> attribute,
                                                    final @Nullable Object value) {
        Objects.requireNonNull(entity, "entity is null");
        return applyJavaMember(
                attribute,
                m -> f -> {
                    if (m != null) {
                        assert m.getName().startsWith("get") || m.getName().startsWith("is");
                        final var setter = getSetter(entity.getClass(), attribute);
                        if (!setter.canAccess(entity)) {
                            setter.setAccessible(true);
                        }
                        try {
                            return (T) setter.invoke(entity, value);
                        } catch (final ReflectiveOperationException roe) {
                            throw new RuntimeException(
                                    """
                                            failed to set value
                                            ; entity: %1$s
                                            ; attribute: %2$s
                                            ; method: %3$s"""
                                            .formatted(entity.getClass().getName(), attribute.getName(), setter),
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
                                        ; field: %3$s"""
                                        .formatted(entity.getClass().getName(), attribute.getName(), f),
                                roe
                        );
                    }
                    return null;
                }
        );
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance, which is not allowed.
     */
    private __AttributeUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
