package com.github.jinahya.persistence.more.test;

import jakarta.persistence.AttributeConverter;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * An abstract class for testing {@link AttributeConverter} implementations.
 * <p>
 * A subclass names the converter class and the two types it converts between; the assertions here then take a
 * {@link __AttributeConverterTestCase test case}, which pairs an entity attribute with the db data it is expected to
 * become, and check the conversion in both directions.
 *
 * @param <C> converter type parameter
 * @param <X> entity attribute type parameter
 * @param <Y> table column type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __AttributeConverterTestCase
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __AttributeConverter_Test<C extends AttributeConverter<X, Y>, X, Y> {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * An abstract {@link TestTemplateInvocationContextProvider} for testing {@link AttributeConverter}.
     */
    abstract static class __AttributeConverterTestInvocationContextProvider
            implements TestTemplateInvocationContextProvider {

    }

    /**
     * An abstract {@link TestTemplateInvocationContextProvider} for testing
     * {@link AttributeConverter#convertToDatabaseColumn(Object) convertToDatabaseColumn(X attribute)} method.
     */
    protected abstract static class __ConvertToDatabaseColumnTestInvocationContextProvider
            extends __AttributeConverterTestInvocationContextProvider {

        /**
         * Creates a new instance.
         */
        protected __ConvertToDatabaseColumnTestInvocationContextProvider() {
            super();
        }

        /**
         * {@inheritDoc}
         *
         * @param context {@inheritDoc}
         * @return {@code true} only for
         *         {@link
         *         __AttributeConverter_Test#convertToDatabaseColumn_ResultEqualsToExpectedDbData_GivenAttribute(__AttributeConverterTestCase)}.
         * @implNote A registered provider is consulted for <em>every</em> {@code @TestTemplate} method in
         *         scope, so returning {@code true} unconditionally makes this provider claim the reverse direction's
         *         method, and any unrelated template a subclass adds, as well as its own.
         */
        @Override
        public final boolean supportsTestTemplate(final ExtensionContext context) {
            logger.log(System.Logger.Level.DEBUG, "testMethod: {0}", context.getTestMethod());
            return supports(context, CONVERT_TO_DATABASE_COLUMN);
        }
    }

    /**
     * An abstract {@link TestTemplateInvocationContextProvider} for testing
     * {@link AttributeConverter#convertToEntityAttribute(Object) convertToEntityAttribute(Y dbData)} method.
     */
    protected abstract static class __ConvertToEntityAttributeTestInvocationContextProvider
            extends __AttributeConverterTestInvocationContextProvider {

        /**
         * Creates a new instance.
         */
        protected __ConvertToEntityAttributeTestInvocationContextProvider() {
            super();
        }

        /**
         * {@inheritDoc}
         *
         * @param context {@inheritDoc}
         * @return {@code true} only for
         *         {@link
         *         __AttributeConverter_Test#convertToEntityAttribute_ResultEqualsToExpectedAttribute_GivenDbData(__AttributeConverterTestCase)}.
         * @implNote See
         *         {@link
         *         __ConvertToDatabaseColumnTestInvocationContextProvider#supportsTestTemplate(ExtensionContext)}.
         */
        @Override
        public boolean supportsTestTemplate(final ExtensionContext context) {
            logger.log(System.Logger.Level.DEBUG, "testMethod: {0}", context.getTestMethod());
            return supports(context, CONVERT_TO_ENTITY_ATTRIBUTE);
        }
    }

    private static final String CONVERT_TO_DATABASE_COLUMN =
            "convertToDatabaseColumn_ResultEqualsToExpectedDbData_GivenAttribute";

    private static final String CONVERT_TO_ENTITY_ATTRIBUTE =
            "convertToEntityAttribute_ResultEqualsToExpectedAttribute_GivenDbData";

    /**
     * Returns whether the specified context's test method is the one named.
     *
     * @param context the extension context.
     * @param name    simple name of the test method this provider serves.
     * @return {@code true} when the {@code context}'s test method has the {@code name}.
     */
    private static boolean supports(final ExtensionContext context, final String name) {
        return context.getTestMethod().map(m -> m.getName().equals(name)).orElse(false);
    }

    // ------------------------------------------------------------------------------------------ STATIC_FACTORY_METHODS

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance for testing specified converter class.
     *
     * @param converterClass the converter class to test.
     * @param attributeClass the type of entity attribute.
     * @param dbDataClass    the type of database column.
     */
    protected __AttributeConverter_Test(final Class<C> converterClass, final Class<X> attributeClass,
                                        final Class<Y> dbDataClass) {
        this.converterClass = Objects.requireNonNull(converterClass, "converterClass is null");
        this.attributeClass = Objects.requireNonNull(attributeClass, "attributeClass is null");
        this.dbDataClass = Objects.requireNonNull(dbDataClass, "dbDataClass is null");
    }

    // ----------------------------------------------------------------------------------------- convertToDatabaseColumn

    /**
     * Asserts that the {@link AttributeConverter#convertToDatabaseColumn(Object) convertToDatabaseColumn(X)} method of
     * an instance of {@link #converterClass} results given test-case's
     * {@link __AttributeConverterTestCase#getDbData() dbDataa} for the test-case's
     * {@link __AttributeConverterTestCase#getAttribute() attribute}.
     *
     * @param testCase the test case to test with.
     * @see #newConverterInstance()
     * @see __ConvertToDatabaseColumnTestInvocationContextProvider
     */
    //    @TestTemplate
    protected void convertToDatabaseColumn_ResultEqualsToExpectedDbData_GivenAttribute(
            final __AttributeConverterTestCase<X, Y> testCase) {
        // ------------------------------------------------------------------------------------------------------- given
        final var instance = newConverterInstance();
        final var attribute = testCase.getAttribute();
        final var expected = testCase.getDbData();
        // -------------------------------------------------------------------------------------------------------- when
        final var actual = instance.convertToDatabaseColumn(attribute);
        // -------------------------------------------------------------------------------------------------------- then
        assertNotNull(actual, () -> "null dbData converted from " + attribute);
        assertEquals(expected, actual, () -> "actual dbData converted from " + attribute);
    }

    // ---------------------------------------------------------------------------------------- convertToEntityAttribute

    /**
     * Asserts that the {@link AttributeConverter#convertToEntityAttribute(Object) convertToEntityAttribute(Y)} method
     * of an instance of {@link #converterClass} results given test-case's
     * {@link __AttributeConverterTestCase#getAttribute() attribute} for the test-case's
     * {@link __AttributeConverterTestCase#getDbData() dbData}.
     *
     * @param testCase the test case to test with.
     * @see #newConverterInstance()
     * @see __ConvertToEntityAttributeTestInvocationContextProvider
     */
    //    @TestTemplate
    protected void convertToEntityAttribute_ResultEqualsToExpectedAttribute_GivenDbData(
            final __AttributeConverterTestCase<X, Y> testCase) {
        // ------------------------------------------------------------------------------------------------------- given
        final var instance = newConverterInstance();
        final var dbData = testCase.getDbData();
        final var expected = testCase.getAttribute();
        // -------------------------------------------------------------------------------------------------------- when
        final var actual = instance.convertToEntityAttribute(dbData);
        // -------------------------------------------------------------------------------------------------------- then
        assertNotNull(actual, () -> "null attribute converted from " + dbData);
        assertEquals(expected, actual, () -> "actual attribute converted from " + dbData);
    }

    // -------------------------------------------------------------------------------------------------- converterClass

    /**
     * Creates a new instance of {@link #converterClass}.
     *
     * @return a new instance of {@link #converterClass}.
     */
    protected C newConverterInstance() {
        return ___Utils.newInstance(converterClass);
    }

    // -------------------------------------------------------------------------------------------------- attributeClass

    /**
     * Creates a new instance of {@link #attributeClass}.
     *
     * @return a new instance of {@link #attributeClass}.
     */
    protected X newAttributeInstance() {
        return ___Utils.newInstance(attributeClass);
    }

    // ----------------------------------------------------------------------------------------------------- dbDataClass

    /**
     * Creates a new instance of {@link #dbDataClass}.
     *
     * @return a new instance of {@link #dbDataClass}.
     */
    protected Y newDbDataInstance() {
        return ___Utils.newInstance(dbDataClass);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The converter class to test.
     */
    protected final Class<C> converterClass;

    /**
     * The type of the entity attribute.
     */
    protected final Class<X> attributeClass;

    /**
     * The type of the database column.
     */
    protected final Class<Y> dbDataClass;
}
