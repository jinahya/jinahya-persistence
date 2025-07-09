package com.github.jinahya.persistence.more.tests;

import com.github.jinahya.persistence.more.__AttributeEnumConverter;

@SuppressWarnings({
        "java:S119" // Type parameter names should comply with a naming convention
})
abstract class __SampleIntegerAttributeEnumConverter<
        ENUM extends Enum<ENUM> & __SampleIntegerAttributeEnum<ENUM>
        >
        extends __AttributeEnumConverter<ENUM, Integer> {

    __SampleIntegerAttributeEnumConverter(final Class<ENUM> enumClass) {
        super(enumClass, Integer.class);
    }
}
