package com.github.jinahya.persistence.more;

/*-
 * #%L
 * jinahya-persistence-more
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

import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Utilities for {@link __AttributeEnum}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @implNote Looking a constant up by its attribute value is backed by a map, built once per enum class and held
 *         by that class itself, rather than by a scan of {@link Class#getEnumConstants() getEnumConstants()}, which
 *         both allocates a defensive copy of the constants and costs {@code O(n)} on every call.
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class __AttributeEnumUtils {

    /**
     * The constants of each enum class, mapped by their attribute values.
     *
     * @implNote A {@link ClassValue} associates each map with the enum class it was built from, so that neither
     *         outlives the class, and so that a redeployed application does not leak its class loader.
     */
    private static final ClassValue<Map<Object, __AttributeEnum<?, ?>>> CONSTANTS = new ClassValue<>() {
        @Override
        protected Map<Object, __AttributeEnum<?, ?>> computeValue(final Class<?> type) {
            assert type != null;
            assert type.isEnum();
            final var constants = type.getEnumConstants();
            final Map<Object, __AttributeEnum<?, ?>> map = new HashMap<>(constants.length);
            for (final var constant : constants) {
                if (!(constant instanceof __AttributeEnum<?, ?> attributeEnum)) {
                    throw new IllegalArgumentException(
                            "not an " + __AttributeEnum.class.getSimpleName() + " constant: " + constant +
                            "; of " + type
                    );
                }
                final var attributeValue = attributeEnum.attributeValue();
                if (attributeValue == null) {
                    throw new IllegalArgumentException(
                            "null attributeValue of " + attributeEnum + "; of " + type
                    );
                }
                final var previous = map.put(attributeValue, attributeEnum);
                if (previous != null) {
                    throw new IllegalArgumentException(
                            "duplicate attributeValue(" + attributeValue + ")" +
                            "; of both " + previous + " and " + attributeEnum +
                            "; of " + type
                    );
                }
            }
            return Collections.unmodifiableMap(map);
        }
    };

    /**
     * Finds the constant, of the specified enum class, whose attribute value equals the specified value.
     *
     * @param enumClass      the enum class to search.
     * @param attributeValue the attribute value to look for.
     * @param <E>            enum type parameter
     * @return the matching enum constant; {@code null} when no constant carries the {@code attributeValue}.
     * @throws IllegalArgumentException when the {@code enumClass} is not an enum class, when any of its constants
     *                                  carries a {@code null} attribute value, or when two of them carry the same
     *                                  attribute value.
     * @implNote The map of the {@code enumClass} is built on the first lookup, and validated while it is built;
     *         an enum whose attribute values are {@code null} or duplicated therefore fails on its first use, rather
     *         than resolving to whichever constant happens to be declared first.
     */
    @Nullable
    @SuppressWarnings({"unchecked"})
    private static <E extends __AttributeEnum<?, ?>>
    E find_(final Class<? extends E> enumClass, final Object attributeValue) {
        if (!Objects.requireNonNull(enumClass, "enumClass is null").isEnum()) {
            throw new IllegalArgumentException("not an enum class: " + enumClass);
        }
        Objects.requireNonNull(attributeValue, "attributeValue is null");
        return (E) CONSTANTS.get(enumClass).get(attributeValue);
    }

    // -------------------------------------------------------------------------------------------------- a single class

    /**
     * Finds the constant, of the specified enum class, whose attribute value equals the specified value.
     *
     * @param enumClass      the enum class to search.
     * @param attributeValue the attribute value to look for.
     * @param <E>            enum type parameter
     * @param <A>            attribute type parameter
     * @return an {@link Optional} of the constant, of the {@code enumClass}, carrying the {@code attributeValue}; an
     *         {@link Optional#empty() empty} optional when no constant carries it.
     * @throws IllegalArgumentException when the {@code enumClass} is not an enum class, when any of its constants
     *                                  carries a {@code null} attribute value, or when two of them carry the same
     *                                  attribute value.
     * @apiNote The result is empty only when the {@code enumClass} is well-formed and simply has no constant
     *         for the {@code attributeValue} &mdash; an outcome to expect from a value read out of a database column. A
     *         malformed {@code enumClass} is a wiring error, not an absent value, and still throws.
     *         <p>
     *         The {@code attributeValue} is matched by {@link Object#equals(Object) equals}, and its type therefore has
     *         to implement {@link Object#hashCode() hashCode()} consistently with it.
     * @see #valueOf(Class, Object)
     */
    public static <E extends Enum<E> & __AttributeEnum<E, A>, A>
    Optional<E> find(final Class<E> enumClass, final A attributeValue) {
        return Optional.ofNullable(find_(enumClass, attributeValue));
    }

    /**
     * Returns the constant, of the specified enum class, whose attribute value equals the specified value.
     *
     * @param enumClass      the enum class to search.
     * @param attributeValue the attribute value to look for.
     * @param <E>            enum type parameter
     * @param <A>            attribute type parameter
     * @return the constant, of the {@code enumClass}, carrying the {@code attributeValue}.
     * @throws IllegalArgumentException when no constant of the {@code enumClass} carries the {@code attributeValue},
     *                                  when the {@code enumClass} is not an enum class, when any of its constants
     *                                  carries a {@code null} attribute value, or when two of them carry the same
     *                                  attribute value.
     * @apiNote This method mirrors {@link Enum#valueOf(Class, String)}, and fails the same way. Use
     *         {@link #find(Class, Object)} where a value with no matching constant is an ordinary outcome rather than
     *         an error.
     * @see #find(Class, Object)
     * @see #valueOf(List, Object)
     */
    public static <E extends Enum<E> & __AttributeEnum<E, A>, A>
    E valueOf(final Class<E> enumClass, final A attributeValue) {
        return find(enumClass, attributeValue).orElseThrow(() -> new IllegalArgumentException(
                "no enum constant, of " + enumClass + ", for attributeValue: " + attributeValue
        ));
    }

    // ------------------------------------------------------------------------------------------------ multiple classes

    /**
     * Finds the constant, of the first of the specified enum classes which has one, whose attribute value equals the
     * specified value.
     *
     * @param enumClasses    the enum classes to search, in order.
     * @param attributeValue the attribute value to look for.
     * @param <E>            enum type parameter
     * @param <A>            attribute type parameter
     * @return an {@link Optional} of the constant, of the first matching class of the {@code enumClasses}, carrying the
     *         {@code attributeValue}; an {@link Optional#empty() empty} optional when none of them has one.
     * @throws IllegalArgumentException when the {@code enumClasses} is empty, or when any of them is not an enum class
     *                                  or is otherwise invalid.
     * @apiNote The classes are searched in the order given, and the first match wins; two classes sharing an
     *         attribute value therefore resolve differently depending on the order of the {@code enumClasses}.
     *         <p>
     *         As with {@link #find(Class, Object)}, the result is empty only for a value no well-formed class carries;
     *         an invalid class still throws.
     * @implNote Every class is validated before any of them is searched, so that an invalid class in the list
     *         is reported whether or not an earlier class happens to match first.
     *         <p>
     *         The {@code E} type parameter is bound to the interface alone &mdash;
     *         {@code __AttributeEnum<? extends Enum<?>, A>} &mdash; rather than to
     *         {@code Enum<E> & __AttributeEnum<E, A>}, as it is in {@link #find(Class, Object)}. The self-referential
     *         form is not merely avoided here, it is unusable: {@code E} has to be a common supertype of distinct enum
     *         classes, and nothing satisfies {@code E extends Enum<E>} except the concrete enum classes themselves,
     *         which are effectively final, so {@code List<Class<? extends E>>} would accept just one class &mdash;
     *         which is exactly {@link #find(Class, Object)}; while any real common supertype of two enum classes is an
     *         interface, and an interface cannot extend {@link Enum}.
     *         <p>
     *         A weaker constraint, {@code E extends Enum<?> & __AttributeEnum<? extends Enum<?>, A>}, does compile, and
     *         does reject a non-enum class at compile time. It is not used because it only holds where the compiler can
     *         infer {@code E} as the intersection of the classes actually given, and that type cannot be written down:
     *         the argument then has to be an inline {@code List.of(...)}, or a {@code var} local. A
     *         {@code List<Class<? extends X>>} field, parameter or return value &mdash; anything whose element type has
     *         been widened to a nameable common interface &mdash; no longer matches. The constraint therefore stays in
     *         the self type parameter, as {@code ? extends Enum<?>}, and each element is verified with
     *         {@link Class#isEnum()} instead.
     *         <p>
     *         The runtime check is required under either bound: the anonymous subclass of a constant declared with a
     *         class body is assignable to {@code Class<? extends E>}, yet {@link Class#isEnum() isEnum()} is
     *         {@code false} for it and {@link Class#getEnumConstants() getEnumConstants()} returns {@code null}.
     * @see #valueOf(List, Object)
     */
    public static <E extends __AttributeEnum<? extends Enum<?>, A>, A>
    Optional<E> find(final List<Class<? extends E>> enumClasses, final A attributeValue) {
        Objects.requireNonNull(attributeValue, "attributeValue is null");
        if (Objects.requireNonNull(enumClasses, "enumClasses is null").isEmpty()) {
            throw new IllegalArgumentException("empty enumClasses");
        }
        for (final var enumClass : enumClasses) {
            if (enumClass == null) {
                throw new NullPointerException("null enumClass in " + enumClasses);
            }
            if (!enumClass.isEnum()) {
                throw new IllegalArgumentException("not an enum class: " + enumClass);
            }
        }
        for (final var enumClass : enumClasses) {
            final var value = find_(enumClass, attributeValue);
            if (value != null) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }

    /**
     * Returns the constant, of the first of the specified enum classes which has one, whose attribute value equals the
     * specified value.
     *
     * @param enumClasses    the enum classes to search, in order.
     * @param attributeValue the attribute value to look for.
     * @param <E>            enum type parameter
     * @param <A>            attribute type parameter
     * @return the constant, of the first matching class of the {@code enumClasses}, carrying the
     *         {@code attributeValue}.
     * @throws IllegalArgumentException when none of the {@code enumClasses} has a constant carrying the
     *                                  {@code attributeValue}, when the {@code enumClasses} is empty, or when any of
     *                                  them is not an enum class or is otherwise invalid.
     * @apiNote Use {@link #find(List, Object)} where a value no class carries is an ordinary outcome rather
     *         than an error.
     * @see #find(List, Object)
     * @see #valueOf(Class, Object)
     */
    public static <E extends __AttributeEnum<? extends Enum<?>, A>, A>
    E valueOf(final List<Class<? extends E>> enumClasses, final A attributeValue) {
        return find(enumClasses, attributeValue).orElseThrow(() -> new IllegalArgumentException(
                "no enum constant, in any of " + enumClasses + ", for attributeValue: " + attributeValue
        ));
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance, which is not allowed.
     */
    private __AttributeEnumUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
