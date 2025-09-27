package com.github.jinahya.persistence.more.test;

import jakarta.annotation.Nonnull;
import jakarta.persistence.AttributeConverter;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.Mockito;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * An abstract class for testing {@link AttributeConverter} implementations.
 *
 * @param <C> converter type parameter
 * @param <X> entity attribute type parameter
 * @param <Y> table column type parameter
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

        @Override
        public boolean mayReturnZeroTestTemplateInvocationContexts(final ExtensionContext context) {
            return TestTemplateInvocationContextProvider.super.mayReturnZeroTestTemplateInvocationContexts(context);
        }
    }

    /**
     * An abstract {@link TestTemplateInvocationContextProvider} for testing
     * {@link AttributeConverter#convertToDatabaseColumn(Object) convertToDatabaseColumn(X attribute)} method.
     */
    protected abstract static class __ConvertToDatabaseColumnTestInvocationContextProvider
            extends __AttributeConverterTestInvocationContextProvider {

        @Override
        public final boolean supportsTestTemplate(final ExtensionContext context) {
            logger.log(System.Logger.Level.DEBUG, "testMethod: {0}", context.getTestMethod());
            return true;
        }
    }

    /**
     * An abstract {@link TestTemplateInvocationContextProvider} for testing
     * {@link AttributeConverter#convertToEntityAttribute(Object) convertToEntityAttribute(Y dbData)} method.
     */
    protected abstract static class __ConvertToEntityAttributeTestInvocationContextProvider
            extends __AttributeConverterTestInvocationContextProvider {

        @Override
        public boolean supportsTestTemplate(final ExtensionContext context) {
            logger.log(System.Logger.Level.DEBUG, "testMethod: {0}", context.getTestMethod());
            return true;
        }
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
            @Nonnull final __AttributeConverterTestCase<X, Y> testCase) {
        // ------------------------------------------------------------------------------------------------------- given
        final var instance = newConverterInstance();
        final var attribute = testCase.getAttribute();
        final var expected = testCase.getDbData();
        // -------------------------------------------------------------------------------------------------------- when
        final var actual = instance.convertToDatabaseColumn(attribute);
        // -------------------------------------------------------------------------------------------------------- then
        assertThat(actual)
                .as("actual dbData converted from %1$s", attribute)
                .isNotNull()
                .isEqualTo(expected);
    }

    // ---------------------------------------------------------------------------------------- convertToEntityAttribute

    /**
     * .
     *
     * @param testCase .
     * @see __ConvertToEntityAttributeTestInvocationContextProvider
     */
    //    @TestTemplate
    protected void convertToEntityAttribute_ResultEqualsToExpectedAttribute_GivenDbData(
            @Nonnull final __AttributeConverterTestCase<X, Y> testCase) {
        // ------------------------------------------------------------------------------------------------------- given
        final var instance = newConverterInstance();
        final var dbData = testCase.getDbData();
        final var expected = testCase.getAttribute();
        // -------------------------------------------------------------------------------------------------------- when
        final var actual = instance.convertToEntityAttribute(dbData);
        // -------------------------------------------------------------------------------------------------------- then
        assertThat(actual)
                .as("actual attribute converted from %1$s", dbData)
                .isNotNull()
                .isEqualTo(expected);
    }

    // -------------------------------------------------------------------------------------------------- converterClass
    protected C newConverterInstance() {
        return ReflectionUtils.newInstance(converterClass);
    }

    protected C newConverterInstanceSpy() {
        return Mockito.spy(newConverterInstance());
    }

    // -------------------------------------------------------------------------------------------------- attributeClass
    protected X newAttributeInstance() {
        return ReflectionUtils.newInstance(attributeClass);
    }

    protected X newAttributeInstanceSpy() {
        return Mockito.spy(newAttributeInstance());
    }

    // ----------------------------------------------------------------------------------------------------- dbDataClass
    protected Y newDbDataInstance() {
        return ReflectionUtils.newInstance(dbDataClass);
    }

    protected Y newDbDataInstanceSpy() {
        return Mockito.spy(newDbDataInstance());
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The converter class to test.
     */
    protected final Class<C> converterClass;

    protected final Class<X> attributeClass;

    protected final Class<Y> dbDataClass;
}
