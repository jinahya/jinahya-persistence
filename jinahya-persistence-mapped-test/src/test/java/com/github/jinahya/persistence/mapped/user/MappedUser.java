package com.github.jinahya.persistence.mapped.user;

import com.github.jinahya.persistence.mapped.__MappedEntity;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
@SuppressWarnings({
        "java:S119" // Type parameter names should comply with a naming convention
})
abstract class MappedUser<SELF extends MappedUser<SELF>> extends __MappedEntity<SELF, String> {

    static final String TABLE_NAME = "user";

    // -----------------------------------------------------------------------------------------------------------------
    static final String COLUMN_NAME_ID = "id";
}