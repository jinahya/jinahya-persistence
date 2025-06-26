package com.github.jinahya.persistence.mapped;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public abstract class __MappedEntityWithGeneratedIdentityTest<
        ENTITY extends __MappedEntityWithGeneratedIdentityTest<ENTITY, ID>, ID
        >
        extends __MappedEntityTest<ENTITY, ID> {

    protected __MappedEntityWithGeneratedIdentityTest(final Class<ENTITY> entityClass, final Class<ID> idClass) {
        super(entityClass, idClass);
    }
}
