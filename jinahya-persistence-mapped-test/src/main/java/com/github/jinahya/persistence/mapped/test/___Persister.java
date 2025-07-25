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
import jakarta.persistence.EntityManager;

import java.util.Objects;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S3011" // Reflection should not be used to increase accessibility of classes, methods, or fields
})
public abstract class ___Persister<ENTITY> {

    protected ___Persister(@Nonnull final Class<ENTITY> entityClass) {
        super();
        this.entityClass = Objects.requireNonNull(entityClass, "entityClass is null");
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Persists specified instance of {@link ENTITY} using specified entity manager.
     *
     * @param entityManager  the entity manager to use.
     * @param entityInstance the instance of {@link ENTITY} to persist.
     * @return given {@code entityInstance}.
     * @see ___RandomizerUtils#newRandomizedInstanceOf(Class)
     */
    public ENTITY persist(final EntityManager entityManager, final ENTITY entityInstance) {
        Objects.requireNonNull(entityManager, "entityManager is null");
        Objects.requireNonNull(entityInstance, "entityInstance is null");
        ___JakartaValidationTestUtils.requireValid(entityInstance);
        entityManager.persist(entityInstance);
        entityManager.flush(); // required?
        ___JakartaValidationTestUtils.requireValid(entityInstance);
        return entityInstance;
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The class of {@link ENTITY}.
     */
    protected final Class<ENTITY> entityClass;
}
