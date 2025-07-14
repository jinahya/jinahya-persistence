package com.github.jinahya.persistence.mapped.tests;

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

import java.util.Objects;
import java.util.Optional;

/**
 * Utilities for {@link ___Randomizer}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ___Randomizer
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class ___RandomizerUtils {

    static Optional<Class<?>> getRandomizerClassOf(final Class<?> type) {
        return Optional.ofNullable(
                ___JavaLangTestUtils.forAnyPostfixes(type, ___Randomizer.class, "Randomizer", "_Randomizer")
        );
    }

    @SuppressWarnings({
            "unchecked"
    })
    static <T> Optional<___Randomizer<T>> newRandomizerInstanceOf(final Class<T> type) {
        return getRandomizerClassOf(type)
                .map(___JavaLangReflectTestUtils::newInstanceOf)
                .map(i -> (___Randomizer<T>) i)
                ;
    }

    public static <T> Optional<T> newRandomizedInstanceOf(final Class<T> type) {
        Objects.requireNonNull(type, "type is null");
        return newRandomizerInstanceOf(type)
                .map(___Randomizer::get);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private ___RandomizerUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
