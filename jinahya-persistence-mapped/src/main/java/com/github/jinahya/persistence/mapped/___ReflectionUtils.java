package com.github.jinahya.persistence.mapped;

import jakarta.annotation.Nonnull;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S3011" // Reflection should not be used to increase accessibility of classes, methods, or fields
})
final class ___ReflectionUtils {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    static <T> void acceptEachField(@Nonnull final Class<T> clazz, @Nonnull final Consumer<? super Field> consumer) {
        Objects.requireNonNull(clazz, "clazz is null");
        Objects.requireNonNull(consumer, "consumer is null");
        for (Class<?> c = clazz; c != null && c != Object.class; c = c.getSuperclass()) {
            for (final var field : c.getDeclaredFields()) {
                consumer.accept(field);
            }
        }
    }

    @Nonnull
    static <T> T reset(@Nonnull final T instance) {
        Objects.requireNonNull(instance, "instance is null");
        acceptEachField(
                instance.getClass(),
                f -> {
                    final var modifiers = f.getModifiers();
                    if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
                        return;
                    }
                    if (!f.canAccess(instance)) {
                        f.setAccessible(true);
                    }
                    final var type = f.getType();
                    try {
                        if (type.isPrimitive()) {
                            if (type == boolean.class) {
                                f.setBoolean(instance, false);
                            } else if (type == char.class) {
                                f.setChar(instance, '\0');
                            } else {
                                f.set(instance, 0);
                            }
                        } else {
                            f.set(instance, null);
                        }
                    } catch (final IllegalAccessException iae) {
                        throw new RuntimeException("failed to reset " + f + " on " + instance, iae);
                    }
                }
        );
        return instance;
    }

    static <T> Map<String, Object> getValues(@Nonnull final Class<T> type, @Nonnull final T object)
            throws IntrospectionException, ReflectiveOperationException {
        Objects.requireNonNull(type, "type is null");
        Objects.requireNonNull(object, "object is null");
        final var values = new HashMap<String, Object>();
        for (Class<?> c = type; c != null && type.isAssignableFrom(c); c = c.getSuperclass()) {
            final var beanInfo = Introspector.getBeanInfo(c, Introspector.USE_ALL_BEANINFO);
            final var descriptors = beanInfo.getPropertyDescriptors();
            for (final var descriptor : descriptors) {
                final var name = descriptor.getName();
                final var getter = descriptor.getReadMethod();
                if (getter == null) {
                    continue;
                }
                if (!getter.canAccess(object)) {
                    getter.setAccessible(true);
                }
                final var value = getter.invoke(object);
                values.putIfAbsent(name, value);
            }
            for (final var field : c.getDeclaredFields()) {
                if (field.isSynthetic()) {
                    continue;
                }
                final var modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers)) {
                    continue;
                }
                final var name = field.getName();
                if (!field.canAccess(object)) {
                    field.setAccessible(true);
                }
                final var value = field.get(object);
                values.putIfAbsent(name, value);
            }
        }
        return values;
    }

    private static <T> Map<String, Object> getValuesHelper(@Nonnull final Class<T> type, @Nonnull final Object object)
            throws IntrospectionException, ReflectiveOperationException {
        return getValues(type, type.cast(object));
    }

    static Map<String, Object> getValues(@Nonnull final Object object)
            throws IntrospectionException, ReflectiveOperationException {
        return getValuesHelper(object.getClass(), object);
    }

    static <T> void setValues(@Nonnull final Class<T> type, @Nonnull final T object,
                              @Nonnull Map<String, Object> values)
            throws IntrospectionException, ReflectiveOperationException {
        Objects.requireNonNull(type, "type is null");
        Objects.requireNonNull(object, "object is null");
        values = new HashMap<>(Objects.requireNonNull(values, "values is null"));
        for (Class<?> c = type; c != null && type.isAssignableFrom(c); c = c.getSuperclass()) {
            final var beanInfo = Introspector.getBeanInfo(c, Introspector.USE_ALL_BEANINFO);
            final var descriptors = beanInfo.getPropertyDescriptors();
            for (final var descriptor : descriptors) {
                final var name = descriptor.getName();
                final var setter = descriptor.getWriteMethod();
                if (setter == null) {
                    continue;
                }
                if (values.containsKey(name)) {
                    continue;
                }
                if (!setter.canAccess(object)) {
                    setter.setAccessible(true);
                }
                setter.invoke(object, values.remove(name));
            }
            for (final var field : c.getDeclaredFields()) {
                if (field.isSynthetic()) {
                    continue;
                }
                final var modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers)) {
                    continue;
                }
                final var name = field.getName();
                if (!values.containsKey(name)) {
                    continue;
                }
                if (!field.canAccess(object)) {
                    field.setAccessible(true);
                }
                field.set(object, values.remove(name));
            }
        }
    }

    private static <T> void setValuesHelper(@Nonnull final Class<T> type, @Nonnull final Object object,
                                            @Nonnull final Map<String, Object> values)
            throws IntrospectionException, ReflectiveOperationException {
        setValues(type, type.cast(object), values);
    }

    static void setValues(@Nonnull final Object object, @Nonnull final Map<String, Object> values)
            throws IntrospectionException, ReflectiveOperationException {
        setValuesHelper(object.getClass(), object, values);
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    private ___ReflectionUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
