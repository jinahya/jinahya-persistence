package com.github.jinahya.persistence.mapped;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
@SuppressWarnings({
        "java:S119" // Type parameter names should comply with a naming convention
})
abstract class MappedUser2<SELF extends MappedUser2<SELF>> extends __MappedEntityWithGeneratedIdentity<SELF> {

    public static final String TABLE_NAME = "user2";

    public static final String COLUMN_NAME_ID = "id";
}