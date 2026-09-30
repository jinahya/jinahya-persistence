package com.github.jinahya.persistence.test.util;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class AbstractEntityPersister_Test {

    static class Ent {

    }

    static class EntPersister
            extends AbstractEntityPersister<Ent> {

        EntPersister() {
            super(Ent.class);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("apply(entityManager, entityInstance) -> persists, and returns the instance")
    @Test
    void apply_PersistsAndReturnsTheInstance_() {
        final var entityManager = Mockito.mock(EntityManager.class);
        final var entityInstance = new Ent();
        final var persister = new EntPersister();
        assertThat(persister.apply(entityManager, entityInstance)).isSameAs(entityInstance);
        Mockito.verify(entityManager, Mockito.times(1)).persist(entityInstance);
        // the "does not flush" contract of apply(EntityManager, Object); a caller batches, then flushes once
        Mockito.verify(entityManager, Mockito.never()).flush();
    }

    @DisplayName("apply(null, _) / apply(_, null) -> NullPointerException")
    @Test
    void apply_NullPointerException_Null() {
        final var persister = new EntPersister();
        assertThatThrownBy(() -> persister.apply(null, new Ent()))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> persister.apply(Mockito.mock(EntityManager.class), null))
                .isInstanceOf(NullPointerException.class);
    }

    @DisplayName("the constructor rejects a null target class")
    @Test
    void _NullPointerException_NullTargetClass() {
        assertThatThrownBy(() -> new AbstractEntityPersister<Ent>(null) {
        }).isInstanceOf(NullPointerException.class);
    }
}
