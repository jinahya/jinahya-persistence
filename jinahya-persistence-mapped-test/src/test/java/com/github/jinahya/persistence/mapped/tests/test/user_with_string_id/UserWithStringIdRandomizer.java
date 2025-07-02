package com.github.jinahya.persistence.mapped.tests.test.user_with_string_id;

import com.github.jinahya.persistence.mapped.tests.__MappedEntityRandomizer;
import jakarta.annotation.Nonnull;
import uk.co.jemos.podam.api.DataProviderStrategy;
import uk.co.jemos.podam.api.PodamFactory;

class UserWithStringIdRandomizer extends __MappedEntityRandomizer<UserWithStringId, String> {

    UserWithStringIdRandomizer() {
        super(UserWithStringId.class, String.class);
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
    public UserWithStringId get() {
        return super.get();
    }
}