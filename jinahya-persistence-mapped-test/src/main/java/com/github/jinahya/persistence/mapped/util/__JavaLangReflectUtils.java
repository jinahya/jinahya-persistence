package com.github.jinahya.persistence.mapped.util;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class __JavaLangReflectUtils {

    private static final Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    // https://stackoverflow.com/a/25974010/330457
    @Nullable
    public static Class<?> getActualTypeParameter(@Nonnull final Class<?> clazz,
                                                  @Nonnull final Class<?> type, final int index) {
        Objects.requireNonNull(clazz, "clazz is null");
        Objects.requireNonNull(type, "type is null");
        if (index < 0) {
            throw new IllegalArgumentException("negative index: " + index);
        }
        final var mapping = new HashMap<TypeVariable<?>, Class<?>>();
        var c = clazz;
        while (c != null) {
            final var genericSuperclass = c.getGenericSuperclass();
            if (genericSuperclass instanceof ParameterizedType parameterizedType) {
                final var rawType = parameterizedType.getRawType();
                if (rawType == type) {
                    final var actualTypeArguments = parameterizedType.getActualTypeArguments();
                    if (index < actualTypeArguments.length) {
                        final var actualTypeArgument = actualTypeArguments[index];
                        if (actualTypeArgument instanceof Class<?>) {
                            return (Class<?>) actualTypeArgument;
                        } else {
                            return mapping.get((TypeVariable<?>) actualTypeArgument);
                        }
                    }
                }
                final var typeParameters = ((GenericDeclaration) (parameterizedType.getRawType())).getTypeParameters();
                final var actualTypeArguments = parameterizedType.getActualTypeArguments();
                for (int i = 0; i < typeParameters.length; i++) {
                    if (actualTypeArguments[i] instanceof Class<?>) {
                        mapping.put(typeParameters[i], (Class<?>) actualTypeArguments[i]);
                    } else {
                        mapping.put(typeParameters[i], mapping.get((TypeVariable<?>) actualTypeArguments[i]));
                    }
                }
                c = (Class<?>) rawType;
            } else {
                c = c.getSuperclass();
            }
        }
        return null;
    }

    @SuppressWarnings({
            "java:S112", // Generic exceptions should never be thrown
            "java:S3011" // Reflection should not be used to increase accessibility of classes, methods, or fields
    })
    @Nullable
    public static <T> T newInstanceOf(@Nonnull final Class<T> type) {
        Objects.requireNonNull(type, "type is null");
        try {
            final var constructor = type.getDeclaredConstructor();
            if (!constructor.canAccess(null)) {
                constructor.setAccessible(true);
            }
            return constructor.newInstance();
        } catch (final ReflectiveOperationException roe) {
            logger.log(Level.SEVERE, roe, () -> "failed to initialize a new instance of " + type);
            return null;
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    private __JavaLangReflectUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
