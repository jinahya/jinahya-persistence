package com.github.jinahya.persistence.mapped;

/*-
 * #%L
 * jinahya-persistence-mapped
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

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Objects;

@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
final class __MappedUtils {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    static <TARGET extends __Mapped>
    void setPropertiesFrom(@Nonnull final TARGET target,
                           @Nonnull final __MappedBuilder<?, ? extends TARGET> builder)
            throws IntrospectionException {
        Objects.requireNonNull(builder, "builder is null");
        Objects.requireNonNull(target, "target is null");
        final var builderValues = new HashMap<String, Object>();
        for (Class<?> c = builder.getClass();
             c != null && __MappedBuilder.class.isAssignableFrom(c);
             c = c.getSuperclass()) {
            for (final var field : c.getDeclaredFields()) {
                if (!field.isSynthetic()) {
                    continue;
                }
                final var name = field.getName();
                if (!field.canAccess(builder)) {
                    field.setAccessible(true);
                }
                final Object value;
                try {
                    value = field.get(builder);
                } catch (final IllegalAccessException iae) {
                    logger.log(System.Logger.Level.WARNING, "failed to get " + field + " from " + builder, iae);
                    continue;
                }
                builderValues.put(name, value);
            }
        }
        for (Class<?> c = target.getClass(); c != null && __Mapped.class.isAssignableFrom(c); c = c.getSuperclass()) {
            final var beanInfo = Introspector.getBeanInfo(c, Introspector.USE_ALL_BEANINFO);
            final var descriptors = beanInfo.getPropertyDescriptors();
            for (final var descriptor : descriptors) {
                final var name = descriptor.getName();
                final Method getter;
                try {
                    getter = builder.getClass().getDeclaredMethod(name);
                } catch (final NoSuchMethodException nsme) {
                    continue;
                }
                final var type = descriptor.getPropertyType();
                if (getter.getReturnType() != type) {
                    continue;
                }
                if (!getter.canAccess(builder)) {
                    getter.setAccessible(false);
                }
                final Object value;
                try {
                    value = getter.invoke(builder);
                } catch (final ReflectiveOperationException roe) {
                    continue;
                }
                final var setter = descriptor.getWriteMethod();
                if (setter != null) {
                    if (!setter.canAccess(target)) {
                        setter.setAccessible(true);
                    }
                    try {
                        setter.invoke(target, value);
                        continue;
                    } catch (final ReflectiveOperationException roe) {
                        continue;
                    }
                }
                final Field field;
                try {
                    field = c.getDeclaredField(name);
                } catch (final NoSuchFieldException nsfe) {
                    continue;
                }
                if (field.getType() != type) {
                    continue;
                }
                if (!field.canAccess(target)) {
                    field.setAccessible(true);
                }
                try {
                    field.set(target, value);
                    continue;
                } catch (final IllegalAccessException iae) {
                    continue;
                }
            }
        }
    }

    private __MappedUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
