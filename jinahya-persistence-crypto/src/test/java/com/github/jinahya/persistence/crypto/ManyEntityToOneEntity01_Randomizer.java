package com.github.jinahya.persistence.crypto;

import com.github.jinahya.persistence.mapped.test.__MappedEntity_Randomizer;
import jakarta.annotation.Nonnull;
import uk.co.jemos.podam.api.ClassInfoStrategy;
import uk.co.jemos.podam.api.DataProviderStrategy;
import uk.co.jemos.podam.api.PodamFactory;

class ManyEntityToOneEntity01_Randomizer extends __MappedEntity_Randomizer<ManyEntityToOneEntity01, Long> {

    ManyEntityToOneEntity01_Randomizer() {
        super(ManyEntityToOneEntity01.class, Long.class,
              "id",
              "entity01"
        );
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
    protected ClassInfoStrategy getClassInfoStrategy() {
        return super.getClassInfoStrategy();
    }

    @Nonnull
    @Override
    public ManyEntityToOneEntity01 get() {
        final var value = super.get();
        return value;
    }
}
