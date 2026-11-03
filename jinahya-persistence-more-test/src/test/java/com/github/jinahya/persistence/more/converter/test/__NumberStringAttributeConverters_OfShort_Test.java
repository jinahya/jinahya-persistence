package com.github.jinahya.persistence.more.converter.test;

import com.github.jinahya.persistence.more.converter.__NumberStringAttributeConverters;

import java.util.List;

/**
 * Tests {@link __NumberStringAttributeConverters.OfShort} against what every attribute converter is expected to do.
 */
class __NumberStringAttributeConverters_OfShort_Test
        extends __StringAttributeConverter_Test<__NumberStringAttributeConverters.OfShort, Short> {

    __NumberStringAttributeConverters_OfShort_Test() {
        super(__NumberStringAttributeConverters.OfShort.class);
    }

    @Override
    protected List<__AttributeConverterTestCase<Short, String>> testCases() {
        return List.of(
                __AttributeConverterTestCase.of((short) 0, "0"),
                __AttributeConverterTestCase.of((short) -1, "-1"),
                __AttributeConverterTestCase.of(Short.MAX_VALUE, "32767"),
                __AttributeConverterTestCase.of(Short.MIN_VALUE, "-32768")
        );
    }
}
