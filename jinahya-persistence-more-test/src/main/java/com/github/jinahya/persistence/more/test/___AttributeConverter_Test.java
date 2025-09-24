package com.github.jinahya.persistence.more.test;

import jakarta.annotation.Nonnull;
import jakarta.persistence.AttributeConverter;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.Mockito;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class ___AttributeConverter_Test<C extends AttributeConverter<X, Y>, X, Y> {

    protected abstract static class TestCase<X, Y> {

        protected TestCase(final X attribute, final Y dbData) {
            super();
            this.attribute = Objects.requireNonNull(attribute, "attribute is null");
            this.dbData = Objects.requireNonNull(dbData, "dbData is null");
        }

        @Nonnull
        public X getAttribute() {
            return attribute;
        }

        @Nonnull
        public Y getDbData() {
            return dbData;
        }

        @Nonnull
        @NotNull
        private final X attribute;

        @Nonnull
        @NotNull
        private final Y dbData;
    }

    // ------------------------------------------------------------------------------------------ STATIC_FACTORY_METHODS

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    protected ___AttributeConverter_Test(final Class<C> converterClass, final Class<X> attributeClass,
                                         final Class<Y> dbDataClass) {
        this.converterClass = Objects.requireNonNull(converterClass, "converterClass is null");
        this.attributeClass = Objects.requireNonNull(attributeClass, "attributeClass is null");
        this.dbDataClass = Objects.requireNonNull(dbDataClass, "dbDataClass is null");
    }

    // ----------------------------------------------------------------------------------------- convertToDatabaseColumn
    protected abstract static class __ConvertToDatabaseColumnTestInvocationContextProvider
            implements TestTemplateInvocationContextProvider {

    }

    @TestTemplate
    protected void convertToDatabaseColumn__(final TestCase<X, Y> testCase) {
        // ------------------------------------------------------------------------------------------------------- given
        final var instance = newConverterInstance();
        final var attribute = testCase.getAttribute();
        final var expectedDbData = testCase.getDbData();
        // -------------------------------------------------------------------------------------------------------- when
        final var actualDbData = instance.convertToDatabaseColumn(attribute);
        // -------------------------------------------------------------------------------------------------------- then
        assertThat(actualDbData)
                .as("actualDbData converted from %1$s; expected to equal to %2$s", attribute, expectedDbData)
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
    protected final Class<C> converterClass;

    protected final Class<X> attributeClass;

    protected final Class<Y> dbDataClass;
}
