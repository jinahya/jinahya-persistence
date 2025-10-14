package com.github.jinahya.persistence.mapped;

/*-
 * #%L
 * jinahya-persistence-mapped
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

/**
 * An abstract builder class for a specific subclass of {@link __MappedEntityWithGeneratedUuid} class.
 *
 * @param <SELF>   self type parameter
 * @param <TARGET> entity type parameter
 */
@SuppressWarnings({
        "java:S100", // Method names should comply with a naming convention
        "java:S101", // Class names should comply with a naming convention
        "java:S115", // Constant names should comply with a naming convention
        "java:s117", // Local variable and method parameter names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public abstract class __MappedEntityWithGeneratedUuidBuilder<
        SELF extends __MappedEntityWithGeneratedUuidBuilder<SELF, TARGET>,
        TARGET extends __MappedEntityWithGeneratedUuid
        >
        extends __MappedEntityWithGeneratedIdBuilder<SELF, TARGET> {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     *
     * @param targetClass the entity class.
     */
    protected __MappedEntityWithGeneratedUuidBuilder(final Class<TARGET> targetClass) {
        super(targetClass);
    }
}
