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

import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
@SuppressWarnings({
        "java:S100", // Method names should comply with a naming convention
        "java:S101", // Class names should comply with a naming convention
        "java:S116", // Field names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public abstract class __MappedEntityWithGeneratedIdentity extends __MappedEntityWithGeneratedId<Long> {

    // ------------------------------------------------------------------------------------------ STATIC_FACTORY_METHODS

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected __MappedEntityWithGeneratedIdentity() {
        super();
    }

    /**
     * Creates a new instance built from the specified builder.
     *
     * @param builder the builder from which a new instance is built.
     */
    protected __MappedEntityWithGeneratedIdentity(
            final @Nonnull __MappedEntityWithGeneratedIdentityBuilder<?, ?> builder) {
        super(builder);
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object
    @Override
    public String toString() {
        return super.toString() + '{' +
               "id__=" + id__ +
               '}';
    }

    // ------------------------------------------------------------------------------------------------------------ id__

    /**
     * Returns curren value of the {@value __MappedEntityWithGeneratedId#ATTRIBUTE_NAME_ID__} attribute.
     *
     * @return current value of the {@value __MappedEntityWithGeneratedId#ATTRIBUTE_NAME_ID__} attribute.
     */
    public Long getId__() {
        return id__;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COLUMN_NAME_ID__,
            nullable = false,
            insertable = true, // EclipseLink
            updatable = false
    )
    private Long id__;
}
