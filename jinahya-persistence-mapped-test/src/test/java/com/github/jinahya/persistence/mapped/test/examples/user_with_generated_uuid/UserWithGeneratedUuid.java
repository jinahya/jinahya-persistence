package com.github.jinahya.persistence.mapped.test.examples.user_with_generated_uuid;

import com.github.jinahya.persistence.mapped.__MappedEntityWithGeneratedUuid;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.util.UUID;

@Entity
@Table(name = UserWithGeneratedUuid.TABLE_NAME)
@AttributeOverride(
        name = __MappedEntityWithGeneratedUuid.ATTRIBUTE_NAME_ID__,
        column = @Column(
                name = UserWithGeneratedUuid.COLUMN_NAME_ID,
                nullable = false,
                insertable = true,
                updatable = false
        )
)
class UserWithGeneratedUuid extends __MappedEntityWithGeneratedUuid {

    public static final String TABLE_NAME = "user_with_generated_uuid";

    // -----------------------------------------------------------------------------------------------------------------
    public static final String COLUMN_NAME_ID = "id";

    // -----------------------------------------------------------------------------------------------------------------

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    // -------------------------------------------------------------------------------------------------------------- id
    @Transient
    public UUID getId() {
        return super.getId__();
    }

    protected void setId(final UUID id) {
        super.setId__(id);
    }

    // -----------------------------------------------------------------------------------------------------------------
//    @Basic(optional = false)
//    @Column(name = "ignore", nullable = true)
//    private String ignore; // EclipseLink
}
