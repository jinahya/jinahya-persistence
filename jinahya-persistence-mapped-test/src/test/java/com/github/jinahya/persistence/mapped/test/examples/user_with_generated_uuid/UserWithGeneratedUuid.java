package com.github.jinahya.persistence.mapped.test.examples.user_with_generated_uuid;

import com.github.jinahya.persistence.mapped.__MappedEntityWithGeneratedUuid;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.Objects;

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
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof __MappedEntityWithGeneratedUuid that)) {
            return false;
        }
        final var thisId = this.getId__();
        final var thatId = that.getId__();
        return thisId != null && Objects.equals(thisId, thatId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId__());
    }
}
