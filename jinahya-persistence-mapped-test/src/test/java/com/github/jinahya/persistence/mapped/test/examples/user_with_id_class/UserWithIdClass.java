package com.github.jinahya.persistence.mapped.test.examples.user_with_id_class;

import com.github.jinahya.persistence.mapped.__MappedEntity;
import jakarta.annotation.Nonnull;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.Objects;

@IdClass(IdForUserWithIdClass.class)
@Entity
@Table(name = UserWithIdClass.TABLE_NAME)
class UserWithIdClass extends __MappedEntity<IdForUserWithIdClass> {

    public static final String TABLE_NAME = "user_with_id_class";

    // -----------------------------------------------------------------------------------------------------------------
    public static final String COLUMN_NAME_NAME = "name";

    public static final String COLUMN_NAME_AGE = "age";

    // ------------------------------------------------------------------------------------------ STATIC_FACTORY_METHODS

    // -----------------------------------------------------------------------------------------------------------------
    protected UserWithIdClass() {
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
        if (!(obj instanceof UserWithIdClass that)) {
            return false;
        }
        return name != null
               && Objects.equals(name, that.name) &&
               Objects.equals(age, that.age);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(name, age);
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
    @Nonnull
    @NotBlank
    @Id
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_NAME, nullable = false, insertable = true, updatable = false)
    private String name;

    @Nonnull
    @PositiveOrZero
    @NotNull
    @Id
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_AGE, nullable = false, insertable = true, updatable = false)
    private Integer age;
}
