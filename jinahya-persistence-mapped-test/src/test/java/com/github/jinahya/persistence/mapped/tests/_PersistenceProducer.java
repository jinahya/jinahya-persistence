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

import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

class _PersistenceProducer extends __PersistenceProducer {

    _PersistenceProducer() {
        super();
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Unit__
    @Produces
    @Override
    protected EntityManagerFactory produceUnitEntityManagerFactory() {
        return super.produceUnitEntityManagerFactory();
    }

    @Override
    protected void disposeUnitEntityManagerFactory(
            @Unit__ @Disposes final EntityManagerFactory entityManagerFactory) {
        super.disposeUnitEntityManagerFactory(entityManagerFactory);
    }

    @Unit__
    @Produces
    @Override
    protected EntityManager produceUnitEntityManager(@Unit__ final EntityManagerFactory entityManagerFactory) {
        return super.produceUnitEntityManager(entityManagerFactory);
    }

    @Override
    protected void disposeUnitEntityManager(@Unit__ @Disposes final EntityManager entityManager) {
        super.disposeUnitEntityManager(entityManager);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Integration__
    @Produces
    @Override
    protected EntityManagerFactory produceIntegrationEntityManagerFactory() {
        return super.produceIntegrationEntityManagerFactory();
    }

    @Override
    protected void disposeIntegrationEntityManagerFactory(
            @Integration__ @Disposes final EntityManagerFactory entityManagerFactory) {
        super.disposeIntegrationEntityManagerFactory(entityManagerFactory);
    }

    @Integration__
    @Produces
    @Override
    protected EntityManager produceIntegrationEntityManager(
            @Integration__ final EntityManagerFactory entityManagerFactory) {
        return super.produceIntegrationEntityManager(entityManagerFactory);
    }

    @Override
    protected void disposeIntegrationEntityManager(@Integration__ @Disposes final EntityManager entityManager) {
        super.disposeIntegrationEntityManager(entityManager);
    }
}
