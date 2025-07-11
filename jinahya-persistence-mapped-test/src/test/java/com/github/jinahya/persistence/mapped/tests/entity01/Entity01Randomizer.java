package com.github.jinahya.persistence.mapped.tests.entity01;

import com.github.jinahya.persistence.mapped.tests.__MappedEntityRandomizer;
import jakarta.annotation.Nonnull;
import uk.co.jemos.podam.api.DataProviderStrategy;
import uk.co.jemos.podam.api.PodamFactory;

class Entity01Randomizer extends __MappedEntityRandomizer<Entity01, Long> {

    Entity01Randomizer() {
        super(Entity01.class, Long.class);
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
    public Entity01 get() {
        final var value = super.get();
        value.setId(null);
        return value;
    }
}
