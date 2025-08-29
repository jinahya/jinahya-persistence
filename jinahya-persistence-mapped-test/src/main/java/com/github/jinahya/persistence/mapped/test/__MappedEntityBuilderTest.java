package com.github.jinahya.persistence.mapped.test;

import com.github.jinahya.persistence.mapped.__MappedEntity;
import com.github.jinahya.persistence.mapped.__MappedEntityBuilder;

import java.util.Objects;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119", // Type parameter names should comply with a naming convention
})
public abstract class __MappedEntityBuilderTest<
        BUILDER extends __MappedEntityBuilder<BUILDER, ENTITY>,
        ENTITY extends __MappedEntity<ID>,
        ID
        > extends __MappedBuilderTest<BUILDER, ENTITY> {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    protected __MappedEntityBuilderTest(final Class<BUILDER> builderClass, final Class<ENTITY> entityClass,
                                        final Class<ID> idClass) {
        super(builderClass, entityClass);
        this.idClass = Objects.requireNonNull(idClass, "idClass is null");
    }

    // -----------------------------------------------------------------------------------------------------------------
    protected final Class<ID> idClass;
}
