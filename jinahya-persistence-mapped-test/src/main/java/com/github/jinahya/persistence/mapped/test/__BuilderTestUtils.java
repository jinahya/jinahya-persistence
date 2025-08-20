package com.github.jinahya.persistence.mapped.test;

import com.github.jinahya.persistence.mapped.__Builder;
import jakarta.annotation.Nonnull;
import org.junit.platform.commons.util.ReflectionUtils;

import java.beans.Introspector;
import java.lang.invoke.MethodHandles;
import java.util.Objects;
import java.util.Optional;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119", // Type parameter names should comply with a naming convention
})
public final class __BuilderTestUtils {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // -----------------------------------------------------------------------------------------------------------------
    public static <
            BUILDER
                    extends __Builder<
                            BUILDER,
                            T
                            >,
            T
            >
    BUILDER newBuilderInstanceFrom(final Class<BUILDER> builderClass, @Nonnull final T targetInstance) {
        Objects.requireNonNull(builderClass, "builderClass is null");
        Objects.requireNonNull(targetInstance, "targetInstance is null");
        final var builderInstance = ReflectionUtils.newInstance(builderClass);
        try {
            final var beanInfo = Introspector.getBeanInfo(targetInstance.getClass());
            final var propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (var propertyDescriptor : propertyDescriptors) {
                final var targetPropertyReader = propertyDescriptor.getReadMethod();
                if (targetPropertyReader == null) {
                    continue;
                }
                if (!targetPropertyReader.canAccess(targetInstance)) {
                    targetPropertyReader.setAccessible(true);
                }
                final var typePropertyValue = propertyDescriptor.getReadMethod().invoke(targetInstance);
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
                    "failed to created a new instance of " + builderClass + " from " + targetInstance,
                    e
            );
        }
    }

    public static <BUILDER extends __Builder<BUILDER, T>, T>
    Optional<BUILDER> newBuilderInstanceFromRandomizedInstanceOf(@Nonnull final Class<BUILDER> builderClass,
                                                                 @Nonnull final Class<T> targetClass) {
        Objects.requireNonNull(builderClass, "builderClass is null");
        Objects.requireNonNull(targetClass, "targetClass is null");
        return ___RandomizerUtils.newRandomizedInstanceOf(targetClass)
                .map(ti -> newBuilderInstanceFrom(builderClass, ti));
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    private __BuilderTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
