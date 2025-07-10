package com.github.jinahya.persistence.mapped.tests;

import com.github.jinahya.persistence.mapped.__MappedEntity;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119", // Type parameter names should comply with a naming convention
        "java:S6813" // Field dependency injection should be avoided
})
public abstract class _MappedEntityTest<ENTITY extends __MappedEntity<ENTITY, ID>, ID>
        extends __MappedEntityTest<ENTITY, ID> {

    // -----------------------------------------------------------------------------------------------------------------
    protected _MappedEntityTest(final Class<ENTITY> entityClass, final Class<ID> idClass) {
        super(entityClass, idClass);
    }
}
