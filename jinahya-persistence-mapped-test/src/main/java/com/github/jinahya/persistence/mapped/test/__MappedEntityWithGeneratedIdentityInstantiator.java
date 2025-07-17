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

import com.github.jinahya.persistence.mapped.__MappedEntityWithGeneratedIdentity;
import jakarta.annotation.Nonnull;

/**
 * An abstract class for instantiating subclasses of {@link __MappedEntityWithGeneratedIdentity}.
 *
 * @param <ENTITY> entity type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public abstract class __MappedEntityWithGeneratedIdentityInstantiator<
        ENTITY extends __MappedEntityWithGeneratedIdentity<ENTITY>
        >
        extends __MappedEntityInstantiator<ENTITY, Long> {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance for instantiating the specified entity class.
     *
     * @param entityClass the entity class to be initialized.
     * @see #entityClass
     * @see #idClass
     */
    protected __MappedEntityWithGeneratedIdentityInstantiator(final Class<ENTITY> entityClass) {
        super(entityClass, Long.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @Override
    public ENTITY get() {
        return super.get();
    }
}
