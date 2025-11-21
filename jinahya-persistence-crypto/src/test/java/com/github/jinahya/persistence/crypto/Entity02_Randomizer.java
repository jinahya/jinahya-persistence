package com.github.jinahya.persistence.crypto;

import com.github.jinahya.persistence.mapped.test.__MappedEntity_Randomizer;
import jakarta.annotation.Nonnull;
import uk.co.jemos.podam.api.ClassInfoStrategy;
import uk.co.jemos.podam.api.DataProviderStrategy;
import uk.co.jemos.podam.api.PodamFactory;

import java.sql.Timestamp;

class Entity02_Randomizer extends __MappedEntity_Randomizer<Entity02, Long> {

    Entity02_Randomizer() {
        super(Entity02.class, Long.class,
              "id",
              "encryptionIdentifier",
//              "javaSqlTimestamp1",
              "embeddable01.javaSqlTimestamp1"
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
    public Entity02 get() {
        final var value = super.get();
//        value.getEmbeddable01().setJavaSqlTimestamp1(new Timestamp(System.currentTimeMillis()));
        return value;
    }
}
