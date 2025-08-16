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
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class IdForUserWithEmbeddedId implements Serializable {

    @Serial
    private static final long serialVersionUID = 7137484531059857534L;

    /**
     * Creates a new instance with the specified name and age.
     *
     * @param name the name
     * @param age  the age
     */
    public IdForUserWithEmbeddedId(@NotBlank final String name, @NotNull final Integer age) {
        super();
        this.name = Objects.requireNonNull(name, "name is null");
        this.age = Objects.requireNonNull(age, "age is null");
    }

    /**
     * Creates a new instance with the specified name and age.
     *
     * @param name the name
     * @param age  the age
     * @return a new instance of {@code UserId}
     */
    public static IdForUserWithEmbeddedId of(@NotBlank final String name, @NotNull final Integer age) {
        return new IdForUserWithEmbeddedId(name, age);
    }

    // -----------------------------------------------------------------------------------------------------------------
    protected IdForUserWithEmbeddedId() {
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
    public boolean equals(final Object obj) {
        if (!(obj instanceof IdForUserWithEmbeddedId that)) {
            return false;
        }
        return Objects.equals(name, that.name)
               && Objects.equals(age, that.age);
    }

    @Override
    public int hashCode() {
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

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    public Integer getAge() {
        return age;
    }

    public void setAge(@Nonnull final Integer age) {
        this.age = age;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @NotBlank
    @Basic(optional = false)
    @Column(name = UserWithEmbeddedId.COLUMN_NAME_NAME, nullable = false, insertable = true, updatable = false)
    private String name;

    @Nonnull
    @PositiveOrZero
    @NotNull
    @Basic(optional = false)
    @Column(name = UserWithEmbeddedId.COLUMN_NAME_AGE, nullable = false, insertable = true, updatable = false)
    private Integer age;
}
