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
    public @interface InMemory {

    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD})
    public @interface OnDisk {

    }

    protected static final String PERSISTENCE_UNIT_NAME_IN_MEMORY = "inMemoryPU";

    protected static final String PERSISTENCE_UNIT_NAME_ON_DISK = "onDiskPU";

    // -----------------------------------------------------------------------------------------------------------------
    protected __PersistenceProducer() {
        super();
    }

    // -------------------------------------------------------------------------------------------------------- InMemory
    @InMemory
    protected EntityManagerFactory produceInMemoryEntityManagerFactory() {
        return Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME_IN_MEMORY);
    }

    protected void disposeInMemoryEntityManagerFactory(
            @InMemory final EntityManagerFactory inMemoryEntityManagerFactory) {
        inMemoryEntityManagerFactory.close();
    }

    @InMemory
    protected EntityManager produceInMemoryEntityManager(final EntityManagerFactory inMemoryEntityManagerFactory) {
        return inMemoryEntityManagerFactory.createEntityManager();
    }

    protected void disposeInMemoryEntityManager(@InMemory final EntityManager inMemoryEntityManager) {
        inMemoryEntityManager.close();
    }

    // ---------------------------------------------------------------------------------------------------------- OnDisk
    @OnDisk
    protected EntityManagerFactory produceOnDiskEntityManagerFactory() {
        return Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME_ON_DISK);
    }

    protected void disposeOnDiskEntityManagerFactory(@OnDisk final EntityManagerFactory onDiskEntityManagerFactory) {
        onDiskEntityManagerFactory.close();
    }

    @OnDisk
    protected EntityManager produceOnDiskEntityManager(final EntityManagerFactory onDiskEntityManagerFactory) {
        return onDiskEntityManagerFactory.createEntityManager();
    }

    protected void disposeOnDiskEntityManager(@OnDisk final EntityManager onDiskEntityManager) {
        onDiskEntityManager.close();
    }
}
