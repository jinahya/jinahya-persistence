package com.github.jinahya.persistence.mapped.test;

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

import com.github.jinahya.persistence.mapped.__MappedEntity;
import jakarta.annotation.Nonnull;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.Mockito;

import java.util.Objects;
import java.util.Optional;

/**
 * A class for testing a specific subclass of {@link __MappedEntity}.
 *
 * @param <ENTITY> entity type parameter
 * @param <ID>     id type parameter
 */
@SuppressWarnings({
        "java:S100", // Method names should comply with a naming convention
        "java:S101", // Class names should comply with a naming convention
        "java:S119", // Type parameter names should comply with a naming convention
        "java:S5960" // Assertions should not be used in production code
})
public abstract class __MappedEntity_Test<ENTITY extends __MappedEntity<ID>, ID> extends __Mapped_Test<ENTITY> {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance to test the specified entity class.
     *
     * @param entityClass the entity class to test.
     * @param idClass     the id class of the {@code entityClass}.
     * @see #mappedClass
     * @see #idClass
     */
    protected __MappedEntity_Test(@Nonnull final Class<ENTITY> entityClass, @Nonnull final Class<ID> idClass) {
        super(entityClass);
        this.idClass = Objects.requireNonNull(idClass, "idClass is null");
    }

    // --------------------------------------------------------------------------------------------------------- idClass

    /**
     * Returns a new instance of {@link #idClass}.
     *
     * @return a new instance of {@link #idClass}.
     */
    @Nonnull
    protected final ID newIdInstance() {
        return ___InstantiatorUtils.newInstantiatedInstanceOf(idClass)
                .orElseGet(() -> ReflectionUtils.newInstance(idClass))
                ;
    }

    /**
     * Returns a spy object of a new instance of {@link #newIdInstance()}.
     *
     * @return a spy object of a new instance of {@link #newIdInstance()}.
     */
    @Nonnull
    protected final ID newIdInstanceSpy() {
        return Mockito.spy(newIdInstance());
    }

    /**
     * Returns a new randomized instance of {@link #idClass}.
     *
     * @return a new randomized instance of {@link #idClass}; {@link Optional#empty() empty} when no randomizer found.
     */
    @Nonnull
    protected final Optional<ID> newRandomizedIdInstance() {
        return ___RandomizerUtils.newRandomizedInstanceOf(idClass);
    }

    /**
     * Returns a spy object of a new randomized instance of {@link #idClass}.
     *
     * @return a spy object of a new randomized instance of {@link #idClass}; {@link Optional#empty() empty} when no
     *         randomizer found.
     */
    @Nonnull
    protected final Optional<ID> newRandomizedIdInstanceSpy() {
        return newRandomizedIdInstance().map(Mockito::spy);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The class of {@link ID}.
     */
    protected final Class<ID> idClass;
}
