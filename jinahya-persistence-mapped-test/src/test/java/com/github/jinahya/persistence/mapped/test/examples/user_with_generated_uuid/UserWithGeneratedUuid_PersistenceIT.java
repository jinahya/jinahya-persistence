package com.github.jinahya.persistence.mapped.test.examples.user_with_generated_uuid;

import com.github.jinahya.persistence.mapped.test.__Disable_EnittyColumnNames_Test;
import com.github.jinahya.persistence.mapped.test.__Disable_PersistEntityInstance_Test;
import com.github.jinahya.persistence.mapped.test.__Disable_RandomSelection_Test;
import com.github.jinahya.persistence.mapped.test.__Disable_TableColumnNames_Test;
import com.github.jinahya.persistence.mapped.test.__MappedEntityWithGeneratedUuid_PersistenceIT;

@__Disable_PersistEntityInstance_Test
@__Disable_RandomSelection_Test
@__Disable_TableColumnNames_Test
@__Disable_EnittyColumnNames_Test
class UserWithGeneratedUuid_PersistenceIT
        extends __MappedEntityWithGeneratedUuid_PersistenceIT<UserWithGeneratedUuid> {

    UserWithGeneratedUuid_PersistenceIT() {
        super(UserWithGeneratedUuid.class);
    }
}
