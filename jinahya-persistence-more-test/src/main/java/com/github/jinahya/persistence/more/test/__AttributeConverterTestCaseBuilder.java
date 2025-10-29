package com.github.jinahya.persistence.more.test;

import com.github.jinahya.persistence.mapped.___Builder;

@SuppressWarnings({
        "unchecked",
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
// TODO: make final?
public class __AttributeConverterTestCaseBuilder<X, Y>
        extends ___Builder<__AttributeConverterTestCaseBuilder<X, Y>, __AttributeConverterTestCase<X, Y>> {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    protected __AttributeConverterTestCaseBuilder() {
        // https://stackoverflow.com/a/30754982/330457
        super((Class<__AttributeConverterTestCase<X, Y>>) (Object) __AttributeConverterTestCase.class);
    }

    // ------------------------------------------------------------------------------------------------------- attribute
    public X attribute() {
        return attribute;
    }

    public __AttributeConverterTestCaseBuilder<X, Y> attribute(final X attribute) {
        this.attribute = attribute;
        return this;
    }

    public __AttributeConverterTestCaseBuilder<X, Y> givenAttribute(final X attribute) {
        return attribute(attribute);
    }

    public __AttributeConverterTestCaseBuilder<X, Y> expectAttribute(final X attribute) {
        return attribute(attribute);
    }

    // ---------------------------------------------------------------------------------------------------------- dbData
    public Y dbData() {
        return dbData;
    }

    public __AttributeConverterTestCaseBuilder<X, Y> dbData(final Y dbData) {
        this.dbData = dbData;
        return this;
    }

    public __AttributeConverterTestCaseBuilder<X, Y> givenDbData(final Y dbData) {
        return dbData(dbData);
    }

    public __AttributeConverterTestCaseBuilder<X, Y> expectDbData(final Y dbData) {
        return dbData(dbData);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private X attribute;

    private Y dbData;
}
