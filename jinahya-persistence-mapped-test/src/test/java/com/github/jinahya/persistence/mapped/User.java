package com.github.jinahya.persistence.mapped;

import jakarta.persistence.*;
import lombok.ToString;

@Entity
@Table(name = MappedUser.TABLE_NAME)
@ToString(callSuper = true)
@AttributeOverride(
        name = __MappedEntityWithGeneratedIdentity.ATTRIBUTE_NAME_ID__, // "id__"
        column = @Column(name = MappedUser.COLUMN_NAME_ID, nullable = false, insertable = false, updatable = false)
)
class User extends __MappedEntity<User, Long> {

    // -----------------------------------------------------------------------------------------------------------------
    protected User() {
        super();
    }

    // ------------------------------------------------------------------------------------------------------ super.id__
    @Override
    protected Long getId__() {
        return id__;
    }

    @Override
    protected void setId__(final Long id__) {
        this.id__ = id__;
    }

    // -------------------------------------------------------------------------------------------------------------- id
    public Long getId() {
        return getId__();
    }

    protected void setId(final Long id) {
        setId__(id);
    }

    @Id
    @Column(name = MappedUser.COLUMN_NAME_ID, nullable = false, insertable = false, updatable = false)
    private Long id__;
}