package com.github.jinahya.persistence.mapped;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public abstract class __MappedEntityWithGeneratedIdentity<SELF extends __MappedEntityWithGeneratedIdentity<SELF>>
        extends __MappedEntity<SELF, Long> {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    protected __MappedEntityWithGeneratedIdentity() {
        super();
    }

    // ------------------------------------------------------------------------------------------------------ super.id__
    @Override
    protected final Long getId__() {
        return id__;
    }

    @Override
    protected final void setId__(final Long id__) {
        this.id__ = id__;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id__", nullable = false, insertable = false, updatable = false)
    @SuppressWarnings({
            "java:S116", // Field names should comply with a naming convention
            "java:S1845" // Methods and field names should not be the same or differ only by capitalization
    })
    private Long id__;
}
