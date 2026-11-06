package com.github.jinahya.persistence.more.converter.test;

import com.github.jinahya.persistence.more.converter.__NumberStringAttributeConverters;

import java.util.List;

/**
 * Tests {@link __NumberStringAttributeConverters.OfByte} against what every attribute converter is expected to do.
 */
class __NumberStringAttributeConverters_OfByte_Test
        extends StringAttributeConverter_Test<__NumberStringAttributeConverters.OfByte, Byte> {

    __NumberStringAttributeConverters_OfByte_Test() {
        super(__NumberStringAttributeConverters.OfByte.class);
    }

    @Override
    protected List<__AttributeConverterTestCase<Byte, String>> testCases() {
        return List.of(
                __AttributeConverterTestCase.of((byte) 0, "0"),
                __AttributeConverterTestCase.of((byte) -1, "-1"),
                __AttributeConverterTestCase.of(Byte.MAX_VALUE, "127"),
                __AttributeConverterTestCase.of(Byte.MIN_VALUE, "-128")
        );
    }
}
