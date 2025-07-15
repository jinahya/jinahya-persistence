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

import java.util.List;
import java.util.Objects;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public final class __AttributeEnumUtils {

    @Nullable
    private static <ENUM extends __AttributeEnum<?, ?>>
    ENUM valueOfAttributeValue_(@Nonnull final Class<? extends ENUM> enumClass, @Nonnull final Object attributeValue) {
        if (!Objects.requireNonNull(enumClass, "enumClass is null").isEnum()) {
            throw new IllegalArgumentException("not an enum class: " + enumClass);
        }
        assert attributeValue != null;
        for (final ENUM enumConstant : enumClass.getEnumConstants()) {
            if (Objects.equals(enumConstant.attributeValue(), attributeValue)) {
                return enumConstant;
            }
        }
        return null;
    }

    /**
     * Finds the value of the specified enum class that has the specified attribute value.
     *
     * @param enumClass      the enum class.
     * @param attributeValue the attribute value.
     * @param <E>            enum type parameter
     * @param <ATTRIBUTE>    attribute type parameter
     * @return the enum constant, of {@code enumClass}, that has the specified attribute value
     * @see #valueOfAttributeValue(Object, List)
     */
    @Nonnull
    public static <E extends Enum<E> & __AttributeEnum<E, ATTRIBUTE>, ATTRIBUTE>
    E valueOfAttributeValue(@Nonnull final Class<E> enumClass, @Nonnull final ATTRIBUTE attributeValue) {
//        Objects.requireNonNull(enumClass, "enumClass is null");
        Objects.requireNonNull(attributeValue, "attributeValue is null");
        final var value = valueOfAttributeValue_(enumClass, attributeValue);
        if (value != null) {
            return value;
        }
        throw new IllegalArgumentException(
                "no enum constant, of " + enumClass + ", for attributeValue: " + attributeValue
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
     * @see #valueOfAttributeValue(Class, Object)
     */
    // https://stackoverflow.com/q/79701562/330457
    @Nonnull
    public static <ENUM extends __AttributeEnum<? extends Enum<?>, ATTRIBUTE>, ATTRIBUTE>
    ENUM valueOfAttributeValue(@Nonnull final ATTRIBUTE attributeValue,
                               @Nonnull final List<Class<? extends ENUM>> enumClasses) {
        Objects.requireNonNull(attributeValue, "attributeValue is null");
        Objects.requireNonNull(enumClasses, "enumClasses is null");
        for (final var enumClass : enumClasses) {
            if (enumClass == null) {
                throw new IllegalArgumentException("null in enum classes: " + enumClasses);
            }
            final var value = valueOfAttributeValue_(enumClass, attributeValue);
            if (value != null) {
                return value;
            }
        }
        throw new IllegalArgumentException(
                "no enum constant, in any of " + enumClasses + ", for attributeValue: " + attributeValue
        );
    }

    // -----------------------------------------------------------------------------------------------------------------
    private __AttributeEnumUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
