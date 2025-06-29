package com.github.jinahya.persistence.mapped.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.ToString;

@Entity
@Table(name = MappedUser.TABLE_NAME)
@ToString(callSuper = true)
class User extends MappedUser<User> {

    // -----------------------------------------------------------------------------------------------------------------
    protected User() {
        super();
    }

    // ------------------------------------------------------------------------------------------------------ super.id__
    @Override
    protected String getId__() {
        return id__;
    }

    @Override
    protected void setId__(final String id__) {
        this.id__ = id__;
    }

    // -------------------------------------------------------------------------------------------------------------- id
    public String getId() {
        return getId__();
    }

    protected void setId(final String id) {
        setId__(id);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Id
    @Column(name = MappedUser.COLUMN_NAME_ID, nullable = false, insertable = false, updatable = false)
    private String id__;
}