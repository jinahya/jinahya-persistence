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

    // ------------------------------------------------------------------------------------------ STATIC_FACTORY_METHODS
    /**
     * Creates a new instance with the specified attribute and its expected db data.
     *
     * @param attribute the entity attribute value.
     * @param dbData    the database column value the {@code attribute} is expected to convert to, and to convert back
     *                  from.
     * @param <X>       entity attribute type parameter
     * @param <Y>       table column type parameter
     * @return a new instance.
     */
    @Nonnull
    public static <X, Y> __AttributeConverterTestCase<X, Y> of(final @Nonnull X attribute, final @Nonnull Y dbData) {
        return new __AttributeConverterTestCase<>(attribute, dbData) {
        };
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    /**
     * Creates a new instance with the specified attribute and its expected db data.
     *
     * @param attribute the entity attribute value.
     * @param dbData    the database column value the {@code attribute} is expected to convert to, and to convert back
     *                  from.
     */
    protected __AttributeConverterTestCase(final @Nonnull X attribute, final @Nonnull Y dbData) {
        super();
        this.attribute = Objects.requireNonNull(attribute, "attribute is null");
        this.dbData = Objects.requireNonNull(dbData, "dbData is null");
    }

    // ------------------------------------------------------------------------------------------------------- attribute
    /**
     * Returns the entity attribute value of this test case.
     *
     * @return the entity attribute value.
     */
    @Nonnull
    public X getAttribute() {
        return attribute;
    }

    // ---------------------------------------------------------------------------------------------------------- dbData
    /**
     * Returns the database column value of this test case.
     *
     * @return the database column value.
     */
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
