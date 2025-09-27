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
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

@Entity
@Table(name = UserWithEmbeddedId.TABLE_NAME)
class UserWithEmbeddedId implements __MappedEntity<IdForUserWithEmbeddedId> {

    public static final String TABLE_NAME = "user_with_embedded_id";

    // -----------------------------------------------------------------------------------------------------------------
    public static final String COLUMN_NAME_NAME = "name";

    public static final String COLUMN_NAME_AGE = "age";

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    protected UserWithEmbeddedId() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof UserWithEmbeddedId that)) {
            return false;
        }
        final var thisId = this.getId();
        final var thatId = that.getId();
        return thisId != null && Objects.equals(thisId, thatId);
    }

    @Override
    public int hashCode() {
//        return getClass().hashCode();
        return Objects.hashCode(getId());
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Transient
    protected IdForUserWithEmbeddedId getId__() {
        return getId();
    }

    protected void setId__(final IdForUserWithEmbeddedId id__) {
        setId(id__);
    }

    // -------------------------------------------------------------------------------------------------------------- id
    public IdForUserWithEmbeddedId getId() {
        return id;
    }

    public void setId(final IdForUserWithEmbeddedId id) {
        this.id = id;
    }

    public IdForUserWithEmbeddedId getIdOrElseSetAndGet(
            @Nonnull final Supplier<? extends IdForUserWithEmbeddedId> supplier) {
        return Optional.ofNullable(getId())
                .orElseGet(
                        () -> {
                            setId(
                                    Objects.requireNonNull(
                                            Objects.requireNonNull(supplier, "supplier is null").get(),
                                            "null returned from " + supplier
                                    )
                            );
                            return getId();
                        }
                );
    }

    // -----------------------------------------------------------------------------------------------------------------
    @EmbeddedId
    @Valid
    @NotNull
    private IdForUserWithEmbeddedId id;
}
