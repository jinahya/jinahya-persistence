package com.github.jinahya.persistence.more.converter.test;

import com.github.jinahya.persistence.more.converter.__NumberStringAttributeConverters;

import java.util.List;

/**
 * Tests {@link __NumberStringAttributeConverters.OfInteger} against what every attribute converter is expected to do.
 */
class __NumberStringAttributeConverters_OfInteger_Test
        extends __StringAttributeConverter_Test<__NumberStringAttributeConverters.OfInteger, Integer> {

    __NumberStringAttributeConverters_OfInteger_Test() {
        super(__NumberStringAttributeConverters.OfInteger.class, Integer.class);
    }

    @Override
    protected List<__AttributeConverterTestCase<Integer, String>> testCases() {
        return List.of(
                __AttributeConverterTestCase.of(0, "0"),
                __AttributeConverterTestCase.of(-1, "-1"),
                __AttributeConverterTestCase.of(Integer.MAX_VALUE, "2147483647"),
                __AttributeConverterTestCase.of(Integer.MIN_VALUE, "-2147483648")
        );
    }
}
