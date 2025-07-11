package com.github.jinahya.persistence.mapped.tests;

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
