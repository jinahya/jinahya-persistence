package com.github.jinahya.persistence.mapped;

import org.junit.jupiter.api.Nested;

import java.util.Objects;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public abstract class __MappedEntityTest<ENTITY extends __MappedEntity<ENTITY, ID>, ID> {

    protected __MappedEntityTest(final Class<ENTITY> entityClass, final Class<ID> idClass) {
        super();
        this.entityClass = Objects.requireNonNull(entityClass, "entityClass is null");
        this.idClass = Objects.requireNonNull(idClass, "idClass is null");
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object
    @Nested
    protected class ToStringTest {

    }

    // -----------------------------------------------------------------------------------------------------------------
    protected final Class<ENTITY> entityClass;

    protected final Class<ID> idClass;
}
