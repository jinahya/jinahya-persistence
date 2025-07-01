package com.github.jinahya.persistence.mapped;

import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;

import java.util.UUID;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public abstract class __MappedEntityWithGeneratedUuidTest<ENTITY extends __MappedEntityWithGeneratedUuid<ENTITY>>
        extends __MappedEntityTest<ENTITY, UUID> {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    protected __MappedEntityWithGeneratedUuidTest(final Class<ENTITY> entityClass) {
        super(entityClass, UUID.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    protected SingleTypeEqualsVerifierApi<ENTITY> getEqualsVerifier() {
        return super.getEqualsVerifier()
                .suppress(Warning.SURROGATE_KEY)
                .suppress(Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY)
                .suppress(Warning.JPA_GETTER) // https://github.com/jqno/equalsverifier/issues/1092 // TODO: check!
                ;
    }
}
