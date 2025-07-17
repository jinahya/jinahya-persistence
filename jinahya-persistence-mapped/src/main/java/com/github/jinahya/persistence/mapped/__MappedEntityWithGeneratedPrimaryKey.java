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

import jakarta.persistence.MappedSuperclass;

/**
 * An abstract mapped superclass for entities with generated primary keys.
 *
 * @param <SELF> self type parameter
 * @param <ID>   id type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@MappedSuperclass
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public abstract class __MappedEntityWithGeneratedPrimaryKey<
        SELF extends __MappedEntityWithGeneratedPrimaryKey<SELF, ID>,
        ID
        >
        extends __MappedEntity<SELF, ID> {

    // -----------------------------------------------------------------------------------------------------------------
    public static final String COLUMN_NAME_ID__ = "id__";

    public static final String ATTRIBUTE_NAME_ID__ = "id__";

    // ------------------------------------------------------------------------------------------ STATIC_FACTORY_METHODS

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected __MappedEntityWithGeneratedPrimaryKey() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    /**
     * Returns a string representation of this object.
     *
     * @return a string representation of this object.
     */
    @Override
    public String toString() {
        return super.toString();
    }

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
    protected abstract ID getId__();

    @Override
    protected final void setId__(final ID id__) {
        throw new UnsupportedOperationException("setting id__ is not allowed");
    }
}
