package com.github.jinahya.persistence.mapped.tests;

import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

class _PersistenceProducer extends __PersistenceProducer {

    _PersistenceProducer() {
        super("testPU");
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Produces
    @Override
    protected EntityManagerFactory produceEntityManagerFactory() {
        return super.produceEntityManagerFactory();
    }

    @Override
    protected void disposeEntityManagerFactory(@Disposes final EntityManagerFactory entityManagerFactory) {
        super.disposeEntityManagerFactory(entityManagerFactory);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Produces
    @Override
    protected EntityManager produceEntityManager(final EntityManagerFactory entityManagerFactory) {
        return super.produceEntityManager(entityManagerFactory);
    }

    @Override
    protected void disposeEntityManager(@Disposes final EntityManager entityManager) {
        super.disposeEntityManager(entityManager);
    }
}
