package com.github.jinahya.persistence.mapped.tests;

import com.github.jinahya.persistence.mapped.__MappedEntity;
import com.github.jinahya.persistence.mapped.tests.util.__JavaLangReflectUtils;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.ParameterDeclarations;

import java.lang.invoke.MethodHandles;
import java.util.logging.Logger;
import java.util.stream.Stream;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
})
public class ___MappedEntityArgumentsProvider implements ArgumentsProvider {

    private static final Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    @Override
    public Stream<? extends Arguments> provideArguments(final ParameterDeclarations parameters,
                                                        final ExtensionContext context)
            throws Exception {
        final var requiredTestClass = context.getRequiredTestClass();
        final var entityClass = __JavaLangReflectUtils.getActualTypeParameter(
                requiredTestClass,
                ___MappedTest.class,
                0
        );
//        final var entityClass = __JavaLangReflectUtils.getActualTypeParameter(
//                requiredTestClass,
//                __MappedEntityTest.class,
//                0
//        );
        return ___MappedTestUtils.getTypeInstanceStream(entityClass.asSubclass(__MappedEntity.class)).map(
                Arguments::of);
    }
}
