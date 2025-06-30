package com.github.jinahya.persistence.mapped;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

/**
 * An abstract mapped-superclass whose {@value __MappedEntityWithGeneratedUuid#ATTRIBUTE_NAME_ID__} attribute is a
 * {@link GeneratedValue} with {@link GenerationType#IDENTITY} strategy.
 *
 * @param <SELF> self type parameter
 * @see GeneratedValue
 * @see GenerationType#IDENTITY
 */
@MappedSuperclass
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S115", // Constant names should comply with a naming convention
        "java:S119", // Type parameter names should comply with a naming convention
})
public abstract class __MappedEntityWithGeneratedIdentity<SELF extends __MappedEntityWithGeneratedIdentity<SELF>>
        extends __MappedEntity<SELF, Long> {

    public static final String COLUMN_NAME_ID__ = "id__";

    public static final String ATTRIBUTE_NAME_ID__ = COLUMN_NAME_ID__;

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected __MappedEntityWithGeneratedIdentity() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object
    @Override
    public String toString() {
        return super.toString() + '{' +
               "id__=" + id__ +
               '}';
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
    @Column(name = COLUMN_NAME_ID__, nullable = false, insertable = false, updatable = false)
    @SuppressWarnings({
            "java:S116", // Field names should comply with a naming convention
            "java:S1845" // Methods and field names should not be the same or differ only by capitalization
    })
    private Long id__;
}
