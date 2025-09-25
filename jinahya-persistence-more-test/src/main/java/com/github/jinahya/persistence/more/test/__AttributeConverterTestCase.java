package com.github.jinahya.persistence.more.test;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

/**
 * A parameter class for testing {@link jakarta.persistence.AttributeConverter} implementations.
 *
 * @param <X> entity attribute type parameter
 * @param <Y> table column type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
// TODO: make final?
public class __AttributeConverterTestCase<X, Y> {

    public static <X, Y> __AttributeConverterTestCaseBuilder<X, Y> builder() {
        return new __AttributeConverterTestCaseBuilder<>();
    }

    // ------------------------------------------------------------------------------------------ STATIC_FACTORY_METHODS
    @Nonnull
    public static <X, Y> __AttributeConverterTestCase<X, Y> of(@Nonnull final X attribute, @Nonnull final Y dbData) {
        return new __AttributeConverterTestCase<>(attribute, dbData) {
        };
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    protected __AttributeConverterTestCase(@Nonnull final X attribute, @Nonnull final Y dbData) {
        super();
        this.attribute = Objects.requireNonNull(attribute, "attribute is null");
        this.dbData = Objects.requireNonNull(dbData, "dbData is null");
    }

    __AttributeConverterTestCase(@Nonnull final __AttributeConverterTestCaseBuilder<X, Y> builder) {
        this(Objects.requireNonNull(builder, "builder is null").attribute(), builder.dbData());
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
