package com.github.jinahya.persistence.test.util;

import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class __PersisterUtils_Test {

//SEP:a fully equipped entity class

    static class Ent {

        String name;
    }

    static class EntRandomizer extends __Randomizer<Ent> {

        EntRandomizer() {
            super(Ent.class, List.of());
        }

        @Nonnull
        @Override
        public Ent get() {
            final var instance = new Ent();
            instance.name = "randomized";
            return instance;
        }
    }

    static class EntPersister extends __Persister<Ent> {

        EntPersister() {
            super(Ent.class);
        }
    }

    /**
     * A subclass which has its own randomizer, but no persister of its own.
     */
    static class SubEnt extends Ent {

    }

    static class SubEntRandomizer extends __Randomizer<SubEnt> {

        SubEntRandomizer() {
            super(SubEnt.class, List.of());
        }

        @Nonnull
        @Override
        public SubEnt get() {
            return new SubEnt();
        }
    }

//SEP:entity classes missing one half

    /**
     * An entity class which has a persister, but no randomizer.
     */
    static class Unrandomized {

    }

    static class UnrandomizedPersister extends __Persister<Unrandomized> {

        UnrandomizedPersister() {
            super(Unrandomized.class);
        }
    }

    /**
     * An entity class which has a randomizer, but no persister.
     */
    static class Unpersisted {

    }

    static class UnpersistedRandomizer extends __Randomizer<Unpersisted> {

        UnpersistedRandomizer() {
            super(Unpersisted.class, List.of());
        }

        @Nonnull
        @Override
        public Unpersisted get() {
            return new Unpersisted();
        }
    }

//SEP:an unrelated persister

    static class Foreign {

    }

    static class ForeignPersister extends __Persister<Foreign> {

        ForeignPersister() {
            super(Foreign.class);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("newPersistedInstanceOf(entityManager, Ent.class) -> randomized, then persisted")
    @Test
    void newPersistedInstanceOf_RandomizedThenPersisted_Ent() {
        final var entityManager = Mockito.mock(EntityManager.class);
        final var instance = __PersisterUtils.newPersistedInstanceOf(entityManager, Ent.class);
        assertThat(instance).isNotNull();
        assertThat(instance.name).isEqualTo("randomized");
        Mockito.verify(entityManager, Mockito.times(1)).persist(instance);
    }

    @DisplayName("newPersistedInstanceOf(entityManager, SubEnt.class, c -> EntPersister.class)"
                 + " -> persisted; a persister of a superclass accepts the subclass")
    @Test
    void newPersistedInstanceOf_Persisted_PersisterOfSuperclass() {
        final var entityManager = Mockito.mock(EntityManager.class);
        final var instance = __PersisterUtils.newPersistedInstanceOf(
                entityManager,
                SubEnt.class,
                c -> EntPersister.class
        );
        assertThat(instance).isNotNull();
        Mockito.verify(entityManager, Mockito.times(1)).persist(instance);
    }

    @DisplayName("newPersistedInstanceOf(entityManager, Unrandomized.class) -> IllegalArgumentException")
    @Test
    void newPersistedInstanceOf_IllegalArgumentException_NoRandomizer() {
        final var entityManager = Mockito.mock(EntityManager.class);
        assertThatThrownBy(() -> __PersisterUtils.newPersistedInstanceOf(entityManager, Unrandomized.class))
                .isInstanceOf(IllegalArgumentException.class);
        Mockito.verifyNoInteractions(entityManager);
    }

    @DisplayName("newPersistedInstanceOf(entityManager, Unpersisted.class) -> IllegalArgumentException")
    @Test
    void newPersistedInstanceOf_IllegalArgumentException_NoPersister() {
        final var entityManager = Mockito.mock(EntityManager.class);
        assertThatThrownBy(() -> __PersisterUtils.newPersistedInstanceOf(entityManager, Unpersisted.class))
                .isInstanceOf(IllegalArgumentException.class);
        Mockito.verifyNoInteractions(entityManager);
    }

    @DisplayName("newPersistedInstanceOf(entityManager, Ent.class, c -> ForeignPersister.class)"
                 + " -> IllegalArgumentException; the persister accepts no Ent")
    @Test
    void newPersistedInstanceOf_IllegalArgumentException_PersisterOfUnrelatedClass() {
        final var entityManager = Mockito.mock(EntityManager.class);
        assertThatThrownBy(() -> __PersisterUtils.newPersistedInstanceOf(
                entityManager,
                Ent.class,
                c -> ForeignPersister.class
        )).isInstanceOf(IllegalArgumentException.class);
        Mockito.verifyNoInteractions(entityManager);
    }
}
