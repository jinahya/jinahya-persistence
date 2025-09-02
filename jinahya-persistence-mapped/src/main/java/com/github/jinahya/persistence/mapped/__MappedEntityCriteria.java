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
import jakarta.persistence.NoResultException;
import jakarta.persistence.metamodel.SingularAttribute;

import java.util.Objects;
import java.util.Optional;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S114",  // Interface names should comply with a naming convention
        "java:S119" // Type parameter names should comply with a naming convention
})
public interface __MappedEntityCriteria<ENTITY extends __MappedEntity<ID>, ID> extends __MappedCriteria<ENTITY> {

    static <T extends __MappedEntity<U>, U> Optional<T> findById(
            @Nonnull final EntityManager entityManager,
            @Nonnull final Class<T> entityClass,
            @Nonnull final SingularAttribute<? super T, U> idAttribute,
            @Nonnull final U idValue) {
        Objects.requireNonNull(entityManager, "entityManager is null");
        Objects.requireNonNull(entityClass, "entityClass is null");
        Objects.requireNonNull(idValue, "idValue is null");
        final var builder = entityManager.getCriteriaBuilder();
        final var criteria = builder.createQuery(entityClass);
        final var root = criteria.from(entityClass);
        criteria.select(root);
        criteria.where(builder.equal(root.get(idAttribute), idValue));
        final var typed = entityManager.createQuery(criteria);
        try {
            return Optional.of(typed.getSingleResult());
        } catch (final NoResultException nre) {
            return Optional.empty();
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    ENTITY findById(ID id);
}
