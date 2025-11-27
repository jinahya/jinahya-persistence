package com.github.jinahya.persistence.mapped.test;

import com.github.jinahya.persistence.mapped.___Builder;
import com.github.jinahya.persistence.mapped.___BuilderUtils;
import jakarta.annotation.Nonnull;
import org.junit.platform.commons.util.ReflectionUtils;

import java.lang.invoke.MethodHandles;
import java.util.Objects;
import java.util.Optional;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119", // Type parameter names should comply with a naming convention
})
public final class ___Builder_TestUtils {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // -----------------------------------------------------------------------------------------------------------------
    // https://stackoverflow.com/a/25974010/330457
    public static <BUILDER extends ___Builder<BUILDER, TARGET>, TARGET> Class<TARGET> getTargetClass(
            final @Nonnull Class<BUILDER> builderClass) {
        return ___JavaLangReflect_TestUtils.getActualTypeClass(builderClass, ___Builder.class, 1);
    }

    // -----------------------------------------------------------------------------------------------------------------
    public static <BUILDER extends ___Builder<BUILDER, T>, T>
    BUILDER newBuilderInstanceFrom(final @Nonnull Class<BUILDER> builderClass, final @Nonnull T targetInstance) {
        Objects.requireNonNull(builderClass, "builderClass is null");
        Objects.requireNonNull(targetInstance, "targetInstance is null");
        final var builderInstance = ReflectionUtils.newInstance(builderClass);
        try {
            ___BuilderUtils.setBuilderValues(targetInstance, builderInstance);
        } catch (final ReflectiveOperationException roe) {
            throw new RuntimeException(
                    "failed to set properties of " + builderInstance + " from " + targetInstance,
                    roe
            );
        }
        return builderInstance;
    }

    public static <
            BUILDER extends ___Builder<BUILDER, TARGET>,
            TARGET
            >
    Optional<BUILDER> newBuilderInstanceFromRandomizedInstanceOf(final @Nonnull Class<BUILDER> builderClass,
                                                                 final @Nonnull Class<TARGET> targetClass) {
        Objects.requireNonNull(builderClass, "builderClass is null");
        Objects.requireNonNull(targetClass, "targetClass is null");
        return ___RandomizerUtils.newRandomizedInstanceOf(targetClass)
                .map(i -> newBuilderInstanceFrom(builderClass, i));
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    private ___Builder_TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
