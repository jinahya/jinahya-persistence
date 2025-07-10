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
    public @interface Local__ {

    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD})
    public @interface Remote__ {

    }

    static final String PERSISTENCE_UNIT_NAME_LOCAL__ = "localPU";

    static final String PERSISTENCE_UNIT_NAME_REMOTE__ = "remotePU";

    // -----------------------------------------------------------------------------------------------------------------
    protected __PersistenceProducer() {
        super();
    }

    // ----------------------------------------------------------------------------------------------------------- local
    @Local__
    protected EntityManagerFactory produceLocalEntityManagerFactory() {
        return Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME_LOCAL__);
    }

    protected void disposeLocalEntityManagerFactory(@Local__ final EntityManagerFactory entityManagerFactory) {
        entityManagerFactory.close();
    }

    @Local__
    protected EntityManager produceLocalEntityManager(final EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory.createEntityManager();
    }

    protected void disposeLocalEntityManager(@Local__ final EntityManager entityManager) {
        entityManager.close();
    }

    // ---------------------------------------------------------------------------------------------------------- remote
    @Remote__
    protected EntityManagerFactory produceRemoteEntityManagerFactory() {
        return Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME_REMOTE__);
    }

    protected void disposeRemoteEntityManagerFactory(@Remote__ final EntityManagerFactory entityManagerFactory) {
        entityManagerFactory.close();
    }

    @Remote__
    protected EntityManager produceRemoteEntityManager(final EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory.createEntityManager();
    }

    protected void disposeRemoteEntityManager(@Remote__ final EntityManager entityManager) {
        entityManager.close();
    }
}
