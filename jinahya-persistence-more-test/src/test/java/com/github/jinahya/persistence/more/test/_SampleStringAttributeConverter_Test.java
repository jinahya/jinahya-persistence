package com.github.jinahya.persistence.more.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
class _SampleStringAttributeConverter_Test
        extends __StringAttributeConverter_Test<_SampleStringAttributeConverter, String> {

    _SampleStringAttributeConverter_Test() {
        super(_SampleStringAttributeConverter.class, String.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    // must extend the provider for the direction it is registered on: the name always said
    // ConvertToDatabaseColumn, but it used to extend the ConvertToEntityAttribute one, which went
    // unnoticed only because supportsTestTemplate returned true for every template
    private static class ConvertToDatabaseColumnTestInvocationContextProvider
            extends __ConvertToDatabaseColumnTestInvocationContextProvider {

        @Override
        public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(
                final ExtensionContext context) {
            return Stream.of(
                    new TestTemplateInvocationContext() {
                        @Override
                        public List<Extension> getAdditionalExtensions() {
                            return List.of(
                                    new TypeBasedParameterResolver<__AttributeConverterTestCase<String, String>>() {
                                        @Override
                                        public __AttributeConverterTestCase<String, String> resolveParameter(
                                                final ParameterContext parameterContext,
                                                final ExtensionContext extensionContext)
                                                throws ParameterResolutionException {
                                            return __AttributeConverterTestCase.of("a", "a");
                                        }
                                    }
                            );
                        }
                    },
                    new TestTemplateInvocationContext() {
                        @Override
                        public List<Extension> getAdditionalExtensions() {
                            return List.of(
                                    new TypeBasedParameterResolver<__AttributeConverterTestCase<String, String>>() {
                                        @Override
                                        public __AttributeConverterTestCase<String, String> resolveParameter(
                                                final ParameterContext parameterContext,
                                                final ExtensionContext extensionContext)
                                                throws ParameterResolutionException {
                                            return __AttributeConverterTestCase.of("a", "a");
                                        }
                                    }
                            );
                        }
                    }
            );
        }
    }

    @ExtendWith(ConvertToDatabaseColumnTestInvocationContextProvider.class)
    @TestTemplate
    @Override
    protected void convertToDatabaseColumn_ResultEqualsToExpectedDbData_GivenAttribute(
            final __AttributeConverterTestCase<String, String> testCase) {
        super.convertToDatabaseColumn_ResultEqualsToExpectedDbData_GivenAttribute(testCase);
    }
}
