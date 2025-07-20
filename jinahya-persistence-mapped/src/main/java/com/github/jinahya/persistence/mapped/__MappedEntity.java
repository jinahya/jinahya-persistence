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

import java.util.Objects;

/**
 * An abstract mapped superclass for entities.
 *
 * @param <ID> id type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@MappedSuperclass
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public abstract class __MappedEntity<ID> {

    // ------------------------------------------------------------------------------------------ STATIC_FACTORY_METHODS

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected __MappedEntity() {
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
        return super.toString() + '{' +
               "id__=" + getId__() +
               '}';
    }

    // https://jqno.nl/equalsverifier/manual/jpa-entities/
    // https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    @Override
    @SuppressWarnings({
            "java:S117" // Local variable and method parameter names should comply with a naming convention
    })
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof __MappedEntity<?> that)) {
            return false;
        }
        final var thisId__ = getId__();
        final var thatId__ = that.getId__();
        if (thisId__ == null && thatId__ == null) {
            return false;
        }
        return Objects.equals(thisId__, thatId__);
    }

    // https://jqno.nl/equalsverifier/manual/jpa-entities/
    // https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    @Override
    public int hashCode() {
        return Objects.hashCode(getId__());
    }

    // ------------------------------------------------------------------------------------------------------------ id__

    /**
     * Returns the current value of {@link ID}.
     *
     * @return the current value of {@link ID}
     */
    @SuppressWarnings({
            "java:S100" // Method names should comply with a naming convention
    })
    protected abstract ID getId__();

    /**
     * Replaces the current value of {@link ID} with the specified value.
     *
     * @param id__ new value for the {@link ID}.
     */
    @SuppressWarnings({
            "java:S100", // Method names should comply with a naming convention
            "java:S117" // Local variable and method parameter names should comply with a naming convention
    })
    protected abstract void setId__(ID id__);
}
