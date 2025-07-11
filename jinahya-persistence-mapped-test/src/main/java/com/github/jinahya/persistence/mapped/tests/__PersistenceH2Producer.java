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

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.Objects;

@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __PersistenceH2Producer {

    // -----------------------------------------------------------------------------------------------------------------
    protected __PersistenceH2Producer(final String persistenceUnitName) {
        super();
        this.persistenceUnitName = Objects.requireNonNull(persistenceUnitName, "persistenceUnitName is null");
    }

    // -----------------------------------------------------------------------------------------------------------------
    protected EntityManagerFactory produceEntityManagerFactory() {
        return Persistence.createEntityManagerFactory(persistenceUnitName);
    }

    protected void disposeEntityManagerFactory(final EntityManagerFactory entityManagerFactory) {
        entityManagerFactory.close();
    }

    // -----------------------------------------------------------------------------------------------------------------
    protected EntityManager produceEntityManager(final EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory.createEntityManager();
    }

    protected void disposeEntityManager(final EntityManager entityManager) {
        entityManager.close();
    }

    // -----------------------------------------------------------------------------------------------------------------
    protected final String persistenceUnitName;
}
