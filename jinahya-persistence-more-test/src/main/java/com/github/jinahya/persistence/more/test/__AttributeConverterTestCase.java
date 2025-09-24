package com.github.jinahya.persistence.more.test;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

public class __AttributeConverterTestCase<X, Y> {

    public static <X, Y> __AttributeConverterTestCase<X, Y> of(final X attribute, final Y dbData) {
        return new __AttributeConverterTestCase<X, Y>(attribute, dbData) {
        };
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    protected __AttributeConverterTestCase(final X attribute, final Y dbData) {
        super();
        this.attribute = Objects.requireNonNull(attribute, "attribute is null");
        this.dbData = Objects.requireNonNull(dbData, "dbData is null");
    }

    protected __AttributeConverterTestCase(
            final __AttributeConverterTestCaseBuilder<?, __AttributeConverterTestCase<X, Y>, X, Y> builder) {
        this(builder.attribute(), builder.dbData());
    }

    // ------------------------------------------------------------------------------------------------------- attribute
    @Nonnull
    public X getAttribute() {
        return attribute;
    }

    // ---------------------------------------------------------------------------------------------------------- dbData
    @Nonnull
    public Y getDbData() {
        return dbData;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @NotNull
    private final X attribute;

    @Nonnull
    @NotNull
    private final Y dbData;
}
