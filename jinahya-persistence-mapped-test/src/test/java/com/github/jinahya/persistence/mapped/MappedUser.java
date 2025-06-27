package com.github.jinahya.persistence.mapped;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
@SuppressWarnings({
        "java:S119" // Type parameter names should comply with a naming convention
})
abstract class MappedUser<SELF extends MappedUser<SELF>> extends __MappedEntity<SELF, Long> {

    public static final String TABLE_NAME = "user";
}