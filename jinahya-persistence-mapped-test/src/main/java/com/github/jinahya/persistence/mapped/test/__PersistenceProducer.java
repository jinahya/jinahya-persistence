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
    @interface __PU {

    }

    /**
     * A qualifier for the {@value __PersistenceProducerConstants#PERSISTENCE_UNIT_NAME_TEST_PU} persistence unit.
     */
    @__PU
    @Qualifier
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
//    @Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
    public @interface __testPU {

    }

    /**
     * A qualifier for the {@value __PersistenceProducerConstants#PERSISTENCE_UNIT_NAME_IT_PU} persistence unit.
     */
    @__PU
    @Qualifier
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
//    @Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
    public @interface __itPU {

    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance.
     */
    protected __PersistenceProducer() {
        super();
    }

    // ---------------------------------------------------------------------------------------------------------- testPU

    /**
     * Produces an entity manager factory for the {@value __PersistenceProducerConstants#PERSISTENCE_UNIT_NAME_TEST_PU}
     * persistence unit.
     *
     * @return an entity manager factory for the {@value __PersistenceProducerConstants#PERSISTENCE_UNIT_NAME_TEST_PU}
     *         persistence unit
     * @see #disposeTestEntityManagerFactory(EntityManagerFactory)
     * @see #produceTestEntityManager(EntityManagerFactory)
     */
    @__testPU
    @Produces
    public EntityManagerFactory produceTestEntityManagerFactory() {
        return Persistence.createEntityManagerFactory(__PersistenceProducerConstants.PERSISTENCE_UNIT_NAME_TEST_PU);
    }

    /**
     * Disposes the specified entity manager factory produced by {@link #produceTestEntityManagerFactory()} method.
     *
     * @param entityManagerFactory the entity manager factory to dispose.
     * @see #produceTestEntityManagerFactory()
     */
    public void disposeTestEntityManagerFactory(@__testPU @Disposes final EntityManagerFactory entityManagerFactory) {
        entityManagerFactory.close();
    }

    /**
     * Produces an entity manager for the {@value __PersistenceProducerConstants#PERSISTENCE_UNIT_NAME_IT_PU} persistence unit.
     *
     * @return an entity manager for the {@value __PersistenceProducerConstants#PERSISTENCE_UNIT_NAME_IT_PU} persistence unit
     * @see #disposeTestEntityManager(EntityManager)
     * @see #produceTestEntityManagerFactory()
     */
    @Deprecated(forRemoval = true)
    @__testPU
    @Produces
    public EntityManager produceTestEntityManager(@__testPU final EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory.createEntityManager();
    }

    /**
     * Disposes the specified entity manager produced by {@link #produceTestEntityManager(EntityManagerFactory)}
     * method.
     *
     * @param entityManager the entity manager to dispose.
     * @see #produceTestEntityManager(EntityManagerFactory)
     */
    @Deprecated(forRemoval = true)
    public void disposeTestEntityManager(@__testPU @Disposes final EntityManager entityManager) {
        entityManager.close();
    }

    // ------------------------------------------------------------------------------------------------------------ itPU

    /**
     * Produces an entity manager factory for the {@value __PersistenceProducerConstants#PERSISTENCE_UNIT_NAME_IT_PU}.
     *
     * @return an entity manager factory for the {@value __PersistenceProducerConstants#PERSISTENCE_UNIT_NAME_IT_PU}.
     */
    @__itPU
    @Produces
    public EntityManagerFactory produceItEntityManagerFactory() {
        return Persistence.createEntityManagerFactory(__PersistenceProducerConstants.PERSISTENCE_UNIT_NAME_IT_PU);
    }

    /**
     * Disposes the specified entity manager factory produced by {@link #produceItEntityManagerFactory()} method.
     *
     * @param entityManagerFactory the entity manager factory to dispose.
     */
    public void disposeItEntityManagerFactory(
            @__itPU @Disposes final EntityManagerFactory entityManagerFactory) {
        entityManagerFactory.close();
    }

    @Deprecated(forRemoval = true)
    @__itPU
    @Produces
    public EntityManager produceItEntityManager(@__itPU final EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory.createEntityManager();
    }

    @Deprecated(forRemoval = true)
    public void disposeItEntityManager(@__itPU @Disposes final EntityManager entityManager) {
        entityManager.close();
    }
}
