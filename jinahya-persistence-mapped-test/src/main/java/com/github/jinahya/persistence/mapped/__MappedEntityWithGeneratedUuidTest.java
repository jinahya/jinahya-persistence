package com.github.jinahya.persistence.mapped;

import java.util.UUID;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public abstract class __MappedEntityWithGeneratedUuidTest<
        ENTITY extends __MappedEntityWithGeneratedUuid<ENTITY>
        >
        extends __MappedEntityTest<ENTITY, UUID> {

    protected __MappedEntityWithGeneratedUuidTest(final Class<ENTITY> entityClass) {
        super(entityClass, UUID.class);
    }
}
