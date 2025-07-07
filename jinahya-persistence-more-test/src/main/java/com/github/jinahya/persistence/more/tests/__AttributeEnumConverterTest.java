package com.github.jinahya.persistence.more.tests;

import com.github.jinahya.persistence.more.__AttributeEnum;
import com.github.jinahya.persistence.more.__AttributeEnumConverter;

import java.util.Objects;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public class __AttributeEnumConverterTest<
        CONVERTER extends __AttributeEnumConverter<E, ATTRIBUTE>,
        E extends Enum<E> & __AttributeEnum<E, ATTRIBUTE>,
        ATTRIBUTE> {

    protected __AttributeEnumConverterTest(final Class<CONVERTER> converterClass) {
        super();
        this.converterClass = Objects.requireNonNull(converterClass, "converterClass is null");
    }

    protected final Class<CONVERTER> converterClass;
}
