package com.github.jinahya.persistence.mapped.tests;

import jakarta.annotation.Nonnull;
import uk.co.jemos.podam.api.DataProviderStrategy;
import uk.co.jemos.podam.api.PodamFactory;

class Entity1Randomizer extends __MappedEntityRandomizer<Entity1, Long> {

    Entity1Randomizer() {
        super(Entity1.class, Long.class);
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
    public Entity1 get() {
        return super.get();
    }
}
