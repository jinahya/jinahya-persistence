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
    @InMemory
    @Produces
    @Override
    protected EntityManagerFactory produceInMemoryEntityManagerFactory() {
        return super.produceInMemoryEntityManagerFactory();
    }

    @Override
    protected void disposeInMemoryEntityManagerFactory(
            @InMemory @Disposes final EntityManagerFactory inMemoryEntityManagerFactory) {
        super.disposeInMemoryEntityManagerFactory(inMemoryEntityManagerFactory);
    }

    @InMemory
    @Produces
    @Override
    protected EntityManager produceInMemoryEntityManager(
            @InMemory final EntityManagerFactory inMemoryEntityManagerFactory) {
        return super.produceInMemoryEntityManager(inMemoryEntityManagerFactory);
    }

    @Override
    protected void disposeInMemoryEntityManager(@InMemory @Disposes final EntityManager inMemoryEntityManager) {
        super.disposeInMemoryEntityManager(inMemoryEntityManager);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @OnDisk
    @Produces
    @Override
    protected EntityManagerFactory produceOnDiskEntityManagerFactory() {
        return super.produceOnDiskEntityManagerFactory();
    }

    @Override
    protected void disposeOnDiskEntityManagerFactory(
            @OnDisk @Disposes final EntityManagerFactory onDiskEntityManagerFactory) {
        super.disposeOnDiskEntityManagerFactory(onDiskEntityManagerFactory);
    }

    @OnDisk
    @Produces
    @Override
    protected EntityManager produceOnDiskEntityManager(@OnDisk final EntityManagerFactory onDiskEntityManagerFactory) {
        return super.produceOnDiskEntityManager(onDiskEntityManagerFactory);
    }

    @Override
    protected void disposeOnDiskEntityManager(@OnDisk @Disposes final EntityManager onDiskEntityManager) {
        super.disposeOnDiskEntityManager(onDiskEntityManager);
    }
}
