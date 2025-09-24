package com.github.jinahya.persistence.more.test;

import jakarta.annotation.Nonnull;
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
class SampleStringAttributeConverter_Test
        extends __StringAttributeConverter_Test<SampleStringAttributeConverter, String> {

    SampleStringAttributeConverter_Test() {
        super(SampleStringAttributeConverter.class, String.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private static class ConvertToDatabaseColumnTestInvocationContextProvider
            extends __ConvertToDatabaseColumnTestInvocationContextProvider {

//        @Override
//        public boolean supportsTestTemplate(final ExtensionContext context) {
//            return true;
//        }
//
//        @Override
//        public boolean mayReturnZeroTestTemplateInvocationContexts(ExtensionContext context) {
//            return super.mayReturnZeroTestTemplateInvocationContexts(context);
//        }

        @Override
        public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(
                final ExtensionContext context) {
            return Stream.of(
                    new TestTemplateInvocationContext() {
//                        @Override
//                        public String getDisplayName(final int invocationIndex) {
//                            return TestTemplateInvocationContext.super.getDisplayName(invocationIndex);
//                        }

                        @Override
                        public List<Extension> getAdditionalExtensions() {
                            return List.of(
                                    new TypeBasedParameterResolver<TestCase<String, String>>() {
                                        @Override
                                        public TestCase<String, String> resolveParameter(
                                                final ParameterContext parameterContext,
                                                final ExtensionContext extensionContext)
                                                throws ParameterResolutionException {
                                            return TestCase.of("a", "a");
                                        }
                                    }
                            );
                        }

//                        @Override
//                        public void prepareInvocation(final ExtensionContext context) {
//                            TestTemplateInvocationContext.super.prepareInvocation(context);
//                        }
                    },
                    new TestTemplateInvocationContext() {
//                        @Override
//                        public String getDisplayName(final int invocationIndex) {
//                            return TestTemplateInvocationContext.super.getDisplayName(invocationIndex);
//                        }

                        @Override
                        public List<Extension> getAdditionalExtensions() {
                            return List.of(
                                    new TypeBasedParameterResolver<TestCase<String, String>>() {
                                        @Override
                                        public TestCase<String, String> resolveParameter(
                                                final ParameterContext parameterContext,
                                                final ExtensionContext extensionContext)
                                                throws ParameterResolutionException {
                                            return TestCase.of("b", "b");
                                        }
                                    }
                            );
                        }

//                        @Override
//                        public void prepareInvocation(final ExtensionContext context) {
//                            TestTemplateInvocationContext.super.prepareInvocation(context);
//                        }
                    }
            );
        }
    }

    @ExtendWith(ConvertToDatabaseColumnTestInvocationContextProvider.class)
    @TestTemplate
    @Override
    protected void convertToDatabaseColumn__(@Nonnull final TestCase<String, String> testCase) {
        super.convertToDatabaseColumn__(testCase);
    }
}
