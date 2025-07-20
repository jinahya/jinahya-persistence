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

import java.util.Objects;

/**
 * An abstract class for instantiating subclasses of {@link __MappedEntity}.
 *
 * @param <ENTITY> entity type parameter
 * @param <ID>     id type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public abstract class __MappedEntityInstantiator<ENTITY extends __MappedEntity<ID>, ID>
        extends ___Instantiator<ENTITY> {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance for instantiating the specified entity class.
     *
     * @param entityClass the entity class to be initialized.
     * @param idClass     the type of {@link ID} of the {@link #entityClass}.
     * @see #entityClass
     * @see #idClass
     */
    protected __MappedEntityInstantiator(final Class<ENTITY> entityClass, final Class<ID> idClass) {
        super(entityClass);
        this.entityClass = Objects.requireNonNull(entityClass, "entityClass is null");
        this.idClass = Objects.requireNonNull(idClass, "idClass is null");
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @Override
    public ENTITY get() {
        return super.get();
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The entity class to be initialized.
     */
    protected final Class<ENTITY> entityClass;

    /**
     * The type of {@link ID} of the {@link #entityClass}.
     */
    protected final Class<ID> idClass;
}
