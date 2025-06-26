package com.github.jinahya.persistence.mapped;

import jakarta.annotation.Nullable;

import java.util.*;
import java.util.stream.Stream;

abstract class ___InitializerUtils<T> {

    // -----------------------------------------------------------------------------------------------------------------
    private static final Map<Class<?>, Class<?>> INITIALIZER_CLASSES = Collections.synchronizedMap(new HashMap<>());

    @Nullable
    private static Class<?> getInitializerClassOf(final Class<?> type) {
        Objects.requireNonNull(type, "type is null");
        return INITIALIZER_CLASSES.computeIfAbsent(type, k -> {
            return Stream.of(k.getName())
                    .flatMap(n -> Stream.of(n + "Initializer", n + "_Initializer"))
                    .map(n -> {
                        try {
                            return Class.forName(n);
                        } catch (final ClassNotFoundException cnfe) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .filter(___Initializer.class::isAssignableFrom)
                    .findFirst()
                    .orElse(null);
        });
    }

    // -----------------------------------------------------------------------------------------------------------------
    private static final Map<Class<?>, Object> INITIALIZER_INSTANCES = Collections.synchronizedMap(new HashMap<>());

    @Nullable
    private static <T> Object getInitializerInstanceOf(final Class<T> type) {
        Objects.requireNonNull(type, "type is null");
        return INITIALIZER_INSTANCES.computeIfAbsent(type, k -> {
            return Optional.ofNullable(getInitializerClassOf(k))
                    .map(ic -> {
                        assert ___Initializer.class.isAssignableFrom(ic);
                        try {
                            final var constructor = ic.getDeclaredConstructor();
                            if (!constructor.canAccess(null)) {
                                constructor.setAccessible(true);
                            }
                            return constructor.newInstance();
                        } catch (final ReflectiveOperationException roe) {
                            return null;
                        }
                    })
                    .orElse(null);
        });
    }

    // -----------------------------------------------------------------------------------------------------------------
    static <T> Optional<T> newInitializedInstanceOf(final Class<T> type) {
        Objects.requireNonNull(type, "type is null");
        return Optional.ofNullable(getInitializerInstanceOf(type))
                .map(i -> ((___Initializer<?>) i).get())
                .map(type::cast) // ClassCastException
                ;
    }

    // -----------------------------------------------------------------------------------------------------------------
    ___InitializerUtils() {
        super();
    }
}
