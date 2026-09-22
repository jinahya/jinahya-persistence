package com.github.jinahya.persistence.more.converter.test;

import com.github.jinahya.persistence.more.converter.__BooleanYnAttributeConverters;

import java.util.List;

/**
 * Tests {@link __BooleanYnAttributeConverters.OfCharacter} against what every attribute converter is expected to do.
 */
class __BooleanYnAttributeConverters_OfCharacter_Test
        extends __AttributeConverter_Test<__BooleanYnAttributeConverters.OfCharacter, Boolean, Character> {

    __BooleanYnAttributeConverters_OfCharacter_Test() {
        super(__BooleanYnAttributeConverters.OfCharacter.class, Boolean.class, Character.class);
    }

    @Override
    protected List<__AttributeConverterTestCase<Boolean, Character>> testCases() {
        return List.of(
                __AttributeConverterTestCase.of(Boolean.TRUE, 'Y'),
                __AttributeConverterTestCase.of(Boolean.FALSE, 'N')
        );
    }
}
