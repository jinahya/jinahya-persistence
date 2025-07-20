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

import jakarta.annotation.Nonnull;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@MappedSuperclass
@SuppressWarnings({
        "java:S119" // Type parameter names should comply with a naming convention
})
//abstract class _MappedIdForUser<SELF extends _MappedIdForUser<SELF>> implements Serializable {
abstract class _MappedIdForUser implements Serializable {

    @Serial
    private static final long serialVersionUID = -5865164757838263094L;

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    _MappedIdForUser() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    @Override
    public String toString() {
        return super.toString() + '{' +
               "name=" + name +
               ",age=" + age +
               '}';
    }

    @Override
    public final boolean equals(final Object obj) {
        if (!(obj instanceof _MappedIdForUser that)) {
            return false;
        }
        return Objects.equals(name, that.name) &&
               Objects.equals(age, that.age);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(name, age);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    public String getName() {
        return name;
    }

    public void setName(@Nonnull final String name) {
        this.name = name;
    }

    @Nonnull
    public Integer getAge() {
        return age;
    }

    public void setAge(@Nonnull final Integer age) {
        this.age = age;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Nonnull
    @NotNull
    @Basic(optional = false)
    @Column(name = _MappedUser.COLUMN_NAME_NAME, nullable = false, insertable = true,
            updatable = false)
    private String name;

    @Nonnull
    @NotNull
    @Basic(optional = false)
    @Column(name = _MappedUser.COLUMN_NAME_AGE, nullable = false, insertable = true,
            updatable = false)
    private Integer age;
}
