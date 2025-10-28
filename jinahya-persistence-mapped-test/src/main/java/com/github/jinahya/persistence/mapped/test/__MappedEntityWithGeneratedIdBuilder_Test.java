package com.github.jinahya.persistence.mapped.test;

import com.github.jinahya.persistence.mapped.__MappedEntityBuilder;
import com.github.jinahya.persistence.mapped.__MappedEntityWithGeneratedId;

@SuppressWarnings({
        "java:S100", // Method names should comply with a naming convention
        "java:S101", // Class names should comply with a naming convention
//        "java:S117", // Local variable and method parameter names should comply with a naming convention
        "java:S119", // Type parameter names should comply with a naming convention
        "java:S3011" // Reflection should not be used to increase accessibility of classes, methods, or fields

})
public abstract class __MappedEntityWithGeneratedIdBuilder_Test<
        BUILDER extends __MappedEntityBuilder<BUILDER, ENTITY>,
        ENTITY extends __MappedEntityWithGeneratedId<ID>,
        ID
        >
        extends __MappedEntityBuilder_Test<BUILDER, ENTITY, ID> {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    @Deprecated(forRemoval = true)
    protected __MappedEntityWithGeneratedIdBuilder_Test(final Class<BUILDER> builderClass,
                                                        final Class<ENTITY> entityClass, final Class<ID> idClass) {
        super(builderClass, entityClass, idClass);
    }

    protected __MappedEntityWithGeneratedIdBuilder_Test(final Class<BUILDER> builderClass,
                                                        final Class<ENTITY> entityClass) {
        super(builderClass, entityClass);
    }
}
