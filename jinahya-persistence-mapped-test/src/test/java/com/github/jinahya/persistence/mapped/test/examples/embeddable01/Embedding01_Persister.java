package com.github.jinahya.persistence.mapped.test.examples.embeddable01;

import com.github.jinahya.persistence.mapped.test.__MappedEntity_Persister;
import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityManager;

class Embedding01_Persister extends __MappedEntity_Persister<Embedding01, Long> {

    Embedding01_Persister() {
        super(Embedding01.class, Long.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public void persist(@Nonnull EntityManager entityManager, @Nonnull Embedding01 entityInstance) {
        super.persist(entityManager, entityInstance);
    }
}
