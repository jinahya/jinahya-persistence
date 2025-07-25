package com.github.jinahya.persistence.mapped.test;

/*-
 * #%L
 * jinahya-persistence-mapped-test
 * %%
 * Copyright (C) 2024 - 2025 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import jakarta.annotation.Nonnull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;
import java.util.Optional;

final class ___JavaLangReflectTestUtils {

    private static final Method AUTO_CLOSEABLE_CLOSE_METHOD;

    static {
        try {
            AUTO_CLOSEABLE_CLOSE_METHOD = AutoCloseable.class.getMethod("close");
        } catch (final NoSuchMethodException nsme) {
            throw new ExceptionInInitializerError(
                    "failed to find the AutoCloseable#close method; " + nsme.getLocalizedMessage()
            );
        }
    }

    @Deprecated(forRemoval = true)
    @SuppressWarnings({
            "unchecked"
    })
    public static <T extends AutoCloseable> T uncloseable(@Nonnull final T closeable) {
        Objects.requireNonNull(closeable, "closeable is null");
        return (T) Proxy.newProxyInstance(
                closeable.getClass().getClassLoader(),
//                AutoCloseable.class.getClassLoader(),
//                Proxy.class.getClassLoader(),
                new Class[]{AutoCloseable.class},
                (p, m, a) -> {
                    if (m == AUTO_CLOSEABLE_CLOSE_METHOD) {
                        throw new UnsupportedOperationException("this instance is not closeable");
                    }
                    return m.invoke(closeable, a);
                });
    }

    @Deprecated(forRemoval = true)
    public static <T extends AutoCloseable, U extends T>
    U uncloseable(@Nonnull final T closeable, final Class<U> type) {
        Objects.requireNonNull(closeable, "closeable is null");
        final var proxy = Proxy.newProxyInstance(
                closeable.getClass().getClassLoader(),
                new Class[]{AutoCloseable.class},
                (p, m, a) -> {
                    if (m == AUTO_CLOSEABLE_CLOSE_METHOD) {
                        throw new UnsupportedOperationException("this instance is not closeable");
                    }
                    return m.invoke(closeable, a);
                });
        return type.cast(proxy);
    }

    public static <A extends Annotation> Optional<A> findAnnotation(final Class<?> type,
                                                                    final Class<A> annotationType) {
        for (var c = type; c != null; c = c.getSuperclass()) {
            final var annotation = c.getAnnotation(annotationType);
            if (annotation != null) {
                return Optional.of(annotation);
            }
        }
        return Optional.empty();
    }

    public static Optional<Field> findField(final Class<?> type, final String name) {
        for (var c = type; c != null; c = c.getSuperclass()) {
            try {
                return Optional.of(c.getDeclaredField(name));
            } catch (final NoSuchFieldException nsme) {
                // ignore
            }
        }
        return Optional.empty();
    }

//    @Deprecated
//    // https://stackoverflow.com/a/25974010/330457
//    @Nullable
//    public static Class<?> getActualTypeParameter(@Nonnull final Class<?> clazz,
//                                                  @Nonnull final Class<?> type, final int index) {
//        Objects.requireNonNull(clazz, "clazz is null");
//        Objects.requireNonNull(type, "type is null");
//        if (index < 0) {
//            throw new IllegalArgumentException("negative index: " + index);
//        }
//        if (true || ThreadLocalRandom.current().nextBoolean()) {
//            final var rawArguments = TypeResolver.resolveRawArguments(type, clazz);
//            if (index >= rawArguments.length) {
//                throw new IllegalArgumentException(
//                        "index(" + index + ") is greater than or equal to the number of type parameters: " +
//                        rawArguments.length
//                );
//            }
//            return rawArguments[index];
//        }
//        final var mapping = new HashMap<TypeVariable<?>, Class<?>>();
//        for (var c = clazz; c != null; ) {
//            final var genericSuperclass = c.getGenericSuperclass();
//            if (genericSuperclass instanceof ParameterizedType parameterizedType) {
//                final var rawType = parameterizedType.getRawType();
//                if (rawType == type) {
//                    final var actualTypeArguments = parameterizedType.getActualTypeArguments();
//                    if (index < actualTypeArguments.length) {
//                        final var actualTypeArgument = actualTypeArguments[index];
//                        if (actualTypeArgument instanceof Class<?>) {
//                            return (Class<?>) actualTypeArgument;
//                        } else {
//                            return mapping.get(actualTypeArgument);
//                        }
//                    }
//                }
//                final var typeParameters = ((GenericDeclaration) (parameterizedType.getRawType())).getTypeParameters();
//                final var actualTypeArguments = parameterizedType.getActualTypeArguments();
//                for (int i = 0; i < typeParameters.length; i++) {
//                    if (actualTypeArguments[i] instanceof Class<?>) {
//                        mapping.put(typeParameters[i], (Class<?>) actualTypeArguments[i]);
//                    } else {
//                        mapping.put(typeParameters[i], mapping.get((TypeVariable<?>) actualTypeArguments[i]));
//                    }
//                }
//                c = (Class<?>) rawType;
//            } else {
//                c = c.getSuperclass();
//            }
//        }
//        return null;
//    }

    @SuppressWarnings({
            "java:S112", // Generic exceptions should never be thrown
            "java:S3011" // Reflection should not be used to increase accessibility of classes, methods, or fields
    })
    @Nonnull
    static <T> T newInstanceOf(@Nonnull final Class<T> type) {
        Objects.requireNonNull(type, "type is null");
        try {
            final var constructor = type.getDeclaredConstructor();
            if (!constructor.canAccess(null)) {
                constructor.setAccessible(true);
            }
            return constructor.newInstance();
        } catch (final ReflectiveOperationException roe) {
            throw new RuntimeException("failed to initialize a new instance of " + type, roe);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    private ___JavaLangReflectTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
