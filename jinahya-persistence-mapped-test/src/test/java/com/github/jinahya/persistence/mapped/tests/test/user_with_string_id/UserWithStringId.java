package com.github.jinahya.persistence.mapped.tests.test.user_with_string_id;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.ToString;

@Entity
@Table(name = MappedUserWithStringId.TABLE_NAME)
@ToString(callSuper = true)
class UserWithStringId extends MappedUserWithStringId<UserWithStringId> {

    // -----------------------------------------------------------------------------------------------------------------
    protected UserWithStringId() {
        super();
    }
}