package com.github.jinahya.persistence.more.converter.test;

import com.github.jinahya.persistence.more.converter.__BooleanYnAttributeConverters;

import java.util.List;

/**
 * Tests {@link __BooleanYnAttributeConverters.OfString} against what every attribute converter is expected to do.
 */
class __BooleanYnAttributeConverters_OfString_Test
        extends __AttributeConverter_Test<__BooleanYnAttributeConverters.OfString, Boolean, String> {

    __BooleanYnAttributeConverters_OfString_Test() {
        super(__BooleanYnAttributeConverters.OfString.class, Boolean.class, String.class);
    }

    @Override
    protected List<__AttributeConverterTestCase<Boolean, String>> testCases() {
        return List.of(
                __AttributeConverterTestCase.of(Boolean.TRUE, "Y"),
                __AttributeConverterTestCase.of(Boolean.FALSE, "N")
        );
    }
}
