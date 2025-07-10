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
    public @interface Memory__ {

    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD})
    public @interface Remote__ {

    }

    static final String PERSISTENCE_UNIT_NAME_MEMORY__ = "memoryPU";

    static final String PERSISTENCE_UNIT_NAME_PHYSICAL__ = "physicalPU";

    // -----------------------------------------------------------------------------------------------------------------
    protected __PersistenceProducer() {
        super();
    }

    // ---------------------------------------------------------------------------------------------------------- memory
    @Memory__
    protected EntityManagerFactory produceMemoryEntityManagerFactory() {
        return Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME_MEMORY__);
    }

    protected void disposeMemoryEntityManagerFactory(@Memory__ final EntityManagerFactory entityManagerFactory) {
        entityManagerFactory.close();
    }

    @Memory__
    protected EntityManager produceMemoryEntityManager(final EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory.createEntityManager();
    }

    protected void disposeMemoryEntityManager(@Memory__ final EntityManager entityManager) {
        entityManager.close();
    }

    // ---------------------------------------------------------------------------------------------------------- remote
    @Remote__
    protected EntityManagerFactory producePhysicalEntityManagerFactory() {
        return Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME_PHYSICAL__);
    }

    protected void disposePhysicalEntityManagerFactory(@Remote__ final EntityManagerFactory entityManagerFactory) {
        entityManagerFactory.close();
    }

    @Remote__
    protected EntityManager producePhysicalEntityManager(final EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory.createEntityManager();
    }

    protected void disposePhysicalEntityManager(@Remote__ final EntityManager entityManager) {
        entityManager.close();
    }
}
