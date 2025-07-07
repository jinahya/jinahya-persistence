package com.github.jinahya.persistence.more;

import java.util.Arrays;
import java.util.Objects;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public final class __AttributeEnumUtils {

    public static <E extends Enum<E> & __AttributeEnum<E, ATTRIBUTE>, ATTRIBUTE> E valueOfAttributeValue(
            final Class<E> enumClass, final ATTRIBUTE attributeValue) {
        Objects.requireNonNull(enumClass, "enumClass is null");
        Objects.requireNonNull(attributeValue, "attributeValue is null");
        for (final E enumConstant : enumClass.getEnumConstants()) {
            if (Objects.equals(enumConstant.attributeValue(), attributeValue)) {
                return enumConstant;
            }
        }
        throw new IllegalArgumentException(
                "no enum constant, of " + enumClass + ", for attributeValue: " + attributeValue
        );
    }

    public static <E extends Enum<E> & __AttributeEnum<E, ATTRIBUTE>, ATTRIBUTE> E valueOfAttributeValue(
            final ATTRIBUTE attributeValue, final Class<? extends E>... enumClasses) {
        Objects.requireNonNull(attributeValue, "attributeValue is null");
        if (Objects.requireNonNull(enumClasses, "enumClasses is null").length == 0) {
            throw new IllegalArgumentException("enumClasses is empty");
        }
        for (final var enumClass : enumClasses) {
            for (final E enumConstant : enumClass.getEnumConstants()) {
                if (Objects.equals(enumConstant.attributeValue(), attributeValue)) {
                    return enumConstant;
                }
            }
        }
        throw new IllegalArgumentException(
                "no enum constant, of " + Arrays.toString(enumClasses) + ", for attributeValue: " + attributeValue
        );
    }

    // -----------------------------------------------------------------------------------------------------------------
    private __AttributeEnumUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
