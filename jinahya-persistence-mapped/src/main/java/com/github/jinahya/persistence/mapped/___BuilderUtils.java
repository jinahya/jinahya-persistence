package com.github.jinahya.persistence.mapped;

import jakarta.annotation.Nonnull;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public final class ___BuilderUtils {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    public static <BUILDER extends ___Builder<BUILDER, ?>> BUILDER reset(final @Nonnull BUILDER instance) {
        Objects.requireNonNull(instance, "instance is null");
        return ___ReflectionUtils.reset(instance);
    }

    private static List<Method> readers(final @Nonnull Class<?> builderClass) {
        Objects.requireNonNull(builderClass, "builderClass is null");
        final var methods = new ArrayList<Method>();
        for (Class<?> c = builderClass;
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
                methods.add(method);
            }
        }
        return methods;
    }

    static void getTargetValues(final @Nonnull ___Builder<?, ?> builder, final @Nonnull Object target)
            throws ReflectiveOperationException {
        Objects.requireNonNull(builder, "builder is null");
        Objects.requireNonNull(target, "target is null");
        for (final var reader : readers(builder.getClass())) {
            final var setter = ___ReflectionUtils.getPropertySetter(
                    target.getClass(),
                    reader.getName(),
                    reader.getReturnType()
            );
            if (setter == null) {
                logger.log(System.Logger.Level.DEBUG, "no setter found for {0}", reader);
                continue;
            }
            if (!setter.canAccess(target)) {
                setter.setAccessible(true);
            }
            if (!reader.canAccess(builder)) {
                reader.setAccessible(true);
            }
            final var value = reader.invoke(builder);
            setter.invoke(target, value);
        }
    }


    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    private ___BuilderUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
