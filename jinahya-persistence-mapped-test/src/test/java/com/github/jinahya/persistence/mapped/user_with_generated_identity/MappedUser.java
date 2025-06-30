package com.github.jinahya.persistence.mapped.user_with_generated_identity;

import com.github.jinahya.persistence.mapped.__MappedEntityWithGeneratedIdentity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;

@MappedSuperclass
@AttributeOverride(
        name = __MappedEntityWithGeneratedIdentity.ATTRIBUTE_NAME_ID__,
        column = @Column(
                name = MappedUser.COLUMN_NAME_ID,
                nullable = false,
                insertable = false,
                updatable = false
        )
)
@SuppressWarnings({
        "java:S119" // Type parameter names should comply with a naming convention
})
abstract class MappedUser<SELF extends MappedUser<SELF>>
        extends __MappedEntityWithGeneratedIdentity<SELF> {

    static final String TABLE_NAME = "user_with_generated_identity";

    // -----------------------------------------------------------------------------------------------------------------
    static final String COLUMN_NAME_ID = "id";

    // -------------------------------------------------------------------------------------------------------------- id
    @Transient
    public Long getId() {
        return getId__();
    }

    void setId(final Long id) {
        setId__(id);
    }
}