package com.github.jinahya.persistence.mapped;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public abstract class __MappedEntityWithGeneratedUuidString<SELF extends __MappedEntityWithGeneratedUuidString<SELF>>
        extends __MappedEntity<SELF, String> {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    protected __MappedEntityWithGeneratedUuidString() {
        super();
    }

    // ------------------------------------------------------------------------------------------------------ super.id__
    @Override
    protected final String getId__() {
        return id__;
    }

    @Override
    protected final void setId__(final String id__) {
        this.id__ = id__;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id__", nullable = false, insertable = false, updatable = false)
    @SuppressWarnings({
            "java:S116", // Field names should comply with a naming convention
            "java:S1845" // Methods and field names should not be the same or differ only by capitalization
    })
    private String id__;
}
