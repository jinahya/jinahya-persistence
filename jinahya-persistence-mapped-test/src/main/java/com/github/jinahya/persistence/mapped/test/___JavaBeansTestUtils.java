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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Test utilities for the {@link java.beans} package.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class ___JavaBeansTestUtils {

    public static <R> R applyPropertyDescriptors(@Nonnull final Class<?> clazz,
                                                 final Function<? super PropertyDescriptor[], ? extends R> function) {
        Objects.requireNonNull(clazz, "clazz is null");
        Objects.requireNonNull(function, "function is null");
        final BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(clazz);
        } catch (final IntrospectionException ie) {
            throw new RuntimeException("failed to get bean info of " + clazz, ie);
        }
        return function.apply(beanInfo.getPropertyDescriptors());
    }

    public static void acceptPropertyDescriptors(@Nonnull final Class<?> clazz,
                                                 @Nonnull final Consumer<? super PropertyDescriptor[]> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        applyPropertyDescriptors(clazz, v -> {
            consumer.accept(v);
            return null;
        });
    }

    public static void acceptEachPropertyDescriptor(@Nonnull final Class<?> clazz,
                                                    @Nonnull final Consumer<? super PropertyDescriptor> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        acceptPropertyDescriptors(clazz, v -> {
            Arrays.stream(v).forEach(consumer);
        });
    }

    // -----------------------------------------------------------------------------------------------------------------
    private ___JavaBeansTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
