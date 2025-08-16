package com.github.jinahya.persistence.mapped.test.examples.user_with_generated_identity;

import com.github.jinahya.persistence.mapped.__MappedEntityWithGeneratedIdentity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.Objects;

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
//    @Override
//    public final boolean equals(final Object obj) {
//        if (!(obj instanceof __MappedEntityWithGeneratedIdentity that)) {
//            return false;
//        }
//        final var thisId = this.getId__();
//        final var thatId = that.getId__();
//        return thisId != null && Objects.equals(thisId, thatId);
//    }
//
//    @Override
//    public final int hashCode() {
//        return Objects.hash(getId__());
//    }

    // -----------------------------------------------------------------------------------------------------------------
    @Basic(optional = true)
    @Column(name = "ignore", nullable = true)
    private String ignore; // EclipseLink
}
