package com.github.jinahya.persistence.mapped.tests;

import com.github.jinahya.persistence.mapped.__MappedEntity;
import jakarta.annotation.Nonnull;
import uk.co.jemos.podam.api.DataProviderStrategy;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.Objects;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public abstract class __MappedEntityRandomizer<ENTITY extends __MappedEntity<ENTITY, ID>, ID>
        extends ___Randomizer<ENTITY> {

    protected __MappedEntityRandomizer(final Class<ENTITY> entityClass, final Class<ID> idClass) {
        super(entityClass);
        this.entityClass = Objects.requireNonNull(entityClass, "entityClass is null");
        this.idClass = Objects.requireNonNull(idClass, "idClass is null");
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    @SuppressWarnings({
            "java:S1855" // Overriding methods should do more than simply call the same method in the super class
    })
    protected DataProviderStrategy strategy() {
        return super.strategy();
    }

    @Override
    @SuppressWarnings({
            "java:S1855" // Overriding methods should do more than simply call the same method in the super class
    })
    protected PodamFactory factory() {
        return super.factory();
    }

    @Nonnull
    @Override
    public ENTITY get() {
        return super.get();
    }

    // -----------------------------------------------------------------------------------------------------------------
    protected final Class<ENTITY> entityClass;

    protected final Class<ID> idClass;
}
