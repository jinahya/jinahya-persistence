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
import jakarta.persistence.EntityManager;

import java.util.Objects;
import java.util.Optional;

@SuppressWarnings({
        "java:S114", // Type parameter names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public interface __MappedEntityWithGeneratedIdentityCriteria<ENTITY extends __MappedEntityWithGeneratedIdentity>
        extends __MappedEntityWithGeneratedIdCriteria<ENTITY, Long> {

    static <T extends __MappedEntityWithGeneratedIdentity> Optional<T> findById(
            final @Nonnull EntityManager entityManager,
            final @Nonnull Class<T> entityClass,
            final @Nonnull Long idValue) {
        Objects.requireNonNull(entityManager, "entityManager is null");
        Objects.requireNonNull(entityClass, "entityClass is null");
        Objects.requireNonNull(idValue, "idValue is null");
        return __MappedEntityWithGeneratedIdCriteria.findById(
                entityManager,
                entityClass,
                __MappedEntityWithGeneratedIdentity_.id__,
                idValue
        );
    }
}
