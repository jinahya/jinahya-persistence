package com.github.jinahya.persistence.mapped;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.UUID;

public abstract class __MappedEntityWithGeneratedUuid<SELF extends __MappedEntityWithGeneratedUuid<SELF>>
        extends __MappedEntity<SELF, UUID> {

    protected __MappedEntityWithGeneratedUuid() {
        super();
    }

    // ------------------------------------------------------------------------------------------------------ super.id__
    @Override
    protected final UUID getId__() {
        return id__;
    }

    @Override
    protected final void setId__(final UUID id__) {
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
    private UUID id__;
}
