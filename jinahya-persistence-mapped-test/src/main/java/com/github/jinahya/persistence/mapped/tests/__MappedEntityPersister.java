package com.github.jinahya.persistence.mapped.tests;

import com.github.jinahya.persistence.mapped.__MappedEntity;
import jakarta.persistence.EntityManager;

import java.util.Objects;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119" // Type parameter names should comply with a naming convention
})
public abstract class __MappedEntityPersister<ENTITY extends __MappedEntity<ENTITY, ID>, ID>
        extends ___Persister<ENTITY> {

    protected __MappedEntityPersister(final Class<ENTITY> entityClass, final Class<ID> idClass) {
        super(entityClass);
        this.idClass = Objects.requireNonNull(idClass, "idClass is null");
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public ENTITY persist(final EntityManager entityManager, final ENTITY entityInstance) {
        return super.persist(entityManager, entityInstance);
    }

    // -----------------------------------------------------------------------------------------------------------------
    protected final Class<ID> idClass;
}
