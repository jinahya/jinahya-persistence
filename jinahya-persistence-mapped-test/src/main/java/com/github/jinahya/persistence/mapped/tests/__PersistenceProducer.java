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

import jakarta.inject.Qualifier;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __PersistenceProducer {

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD})
    public @interface Unit__ {

    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD})
    public @interface Integration__ {

    }

    public static final String PERSISTENCE_UNIT_NAME_UNIT_PU = "unitPU";

    public static final String PERSISTENCE_UNIT_NAME_INTEGRATION_PU = "integrationPU";

    // -----------------------------------------------------------------------------------------------------------------
    protected __PersistenceProducer() {
        super();
    }

    // ------------------------------------------------------------------------------------------------------------ unit
    @Unit__
    protected EntityManagerFactory produceUnitEntityManagerFactory() {
        return Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME_UNIT_PU);
    }

    protected void disposeUnitEntityManagerFactory(@Unit__ final EntityManagerFactory entityManagerFactory) {
        entityManagerFactory.close();
    }

    @Unit__
    protected EntityManager produceUnitEntityManager(final EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory.createEntityManager();
    }

    protected void disposeUnitEntityManager(@Unit__ final EntityManager entityManager) {
        entityManager.close();
    }

    // ----------------------------------------------------------------------------------------------------- integration
    @Integration__
    protected EntityManagerFactory produceIntegrationEntityManagerFactory() {
        return Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME_INTEGRATION_PU);
    }

    protected void disposeIntegrationEntityManagerFactory(
            @Integration__ final EntityManagerFactory entityManagerFactory) {
        entityManagerFactory.close();
    }

    @Integration__
    protected EntityManager produceIntegrationEntityManager(final EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory.createEntityManager();
    }

    protected void disposeIntegrationEntityManager(@Integration__ final EntityManager entityManager) {
        entityManager.close();
    }
}
