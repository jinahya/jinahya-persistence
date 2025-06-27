package com.github.jinahya.persistence.mapped;

import jakarta.annotation.Nonnull;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;

import java.util.Objects;

@MappedSuperclass
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public abstract class __MappedEntity<SELF extends __MappedEntity<SELF, ID>, ID> {

    // ------------------------------------------------------------------------------------------ STATIC_FACTORY_METHODS

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    /**
     * Creates a new instance.
     */
    protected __MappedEntity() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object
    @Override
    public String toString() {
        return super.toString() + '{' +
                "id__=" + getId__() +
                '}';
    }

    // https://jqno.nl/equalsverifier/manual/jpa-entities/
    // https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof __MappedEntity<?, ?> that)) {
            return false;
        }
        return Objects.equals(getId__(), that.getId__());
    }

    // https://jqno.nl/equalsverifier/manual/jpa-entities/
    // https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    @Override
    public final int hashCode() {
        return Objects.hashCode(getId__());
    }

    // ------------------------------------------------------------------------------------------------------------ id__
    @SuppressWarnings({
            "java:S100" // Method names should comply with a naming convention
    })
    protected abstract ID getId__();

    @SuppressWarnings({
            "java:S100", // Method names should comply with a naming convention
            "java:S117" // Local variable and method parameter names should comply with a naming convention
    })
    protected abstract void setId__(ID id__);

    @Nonnull
    @SuppressWarnings({
            "java:S100", // Method names should comply with a naming convention
            "java:S117", // Local variable and method parameter names should comply with a naming convention
            "unchecked"
    })
    protected SELF id__(final ID id__) {
        setId__(id__);
        return (SELF) this;
    }
}
