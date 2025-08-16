package com.github.jinahya.persistence.mapped.test;

import com.github.jinahya.persistence.mapped.__MappedEntityBuilder;
import com.github.jinahya.persistence.mapped.__MappedEntityWithGeneratedUuid;

import java.util.UUID;

@SuppressWarnings({
        "java:S100", // Method names should comply with a naming convention
        "java:S101", // Class names should comply with a naming convention
//        "java:S117", // Local variable and method parameter names should comply with a naming convention
        "java:S119", // Type parameter names should comply with a naming convention
        "java:S3011" // Reflection should not be used to increase accessibility of classes, methods, or fields

})
public abstract class __MappedEntityWithGeneratedUuidBuilderTest<
        BUILDER extends __MappedEntityBuilder<BUILDER, ENTITY>,
        ENTITY extends __MappedEntityWithGeneratedUuid
        >
        extends __MappedEntityWithGeneratedIdBuilderTest<BUILDER, ENTITY, UUID> {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    protected __MappedEntityWithGeneratedUuidBuilderTest(final Class<BUILDER> builderClass,
                                                         final Class<ENTITY> entityClass) {
        super(builderClass, entityClass, UUID.class);
    }
}
