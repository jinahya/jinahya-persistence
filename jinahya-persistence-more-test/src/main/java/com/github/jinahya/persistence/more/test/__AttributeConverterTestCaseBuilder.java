package com.github.jinahya.persistence.more.test;

import com.github.jinahya.persistence.mapped.__Builder;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public abstract class __AttributeConverterTestCaseBuilder<
        SELF extends __AttributeConverterTestCaseBuilder<SELF, TARGET, X, Y>,
        TARGET extends __AttributeConverterTestCase<X, Y>,
        X,
        Y
        >
        extends __Builder<SELF, TARGET> {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    protected __AttributeConverterTestCaseBuilder(final Class<TARGET> targetClass) {
        super(targetClass);
    }

    // ------------------------------------------------------------------------------------------------------- attribute
    public X attribute() {
        return attribute;
    }

    public SELF attribute(final X attribute) {
        this.attribute = attribute;
        return (SELF) this;
    }

    // ---------------------------------------------------------------------------------------------------------- dbData
    public Y dbData() {
        return dbData;
    }

    public SELF dbData(final Y dbData) {
        this.dbData = dbData;
        return (SELF) this;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private X attribute;

    private Y dbData;
}
