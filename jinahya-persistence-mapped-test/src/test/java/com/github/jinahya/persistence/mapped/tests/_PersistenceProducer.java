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
    @Local__
    @Produces
    @Override
    protected EntityManagerFactory produceLocalEntityManagerFactory() {
        return super.produceLocalEntityManagerFactory();
    }

    @Override
    protected void disposeLocalEntityManagerFactory(
            @Local__ @Disposes final EntityManagerFactory entityManagerFactory) {
        super.disposeLocalEntityManagerFactory(entityManagerFactory);
    }

    @Local__
    @Produces
    @Override
    protected EntityManager produceLocalEntityManager(
            @Local__ final EntityManagerFactory eEntityManagerFactory) {
        return super.produceLocalEntityManager(eEntityManagerFactory);
    }

    @Override
    protected void disposeLocalEntityManager(@Local__ @Disposes final EntityManager entityManager) {
        super.disposeLocalEntityManager(entityManager);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Remote__
    @Produces
    @Override
    protected EntityManagerFactory produceRemoteEntityManagerFactory() {
        return super.produceRemoteEntityManagerFactory();
    }

    @Override
    protected void disposeRemoteEntityManagerFactory(
            @Remote__ @Disposes final EntityManagerFactory entityManagerFactory) {
        super.disposeRemoteEntityManagerFactory(entityManagerFactory);
    }

    @Remote__
    @Produces
    @Override
    protected EntityManager produceRemoteEntityManager(@Remote__ final EntityManagerFactory entityManagerFactory) {
        return super.produceRemoteEntityManager(entityManagerFactory);
    }

    @Override
    protected void disposeRemoteEntityManager(@Remote__ @Disposes final EntityManager entityManager) {
        super.disposeRemoteEntityManager(entityManager);
    }
}
