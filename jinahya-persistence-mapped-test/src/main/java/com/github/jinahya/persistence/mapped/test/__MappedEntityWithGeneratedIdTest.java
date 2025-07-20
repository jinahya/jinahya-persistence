package com.github.jinahya.persistence.mapped.test;

import com.github.jinahya.persistence.mapped.__MappedEntityWithGeneratedId;

public abstract class __MappedEntityWithGeneratedIdTest<ENITTY extends __MappedEntityWithGeneratedId<ID>, ID>
        extends __MappedEntityTest<ENITTY, ID> {

    protected __MappedEntityWithGeneratedIdTest(final Class<ENITTY> entityClass, final Class<ID> idClass) {
        super(entityClass, idClass);
    }
}
