package com.github.jinahya.persistence.mapped.test.examples.user_with_embedded_id;

import com.github.jinahya.persistence.mapped.test.__MappedEntityRandomizer;
import jakarta.annotation.Nonnull;
import uk.co.jemos.podam.api.DataProviderStrategy;
import uk.co.jemos.podam.api.PodamFactory;

class UserWithEmbeddedIdRandomizer extends __MappedEntityRandomizer<UserWithEmbeddedId, IdForUserWithEmbeddedId> {

    UserWithEmbeddedIdRandomizer() {
        super(UserWithEmbeddedId.class, IdForUserWithEmbeddedId.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @Override
    protected DataProviderStrategy getDataProviderStrategy() {
        return super.getDataProviderStrategy();
    }

    @Nonnull
    @Override
    protected PodamFactory getPodamFactory() {
        return super.getPodamFactory();
    }

    @Nonnull
    @Override
    public UserWithEmbeddedId get() {
        return super.get();
    }
}
