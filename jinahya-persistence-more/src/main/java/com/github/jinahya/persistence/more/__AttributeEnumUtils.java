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
    ENUM valueOfAttributeValue_(final Class<? extends ENUM> enumClass, final Object attributeValue) {
        assert enumClass.isEnum();
        for (final ENUM enumConstant : enumClass.getEnumConstants()) {
            if (Objects.equals(enumConstant.attributeValue(), attributeValue)) {
                return enumConstant;
            }
        }
        return null;
    }

    public static <ENUM extends Enum<ENUM> & __AttributeEnum<ENUM, ATTRIBUTE>, ATTRIBUTE>
    ENUM valueOfAttributeValue(final Class<ENUM> enumClass, final ATTRIBUTE attributeValue) {
        Objects.requireNonNull(enumClass, "enumClass is null");
        Objects.requireNonNull(attributeValue, "attributeValue is null");
        final var value = valueOfAttributeValue_(enumClass, attributeValue);
        if (value != null) {
            return value;
        }
        throw new IllegalArgumentException(
                "no enum constant, of " + enumClass + ", for attributeValue: " + attributeValue
        );
    }

    @Nonnull
    public static <ENUM extends __AttributeEnum<?, ATTRIBUTE>, ATTRIBUTE>
    ENUM valueOfAttributeValue(@Nonnull final ATTRIBUTE attributeValue,
                               @Nonnull final List<Class<? extends ENUM>> enumClasses) {
        Objects.requireNonNull(attributeValue, "attributeValue is null");
        if (Objects.requireNonNull(enumClasses, "enumClasses is null").isEmpty()) {
            throw new IllegalArgumentException("enumClasses is empty");
        }
        for (final var enumClass : enumClasses) {
            if (!enumClass.isEnum()) {
                throw new IllegalArgumentException("not an enum class: " + enumClass);
            }
            final var value = valueOfAttributeValue_(enumClass, attributeValue);
            if (value != null) {
                return value;
            }
        }
        throw new IllegalArgumentException(
                "no enum constant, of " + enumClasses + ", for attributeValue: " + attributeValue
        );
    }

    // -----------------------------------------------------------------------------------------------------------------
    private __AttributeEnumUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
