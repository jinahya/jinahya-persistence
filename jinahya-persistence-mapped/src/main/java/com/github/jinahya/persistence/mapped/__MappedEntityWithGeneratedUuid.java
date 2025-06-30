package com.github.jinahya.persistence.mapped;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import java.util.UUID;

/**
 * An abstract mapped-superclass whose {@link __MappedEntityWithGeneratedUuid#ATTRIBUTE_NAME_ID__ id__} attribute is a
 * {@link GeneratedValue generated} {@link GenerationType#IDENTITY identity}.
 * <p>
 * Subclasses might want to override the attribute for the actual column.
 * {@snippet lang = "java":
 *     @AttributeOverride(
 *         name = __MappedEntityWithGeneratedIdentity.ATTRIBUTE_NAME_ID__, // "id__"
 *         column = @Column(name = "whatever", nullable = false, insertable = false, updatable = false)
 *     )
 *}
 *
 * @param <SELF> self type parameter
 * @see GeneratedValue
 * @see GenerationType#UUID
 */
@MappedSuperclass
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S115", // Constant names should comply with a naming convention
        "java:S119", // Type parameter names should comply with a naming convention
})
public abstract class __MappedEntityWithGeneratedUuid<SELF extends __MappedEntityWithGeneratedUuid<SELF>>
        extends __MappedEntity<SELF, UUID> {

    // -----------------------------------------------------------------------------------------------------------------
    public static final String COLUMN_NAME_ID__ = "id__";

    public static final String ATTRIBUTE_NAME_ID__ = COLUMN_NAME_ID__;

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
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
    @Column(name = COLUMN_NAME_ID__, nullable = false, insertable = false, updatable = false)
    @SuppressWarnings({
            "java:S116", // Field names should comply with a naming convention
            "java:S1845" // Methods and field names should not be the same or differ only by capitalization
    })
    private UUID id__;
}
