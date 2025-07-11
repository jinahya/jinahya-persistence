package com.github.jinahya.persistence.mapped.tests;

/*-
 * #%L
 * jinahya-persistence-mapped-test
 * %%
 * Copyright (C) 2024 - 2025 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import jakarta.annotation.Nonnull;
import uk.co.jemos.podam.api.DataProviderStrategy;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;
import uk.co.jemos.podam.api.RandomDataProviderStrategyImpl;

import java.util.Objects;

/**
 * An abstract class for randomizing a specific class.
 *
 * @param <T> type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ___Instantiator
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class ___Randomizer<T> {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance for initializing a randomized instance of the specified class.
     *
     * @param type the class to be randomized.
     */
    protected ___Randomizer(@Nonnull final Class<T> type) {
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
    @Nonnull
    protected DataProviderStrategy getDataProviderStrategy() {
        return new RandomDataProviderStrategyImpl();
    }

    /**
     * Creates a new factory with a strategy from {@link #getDataProviderStrategy()}.
     *
     * @return a new factory.
     * @see PodamFactoryImpl#PodamFactoryImpl(DataProviderStrategy)
     */
    @Nonnull
    protected PodamFactory getPodamFactory() {
        return new PodamFactoryImpl(getDataProviderStrategy());
    }

    /**
     * Returns a randomized instance of the {@link #type}.
     *
     * @return a randomized instance of the {@link #type}.
     */
    @Nonnull
    public T get() {
        return ___InstantiatorUtils.newInstantiatedInstanceOf(type)
                .map(o -> getPodamFactory().populatePojo(o))
                .orElseGet(() -> getPodamFactory().manufacturePojo(type));
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The type to be randomized.
     */
    protected final Class<T> type;
}
