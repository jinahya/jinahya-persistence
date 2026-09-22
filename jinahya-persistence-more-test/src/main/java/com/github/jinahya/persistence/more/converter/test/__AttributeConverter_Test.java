package com.github.jinahya.persistence.more.converter.test;

import com.github.jinahya.persistence.more.test.___Utils;
import jakarta.persistence.AttributeConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;
import org.junit.jupiter.api.function.Executable;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

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

    // ------------------------------------------------------------------------------------------------------ testCases

    /**
     * Returns the test cases with which both directions of the converter are checked.
     * <p>
     * Overriding this method is all a subclass has to do to get both assertions below running; the
     * {@link __ConvertToDatabaseColumnTestInvocationContextProvider provider} machinery is for a subclass which wants
     * a separate invocation, with its own display name, per case.
     *
     * @return the test cases; empty — as it is here — for a subclass which drives the assertions itself.
     * @implSpec The default implementation returns an empty list, which leaves both assertions below
     *         {@linkplain org.junit.jupiter.api.Assumptions#assumeFalse(boolean, String) skipped} rather than
     *         passing. A test which reports green without having converted anything is the one outcome worth ruling
     *         out here.
     */
    protected List<__AttributeConverterTestCase<X, Y>> testCases() {
        return List.of();
    }

    /**
     * Asserts that every {@link #testCases() test case} converts to its expected db data.
     *
     * @see #convertToDatabaseColumn_ResultEqualsToExpectedDbData_GivenAttribute(__AttributeConverterTestCase)
     */
    @DisplayName("convertToDatabaseColumn(attribute)expected dbData, for every test case")
    @Test
    protected void _ExpectedDbData_EachTestCase() {
        final var testCases = testCases();
        assumeFalse(testCases.isEmpty(), () -> "no test case supplied by " + getClass());
        assertAll(testCases.stream().map(
                tc -> (Executable) () -> convertToDatabaseColumn_ResultEqualsToExpectedDbData_GivenAttribute(tc)
        ));
    }

    /**
     * Asserts that every {@link #testCases() test case} converts back to its expected attribute.
     *
     * @see #convertToEntityAttribute_ResultEqualsToExpectedAttribute_GivenDbData(__AttributeConverterTestCase)
     */
    @DisplayName("convertToEntityAttribute(dbData)expected attribute, for every test case")
    @Test
    protected void _ExpectedAttribute_EachTestCase() {
        final var testCases = testCases();
        assumeFalse(testCases.isEmpty(), () -> "no test case supplied by " + getClass());
        assertAll(testCases.stream().map(
                tc -> (Executable) () -> convertToEntityAttribute_ResultEqualsToExpectedAttribute_GivenDbData(tc)
        ));
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
