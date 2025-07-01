package com.github.jinahya.persistence.mapped.tests.test.user_with_generated_identity;

import com.github.jinahya.persistence.mapped.__MappedEntityWithGeneratedIdentity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.ToString;

@Entity
@Table(name = MappedUserWithGeneratedIdentity.TABLE_NAME)
@ToString(callSuper = true)
class UserWithGeneratedIdentity extends __MappedEntityWithGeneratedIdentity<UserWithGeneratedIdentity> {

    // -----------------------------------------------------------------------------------------------------------------
    protected UserWithGeneratedIdentity() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object
}