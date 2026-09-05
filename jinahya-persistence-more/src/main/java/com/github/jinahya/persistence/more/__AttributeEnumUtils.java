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

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Utilities for {@link __AttributeEnum}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @implNote Looking a constant up by its attribute value is backed by a map, built once per enum class and held by
 * that class itself, rather than by a scan of {@link Class#getEnumConstants() getEnumConstants()}, which both allocates
 * a defensive copy of the constants and costs {@code O(n)} on every call.
 */
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public final class __AttributeEnumUtils {

    /**
     * The constants of each enum class, mapped by their attribute values.
     *
     * @implNote A {@link ClassValue} associates each map with the enum class it was built from, so that neither
     * outlives the class, and so that a redeployed application does not leak its class loader.
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
     * @param <ENUM>         enum type parameter
     * @return the matching enum constant; {@code null} when no constant carries the {@code attributeValue}.
     * @throws IllegalArgumentException when the {@code enumClass} is not an enum class, when any of its constants
     *                                  carries a {@code null} attribute value, or when two of them carry the same
     *                                  attribute value.
     * @implNote The map of the {@code enumClass} is built on the first lookup, and validated while it is built; an
     * enum whose attribute values are {@code null} or duplicated therefore fails on its first use, rather than
     * resolving to whichever constant happens to be declared first.
     */
    @Nullable
    @SuppressWarnings({"unchecked"})
    private static <ENUM extends __AttributeEnum<?, ?>>
    ENUM valueOfAttributeValue_(final @Nonnull Class<? extends ENUM> enumClass, final @Nonnull Object attributeValue) {
        if (!Objects.requireNonNull(enumClass, "enumClass is null").isEnum()) {
            throw new IllegalArgumentException("not an enum class: " + enumClass);
        }
        Objects.requireNonNull(attributeValue, "attributeValue is null");
        return (ENUM) CONSTANTS.get(enumClass).get(attributeValue);
    }

    /**
     * Finds the value of the specified enum class that has the specified attribute value.
     *
     * @param enumClass      the enum class.
     * @param attributeValue the attribute value.
     * @param <E>            enum type parameter
     * @param <ATTRIBUTE>    attribute type parameter
     * @return the enum constant, of {@code enumClass}, that has the specified attribute value
     * @throws IllegalArgumentException when no constant of the {@code enumClass} carries the {@code attributeValue},
     *                                  or when the {@code enumClass} itself is invalid; see
     *                                  {@link #valueOfAttributeValueIn(List, Object)}.
     * @apiNote The {@code attributeValue} is matched by {@link Object#equals(Object) equals}, and its type therefore
     * has to implement {@link Object#hashCode() hashCode()} consistently with it.
     * @see #valueOfAttributeValueIn(List, Object)
     */
    @Nonnull
    public static <E extends Enum<E> & __AttributeEnum<E, ATTRIBUTE>, ATTRIBUTE>
    E valueOfAttributeValue(final @Nonnull Class<E> enumClass, final @Nonnull ATTRIBUTE attributeValue) {
        final var value = valueOfAttributeValue_(enumClass, attributeValue);
        if (value == null) {
            throw new IllegalArgumentException(
                    "no enum constant, of " + enumClass + ", for attributeValue: " + attributeValue
            );
        }
        return value;
    }

    /**
     * Finds the constant, of the first of the specified enum classes which has one, whose attribute value equals the
     * specified value.
     *
     * @param enumClasses    the enum classes to search, in order.
     * @param attributeValue the attribute value to look for.
     * @param <ENUM>         enum type parameter
     * @param <ATTRIBUTE>    attribute type parameter
     * @return the enum constant, of the first matched class of {@code enumClasses}, that has the specified attribute
     *         value.
     * @throws IllegalArgumentException when the {@code enumClasses} is empty, when any of them is not an enum class or
     *                                  is otherwise invalid, or when none of them has a constant carrying the
     *                                  {@code attributeValue}.
     * @apiNote The classes are searched in the order given, and the first match wins; two classes sharing an attribute
     * value therefore resolve differently depending on the order of the {@code enumClasses}.
     * @implNote Every class is validated before any of them is searched, so that an invalid class in the list is
     * reported whether or not an earlier class happens to match first.
     * <p>
     * The {@code ENUM} type parameter is bound to the interface alone &mdash; {@code __AttributeEnum<? extends
     * Enum<?>, ATTRIBUTE>} &mdash; rather than to {@code Enum<ENUM> & __AttributeEnum<ENUM, ATTRIBUTE>}, as it is in
     * {@link #valueOfAttributeValue(Class, Object)}. Here {@code ENUM} has to be a common supertype of several
     * distinct enum classes, and no such type can satisfy {@code ENUM extends Enum<ENUM>}: the only types which do
     * are the concrete enum classes themselves, and those are effectively final, so {@code List<Class<? extends
     * ENUM>>} would then accept just one class; while any real common supertype of two enum classes is an interface,
     * which cannot extend {@link Enum}. The {@link Enum} constraint therefore moves into the self type parameter, as
     * {@code ? extends Enum<?>}, and the fact that each element really is an enum class is verified at runtime, with
     * {@link Class#isEnum()}, rather than by the compiler.
     * @see #valueOfAttributeValue(Class, Object)
     */
    // https://stackoverflow.com/q/79701562/330457
    @Nonnull
    public static <ENUM extends __AttributeEnum<? extends Enum<?>, ATTRIBUTE>, ATTRIBUTE>
    ENUM valueOfAttributeValueIn(final @Nonnull List<Class<? extends ENUM>> enumClasses,
                                 final @Nonnull ATTRIBUTE attributeValue) {
        Objects.requireNonNull(attributeValue, "attributeValue is null");
        if (Objects.requireNonNull(enumClasses, "enumClasses is null").isEmpty()) {
            throw new IllegalArgumentException("empty enumClasses:");
        }
        for (final var enumClass : enumClasses) {
            if (!Objects.requireNonNull(enumClass, "null enumClass in " + enumClasses).isEnum()) {
                throw new IllegalArgumentException("not an enum class: " + enumClass);
            }
        }
        for (final var enumClass : enumClasses) {
            final var value = valueOfAttributeValue_(enumClass, attributeValue);
            if (value != null) {
                return value;
            }
        }
        throw new IllegalArgumentException(
                "no enum constant, in any of " + enumClasses + ", for attributeValue: " + attributeValue
        );
    }

    /**
     * Finds the value from all enum classes that has the specified attribute value.
     *
     * @param attributeValue the attribute value.
     * @param enumClasses    enum classes.
     * @param <ENUM>         enum type parameter
     * @param <ATTRIBUTE>    attribute type parameter
     * @return the enum constant, of the first matched class of {@code enumClasses}, that has the specified attribute
     *         value
     * @deprecated Use {@link #valueOfAttributeValueIn(List, Object)}, which takes the classes first, as
     * {@link #valueOfAttributeValue(Class, Object)} does.
     * @see #valueOfAttributeValueIn(List, Object)
     */
    @Deprecated(forRemoval = true)
    @Nonnull
    public static <ENUM extends __AttributeEnum<? extends Enum<?>, ATTRIBUTE>, ATTRIBUTE>
    ENUM valueOfAttributeValue(final @Nonnull ATTRIBUTE attributeValue,
                               final @Nonnull List<Class<? extends ENUM>> enumClasses) {
        return valueOfAttributeValueIn(enumClasses, attributeValue);
    }

    // -----------------------------------------------------------------------------------------------------------------
    /**
     * Creates a new instance, which is not allowed.
     */
    private __AttributeEnumUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
