package com.github.jinahya.persistence.mapped.test.examples.user_with_id_class;

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
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@MappedSuperclass
class IdForUserWithIdClass implements Serializable {

    @Serial
    private static final long serialVersionUID = -2192852258006202034L;

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    IdForUserWithIdClass() {
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
        if (!(obj instanceof IdForUserWithIdClass that)) {
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
    private String name;

    @Nonnull
    @NotNull
    private Integer age;
}
