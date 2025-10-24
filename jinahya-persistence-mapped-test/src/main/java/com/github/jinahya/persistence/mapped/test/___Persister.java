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

/**
 * An abstract class for persisting a specific class.
 *
 * @param <T> entity type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S3011" // Reflection should not be used to increase accessibility of classes, methods, or fields
})
public abstract class ___Persister<T> {

    /**
     * Creates a new instance for persisting specified class.
     *
     * @param type the class to persist.
     */
    protected ___Persister(@Nonnull final Class<T> type) {
        super();
        this.type = Objects.requireNonNull(type, "type is null");
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Persists specified instance of {@link T} using specified entity manager.
     *
     * @param entityManager  the entity manager to use.
     * @param entityInstance the instance of {@link T} to persist.
     * @see ___RandomizerUtils#newRandomizedInstanceOf(Class)
     */
    public void persist(@Nonnull final EntityManager entityManager, @Nonnull final T entityInstance) {
        Objects.requireNonNull(entityManager, "entityManager is null");
        Objects.requireNonNull(entityInstance, "entityInstance is null");
//        ___JakartaValidation_TestUtils.requireValid(entityInstance);
        entityManager.persist(entityInstance);
        entityManager.flush(); // required?
        ___JakartaValidation_TestUtils.requireValid(entityInstance);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The entity class to persist.
     */
    protected final Class<T> type;
}
