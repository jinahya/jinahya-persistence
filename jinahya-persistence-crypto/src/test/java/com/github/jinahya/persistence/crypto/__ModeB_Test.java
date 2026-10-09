package com.github.jinahya.persistence.crypto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Measures, on both providers, what a Mode B entity (transient plaintext) does (#82). The Mode B entity is mapped onto
 * the same table as the Mode A one, {@link _GuardedEntity}, so that rows can cross from one mode to the other.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class __ModeB_Test {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static EntityManagerFactory ENTITY_MANAGER_FACTORY;

    @BeforeAll
    static void setUp() {
        ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("__cryptoPU");
        _LifecycleListenerEntity.SERVICE =
                new _EntityEncryptionService(ENTITY_MANAGER_FACTORY, new _EntityEncryptionManager());
    }

    @AfterAll
    static void tearDown() {
        _LifecycleListenerEntity.SERVICE = null;
        if (ENTITY_MANAGER_FACTORY != null) {
            ENTITY_MANAGER_FACTORY.close();
        }
    }

    private static <R> R apply(final Function<? super EntityManager, ? extends R> function) {
        try (var em = ENTITY_MANAGER_FACTORY.createEntityManager()) {
            final var tx = em.getTransaction();
            tx.begin();
            try {
                final var result = function.apply(em);
                tx.commit();
                return result;
            } catch (final RuntimeException re) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                throw re;
            }
        }
    }

    /** {@code version, name, name_enc} of the row, read natively. */
    private static Object[] row(final long id) {
        return apply(em -> (Object[]) em.createNativeQuery(
                        "SELECT version, name, name_enc FROM " + _GuardedEntity.TABLE_NAME + " WHERE id = " + id)
                .getSingleResult());
    }

    /** The plaintext of the row, as Mode B reads it. */
    private static String readB(final long id) {
        return apply(em -> em.find(_GuardedModeBEntity.class, id).name);
    }

    /** The plaintext of the row, as Mode A reads it. */
    private static String readA(final long id) {
        return apply(em -> em.find(_GuardedEntity.class, id).name);
    }

    private static long persistB(final String name) {
        return apply(em -> {
            final var entity = new _GuardedModeBEntity();
            entity.setName(name);
            em.persist(entity);
            return entity;
        }).id;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("B: the plaintext reaches no column, and the object keeps it after persist() and flush")
    @Test
    void __persistKeepsTheObject() {
        apply(em -> {
            final var entity = new _GuardedModeBEntity();
            entity.setName("keep-me");
            em.persist(entity);
            assertThat(entity.name).as("after persist()").isEqualTo("keep-me");
            em.flush();
            assertThat(entity.name).as("after flush()").isEqualTo("keep-me");
            final var row = (Object[]) em.createNativeQuery(
                            "SELECT name, name_enc FROM " + _GuardedEntity.TABLE_NAME + " WHERE id = " + entity.id)
                    .getSingleResult();
            assertThat(row[0]).as("the leftover column stays NULL").isNull();
            assertThat(row[1]).as("the ciphertext is stored").isNotNull();
            return null;
        });
    }

    @DisplayName("B: a transaction which only reads does not write")
    @Test
    void __readDoesNotWrite() {
        final var id = persistB("read-me");
        final var before = row(id);
        final var read = readB(id);
        final var after = row(id);
        log.info("[B read] version {} -> {}", before[0], after[0]);
        assertThat(read).isEqualTo("read-me");
        assertThat(after[0]).as("no UPDATE: the version is unchanged").isEqualTo(before[0]);
        assertThat((byte[]) after[2]).as("nor the ciphertext").isEqualTo((byte[]) before[2]);
    }

    @DisplayName("B: an edit through the setter is stored; a direct field write is lost")
    @Test
    void __editDetection() {
        final var id = persistB("first");
        apply(em -> {
            em.find(_GuardedModeBEntity.class, id).setName("second");
            return null;
        });
        assertThat(readB(id)).as("setter").isEqualTo("second");

        apply(em -> {
            em.find(_GuardedModeBEntity.class, id).name = "third"; // the field, not the setter
            return null;
        });
        final var afterFieldWrite = readB(id);
        log.info("[B edit] after a direct field write: '{}'", afterFieldWrite);
        assertThat(afterFieldWrite).as("a transient field is not dirty-checked: the edit is lost").isEqualTo("second");
    }

    @DisplayName("B: a value can be cleared")
    @Test
    void __clear() {
        final var id = persistB("clear-me");
        apply(em -> {
            em.find(_GuardedModeBEntity.class, id).setName(null);
            return null;
        });
        assertThat(readB(id)).isNull();
        assertThat(row(id)[2]).as("the ciphertext is gone too").isNull();
    }

    @DisplayName("A -> B: a row written in Mode A reads in Mode B")
    @Test
    void __aToB() {
        final long id = apply(em -> {
            final var entity = new _GuardedEntity();
            entity.name = "written-in-A";
            em.persist(entity);
            return entity;
        }).id;
        assertThat(readB(id)).isEqualTo("written-in-A");
    }

    @DisplayName("B -> A: a row written in Mode B reads in Mode A (a rollback, or a mixed rolling deploy)")
    @Test
    void __bToA() {
        final var id = persistB("written-in-B");
        assertThat(readA(id)).isEqualTo("written-in-B");
        // and A writing it again leaves it readable in B
        apply(em -> {
            em.find(_GuardedEntity.class, id).name = "rewritten-in-A";
            return null;
        });
        assertThat(readB(id)).isEqualTo("rewritten-in-A");
    }

    @DisplayName("B cannot see a legacy plaintext row: every row has to be migrated before switching")
    @Test
    void __legacyRowInvisible() {
        final var plaintext = "legacy-" + System.nanoTime();
        apply(em -> em.createNativeQuery(
                        "INSERT INTO " + _GuardedEntity.TABLE_NAME + " (version, name, name_enc)"
                        + " VALUES (0, '" + plaintext + "', NULL)")
                .executeUpdate());
        final long id = apply(em -> ((Number) em.createNativeQuery(
                        "SELECT id FROM " + _GuardedEntity.TABLE_NAME + " WHERE name = '" + plaintext + "'")
                .getSingleResult()).longValue());
        assertThat(readB(id))
                .as("the value is still in the leftover column, but B has no mapping for it")
                .isNull();
    }

    @DisplayName("B: a direct field write is kept when another change makes the provider flush the instance")
    @Test
    void __safetyNet() {
        final long id = apply(em -> {
            final var entity = new _ModeBNoteEntity();
            entity.setName("first");
            em.persist(entity);
            return entity;
        }).id;
        apply(em -> {
            final var entity = em.find(_ModeBNoteEntity.class, id);
            entity.name = "direct";  // the field, not the setter
            entity.note = "changed"; // but something mapped changed too, so the instance is flushed
            return null;
        });
        final String stored = apply(em -> em.find(_ModeBNoteEntity.class, id).name);
        assertThat(stored)
                .as("@PreUpdate compares the plaintext with the ciphertext, and re-encrypts")
                .isEqualTo("direct");
    }

    @DisplayName("B: an unrelated change does not re-encrypt an unchanged plaintext")
    @Test
    void __noNeedlessReEncryption() {
        final long id = apply(em -> {
            final var entity = new _ModeBNoteEntity();
            entity.setName("stable");
            em.persist(entity);
            return entity;
        }).id;
        final var before = apply(em -> em.find(_ModeBNoteEntity.class, id).nameEnc__.clone());
        apply(em -> {
            em.find(_ModeBNoteEntity.class, id).note = "unrelated";
            return null;
        });
        final var after = apply(em -> em.find(_ModeBNoteEntity.class, id).nameEnc__.clone());
        assertThat(after).as("the same ciphertext, no new IV").isEqualTo(before);
    }

    @DisplayName("B: a Bean Validation constraint on the plaintext sees the real value")
    @Test
    void __constraintSeesTheValue() {
        final var thrown = org.assertj.core.api.Assertions.catchThrowable(() -> apply(em -> {
            final var entity = new _ModeBConstrainedEntity();
            entity.setName("longer than four");
            em.persist(entity);
            em.flush();
            return null;
        }));
        var cause = thrown;
        while (cause != null && !(cause instanceof jakarta.validation.ConstraintViolationException)) {
            cause = cause.getCause();
        }
        assertThat(cause).as("@Size(max = 4) rejects the value, not a null").isNotNull();

        final long id = apply(em -> {
            final var entity = new _ModeBConstrainedEntity();
            entity.setName("ok");
            em.persist(entity);
            return entity;
        }).id;
        final String stored = apply(em -> em.find(_ModeBConstrainedEntity.class, id).name);
        assertThat(stored).isEqualTo("ok");
    }

    @DisplayName("B: merge() does not copy a transient plaintext from a detached instance")
    @Test
    void __mergeOfADetachedInstance() {
        final var id = persistB("before-merge");
        final var detached = apply(em -> em.find(_GuardedModeBEntity.class, id));
        detached.setName("after-merge");
        apply(em -> em.merge(detached));
        final var stored = readB(id);
        log.info("[B merge] after merging a detached instance whose plaintext changed: '{}'", stored);
        // Jakarta Persistence merges persistent state only: the transient plaintext is not copied, so the managed
        // instance re-encrypts its own, earlier, plaintext. Set it on the instance merge() returns instead.
        assertThat(stored).isEqualTo("before-merge");
        final var fresh = apply(em -> em.find(_GuardedModeBEntity.class, id)); // the first one is stale now
        apply(em -> {
            em.merge(fresh).setName("after-merge");
            return null;
        });
        assertThat(readB(id)).as("set on the managed instance merge() returns").isEqualTo("after-merge");
    }

    @DisplayName("countUnmigrated(Class) counts the rows still holding plaintext, and none once migrated")
    @Test
    void __countUnmigrated() {
        final var service = _LifecycleListenerEntity.SERVICE;
        final var before = service.countUnmigrated(_GuardedEntity.class);
        final var plaintext = "legacy-" + System.nanoTime();
        apply(em -> em.createNativeQuery(
                        "INSERT INTO " + _GuardedEntity.TABLE_NAME + " (version, name, name_enc)"
                        + " VALUES (0, '" + plaintext + "', NULL)")
                .executeUpdate());
        assertThat(service.countUnmigrated(_GuardedEntity.class)).isEqualTo(before + 1);
        final long id = apply(em -> ((Number) em.createNativeQuery(
                        "SELECT id FROM " + _GuardedEntity.TABLE_NAME + " WHERE name = '" + plaintext + "'")
                .getSingleResult()).longValue());
        apply(em -> {
            service.encrypt(em.find(_GuardedEntity.class, id)); // the batch, for this row
            return null;
        });
        assertThat(service.countUnmigrated(_GuardedEntity.class)).isEqualTo(before);
    }
}
