package com.github.jinahya.persistence.more.converter.test;

import java.util.Objects;

/**
 * A parameter class for testing {@link jakarta.persistence.AttributeConverter} implementations.
 *
 * @param <X> entity attribute type parameter
 * @param <Y> table column type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
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
     * @implNote Returns this class, not an anonymous subclass. It used to return
     *         {@code new __AttributeConverterTestCase<>(attribute, dbData) {}}, which minted a distinct class per call
     *         site for no benefit &mdash; it defeats any {@link Object#getClass() getClass()} comparison, and the
     *         factory sits in the very class whose constructor it calls, so the subclass bought no access. The class
     *         stays non-final so a caller can still extend it deliberately.
     */
    public static <X, Y> __AttributeConverterTestCase<X, Y> of(final X attribute, final Y dbData) {
        return new __AttributeConverterTestCase<>(attribute, dbData);
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance with the specified attribute and its expected db data.
     *
     * @param attribute the entity attribute value.
     * @param dbData    the database column value the {@code attribute} is expected to convert to, and to convert back
     *                  from.
     */
    protected __AttributeConverterTestCase(final X attribute, final Y dbData) {
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
    public X getAttribute() {
        return attribute;
    }

    // ---------------------------------------------------------------------------------------------------------- dbData

    /**
     * Returns the database column value of this test case.
     *
     * @return the database column value.
     */
    public Y getDbData() {
        return dbData;
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The entity attribute value of this test case; never {@code null}, as the constructor requires.
     */
    private final X attribute;

    /**
     * The database column value of this test case; never {@code null}, as the constructor requires.
     */
    private final Y dbData;
}
