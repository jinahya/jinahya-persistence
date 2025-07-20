package com.github.jinahya.persistence.mapped.test.examples.user_with_string_id;

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
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;

@MappedSuperclass
@SuppressWarnings({
        "java:S119" // Type parameter names should comply with a naming convention
})
abstract class _MappedUserWithStringId<SELF extends _MappedUserWithStringId<SELF>>
        extends __MappedEntity<SELF, String> {

    static final String TABLE_NAME = "user_with_string_id";

    // -----------------------------------------------------------------------------------------------------------------
    static final String COLUMN_NAME_NAME = "name";

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    _MappedUserWithStringId() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object
    @Override
    public String toString() {
        return super.toString() + '{' +
               "name=" + name +
               '}';
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof _MappedUserWithStringId<?>)) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    // ------------------------------------------------------------------------------------------------------ super.id__
    @Override
    protected String getId__() {
        return name;
    }

    @Override
    protected void setId__(final String id__) {
        this.name = id__;
    }

    // -------------------------------------------------------------------------------------------------------------- id
    @Nonnull
    public String getName() {
        return getId__();
    }

    public void setName(@Nonnull final String name) {
        setId__(name);
    }

    @SuppressWarnings({
            "unchecked"
    })
    public SELF name(@Nonnull final String name) {
        setName(name);
        return (SELF) this;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @NotNull
    @Id
    @Column(name = _MappedUserWithStringId.COLUMN_NAME_NAME, nullable = false, insertable = true, updatable = false)
    private String name;
}
