package com.github.jinahya.persistence.mapped.tests;

import com.github.jinahya.persistence.mapped.__MappedEntity;
import jakarta.persistence.EntityManager;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public final class __MappedEntityPersisterUtils {

    public static <ENTITY extends __MappedEntity<ENTITY, ?>>
    ENTITY newPersistedInstanceOf(final EntityManager entityManager, final Class<ENTITY> entityClass) {
        return ___PersisterUtils.newPersistedInstanceOf(entityManager, entityClass);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private __MappedEntityPersisterUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
