package com.github.jinahya.persistence.mapped.tests;

import com.github.jinahya.persistence.mapped.__MappedEntity;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119", // Type parameter names should comply with a naming convention
        "java:S6813" // Field dependency injection should be avoided
})
public abstract class __MappedEntityPersistenceInMemoryTest<ENTITY extends __MappedEntity<ENTITY, ID>, ID>
        extends ___MappedEntityPersistenceTest<ENTITY, ID> {

    // -----------------------------------------------------------------------------------------------------------------
    protected __MappedEntityPersistenceInMemoryTest(final Class<ENTITY> entityClass, final Class<ID> idClass) {
        super(entityClass, idClass);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    protected final EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    @Override
    protected final EntityManager getEntityManager() {
        return entityManager;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @__PersistenceProducer.InMemory
    @Inject
    private EntityManagerFactory entityManagerFactory;

    @__PersistenceProducer.InMemory
    @Inject
    private EntityManager entityManager;
}
