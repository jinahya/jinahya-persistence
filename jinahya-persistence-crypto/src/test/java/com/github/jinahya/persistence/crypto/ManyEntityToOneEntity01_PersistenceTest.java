package com.github.jinahya.persistence.crypto;

import com.github.jinahya.persistence.mapped.test.__MappedEntity_PersistenceTest;
import jakarta.persistence.EntityManager;
import org.jboss.weld.junit5.auto.AddBeanClasses;

@AddBeanClasses({
        _EncryptionProducer.class
})
class ManyEntityToOneEntity01_PersistenceTest extends __MappedEntity_PersistenceTest<ManyEntityToOneEntity01, Long> {

    ManyEntityToOneEntity01_PersistenceTest() {
        super(ManyEntityToOneEntity01.class, Long.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    protected void __persistEntityInstance(EntityManager entityManager, ManyEntityToOneEntity01 persisted) {
        super.__persistEntityInstance(entityManager, persisted);
    }
}
