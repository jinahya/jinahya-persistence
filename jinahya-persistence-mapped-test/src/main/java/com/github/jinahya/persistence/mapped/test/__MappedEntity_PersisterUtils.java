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

import com.github.jinahya.persistence.mapped.__MappedEntity;
import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityManager;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public final class __MappedEntity_PersisterUtils {

    /**
     * Returns a persisted instance of the specified entity class.
     *
     * @param entityManager an entity manager.
     * @param entityClass   the entity class.
     * @param <ENTITY>      entity type parameter
     * @return a new persisted instance of the {@code entityClass}.
     * @see ___PersisterUtils#newPersistedInstanceOf(EntityManager, Class)
     */
    @Nonnull
    public static <ENTITY extends __MappedEntity<?>>
    ENTITY newPersistedInstanceOf(@Nonnull final EntityManager entityManager,
                                  @Nonnull final Class<ENTITY> entityClass) {
        return ___PersisterUtils.newPersistedInstanceOf(entityManager, entityClass);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private __MappedEntity_PersisterUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
