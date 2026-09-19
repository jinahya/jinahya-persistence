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
     * @throws IllegalArgumentException when the {@code attribute}'s java member is neither a {@link Method} nor a
     *                                  {@link Field}.
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
        throw new IllegalArgumentException(
                """
                        unknown java member type
                        ; attribute: %1$s
                        ; java member: %2$s"""
                        .formatted(attribute, javaMember)
        );
    }

    /**
     * Returns the type declared by the specified attribute's java member.
     *
     * @param attribute the attribute.
     * @return the {@link Field#getType() type} of the field, or the {@link Method#getReturnType() return type} of the
     *         method, the {@code attribute} is mapped to.
     * @apiNote Prefer this over {@link Attribute#getJavaType()} wherever the answer has to mean the same thing
     *         on every provider &mdash; picking a codec, say, or checking a value against the type which will hold
     *         it. {@link Attribute#getJavaType()} is the provider's view of the attribute, and providers disagree:
     *         for a {@code java.util.Date} field mapped as a timestamp, Hibernate ORM 7.4 and EclipseLink report
     *         {@code java.util.Date}, while Hibernate ORM 7.2 reports {@code java.sql.Timestamp} &mdash; a type no
     *         value ever actually held. The java member cannot drift that way: it is the field, or the getter, as
     *         written.
     * @see Attribute#getJavaType()
     */
    public static Class<?> getJavaMemberType(final Attribute<?, ?> attribute) {
        return applyJavaMember(
                attribute,
                m -> f -> {
                    if (m != null) {
                        return m.getReturnType();
                    }
                    assert f != null;
                    return f.getType();
                }
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
     * @return the value of the {@code attribute} of the {@code entity}; possibly {@code null}.
     * @throws RuntimeException when the value cannot be read reflectively.
     * @apiNote The result is {@link Nullable @Nullable} because an attribute value simply is: an optional
     *         basic column, an unset association, or a not-yet-assigned generated id all read back as {@code null}.
     *         Callers must check.
     * @see #setAttributeValue(Object, Attribute, Object)
     */
    @SuppressWarnings({"unchecked"})
    public static <Y> @Nullable Y getAttributeValue(final Object entity,
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

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Write methods already resolved, per class, keyed by attribute name.
     *
     * @implNote The class is part of the key on purpose. One {@link Attribute} of a {@code @MappedSuperclass}
     *         is shared by every subclass which inherits it, so caching by attribute alone let the first subclass to be
     *         written decide the write method for all of them &mdash; and a subclass which overrides the setter yields
     *         a {@link Method} whose declaring class its siblings are not instances of.
     *         <p>
     *         A {@link ClassValue}, rather than a {@code static} map keyed by the class, so that nothing here outlives
     *         what it describes: the entries hang off the class they were computed for and go when it does. The inner
     *         map is keyed by the attribute <em>name</em> rather than by the {@link Attribute} itself for the same
     *         reason &mdash; an {@link Attribute} reaches its metamodel, and so the factory which built it, and a build
     *         which opens and closes a factory per test would otherwise accumulate one live persistence unit per
     *         entry. A class cannot declare two attributes of one name, so the name is as selective as the instance.
     *         <p>
     *         The lookup is memoized at all because, unlike a metamodel lookup, introspecting a bean and then scanning
     *         its hierarchy is not cheap.
     * @see Introspector#getBeanInfo(Class)
     */
    private static final ClassValue<Map<String, Method>> SETTERS = new ClassValue<>() {
        @Override
        protected Map<String, Method> computeValue(final Class<?> type) {
            return new ConcurrentHashMap<>();
        }
    };

    /**
     * Returns the write method, of the specified class, for the specified attribute.
     *
     * @param clazz     the class to introspect.
     * @param attribute the attribute whose write method is returned.
     * @return the write method for the {@code attribute}.
     * @throws RuntimeException when the {@code clazz} cannot be introspected, or when no write method matches the
     *                          {@code attribute}.
     */
    private static Method getSetter(final Class<?> clazz, final Attribute<?, ?> attribute) {
        return SETTERS.get(clazz).computeIfAbsent(
                attribute.getName(),
                n -> resolveSetter(clazz, n, getJavaMemberType(attribute))
        );
    }

    /**
     * Resolves the write method, of the specified class, for a property of the specified name holding values of the
     * specified type.
     *
     * @param clazz      the class to introspect.
     * @param name       the property name.
     * @param memberType the type the property's java member declares.
     * @return the write method.
     * @throws RuntimeException when the {@code clazz} cannot be introspected, or when no write method is found.
     * @implNote {@link Introspector} reports only {@code public} write methods, while Jakarta Persistence 3.2
     *         &sect;2.2 permits a {@code protected} property accessor; a declared-method scan up the hierarchy covers
     *         that before giving up.
     *         <p>
     *         Both steps admit a candidate by asking whether its <em>parameter</em> accepts the member type, which is
     *         the only direction that means anything for a write method. This used to compare
     *         {@link Attribute#getJavaType()} against {@link PropertyDescriptor#getPropertyType()} &mdash; the
     *         <em>read</em> type &mdash; the other way round, so the provider disagreement described on
     *         {@link #getJavaMemberType(Attribute)} could make the introspected step reject the very setter it was
     *         looking for, and only the hierarchy scan, which happened to compare the other way, saved it.
     */
    private static Method resolveSetter(final Class<?> clazz, final String name, final Class<?> memberType) {
        final BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(clazz);
        } catch (final IntrospectionException ie) {
            throw new RuntimeException("failed to get bean info of " + clazz, ie);
        }
        final var introspected = Arrays.stream(beanInfo.getPropertyDescriptors())
                .filter(d -> d.getName().equals(name))
                // a read-only property has no write method; skip it rather than mapping to null,
                // which findFirst() would reject with a NullPointerException
                .map(PropertyDescriptor::getWriteMethod)
                .filter(Objects::nonNull)
                // an indexed property's write method takes (int, value); it is not what we want
                .filter(m -> m.getParameterCount() == 1)
                .filter(m -> m.getParameterTypes()[0].isAssignableFrom(memberType))
                .findFirst();
        if (introspected.isPresent()) {
            return introspected.get();
        }
        return findDeclaredSetter(clazz, name, memberType).orElseThrow(
                () -> new RuntimeException("no setter found for '" + name + "' on " + clazz)
        );
    }

    /**
     * Finds a write method for a property of the specified name among the methods the specified class, or any of its
     * supertypes, declares &mdash; whatever their visibility.
     *
     * @param clazz      the class to start from.
     * @param name       the property name.
     * @param memberType the type the property's java member declares.
     * @return an optional of the write method; {@link Optional#empty() empty} when none is declared.
     * @implNote Two passes, so that the choice does not depend on the unspecified order
     *         {@link Class#getDeclaredMethods()} returns: a setter whose parameter accepts the member type wins over
     *         any other single-argument setter of that name, and only a class which overloads its setter can tell the
     *         difference. The second pass exists for the one shape the first cannot match &mdash; a property whose
     *         getter and setter disagree across the primitive/wrapper boundary.
     */
    private static Optional<Method> findDeclaredSetter(final Class<?> clazz, final String name,
                                                       final Class<?> memberType) {
        if (name.isEmpty()) {
            return Optional.empty();
        }
        final var setterName = "set" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
        Method fallback = null;
        for (var c = clazz; c != null && c != Object.class; c = c.getSuperclass()) {
            for (final var method : c.getDeclaredMethods()) {
                if (!method.getName().equals(setterName) || method.getParameterCount() != 1) {
                    continue;
                }
                if (method.getParameterTypes()[0].isAssignableFrom(memberType)) {
                    return Optional.of(method);
                }
                if (fallback == null) {
                    fallback = method;
                }
            }
        }
        return Optional.ofNullable(fallback);
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
    @SuppressWarnings({"NullAway", "unchecked"})
    public static <T> @Nullable T setAttributeValue(final Object entity,
                                                    final Attribute<?, ? extends T> attribute,
                                                    final @Nullable Object value) {
        Objects.requireNonNull(entity, "entity is null");
        return applyJavaMember(
                attribute,
                m -> f -> {
                    if (m != null) {
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
