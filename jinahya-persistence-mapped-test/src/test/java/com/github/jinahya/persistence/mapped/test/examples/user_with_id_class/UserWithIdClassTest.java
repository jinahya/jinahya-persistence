package com.github.jinahya.persistence.mapped.test.examples.user_with_id_class;

import com.github.jinahya.persistence.mapped.test.__Configure_EqualsVerifier;
import com.github.jinahya.persistence.mapped.test.__MappedEntity_Test;
import jakarta.annotation.Nonnull;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;

class UserWithIdClassTest extends __MappedEntity_Test<UserWithIdClass, IdForUserWithIdClass> {

    UserWithIdClassTest() {
        super(UserWithIdClass.class, IdForUserWithIdClass.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @__Configure_EqualsVerifier
    @Nonnull
    @Override
    protected SingleTypeEqualsVerifierApi<UserWithIdClass> equals_Verify_(
            @Nonnull SingleTypeEqualsVerifierApi<UserWithIdClass> equalsVerifier) {
        return super.equals_Verify_(equalsVerifier)
                .suppress(Warning.SURROGATE_KEY)
                ;
    }
}
