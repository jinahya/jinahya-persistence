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
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.invoke.MethodHandles;

/**
 * A class for providing persistence resources.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public class __PersistenceProducer {

    @Retention(RetentionPolicy.CLASS)
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
     * A qualifier for the {@value __PersistenceProducer_TestConstants#PERSISTENCE_UNIT_NAME_TEST_PU} persistence unit.
     */
    @Qualifier
    @Documented
    @__PU
    @Retention(RetentionPolicy.RUNTIME)
    public @interface __testPU {

    }

    /**
     * A qualifier for the {@value __PersistenceProducer_TestConstants#PERSISTENCE_UNIT_NAME_IT_PU} persistence unit.
     */
    @__PU
    @Qualifier
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    public @interface __itPU {

    }

    // -----------------------------------------------------------------------------------------------------------------
    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance.
     */
    protected __PersistenceProducer() {
        super();
    }

    // ---------------------------------------------------------------------------------------------------------- testPU

    /**
     * Produces an entity manager factory for the
     * {@value __PersistenceProducer_TestConstants#PERSISTENCE_UNIT_NAME_TEST_PU} persistence unit.
     *
     * @return an entity manager factory for the
     *         {@value __PersistenceProducer_TestConstants#PERSISTENCE_UNIT_NAME_TEST_PU} persistence unit
     * @see #disposeTestEntityManagerFactory(EntityManagerFactory)
     */
    @__testPU
    @Produces
    public EntityManagerFactory produceTestEntityManagerFactory() {
        return Persistence.createEntityManagerFactory(
                __PersistenceProducer_TestConstants.PERSISTENCE_UNIT_NAME_TEST_PU
        );
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

    // ------------------------------------------------------------------------------------------------------------ itPU

    /**
     * Produces an entity manager factory for the
     * {@value __PersistenceProducer_TestConstants#PERSISTENCE_UNIT_NAME_IT_PU}.
     *
     * @return an entity manager factory for the
     *         {@value __PersistenceProducer_TestConstants#PERSISTENCE_UNIT_NAME_IT_PU}.
     * @see __PersistenceProducer_TestUtils#assertSchemagenDatabaseActionNone(EntityManagerFactory)
     * @see __PersistenceProducer_TestUtils#assertEclipselinkDdlGenerationNone(EntityManagerFactory)
     * @see __PersistenceProducer_TestUtils#assertHibernateHbm2ddlAutoNone(EntityManagerFactory)
     */
    @__itPU
    @Produces
    public EntityManagerFactory produceItEntityManagerFactory() {
        final var entityManagerFactory = Persistence.createEntityManagerFactory(
                __PersistenceProducer_TestConstants.PERSISTENCE_UNIT_NAME_IT_PU
        );
        if (false) {
            __PersistenceProducer_TestUtils.assertSchemagenDatabaseActionNone(entityManagerFactory);
            __PersistenceProducer_TestUtils.assertEclipselinkDdlGenerationNone(entityManagerFactory);
            __PersistenceProducer_TestUtils.assertHibernateHbm2ddlAutoNone(entityManagerFactory);
        }
        logger.log(
                System.Logger.Level.DEBUG,
                "producing entity manager factory: {0} for {1}",
                entityManagerFactory,
                __itPU.class
        );
        return entityManagerFactory;
    }

    /**
     * Disposes the specified entity manager factory produced by {@link #produceItEntityManagerFactory()} method.
     *
     * @param entityManagerFactory the entity manager factory to dispose.
     */
    public void disposeItEntityManagerFactory(
            @__itPU @Disposes final EntityManagerFactory entityManagerFactory) {
        logger.log(
                System.Logger.Level.DEBUG,
                "disposing entity manager factory: {0}, produced for {1{",
                entityManagerFactory,
                __itPU.class
        );
        entityManagerFactory.close();
    }
}
