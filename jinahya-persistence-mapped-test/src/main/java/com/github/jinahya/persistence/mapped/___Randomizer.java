package com.github.jinahya.persistence.mapped;

import jakarta.annotation.Nonnull;
import uk.co.jemos.podam.api.DataProviderStrategy;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;
import uk.co.jemos.podam.api.RandomDataProviderStrategyImpl;

import java.util.Objects;

public abstract class ___Randomizer<T> {

    protected ___Randomizer(final Class<T> type) {
        super();
        this.type = Objects.requireNonNull(type, "type is null");
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
    public T get() {
        return ___InitializerUtils.newInitializedInstanceOf(type)
                .map(o -> factory().populatePojo(o))
                .orElseGet(() -> factory().manufacturePojo(type));
    }

    // -----------------------------------------------------------------------------------------------------------------
    protected final Class<T> type;
}
