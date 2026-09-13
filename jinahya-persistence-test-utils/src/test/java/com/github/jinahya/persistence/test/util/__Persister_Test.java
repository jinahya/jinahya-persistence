package com.github.jinahya.persistence.test.util;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class __Persister_Test {

    static class Entity {

    }

    static class EntityPersister extends __Persister<Entity> {

        EntityPersister() {
            super(Entity.class);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("apply(entityManager, entityInstance) -> persists, and returns the instance")
    @Test
    void apply_PersistsAndReturnsTheInstance_() {
        final var entityManager = Mockito.mock(EntityManager.class);
        final var entityInstance = new Entity();
        final var persister = new EntityPersister();
        assertThat(persister.apply(entityManager, entityInstance)).isSameAs(entityInstance);
        Mockito.verify(entityManager, Mockito.times(1)).persist(entityInstance);
        // the "does not flush" contract of apply(EntityManager, Object); a caller batches, then flushes once
        Mockito.verify(entityManager, Mockito.never()).flush();
    }
}
