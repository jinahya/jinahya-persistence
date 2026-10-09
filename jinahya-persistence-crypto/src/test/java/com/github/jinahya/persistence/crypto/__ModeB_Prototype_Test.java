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
 * Measures, on both providers, what a Mode B entity (transient plaintext) actually does, before the module implements
 * Mode B (#82). The Mode B entity is mapped onto the same table as the Mode A one, {@link _GuardedEntity}, so that
 * rows can cross from one mode to the other.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class __ModeB_Prototype_Test {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static EntityManagerFactory ENTITY_MANAGER_FACTORY;

    @BeforeAll
    static void setUp() {
        ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("__cryptoPU");
        final var manager = new _Entity_EncryptionManager();
        _ModeBPrototypeListener.MANAGER = manager;
        _LifecycleListenerEntity.SERVICE = new EntityEncryptionService(ENTITY_MANAGER_FACTORY, manager);
    }

    @AfterAll
    static void tearDown() {
        _ModeBPrototypeListener.MANAGER = null;
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
}
