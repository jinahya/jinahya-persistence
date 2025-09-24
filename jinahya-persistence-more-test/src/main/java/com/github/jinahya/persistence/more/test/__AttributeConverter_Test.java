package com.github.jinahya.persistence.more.test;

import jakarta.annotation.Nonnull;
import jakarta.persistence.AttributeConverter;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.Mockito;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __AttributeConverter_Test<C extends AttributeConverter<X, Y>, X, Y> {

    // ------------------------------------------------------------------------------------------ STATIC_FACTORY_METHODS

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    protected __AttributeConverter_Test(final Class<C> converterClass, final Class<X> attributeClass,
                                        final Class<Y> dbDataClass) {
        this.converterClass = Objects.requireNonNull(converterClass, "converterClass is null");
        this.attributeClass = Objects.requireNonNull(attributeClass, "attributeClass is null");
        this.dbDataClass = Objects.requireNonNull(dbDataClass, "dbDataClass is null");
    }

    // ----------------------------------------------------------------------------------------- convertToDatabaseColumn
    protected abstract static class __ConvertToDatabaseColumnTestInvocationContextProvider
            implements TestTemplateInvocationContextProvider {

        @Override
        public boolean supportsTestTemplate(final ExtensionContext context) {
            return true;
        }

        @Override
        public boolean mayReturnZeroTestTemplateInvocationContexts(final ExtensionContext context) {
            return TestTemplateInvocationContextProvider.super.mayReturnZeroTestTemplateInvocationContexts(context);
        }
    }

    @TestTemplate
    protected void convertToDatabaseColumn__(@Nonnull final __AttributeConverterTestCase<X, Y> testCase) {
        // ------------------------------------------------------------------------------------------------------- given
        final var instance = newConverterInstance();
        final var attribute = testCase.getAttribute();
        final var expectedDbData = testCase.getDbData();
        // -------------------------------------------------------------------------------------------------------- when
        final var actualDbData = instance.convertToDatabaseColumn(attribute);
        // -------------------------------------------------------------------------------------------------------- then
        assertThat(actualDbData)
                .as("actual dbData converted from %1$s", attribute, expectedDbData)
                .isNotNull()
                .isEqualTo(expectedDbData);
    }

    // ---------------------------------------------------------------------------------------- convertToEntityAttribute

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
