package com.github.jinahya.persistence.mapped.test.examples.user_with_generated_identity;

import com.github.jinahya.persistence.mapped.__MappedEntityWithGeneratedIdentity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = UserWithGeneratedIdentity.TABLE_NAME)
@AttributeOverride(
        name = __MappedEntityWithGeneratedIdentity.ATTRIBUTE_NAME_ID__,
        column = @Column(
                name = UserWithGeneratedIdentity.COLUMN_NAME_ID,
                nullable = false,
                insertable = true, // EclipseLink
                updatable = false
        )
)
class UserWithGeneratedIdentity extends __MappedEntityWithGeneratedIdentity {

    public static final String TABLE_NAME = "user_with_generated_identity";

    // -----------------------------------------------------------------------------------------------------------------
    public static final String COLUMN_NAME_ID = "id";

    // -----------------------------------------------------------------------------------------------------------------

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    // -------------------------------------------------------------------------------------------------------------- id
    @Transient
    public Long getId() {
        return super.getId__();
    }

    protected void setId(final Long id) {
        super.setId__(id);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Basic(optional = true)
    @Column(name = "ignore", nullable = true)
    private String ignore; // EclipseLink
}
