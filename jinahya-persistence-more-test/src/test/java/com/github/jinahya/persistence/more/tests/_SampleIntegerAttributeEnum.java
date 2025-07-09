package com.github.jinahya.persistence.more.tests;

enum _SampleIntegerAttributeEnum implements __SampleIntegerAttributeEnum<_SampleIntegerAttributeEnum> {

    A(0),

    B(1);

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    _SampleIntegerAttributeEnum(final Integer attributeValue) {
        this.attributeValue = attributeValue;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public Integer attributeValue() {
        return attributeValue;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final Integer attributeValue;
}
