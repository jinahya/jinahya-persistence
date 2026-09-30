package com.github.jinahya.persistence.test.util;

import java.util.Objects;

@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
abstract class EntityPersister_Test<T extends EntityPersister<U>, U> {

    EntityPersister_Test(final Class<T> persisterClass, final Class<U> entityClass) {
        super();
        this.persisterClass = Objects.requireNonNull(persisterClass, "persisterClass is null");
        this.entityClass = Objects.requireNonNull(entityClass, "entityClass is null");
    }

    // -----------------------------------------------------------------------------------------------------------------
    final Class<T> persisterClass;

    final Class<U> entityClass;
}
