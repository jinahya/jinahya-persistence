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
import jakarta.annotation.Nullable;

import java.util.Arrays;
import java.util.Objects;

/**
 * Test utilities for the {@link java.lang} package.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
final class ___JavaLang_TestUtils {

    /**
     * Finds a sibling class of the specified type, which (optionally) extends the specified supertype and has any of
     * the specified postfixes.
     *
     * @param type      the type.
     * @param supertype the supertype; {@code null} to ignore.
     * @param postfixes the postfix candidates.
     * @return the sibling class meets given conditions; {@code null} when not found.
     */
    @Nullable
    static Class<?> siblingClassForPostfix(@Nonnull final Class<?> type,
                                           @Nullable final Class<?> supertype,
                                           @Nonnull final String... postfixes) {
        Objects.requireNonNull(type, "type is null");
        if (Objects.requireNonNull(postfixes, "postfixes is null").length == 0) {
            throw new IllegalArgumentException("postfixes is empty");
        }
        final String typeName = type.getName();
        return Arrays.stream(postfixes)
                .filter(Objects::nonNull)
                .map(String::strip)
                .filter(v -> !v.isBlank())
                .<String>map((String postfix) -> typeName + postfix)
                .map((String className) -> {
                    try {
                        return Class.forName(className);
                    } catch (final ClassNotFoundException cnfe) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .filter(c -> supertype == null || supertype.isAssignableFrom(c))
                .findAny()
                .orElse(null);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private ___JavaLang_TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
