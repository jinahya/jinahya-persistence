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
import uk.co.jemos.podam.api.ClassInfoStrategy;
import uk.co.jemos.podam.api.DataProviderStrategy;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.Objects;

/**
 * An abstract class for randomizing subclasses of {@link __MappedEntity}.
 *
 * @param <ENTITY> entity type parameter
 * @param <ID>     id type parameter
 */
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public abstract class __MappedEntity_Randomizer<ENTITY extends __MappedEntity<ID>, ID>
        extends ___Randomizer<ENTITY> {

    /**
     * Creates a new instance for randomizing the specified entity class.
     *
     * @param entityClass    the entity class to be randomized.
     * @param idClass        the type of {@link ID} of the {@code entityClass}.
     * @param excludedFields the names of the fields to be excluded from randomization.
     * @see #idClass
     */
    protected __MappedEntity_Randomizer(@Nonnull final Class<ENTITY> entityClass, @Nonnull final Class<ID> idClass,
                                        @Nonnull final String... excludedFields) {
        super(entityClass, excludedFields);
        this.idClass = Objects.requireNonNull(idClass, "idClass is null");
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @Override
    @SuppressWarnings({
            "java:S1855" // Overriding methods should do more than simply call the same method in the super class
    })
    protected DataProviderStrategy getDataProviderStrategy() {
        return super.getDataProviderStrategy();
    }

    @Nonnull
    @Override
    @SuppressWarnings({
            "java:S1855" // Overriding methods should do more than simply call the same method in the super class
    })
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
    public ENTITY get() {
        return super.get();
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The type of {@link ID} of the {@code #entityClass}.
     */
    protected final Class<ID> idClass;
}
