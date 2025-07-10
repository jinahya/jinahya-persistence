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
    @Memory__
    @Produces
    @Override
    protected EntityManagerFactory produceMemoryEntityManagerFactory() {
        return super.produceMemoryEntityManagerFactory();
    }

    @Override
    protected void disposeMemoryEntityManagerFactory(
            @Memory__ @Disposes final EntityManagerFactory entityManagerFactory) {
        super.disposeMemoryEntityManagerFactory(entityManagerFactory);
    }

    @Memory__
    @Produces
    @Override
    protected EntityManager produceMemoryEntityManager(
            @Memory__ final EntityManagerFactory eEntityManagerFactory) {
        return super.produceMemoryEntityManager(eEntityManagerFactory);
    }

    @Override
    protected void disposeMemoryEntityManager(@Memory__ @Disposes final EntityManager entityManager) {
        super.disposeMemoryEntityManager(entityManager);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Remote__
    @Produces
    @Override
    protected EntityManagerFactory producePhysicalEntityManagerFactory() {
        return super.producePhysicalEntityManagerFactory();
    }

    @Override
    protected void disposePhysicalEntityManagerFactory(
            @Remote__ @Disposes final EntityManagerFactory entityManagerFactory) {
        super.disposePhysicalEntityManagerFactory(entityManagerFactory);
    }

    @Remote__
    @Produces
    @Override
    protected EntityManager producePhysicalEntityManager(@Remote__ final EntityManagerFactory entityManagerFactory) {
        return super.producePhysicalEntityManager(entityManagerFactory);
    }

    @Override
    protected void disposePhysicalEntityManager(@Remote__ @Disposes final EntityManager entityManager) {
        super.disposePhysicalEntityManager(entityManager);
    }
}
