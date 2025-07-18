package com.github.jinahya.persistence.mapped;

/*-
 * #%L
 * jinahya-persistence-mapped
 * %%
 * Copyright (C) 2025 Jinahya, Inc.
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

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * Just a concrete implementation of {@link __MappedEntity} for testing coverage.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Entity
@SuppressWarnings({
        "java:S116" // Field names should comply with a naming convention
})
class __ConcreteMappedEntity extends __MappedEntity<__ConcreteMappedEntity, Long> {

    protected __ConcreteMappedEntity() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    @Override
    public final boolean equals(final Object obj) {
        return super.equals(obj);
    }

    @Override
    public final int hashCode() {
        return super.hashCode();
    }

    // ------------------------------------------------------------------------------------------------------ super.id__
    @Override
    protected final Long getId__() {
        return id__;
    }

    @Override
    protected final void setId__(final Long id__) {
        this.id__ = id__;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Id
    private Long id__;
}
