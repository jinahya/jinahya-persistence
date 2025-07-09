package com.github.jinahya.persistence.more.tests;

import java.util.Optional;

enum _SampleStringAttributeEnum implements __SampleStringAttributeEnum<_SampleStringAttributeEnum> {

    A,

    B("SOME");

    // ----------------------------------------------------------------------------------------------------- CONSTRUCTORS
    _SampleStringAttributeEnum(final String attributeValue) {
        this.attributeValue = attributeValue;
    }

    _SampleStringAttributeEnum() {
        this(null);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public String attributeValue() {
        return Optional.ofNullable(attributeValue)
                .orElseGet(__SampleStringAttributeEnum.super::attributeValue);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final String attributeValue;
}
