package com.github.jinahya.persistence.mapped.user_with_generated_uuid;

import com.github.jinahya.persistence.mapped.__MappedEntityWithGeneratedUuid;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.ToString;

import java.util.UUID;

@Entity
@Table(name = MappedUserWithGeneratedUuid.TABLE_NAME)
@ToString(callSuper = true)
class UserWithGeneratedUuid extends __MappedEntityWithGeneratedUuid<UserWithGeneratedUuid> {

    // -----------------------------------------------------------------------------------------------------------------
    protected UserWithGeneratedUuid() {
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