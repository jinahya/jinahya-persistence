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
import jakarta.inject.Qualifier;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A class for providing persistence resources.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public class __PersistenceProducer {

    @Retention(RetentionPolicy.RUNTIME)
    @Target({
            ElementType.ANNOTATION_TYPE,
            ElementType.PARAMETER,
            ElementType.FIELD,
            ElementType.METHOD,
            ElementType.TYPE
    })
    @interface Persistence__ {

    }

    /**
     * A qualifier for the {@value __PersistenceProducer#PERSISTENCE_UNIT_NAME_UNIT_PU} persistence unit.
     */
    @Persistence__
    @Qualifier
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
//    @Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
    public @interface Unit__ {

    }

    /**
     * A qualifier for the {@value __PersistenceProducer#PERSISTENCE_UNIT_NAME_INTEGRATION_PU} persistence unit.
     */
    @Persistence__
    @Qualifier
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
//    @Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
    public @interface Integration__ {

    }

    static final String PERSISTENCE_UNIT_NAME_UNIT_PU = "unitPU";

    static final String PERSISTENCE_UNIT_NAME_INTEGRATION_PU = "integrationPU";

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance.
     */
    protected __PersistenceProducer() {
        super();
    }

    // ------------------------------------------------------------------------------------------------------------ unit

    /**
     * Produces an entity manager factory for the {@value #PERSISTENCE_UNIT_NAME_UNIT_PU} persistence unit.
     *
     * @return an entity manager factory for the {@value #PERSISTENCE_UNIT_NAME_UNIT_PU} persistence unit
     * @see #disposeUnitEntityManagerFactory(EntityManagerFactory)
     * @see #produceUnitEntityManager(EntityManagerFactory)
     */
    @Unit__
    @Produces
    public EntityManagerFactory produceUnitEntityManagerFactory() {
        return Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME_UNIT_PU);
    }

    /**
     * Disposes the specified entity manager factory produced by {@link #produceUnitEntityManagerFactory()} method.
     *
     * @param entityManagerFactory the entity manager factory to dispose.
     * @see #produceUnitEntityManagerFactory()
     */
    public void disposeUnitEntityManagerFactory(@Unit__ @Disposes final EntityManagerFactory entityManagerFactory) {
        entityManagerFactory.close();
    }

    /**
     * Produces an entity manager for the {@value #PERSISTENCE_UNIT_NAME_UNIT_PU} persistence unit.
     *
     * @return an entity manager for the {@value #PERSISTENCE_UNIT_NAME_UNIT_PU} persistence unit
     * @see #disposeUnitEntityManager(EntityManager)
     * @see #produceUnitEntityManagerFactory()
     */
    @Unit__
    @Produces
    public EntityManager produceUnitEntityManager(@Unit__ final EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory.createEntityManager();
    }

    /**
     * Disposes the specified entity manager produced by {@link #produceUnitEntityManager(EntityManagerFactory)}
     * method.
     *
     * @param entityManager the entity manager to dispose.
     * @see #produceUnitEntityManager(EntityManagerFactory)
     */
    public void disposeUnitEntityManager(@Unit__ @Disposes final EntityManager entityManager) {
        entityManager.close();
    }

    // ----------------------------------------------------------------------------------------------------- integration
    @Integration__
    @Produces
    public EntityManagerFactory produceIntegrationEntityManagerFactory() {
        return Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME_INTEGRATION_PU);
    }

    public void disposeIntegrationEntityManagerFactory(
            @Integration__ @Disposes final EntityManagerFactory entityManagerFactory) {
        entityManagerFactory.close();
    }

    @Integration__
    @Produces
    public EntityManager produceIntegrationEntityManager(
            @Integration__ final EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory.createEntityManager();
    }

    public void disposeIntegrationEntityManager(@Integration__ @Disposes final EntityManager entityManager) {
        entityManager.close();
    }
}
