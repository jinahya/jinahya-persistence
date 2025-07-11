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

import com.github.jinahya.persistence.mapped.__MappedEntity;
import jakarta.persistence.EntityManager;

import java.util.Objects;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119" // Type parameter names should comply with a naming convention
})
public abstract class __MappedEntityPersister<ENTITY extends __MappedEntity<ENTITY, ID>, ID>
        extends ___Persister<ENTITY> {

    protected __MappedEntityPersister(final Class<ENTITY> entityClass, final Class<ID> idClass) {
        super(entityClass);
        this.idClass = Objects.requireNonNull(idClass, "idClass is null");
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public ENTITY persist(final EntityManager entityManager, final ENTITY entityInstance) {
        return super.persist(entityManager, entityInstance);
    }

    // -----------------------------------------------------------------------------------------------------------------
    protected final Class<ID> idClass;
}
