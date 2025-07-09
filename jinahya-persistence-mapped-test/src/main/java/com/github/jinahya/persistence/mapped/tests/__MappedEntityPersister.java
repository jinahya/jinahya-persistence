package com.github.jinahya.persistence.mapped.tests;

import com.github.jinahya.persistence.mapped.__MappedEntity;
import com.github.jinahya.persistence.mapped.tests.util.__JakartaValidationTestUtils;
import jakarta.persistence.EntityManager;

import java.util.Objects;

public abstract class __MappedEntityPersister<ENTITY extends __MappedEntity<ENTITY, ?>> {

    protected __MappedEntityPersister(final Class<ENTITY> entityClass) {
        super();
        this.entityClass = Objects.requireNonNull(entityClass, "entityClass is null");
    }

    // -----------------------------------------------------------------------------------------------------------------
    protected ENTITY persist(final EntityManager entityManager, final ENTITY entityInstance) {
        Objects.requireNonNull(entityManager, "entityManager is null");
        Objects.requireNonNull(entityInstance, "entityInstance is null");
        __JakartaValidationTestUtils.requireValid(entityInstance);
        entityManager.persist(entityInstance);
        entityManager.flush(); // required?
        __JakartaValidationTestUtils.requireValid(entityInstance);
        return entityInstance;
    }

    // -----------------------------------------------------------------------------------------------------------------
    protected final Class<ENTITY> entityClass;
}
