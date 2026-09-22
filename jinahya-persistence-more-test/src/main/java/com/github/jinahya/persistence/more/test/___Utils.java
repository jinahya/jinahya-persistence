package com.github.jinahya.persistence.more.test;

/*-
 * #%L
 * jinahya-persistence-more-test
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

import java.util.Objects;

/**
 * Utilities shared by the abstract test classes of this package and of the sibling {@code .test}
 * packages mirroring what {@code jinahya-persistence-more} lays out.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S3011" // Reflection should not be used to increase accessibility of classes, methods, or fields
})
public final class ___Utils {

    /**
     * Creates a new instance of the specified class, using its no-argument constructor.
     *
     * @param clazz the class to instantiate.
     * @param <T>   instance type parameter
     * @return a new instance of the {@code clazz}.
     * @throws RuntimeException when the {@code clazz} declares no accessible no-argument constructor, or when the
     *                          constructor itself fails.
     * @implNote This replaces {@code org.junit.platform.commons.util.ReflectionUtils.newInstance}, which JUnit
     *         marks {@code @API(status = INTERNAL)}: a published library has no business binding to a testing
     *         framework's internals, where a patch release is free to move the method out from under it.
     */
    public static <T> T newInstance(final Class<T> clazz) {
        Objects.requireNonNull(clazz, "clazz is null");
        final java.lang.reflect.Constructor<T> constructor;
        try {
            constructor = clazz.getDeclaredConstructor();
        } catch (final NoSuchMethodException nsme) {
            throw new RuntimeException("no no-arg constructor on " + clazz, nsme);
        }
        if (!constructor.canAccess(null)) {
            constructor.setAccessible(true);
        }
        try {
            return constructor.newInstance();
        } catch (final ReflectiveOperationException roe) {
            throw new RuntimeException("failed to instantiate " + clazz, roe);
        }
    }

    /**
     * Creates a new instance, which is not allowed.
     */
    private ___Utils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
