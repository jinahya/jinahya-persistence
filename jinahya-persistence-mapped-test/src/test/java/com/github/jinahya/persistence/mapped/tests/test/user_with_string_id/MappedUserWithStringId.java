package com.github.jinahya.persistence.mapped.tests.test.user_with_string_id;

import com.github.jinahya.persistence.mapped.__MappedEntity;
import jakarta.annotation.Nonnull;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
@SuppressWarnings({
        "java:S119" // Type parameter names should comply with a naming convention
})
abstract class MappedUserWithStringId<SELF extends MappedUserWithStringId<SELF>> extends __MappedEntity<SELF, String> {

    static final String TABLE_NAME = "user_with_string_id";

    // -----------------------------------------------------------------------------------------------------------------
    static final String COLUMN_NAME_NAME = "name";

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    MappedUserWithStringId() {
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
    public final boolean equals(final Object obj) {
        return super.equals(obj);
    }

    @Override
    public final int hashCode() {
        return super.hashCode();
    }

    // ------------------------------------------------------------------------------------------------------ super.id__
    @Override
    protected final String getId__() {
        return name;
    }

    @Override
    protected final void setId__(final String id__) {
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
    @jakarta.annotation.Nonnull
    @jakarta.validation.constraints.NotNull
    @jakarta.persistence.Id
    @jakarta.persistence.Column(
            name = MappedUserWithStringId.COLUMN_NAME_NAME,
            nullable = false,
            insertable = true,
            updatable = false
    )
    private String name;
}