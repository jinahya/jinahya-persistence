package com.github.jinahya.persistence.mapped.tests;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.Objects;

@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __PersistenceProducer {

    // -----------------------------------------------------------------------------------------------------------------
    protected __PersistenceProducer(final String persistenceUnitName) {
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
