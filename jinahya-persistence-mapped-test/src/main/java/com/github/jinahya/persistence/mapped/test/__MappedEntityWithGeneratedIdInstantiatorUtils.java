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

import com.github.jinahya.persistence.mapped.__MappedEntityWithGeneratedId;
import jakarta.annotation.Nonnull;

import java.util.Optional;

@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class __MappedEntityWithGeneratedIdInstantiatorUtils {

    /**
     * Returns an initialized instance of the specified entity class.
     *
     * @param entityClass the entity class to be initialized.
     * @param <ENTITY>    entity type parameter
     * @return an optional of the initialized entity; {@link Optional#empty() empty} when no initializer found.
     */
    @Nonnull
    public static <ENTITY extends __MappedEntityWithGeneratedId<ENTITY, ?>>
    Optional<ENTITY> newInstanceOf(@Nonnull final Class<ENTITY> entityClass) {
        return __MappedEntityInstantiatorUtils.newInstanceOf(entityClass);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private __MappedEntityWithGeneratedIdInstantiatorUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
