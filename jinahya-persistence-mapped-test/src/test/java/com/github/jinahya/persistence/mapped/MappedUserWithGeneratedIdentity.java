package com.github.jinahya.persistence.mapped;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
@AttributeOverride(
        name = __MappedEntityWithGeneratedIdentity.ATTRIBUTE_NAME_ID__,
        column = @Column(name = MappedUserWithGeneratedIdentity.COLUMN_NAME_ID, nullable = false, insertable = false,
                updatable = false)
)
@SuppressWarnings({
        "java:S119" // Type parameter names should comply with a naming convention
})
abstract class MappedUserWithGeneratedIdentity<SELF extends MappedUserWithGeneratedIdentity<SELF>>
        extends __MappedEntityWithGeneratedIdentity<SELF> {

    public static final String TABLE_NAME = "user_with_generated_identity";

    // -----------------------------------------------------------------------------------------------------------------
    public static final String COLUMN_NAME_ID = "id";
}