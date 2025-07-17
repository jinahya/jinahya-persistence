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

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import java.util.UUID;

@MappedSuperclass
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public abstract class __MappedEntityWithGeneratedUuid<SELF extends __MappedEntityWithGeneratedUuid<SELF>>
        extends __MappedEntityWithGeneratedPrimaryKey<SELF, UUID> {

    // ------------------------------------------------------------------------------------------ STATIC_FACTORY_METHODS

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected __MappedEntityWithGeneratedUuid() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    @Override
    public final boolean equals(final Object obj) {
        if (!(obj instanceof __MappedEntityWithGeneratedUuid<?>)) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public final int hashCode() {
        return super.hashCode();
    }

    // ------------------------------------------------------------------------------------------------------ super.id__
    @Override
    protected /* final */ UUID getId__() {
        return id__;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = COLUMN_NAME_ID__, nullable = false, insertable = false, updatable = false)
    private UUID id__;
}
