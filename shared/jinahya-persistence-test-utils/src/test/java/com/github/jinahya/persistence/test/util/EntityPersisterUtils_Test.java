package com.github.jinahya.persistence.test.util;

import com.github.jinahya.object.randomizer.ObjectRandomizer;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class EntityPersisterUtils_Test {

//SEP:a fully equipped entity class

    static class Ent {

        String name;
    }

    static class EntRandomizer
            implements ObjectRandomizer<Ent> {

        @Override
        public Ent get() {
            final var instance = new Ent();
            instance.name = "randomized";
            return instance;
        }
    }

    static class EntPersister
            extends AbstractEntityPersister<Ent> {

        EntPersister() {
            super(Ent.class);
        }
    }

//SEP:consumer contravariance -- a persister declared for a superclass accepts instances of the subclass

    /**
     * A subclass whose conventionally named persister is declared for its superclass.
     */
    static class SubEnt
            extends Ent {

    }

    static class SubEntPersister
            extends AbstractEntityPersister<Ent> {

        SubEntPersister() {
            super(Ent.class);
        }
    }

    static class SubEntRandomizer
            implements ObjectRandomizer<SubEnt> {

        @Override
        public SubEnt get() {
            return new SubEnt();
        }
    }

//SEP:an entity whose persister implements the role directly, and so declares no target class to be checked against

    static class Bare {

    }

    static class BareRandomizer
            implements ObjectRandomizer<Bare> {

        @Override
        public Bare get() {
            return new Bare();
        }
    }

    static class BarePersister
            implements EntityPersister<Bare> {

        @Override
        public Bare apply(final EntityManager entityManager, final Bare entityInstance) {
            entityManager.persist(entityInstance);
            return entityInstance;
        }
    }

//SEP:entity classes missing one half

    /**
     * An entity class which has a persister, but no randomizer.
     */
    static class Unrandomized {

    }

    static class UnrandomizedPersister
            extends AbstractEntityPersister<Unrandomized> {

        UnrandomizedPersister() {
            super(Unrandomized.class);
        }
    }

    /**
     * An entity class which has a randomizer, but no persister.
     */
    static class Unpersisted {

    }

    static class UnpersistedRandomizer
            implements ObjectRandomizer<Unpersisted> {

        @Override
        public Unpersisted get() {
            return new Unpersisted();
        }
    }

//SEP:an entity class whose conventionally named persister is declared for an unrelated class

    static class Foreign {

    }

    static class Misdeclared {

    }

    static class MisdeclaredRandomizer
            implements ObjectRandomizer<Misdeclared> {

        @Override
        public Misdeclared get() {
            return new Misdeclared();
        }
    }

    static class MisdeclaredPersister
            extends AbstractEntityPersister<Foreign> {

        MisdeclaredPersister() {
            super(Foreign.class);
        }
    }

    // ---------------------------------------------------------------------------------------------------------------------
    @DisplayName("newPersisterInstanceOf(Ent.class) -> present, the sibling EntPersister")
    @Test
    void newPersisterInstanceOf_Present_Ent() {
        assertThat(EntityPersisterUtils.newPersisterInstanceOf(Ent.class))
                .isPresent()
                .containsInstanceOf(EntPersister.class);
    }

    @DisplayName("newPersisterInstanceOf(Bare.class) -> present; the role may be implemented directly")
    @Test
    void newPersisterInstanceOf_Present_PersisterImplementingTheRoleDirectly() {
        assertThat(EntityPersisterUtils.newPersisterInstanceOf(Bare.class))
                .isPresent()
                .containsInstanceOf(BarePersister.class);
    }

    @DisplayName("newPersisterInstanceOf(Unpersisted.class) -> empty; no persister is named for it")
    @Test
    void newPersisterInstanceOf_Empty_NoSibling() {
        assertThat(EntityPersisterUtils.newPersisterInstanceOf(Unpersisted.class)).isEmpty();
    }

    @DisplayName("newPersisterInstanceOf(null) -> NullPointerException")
    @Test
    void newPersisterInstanceOf_NullPointerException_Null() {
        assertThatThrownBy(() -> EntityPersisterUtils.newPersisterInstanceOf(null))
                .isInstanceOf(NullPointerException.class);
    }

    @DisplayName("newPersisterInstanceOf(Misdeclared.class) -> RuntimeException;"
                 + " MisdeclaredPersister was provided, so accepting no Misdeclared is a fault, not an absence")
    @Test
    void newPersisterInstanceOf_RuntimeException_PersisterOfUnrelatedClass() {
        assertThatThrownBy(() -> EntityPersisterUtils.newPersisterInstanceOf(Misdeclared.class))
                .isExactlyInstanceOf(RuntimeException.class)
                .hasMessageContaining("accepts only");
    }

    // ---------------------------------------------------------------------------------------------------------------------
    @DisplayName("newPersistedInstanceOf(entityManager, Ent.class) -> randomized, then persisted")
    @Test
    void newPersistedInstanceOf_RandomizedThenPersisted_Ent() {
        final var entityManager = Mockito.mock(EntityManager.class);
        final var instance = EntityPersisterUtils.newPersistedInstanceOf(entityManager, Ent.class);
        assertThat(instance).isNotNull();
        assertThat(instance.name).isEqualTo("randomized");
        Mockito.verify(entityManager, Mockito.times(1)).persist(instance);
        // the "does not flush" contract of newPersistedInstanceOf
        Mockito.verify(entityManager, Mockito.never()).flush();
    }

    @DisplayName("newPersistedInstanceOf(entityManager, SubEnt.class)"
                 + " -> persisted; SubEntPersister, declared for the superclass, accepts a SubEnt")
    @Test
    void newPersistedInstanceOf_Persisted_PersisterOfSuperclass() {
        final var entityManager = Mockito.mock(EntityManager.class);
        final var instance = EntityPersisterUtils.newPersistedInstanceOf(entityManager, SubEnt.class);
        assertThat(instance).isNotNull();
        Mockito.verify(entityManager, Mockito.times(1)).persist(instance);
    }

    @DisplayName("newPersistedInstanceOf(entityManager, Bare.class) -> persisted by a directly implemented persister")
    @Test
    void newPersistedInstanceOf_Persisted_PersisterImplementingTheRoleDirectly() {
        final var entityManager = Mockito.mock(EntityManager.class);
        final var instance = EntityPersisterUtils.newPersistedInstanceOf(entityManager, Bare.class);
        assertThat(instance).isNotNull();
        Mockito.verify(entityManager, Mockito.times(1)).persist(instance);
    }

    @DisplayName("newPersistedInstanceOf(entityManager, Unrandomized.class) -> IllegalArgumentException")
    @Test
    void newPersistedInstanceOf_IllegalArgumentException_NoRandomizer() {
        final var entityManager = Mockito.mock(EntityManager.class);
        assertThatThrownBy(() -> EntityPersisterUtils.newPersistedInstanceOf(entityManager, Unrandomized.class))
                .isInstanceOf(IllegalArgumentException.class);
        Mockito.verifyNoInteractions(entityManager);
    }

    @DisplayName("newPersistedInstanceOf(entityManager, Unpersisted.class) -> IllegalArgumentException")
    @Test
    void newPersistedInstanceOf_IllegalArgumentException_NoPersister() {
        final var entityManager = Mockito.mock(EntityManager.class);
        assertThatThrownBy(() -> EntityPersisterUtils.newPersistedInstanceOf(entityManager, Unpersisted.class))
                .isInstanceOf(IllegalArgumentException.class);
        Mockito.verifyNoInteractions(entityManager);
    }

    @DisplayName("newPersistedInstanceOf(entityManager, Misdeclared.class) -> RuntimeException;"
                 + " MisdeclaredPersister was provided, so accepting no Misdeclared is a fault, not an absence")
    @Test
    void newPersistedInstanceOf_RuntimeException_PersisterOfUnrelatedClass() {
        final var entityManager = Mockito.mock(EntityManager.class);
        assertThatThrownBy(() -> EntityPersisterUtils.newPersistedInstanceOf(entityManager, Misdeclared.class))
                .isExactlyInstanceOf(RuntimeException.class)
                .hasMessageContaining("accepts only");
        Mockito.verifyNoInteractions(entityManager);
    }
}
