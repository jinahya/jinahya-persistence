package com.github.jinahya.persistence.mapped.user_with_string_id;

import com.github.jinahya.persistence.mapped.__MappedEntityRandomizer;
import jakarta.annotation.Nonnull;
import uk.co.jemos.podam.api.DataProviderStrategy;
import uk.co.jemos.podam.api.PodamFactory;

class UserWithStringIdRandomizer extends __MappedEntityRandomizer<UserWithStringId, String> {

    UserWithStringIdRandomizer() {
        super(UserWithStringId.class, String.class);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    protected DataProviderStrategy strategy() {
        return super.strategy();
    }

    @Override
    protected PodamFactory factory() {
        return super.factory();
    }

    @Nonnull
    @Override
    public UserWithStringId get() {
        return super.get();
    }
}