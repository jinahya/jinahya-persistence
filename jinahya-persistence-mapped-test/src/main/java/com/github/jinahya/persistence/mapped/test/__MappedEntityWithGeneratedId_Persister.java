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

import com.github.jinahya.persistence.mapped.__MappedEntityWithGeneratedId;
import jakarta.annotation.Nonnull;

/**
 * An abstract class for randomizing subclasses of {@link __MappedEntityWithGeneratedId}.
 *
 * @param <ENTITY> entity type parameter
 * @param <ID>     id type parameter
 */
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public abstract class __MappedEntityWithGeneratedId_Persister<
        ENTITY extends __MappedEntityWithGeneratedId<ID>,
        ID
        >
        extends __MappedEntity_Persister<ENTITY, ID> {

    /**
     * Creates a new instance for persisting the specified entity class.
     *
     * @param entityClass the entity class to persist.
     * @param idClass     the type of {@link ID} of the {@code entityClass}.
     * @see #entityClass
     * @see #idClass
     */
    protected __MappedEntityWithGeneratedId_Persister(final @Nonnull Class<ENTITY> entityClass,
                                                      final @Nonnull Class<ID> idClass) {
        super(entityClass, idClass);
    }
}
