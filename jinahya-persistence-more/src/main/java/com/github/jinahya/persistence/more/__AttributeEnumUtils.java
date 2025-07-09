package com.github.jinahya.persistence.more;

import java.util.Arrays;
import java.util.Objects;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public final class __AttributeEnumUtils {

    public static <ENUM extends Enum<ENUM> & __AttributeEnum<ENUM, ATTRIBUTE>, ATTRIBUTE>
    ENUM valueOfAttributeValue(final Class<ENUM> enumClass, final ATTRIBUTE attributeValue) {
        Objects.requireNonNull(enumClass, "enumClass is null");
        Objects.requireNonNull(attributeValue, "attributeValue is null");
        for (final ENUM enumConstant : enumClass.getEnumConstants()) {
            if (Objects.equals(enumConstant.attributeValue(), attributeValue)) {
                return enumConstant;
            }
        }
        throw new IllegalArgumentException(
                "no enum constant, of " + enumClass + ", for attributeValue: " + attributeValue
        );
    }

    public static <ENUM extends Enum<ENUM> & __AttributeEnum<ENUM, ATTRIBUTE>, ATTRIBUTE>
    ENUM valueOfAttributeValue(final ATTRIBUTE attributeValue, final Class<? extends ENUM>... enumClasses) {
        Objects.requireNonNull(attributeValue, "attributeValue is null");
        if (Objects.requireNonNull(enumClasses, "enumClasses is null").length == 0) {
            throw new IllegalArgumentException("enumClasses is empty");
        }
        for (final var enumClass : enumClasses) {
            for (final ENUM enumConstant : enumClass.getEnumConstants()) {
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
