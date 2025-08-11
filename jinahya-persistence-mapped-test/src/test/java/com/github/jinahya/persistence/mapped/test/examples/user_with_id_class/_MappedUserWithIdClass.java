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

import com.github.jinahya.persistence.mapped.__MappedEntity;
import jakarta.annotation.Nonnull;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

@MappedSuperclass
@SuppressWarnings({
        "java:S119" // Type parameter names should comply with a naming convention
})
abstract class _MappedUserWithIdClass<ID extends _MappedIdForUserWithIdClass>
        extends __MappedEntity<ID> {

    static final String TABLE_NAME = "user_with_id_class";

    // -----------------------------------------------------------------------------------------------------------------
    static final String COLUMN_NAME_NAME = "name";

    static final String COLUMN_NAME_AGE = "age_check";

    // ------------------------------------------------------------------------------------------ STATIC_FACTORY_METHODS
    protected static <T extends _MappedUserWithIdClass<ID>, ID extends _MappedIdForUserWithIdClass> T newInstance(
            @Nonnull final Supplier<T> instantiator, final String name, final Integer age) {
        final var instance = Objects.requireNonNull(
                Objects.requireNonNull(instantiator, "instantiator is null").get(),
                "null instantiated from " + instantiator
        );
        instance.setName(name);
        instance.setAge(age);
        return instance;
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    _MappedUserWithIdClass() {
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
        if (!(obj instanceof _MappedUserWithIdClass<?> that)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        return Objects.equals(name, that.name) &&
               Objects.equals(age, that.age);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(super.hashCode(), name, age);
    }

    // ------------------------------------------------------------------------------------------------------ super.id__
    @Override
    protected ID getId__() {
        return null;
    }

    @Override
    protected void setId__(final ID id__) {
        setName(
                Optional.ofNullable(id__)
                        .map(_MappedIdForUserWithIdClass::getName)
                        .orElse(null)
        );
        setAge(
                Optional.ofNullable(id__)
                        .map(_MappedIdForUserWithIdClass::getAge)
                        .orElse(null)
        );
    }

    // ------------------------------------------------------------------------------------------------------------ name
    @Nonnull
    public String getName() {
        return name;
    }

    protected void setName(@Nonnull final String name) {
        this.name = name;
    }

    // ------------------------------------------------------------------------------------------------------------- age
    @Nonnull
    public Integer getAge() {
        return age;
    }

    protected void setAge(@Nonnull final Integer age) {
        this.age = age;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Id
    @Nonnull
    @NotBlank
    @Basic(optional = false)
    @Column(name = _MappedUserWithIdClass.COLUMN_NAME_NAME, nullable = false, insertable = true, updatable = false)
    private String name;

    @Id
    @Nonnull
    @PositiveOrZero
    @NotNull
    @Basic(optional = false)
    @Column(name = _MappedUserWithIdClass.COLUMN_NAME_AGE, nullable = false, insertable = true, updatable = false)
    private Integer age;
}
