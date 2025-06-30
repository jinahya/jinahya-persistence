package com.github.jinahya.persistence.mapped.user_with_string_id;

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
abstract class MappedUser<SELF extends MappedUser<SELF>> extends __MappedEntity<SELF, String> {

    static final String TABLE_NAME = "user_with_string_id";

    // -----------------------------------------------------------------------------------------------------------------
    static final String COLUMN_NAME_NAME = "name";

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
    @NotNull
    @Id
    @Column(name = MappedUser.COLUMN_NAME_NAME, nullable = false, insertable = true, updatable = false)
    private String name;
}