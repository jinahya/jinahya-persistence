package com.github.jinahya.persistence.mapped.test.examples.user_with_id_class;

import com.github.jinahya.persistence.mapped.test.__Disable_EnittyColumnNames_Test;
import com.github.jinahya.persistence.mapped.test.__Disable_PersistEntityInstance_Test;
import com.github.jinahya.persistence.mapped.test.__Disable_RandomSelection_Test;
import com.github.jinahya.persistence.mapped.test.__Disable_TableColumnNames_Test;
import com.github.jinahya.persistence.mapped.test.__MappedEntity_PersistenceIT;

@__Disable_PersistEntityInstance_Test
@__Disable_RandomSelection_Test
@__Disable_TableColumnNames_Test
@__Disable_EnittyColumnNames_Test
class UserWithIdClass_PersistenceIT extends __MappedEntity_PersistenceIT<UserWithIdClass, IdForUserWithIdClass> {

    UserWithIdClass_PersistenceIT() {
        super(UserWithIdClass.class, IdForUserWithIdClass.class);
    }
}
