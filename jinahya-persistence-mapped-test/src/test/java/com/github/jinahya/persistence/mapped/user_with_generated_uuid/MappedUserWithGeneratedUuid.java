package com.github.jinahya.persistence.mapped.user_with_generated_uuid;

import com.github.jinahya.persistence.mapped.__MappedEntityWithGeneratedUuid;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
@AttributeOverride(
        name = __MappedEntityWithGeneratedUuid.ATTRIBUTE_NAME_ID__,
        column = @Column(
                name = MappedUserWithGeneratedUuid.COLUMN_NAME_ID,
                nullable = false,
                insertable = false,
                updatable = false
        )
)
@SuppressWarnings({
        "java:S119" // Type parameter names should comply with a naming convention
})
abstract class MappedUserWithGeneratedUuid<SELF extends MappedUserWithGeneratedUuid<SELF>>
        extends __MappedEntityWithGeneratedUuid<SELF> {

    static final String TABLE_NAME = "user_with_generated_uuid";

    // -----------------------------------------------------------------------------------------------------------------
    static final String COLUMN_NAME_ID = "id";
}