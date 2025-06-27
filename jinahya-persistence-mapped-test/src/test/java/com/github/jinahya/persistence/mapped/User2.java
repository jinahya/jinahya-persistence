package com.github.jinahya.persistence.mapped;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.ToString;

@Entity
@Table(name = MappedUser.TABLE_NAME)
@ToString(callSuper = true)
@AttributeOverride(
        name = __MappedEntityWithGeneratedIdentity.ATTRIBUTE_NAME_ID__, // "id__"
        column = @Column(name = MappedUser.COLUMN_NAME_ID, nullable = false, insertable = false, updatable = false)
)
class User2 extends __MappedEntityWithGeneratedIdentity<User2> {

    // -----------------------------------------------------------------------------------------------------------------
    protected User2() {
        super();
    }

    // -----------------------------------------------------------------------------------------------------------------

    // -------------------------------------------------------------------------------------------------------------- id
    public Long getId() {
        return getId__();
    }

    protected void setId(final Long id) {
        setId__(id);
    }
}