package com.github.jinahya.persistence.mapped.tests;

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
    protected EntityManager produceIntegrationEntityManager(@Integration__ final EntityManagerFactory entityManagerFactory) {
        return super.produceIntegrationEntityManager(entityManagerFactory);
    }

    @Override
    protected void disposeIntegrationEntityManager(@Integration__ @Disposes final EntityManager entityManager) {
        super.disposeIntegrationEntityManager(entityManager);
    }
}
