package com.github.jinahya.persistence.mapped.user_with_generated_uuid;

import com.github.jinahya.persistence.mapped.__MappedEntityWithGeneratedUuid;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.ToString;

import java.util.UUID;

@Entity
@Table(name = MappedUser.TABLE_NAME)
@ToString(callSuper = true)
class User extends __MappedEntityWithGeneratedUuid<User> {

    // -----------------------------------------------------------------------------------------------------------------
    protected User() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    // -------------------------------------------------------------------------------------------------------------- id
    public UUID getId() {
        return getId__();
    }

    void setId(final UUID id) {
        setId__(id);
    }
}