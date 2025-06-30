package com.github.jinahya.persistence.mapped.user_with_string_id;

import jakarta.persistence.Entity;
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
}