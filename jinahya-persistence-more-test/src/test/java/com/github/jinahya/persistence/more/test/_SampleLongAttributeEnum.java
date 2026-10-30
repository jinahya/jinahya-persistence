package com.github.jinahya.persistence.more.test;

import com.github.jinahya.persistence.more.AttributeEnum;

public enum _SampleLongAttributeEnum implements AttributeEnum.__OfLong<_SampleLongAttributeEnum> {

    A(0L),

    B(1L);

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    _SampleLongAttributeEnum(final Long attributeValue) {
        this.attributeValue = attributeValue;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public Long attributeValue() {
        return attributeValue;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final Long attributeValue;
}
