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

import java.lang.invoke.MethodHandles;
import java.util.Objects;

/**
 * An abstract class for persisting a specific class.
 *
 * @param <ENTITY> entity type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S3011" // Reflection should not be used to increase accessibility of classes, methods, or fields
})
public abstract class ___Persister<ENTITY> {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns a new persisted instance of the specified entity class.
     *
     * @param entityManager an entity manager.
     * @param entityClass   the entity class.
     * @param <ENTITY>      entity type parameter
     * @return a new persisted instance of the {@code entityClass}.
     */
    protected static <ENTITY> ENTITY newPersistedInstanceOf(final @Nonnull EntityManager entityManager,
                                                            final @Nonnull Class<ENTITY> entityClass) {
        Objects.requireNonNull(entityManager, "entityManager is null");
        Objects.requireNonNull(entityClass, "entityClass is null");
        return ___PersisterUtils.newPersistedInstanceOf(entityManager, entityClass);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance for persisting specified class.
     *
     * @param entityClass the class to persist.
     */
    protected ___Persister(final @Nonnull Class<ENTITY> entityClass) {
        super();
        this.entityClass = Objects.requireNonNull(entityClass, "entityClass is null");
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Persists specified instance of {@link ENTITY} using specified entity manager.
     *
     * @param entityManager  the entity manager to use.
     * @param entityInstance the instance of {@link ENTITY} to persist.
     * @see ___RandomizerUtils#newRandomizedInstanceOf(Class)
     */
    public void persist(final @Nonnull EntityManager entityManager, final @Nonnull ENTITY entityInstance) {
        Objects.requireNonNull(entityManager, "entityManager is null");
        Objects.requireNonNull(entityInstance, "entityInstance is null");
        logger.log(System.Logger.Level.DEBUG, "persisting {0}", entityInstance);
        entityManager.persist(entityInstance);
//        entityManager.flush(); // required?
        ___JakartaValidation_TestUtils.requireValid(entityInstance);
    }

    // ----------------------------------------------------------------------------------------------------- entityClass

    /**
     * Returns a new persisted instance of the {@link #entityClass}.
     *
     * @param entityManager an entity manager.
     * @return a new persisted instance of the {@link #entityClass}.
     */
    protected ENTITY newPersistedEntityInstance(final @Nonnull EntityManager entityManager) {
        return newPersistedInstanceOf(entityManager, entityClass);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The entity class to persist.
     */
    protected final Class<ENTITY> entityClass;
}
