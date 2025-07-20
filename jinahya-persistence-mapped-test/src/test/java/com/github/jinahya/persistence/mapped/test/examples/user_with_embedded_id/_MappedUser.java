package com.github.jinahya.persistence.mapped.test.examples.user_with_embedded_id;

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
import jakarta.annotation.Nonnull;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

@MappedSuperclass
@SuppressWarnings({
        "java:S119" // Type parameter names should comply with a naming convention
})
abstract class _MappedUser<
        SELF extends _MappedUser<SELF, ID>,
        ID extends _MappedIdForUser
        >
        extends __MappedEntity<SELF, ID> {

    static final String TABLE_NAME = "user_with_embedded_id";

    // -----------------------------------------------------------------------------------------------------------------
    static final String COLUMN_NAME_NAME = "name";

    static final String COLUMN_NAME_AGE = "age";

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    _MappedUser() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object
    @Override
    public String toString() {
        return super.toString() + '{' +
               "id__=" + id +
               '}';
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof _MappedUser<?, ?>)) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    // ------------------------------------------------------------------------------------------------------ super.id__
    protected ID getId__() {
        return getId();
    }

    protected void setId__(final ID id__) {
        setId(id__);
    }

    // -------------------------------------------------------------------------------------------------------------- id
    public ID getId() {
        return id;
    }

    public void setId(final ID id) {
        this.id = id;
    }

    public final SELF id(final ID id) {
        setId(id);
        return (SELF) this;
    }

    public final ID getIdOrElseSetAndGet(@Nonnull final Supplier<? extends ID> supplier) {
        return Optional.ofNullable(getId())
                .orElseGet(
                        () -> id(
                                Objects.requireNonNull(
                                        Objects.requireNonNull(supplier, "supplier is null").get(),
                                        "null returned from " + supplier
                                )
                        ).getId()
                );
    }

    // -----------------------------------------------------------------------------------------------------------------
    @EmbeddedId
//    @Valid
    @NotNull
    private ID id;
}
