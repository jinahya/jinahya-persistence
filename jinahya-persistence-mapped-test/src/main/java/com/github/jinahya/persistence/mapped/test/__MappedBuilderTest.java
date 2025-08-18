package com.github.jinahya.persistence.mapped.test;

import com.github.jinahya.persistence.mapped.__MappedBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;

import java.beans.Introspector;
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
public abstract class __MappedBuilderTest<
        BUILDER extends __MappedBuilder<BUILDER, T>,
        T
        > {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    protected __MappedBuilderTest(final Class<BUILDER> builderClass, final Class<T> targetClass) {
        super();
        this.builderClass = Objects.requireNonNull(builderClass, "builderClass is null");
        this.targetClass = Objects.requireNonNull(targetClass, "targetClass is null");
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("build() should return non-null ENTITY")
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
        return newRandomizedTargetInstance()
                .map(t -> {
                    final var builderInstance = newBuilderInstance();
                    try {
                        final var beanInfo = Introspector.getBeanInfo(targetClass);
                        final var propertyDescriptors = beanInfo.getPropertyDescriptors();
                        for (var propertyDescriptor : propertyDescriptors) {
                            final var targetPropertyReader = propertyDescriptor.getReadMethod();
                            if (targetPropertyReader == null) {
                                continue;
                            }
                            if (!targetPropertyReader.canAccess(t)) {
                                targetPropertyReader.setAccessible(true);
                            }
                            final var typePropertyValue = propertyDescriptor.getReadMethod().invoke(t);
                            final var typePropertyName = propertyDescriptor.getName();
                            final var typePropertyType = propertyDescriptor.getPropertyType();
                            final var builderPropertyWriter =
                                    ReflectionUtils
                                            .findMethod(builderClass, typePropertyName, typePropertyType)
                                            .orElse(null);
                            if (builderPropertyWriter == null) {
                                logger.log(System.Logger.Level.DEBUG, "no builder writer method: {0}({1})",
                                           typePropertyName, typePropertyType);
                                continue;
                            }
                            if (!builderPropertyWriter.canAccess(builderInstance)) {
                                builderPropertyWriter.setAccessible(true);
                            }
                            builderPropertyWriter.invoke(builderInstance, typePropertyValue);
                        }
                        return builderInstance;
                    } catch (final Exception e) {
                        throw new RuntimeException(
                                "failed to randomize " + builderInstance + " from " + t,
                                e
                        );
                    }
                });
    }

    // ----------------------------------------------------------------------------------------------------- targetClass
    protected T newTargetInstance() {
        return ReflectionUtils.newInstance(targetClass);
    }

    protected Optional<T> newRandomizedTargetInstance() {
        return ___RandomizerUtils.newRandomizedInstanceOf(targetClass);
    }

    // -----------------------------------------------------------------------------------------------------------------
    protected final Class<BUILDER> builderClass;

    protected final Class<T> targetClass;
}
