package com.github.jinahya.persistence.more;

import jakarta.persistence.AttributeConverter;
import jakarta.validation.constraints.NotNull;

public interface __AttributeEnum<E extends Enum<E> & __AttributeEnum<E, ATTRIBUTE>, ATTRIBUTE>
        extends AttributeConverter<ATTRIBUTE, ATTRIBUTE> {

    interface __OfString<E extends Enum<E> & __OfString<E>> extends __AttributeEnum<E, String> {

        @Override
        @SuppressWarnings({"unchecked"})
        default String attributeValue() {
            return ((Enum<E>) this).name();
        }
    }

//    interface ___OfInt<E extends Enum<E> & ___OfInt<E>> {
//
//        static <E extends Enum<E> & ___OfInt<E>> E valueOfAttributeValue(
//                final Class<? extends E> enumClass, final int attributeValueAsInt) {
//            Objects.requireNonNull(enumClass, "enumClass is null");
//            for (final E enumConstant : enumClass.getEnumConstants()) {
//                if (Objects.equals(enumConstant.getAttributeValueAsInt(), attributeValueAsInt)) {
//                    return enumConstant;
//                }
//            }
//            throw new IllegalArgumentException(
//                    "no enum constant, of " + enumClass + ", for attributeValueAsInt: " + attributeValueAsInt
//            );
//        }
//
//        int getAttributeValueAsInt();
//    }
//
//    interface ___OfLong<E extends Enum<E> & ___OfLong<E>> {
//
//        static <E extends Enum<E> & ___OfLong<E>> E valueOfAttributeValue(
//                final Class<? extends E> enumClass, final long attributeValueAsLong) {
//            Objects.requireNonNull(enumClass, "enumClass is null");
//            for (final E enumConstant : enumClass.getEnumConstants()) {
//                if (Objects.equals(enumConstant.getAttributeValueAsLong(), attributeValueAsLong)) {
//                    return enumConstant;
//                }
//            }
//            throw new IllegalArgumentException(
//                    "no enum constant, of " + enumClass + ", for attributeValueAsLong: " + attributeValueAsLong
//            );
//        }
//
//        long getAttributeValueAsLong();
//    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns the attribute value of this enum constant.
     *
     * @return the attribute value of this enum constant.
     */
    @NotNull
    ATTRIBUTE attributeValue();
}
