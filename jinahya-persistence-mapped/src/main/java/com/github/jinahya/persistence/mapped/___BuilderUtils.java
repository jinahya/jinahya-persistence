package com.github.jinahya.persistence.mapped;

import jakarta.annotation.Nonnull;

import java.util.Objects;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public final class ___BuilderUtils {

    public static <BUILDER extends ___Builder<BUILDER, ?>> BUILDER reset(final @Nonnull BUILDER instance) {
        Objects.requireNonNull(instance, "instance is null");
        return ___ReflectionUtils.reset(instance);
    }

    static void setValues(final @Nonnull ___Builder<?, ?> builder, final @Nonnull Object target)
            throws ReflectiveOperationException {
        Objects.requireNonNull(builder, "builder is null");
        Objects.requireNonNull(target, "target is null");
        for (Class<?> c = builder.getClass();
             c != null && ___Builder.class.isAssignableFrom(c); c = c.getSuperclass()) {
            for (final var method : c.getDeclaredMethods()) {
                final var parameters = method.getParameters();
                if (parameters.length > 0) {
                    continue;
                }
                final var returnType = method.getReturnType();
                if (returnType == void.class) {
                    continue;
                }
                final var setter = ___ReflectionUtils.getPropertySetter(
                        target.getClass(),
                        method.getName(),
                        returnType
                );
                if (setter == null) {
                    continue;
                }
                if (!setter.canAccess(target)) {
                    setter.setAccessible(true);
                }
                if (!method.canAccess(builder)) {
                    method.setAccessible(true);
                }
                final var value = method.invoke(builder);
                setter.invoke(target, value);
            }
        }
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    private ___BuilderUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
