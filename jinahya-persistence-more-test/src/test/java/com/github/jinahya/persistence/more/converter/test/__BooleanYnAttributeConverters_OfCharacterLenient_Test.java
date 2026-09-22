package com.github.jinahya.persistence.more.converter.test;

import com.github.jinahya.persistence.more.converter.__BooleanYnAttributeConverters;

import java.util.List;

/**
 * Tests {@link __BooleanYnAttributeConverters.OfCharacterLenient} against what every attribute converter is expected to do.
 */
class __BooleanYnAttributeConverters_OfCharacterLenient_Test
        extends __AttributeConverter_Test<__BooleanYnAttributeConverters.OfCharacterLenient, Boolean, Character> {

    __BooleanYnAttributeConverters_OfCharacterLenient_Test() {
        super(__BooleanYnAttributeConverters.OfCharacterLenient.class);
    }

    @Override
    protected List<__AttributeConverterTestCase<Boolean, Character>> testCases() {
        return List.of(
                __AttributeConverterTestCase.of(Boolean.TRUE, 'Y'),
                __AttributeConverterTestCase.of(Boolean.FALSE, 'N')
        );
    }
}
