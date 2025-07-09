package com.github.jinahya.persistence.more.tests;

import com.github.jinahya.persistence.more.__AttributeEnumConverter;

@SuppressWarnings({
        "java:S119" // Type parameter names should comply with a naming convention
})
abstract class __SampleIntegerAttributeEnumConverter<
        E extends Enum<E> & __SampleIntegerAttributeEnum<E>
        >
        extends __AttributeEnumConverter<E, Integer> {

    __SampleIntegerAttributeEnumConverter(final Class<E> enumClass) {
        super(enumClass, Integer.class);
    }
}
