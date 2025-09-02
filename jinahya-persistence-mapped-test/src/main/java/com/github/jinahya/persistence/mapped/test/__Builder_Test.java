package com.github.jinahya.persistence.mapped.test;

import com.github.jinahya.persistence.mapped.__Builder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;

import java.lang.invoke.MethodHandles;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings({
        "java:S100", // Method names should comply with a naming convention
        "java:S101", // Class names should comply with a naming convention
//        "java:S117", // Local variable and method parameter names should comply with a naming convention
        "java:S119", // Type parameter names should comply with a naming convention
        "java:S3011" // Reflection should not be used to increase accessibility of classes, methods, or fields

})
public abstract class __Builder_Test<
        BUILDER extends __Builder<BUILDER, TARGET>,
        TARGET
        > {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    protected __Builder_Test(final Class<BUILDER> builderClass, final Class<TARGET> targetClass) {
        super();
        this.builderClass = Objects.requireNonNull(builderClass, "builderClass is null");
        this.targetClass = Objects.requireNonNull(targetClass, "targetClass is null");
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("build() should return non-null TARGET")
    @Test
    protected void build_NotNull_() {
        newRandomizedBuilderInstance().ifPresent(b -> {
            final var built = b.build();
            logger.log(System.Logger.Level.DEBUG, "built: {0}", built);
            assertThat(built).isNotNull();
        });
    }

    // ---------------------------------------------------------------------------------------------------- builderClass
    protected BUILDER newBuilderInstance() {
        return ReflectionUtils.newInstance(builderClass);
    }

    protected Optional<BUILDER> newRandomizedBuilderInstance() {
        return __Builder_TestUtils.newBuilderInstanceFromRandomizedInstanceOf(builderClass, targetClass);
    }

    // ----------------------------------------------------------------------------------------------------- targetClass
    protected TARGET newTargetInstance() {
        return ReflectionUtils.newInstance(targetClass);
    }

    protected Optional<TARGET> newRandomizedTargetInstance() {
        return ___RandomizerUtils.newRandomizedInstanceOf(targetClass);
    }

    // -----------------------------------------------------------------------------------------------------------------
    protected final Class<BUILDER> builderClass;

    protected final Class<TARGET> targetClass;
}
