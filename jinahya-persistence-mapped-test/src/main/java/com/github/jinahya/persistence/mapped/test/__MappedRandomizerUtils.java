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

import com.github.jinahya.persistence.mapped.__Mapped;
import jakarta.annotation.Nonnull;

import java.util.Objects;
import java.util.Optional;

/**
 * Utilities for {@link __MappedEntityRandomizer}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class __MappedRandomizerUtils {

    /**
     * Returns a randomized instance of the specified entity class.
     *
     * @param mappedClass the entity class to be randomized.
     * @param <MAPPED>    entity type parameter
     * @return an optional of a randomized instance of {@code mappedClass}; {@link Optional#empty() empty} when no
     *         randomizer found.
     * @see ___RandomizerUtils#newRandomizedInstanceOf(Class)
     */
    @Nonnull
    @SuppressWarnings({
            "java:S119" // Type parameter names should comply with a naming convention
    })
    public static <MAPPED extends __Mapped>
    Optional<MAPPED> newRandomizedInstanceOf(@Nonnull final Class<MAPPED> mappedClass) {
        Objects.requireNonNull(mappedClass, "mappedClass is null");
        return ___RandomizerUtils.newRandomizedInstanceOf(mappedClass);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private __MappedRandomizerUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
