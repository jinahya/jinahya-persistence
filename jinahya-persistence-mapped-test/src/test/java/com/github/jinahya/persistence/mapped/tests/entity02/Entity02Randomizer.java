package com.github.jinahya.persistence.mapped.tests.entity02;

import com.github.jinahya.persistence.mapped.tests.__MappedEntityRandomizer;
import jakarta.annotation.Nonnull;
import uk.co.jemos.podam.api.DataProviderStrategy;
import uk.co.jemos.podam.api.PodamFactory;

class Entity02Randomizer extends __MappedEntityRandomizer<Entity02, Long> {

    Entity02Randomizer() {
        super(Entity02.class, Long.class);
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
    public Entity02 get() {
        final var value = super.get();
        value.setId(null);
        assert value.getId() == null;
        return value;
    }
}
