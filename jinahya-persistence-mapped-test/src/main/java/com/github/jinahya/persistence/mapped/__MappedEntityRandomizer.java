package com.github.jinahya.persistence.mapped;

import jakarta.annotation.Nonnull;
import uk.co.jemos.podam.api.DataProviderStrategy;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;
import uk.co.jemos.podam.api.RandomDataProviderStrategyImpl;

import java.util.Objects;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public abstract class __MappedEntityRandomizer<ENTITY extends __MappedEntity<ENTITY, ID>, ID>
        implements ___Randomizer<ENTITY> {

    protected __MappedEntityRandomizer(final Class<ENTITY> entityClass, final Class<ID> idClass) {
        super();
        this.entityClass = Objects.requireNonNull(entityClass, "entityClass is null");
        this.idClass = Objects.requireNonNull(idClass, "idClass is null");
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new strategy.
     *
     * @return a new strategy.
     * @see RandomDataProviderStrategyImpl#RandomDataProviderStrategyImpl()
     */
    protected DataProviderStrategy strategy() {
        return new RandomDataProviderStrategyImpl();
    }

    /**
     * Creates a new factory with a strategy from {@link #strategy()}.
     *
     * @return a new factory.
     * @see PodamFactoryImpl#PodamFactoryImpl(DataProviderStrategy)
     */
    protected PodamFactory factory() {
        return new PodamFactoryImpl(strategy());
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Nonnull
    @Override
    public ENTITY get() {
        return ___InitializerUtils.newInitializedInstanceOf(entityClass)
                .map(o -> factory().populatePojo(o))
                .orElseGet(() -> factory().manufacturePojo(entityClass));
    }

    // -----------------------------------------------------------------------------------------------------------------
    protected final Class<ENTITY> entityClass;

    protected final Class<ID> idClass;
}
