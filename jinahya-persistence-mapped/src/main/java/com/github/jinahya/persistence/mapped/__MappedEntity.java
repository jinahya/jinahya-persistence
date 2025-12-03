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
import jakarta.persistence.MappedSuperclass;

import java.util.Objects;
import java.util.function.Function;

/**
 * An abstract mapped superclass for entities.
 *
 * @param <ID> id type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@MappedSuperclass
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S114", // Interface names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public abstract class __MappedEntity<ID> extends __Mapped {

    // ------------------------------------------------------------------------------------------ STATIC_FACTORY_METHODS

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected __MappedEntity() {
        super();
    }

    /**
     * Creates a new instance built from the specified builder.
     *
     * @param builder the builder from which a new instance is built.
     */
    protected __MappedEntity(final @Nonnull __MappedEntityBuilder<?, ?> builder) {
        super(builder);
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object
    // TODO: is this usable?
    protected final boolean equalsWithId(final Function<Object, ? extends ID> mapper, final Object obj) {
        Objects.requireNonNull(mapper, "mapper is null");
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        final var thisId = Objects.requireNonNull(mapper.apply(this), "null id resulted from " + this);
        final var thatId = Objects.requireNonNull(mapper.apply(obj), "null id resulted from " + obj);
        return Objects.equals(thisId, thatId);
    }

    // TODO: is this usable?
    protected final int hashCodeWithId(final Function<Object, ? extends ID> mapper) {
        Objects.requireNonNull(mapper, "mapper is null");
        return Objects.hash(mapper.apply(this));
    }
}
