package com.github.jinahya.persistence.mapped.user_with_generated_identity;

import com.github.jinahya.persistence.mapped.__MappedEntityWithGeneratedIdentity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.ToString;

@Entity
@Table(name = MappedUser.TABLE_NAME)
@ToString(callSuper = true)
class User extends __MappedEntityWithGeneratedIdentity<User> {

    // -----------------------------------------------------------------------------------------------------------------
    protected User() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object
}