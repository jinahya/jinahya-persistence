package com.github.jinahya.persistence.mapped.test;

import com.github.jinahya.persistence.mapped.___Builder;
import com.github.jinahya.persistence.mapped.___BuilderUtils;
import jakarta.annotation.Nonnull;
import org.junit.platform.commons.util.ReflectionUtils;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
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
    private static List<Method> writers(final @Nonnull Class<?> builderClass) {
        Objects.requireNonNull(builderClass, "builderClass is null");
        final var methods = new ArrayList<Method>();
        for (Class<?> c = builderClass;
             c != null && ___Builder.class.isAssignableFrom(c); c = c.getSuperclass()) {
            for (final var method : c.getDeclaredMethods()) {
                final var parameters = method.getParameters();
                if (parameters.length != 1) {
                    continue;
                }
                final var returnType = method.getReturnType();
                if (!returnType.isAssignableFrom(c)) {
                    continue;
                }
                methods.add(method);
            }
        }
        return methods;
    }

    static void setBuilderValues(final @Nonnull Object target, final @Nonnull ___Builder<?, ?> builder)
            throws ReflectiveOperationException {
        Objects.requireNonNull(target, "target is null");
        Objects.requireNonNull(builder, "builder is null");
        for (final var writer : writers(builder.getClass())) {
            final var getter = ___Reflection_TestUtils.getPropertyGetter(
                    target.getClass(),
                    writer.getName(),
                    writer.getParameterTypes()[0]
            );
            if (getter == null) {
                logger.log(System.Logger.Level.DEBUG, "no getter found for {0}", writer);
                continue;
            }
            if (!getter.canAccess(target)) {
                getter.setAccessible(true);
            }
            final var value = getter.invoke(target);
            if (value == null) {
                continue;
            }
            if (!writer.canAccess(builder)) {
                writer.setAccessible(true);
            }
            writer.invoke(builder, value);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    public static <BUILDER extends ___Builder<BUILDER, T>, T>
    BUILDER newBuilderInstanceFrom(final @Nonnull Class<BUILDER> builderClass, final @Nonnull T targetInstance) {
        Objects.requireNonNull(builderClass, "builderClass is null");
        Objects.requireNonNull(targetInstance, "targetInstance is null");
        final var builderInstance = ReflectionUtils.newInstance(builderClass);
        try {
            setBuilderValues(targetInstance, builderInstance);
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
