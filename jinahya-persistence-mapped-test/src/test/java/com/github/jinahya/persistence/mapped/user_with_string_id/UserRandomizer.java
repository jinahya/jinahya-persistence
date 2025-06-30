package com.github.jinahya.persistence.mapped.user_with_string_id;

import com.github.jinahya.persistence.mapped.__MappedEntityRandomizer;
import jakarta.annotation.Nonnull;
import uk.co.jemos.podam.api.DataProviderStrategy;
import uk.co.jemos.podam.api.PodamFactory;

class UserRandomizer extends __MappedEntityRandomizer<User, String> {

    UserRandomizer() {
        super(User.class, String.class);
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
    public User get() {
        return super.get();
    }
}