package com.github.jinahya.persistence.mapped.test;

import com.github.jinahya.persistence.mapped.___Builder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;

import java.lang.invoke.MethodHandles;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.assertj.core.api.Assumptions.assumeThatCode;

@SuppressWarnings({
        "java:S100", // Method names should comply with a naming convention
        "java:S101", // Class names should comply with a naming convention
//        "java:S117", // Local variable and method parameter names should comply with a naming convention
        "java:S119", // Type parameter names should comply with a naming convention
        "java:S3011" // Reflection should not be used to increase accessibility of classes, methods, or fields

})
public abstract class ___Builder_Test<
        BUILDER extends ___Builder<BUILDER, TARGET>,
        TARGET
        > {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    protected ___Builder_Test(final Class<BUILDER> builderClass, final Class<TARGET> targetClass) {
        super();
        this.builderClass = Objects.requireNonNull(builderClass, "builderClass is null");
        this.targetClass = Objects.requireNonNull(targetClass, "targetClass is null");
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("<constructed> -> build() should return non-null <TARGET>")
    @Test
    protected void build_NotNull_newBuilderInstance() {
        assumeThatCode(() -> { // some builder constructors may throw
            final var built = newBuilderInstance().build();
            logger.log(System.Logger.Level.DEBUG, "built: {0}", built);
            assertThat(built).isNotNull();
        }).doesNotThrowAnyException();
    }

//    @DisplayName("<randomized> -> build() should return non-null <TARGET>")
//    @Test
//    protected void build_NotNull_newRandomizedBuilderInstance() {
//        final var instance = newRandomizedBuilderInstance();
//        assumeThat(instance)
//                .as("optional of new randomized builder instance of %1$s", targetClass)
//                .isNotEmpty();
//        final var built = instance.get().build();
//        logger.log(System.Logger.Level.DEBUG, "built: {0}", built);
//        assertThat(built).isNotNull();
//    }

    // ---------------------------------------------------------------------------------------------------- builderClass
    protected BUILDER newBuilderInstance() {
        return ReflectionUtils.newInstance(builderClass);
    }

//    protected Optional<BUILDER> newRandomizedBuilderInstance() {
//        return ___Builder_TestUtils.newBuilderInstanceFromRandomizedInstanceOf(builderClass, targetClass);
//    }

    // ----------------------------------------------------------------------------------------------------- targetClass
    protected TARGET newTargetInstance() {
        return ReflectionUtils.newInstance(targetClass);
    }

//    protected Optional<TARGET> newRandomizedTargetInstance() {
//        return ___RandomizerUtils.newRandomizedInstanceOf(targetClass);
//    }

    // -----------------------------------------------------------------------------------------------------------------
    protected final Class<BUILDER> builderClass;

    protected final Class<TARGET> targetClass;
}
