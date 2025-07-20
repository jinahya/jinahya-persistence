package com.github.jinahya.persistence.mapped.test.examples.user_with_embedded_id;

import com.github.jinahya.persistence.mapped.test.__MappedEntityTest;
import jakarta.annotation.Nonnull;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;
import org.junit.jupiter.api.Test;

class UserTest extends __MappedEntityTest<User, IdForUser> {

    UserTest() {
        super(User.class, IdForUser.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Test
    @Override
    protected void equals_verify() {
        super.equals_verify();
    }

    @Nonnull
    @Override
    protected SingleTypeEqualsVerifierApi<User> getEqualsVerifier() {
        return super.getEqualsVerifier()
                .suppress(Warning.SURROGATE_KEY)
                ;
    }
}
