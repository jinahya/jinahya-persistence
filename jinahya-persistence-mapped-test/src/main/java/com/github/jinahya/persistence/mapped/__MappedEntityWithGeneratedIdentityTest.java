package com.github.jinahya.persistence.mapped;

import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public abstract class __MappedEntityWithGeneratedIdentityTest<
        ENTITY extends __MappedEntityWithGeneratedIdentity<ENTITY>
        >
        extends __MappedEntityTest<ENTITY, Long> {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    protected __MappedEntityWithGeneratedIdentityTest(final Class<ENTITY> entityClass) {
        super(entityClass, Long.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Test
    @Override
    protected void _NotBlank_NewInstance() {
        super._NotBlank_NewInstance();
    }

    @Test
    @Override
    protected void _NotBlank_NewRandomizedInstance() {
        super._NotBlank_NewRandomizedInstance();
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("equals/hashCode")
    @Test
    @Override
    protected void _verify_equals() {
        super._verify_equals();
    }

    @Override
    protected SingleTypeEqualsVerifierApi<ENTITY> getEqualsVerifier() {
        return super.getEqualsVerifier()
                .suppress(Warning.SURROGATE_KEY)
                .suppress(Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY)
                .suppress(Warning.JPA_GETTER) // https://github.com/jqno/equalsverifier/issues/1092 // TODO: check!
                ;
    }
}
