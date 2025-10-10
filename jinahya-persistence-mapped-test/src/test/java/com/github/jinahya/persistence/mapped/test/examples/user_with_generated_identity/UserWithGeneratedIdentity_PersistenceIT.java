package com.github.jinahya.persistence.mapped.test.examples.user_with_generated_identity;

import com.github.jinahya.persistence.mapped.test.__Disable_EnittyColumnNames_Test;
import com.github.jinahya.persistence.mapped.test.__Disable_PersistEntityInstance_Test;
import com.github.jinahya.persistence.mapped.test.__Disable_RandomSelection_Test;
import com.github.jinahya.persistence.mapped.test.__Disable_TableColumnNames_Test;
import com.github.jinahya.persistence.mapped.test.__MappedEntityWithGeneratedIdentity_PersistenceIT;

@__Disable_PersistEntityInstance_Test
@__Disable_RandomSelection_Test
@__Disable_TableColumnNames_Test
@__Disable_EnittyColumnNames_Test
class UserWithGeneratedIdentity_PersistenceIT
        extends __MappedEntityWithGeneratedIdentity_PersistenceIT<UserWithGeneratedIdentity> {

    UserWithGeneratedIdentity_PersistenceIT() {
        super(UserWithGeneratedIdentity.class);
    }
}
