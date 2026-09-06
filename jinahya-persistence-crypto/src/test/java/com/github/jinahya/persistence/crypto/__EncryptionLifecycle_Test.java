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
import java.util.Arrays;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Observes when a provider writes the two attributes of an encrypted pair, so that the choice of life cycle callbacks
 * can be made on measurements rather than on assumptions.
 * <p>
 * The observations are logged rather than asserted; providers differ here, and the point of this class is to say what
 * each one actually does.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class __EncryptionLifecycle_Test {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static EntityManagerFactory ENTITY_MANAGER_FACTORY;

    @BeforeAll
    static void openEntityManagerFactory() {
        ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("__testPU");
        _LifecycleListener.SERVICE = new _EncryptionService(ENTITY_MANAGER_FACTORY, new _EncryptionManager());
    }

    @AfterAll
    static void closeEntityManagerFactory() {
        _LifecycleListener.SERVICE = null;
        if (ENTITY_MANAGER_FACTORY != null) {
            ENTITY_MANAGER_FACTORY.close();
        }
    }

    private static <R> R apply(final Function<? super EntityManager, ? extends R> function) {
        try (final var entityManager = ENTITY_MANAGER_FACTORY.createEntityManager()) {
            final var transaction = entityManager.getTransaction();
            transaction.begin();
            try {
                final var result = function.apply(entityManager);
                transaction.commit();
                return result;
            } catch (final Exception e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                throw e;
            }
        }
    }

    /**
     * Reads the row behind the entity, bypassing the entity and its callbacks entirely.
     */
    private static Object[] row(final long id) {
        return apply(em -> (Object[]) em
                .createNativeQuery("SELECT version, name, name_enc FROM " + _GuardedEntity.TABLE_NAME
                                   + " WHERE id = " + id)
                .getSingleResult());
    }

    private static void installRecorder() {
        apply(em -> em.createNativeQuery(
                        "CREATE TRIGGER IF NOT EXISTS trg_" + _GuardedEntity.TABLE_NAME
                        + " BEFORE INSERT, UPDATE ON " + _GuardedEntity.TABLE_NAME
                        + " FOR EACH ROW CALL \"" + _WriteRecorder.class.getName() + "\"")
                .executeUpdate());
    }

    private static String describe(final Object[] row) {
        final var name = row[1];
        final var cipher = row[2];
        return "version=" + row[0]
               + ", name=" + (name == null ? "NULL" : "'" + name + "'")
               + ", name_enc=" + (cipher == null ? "NULL" : ((byte[]) cipher).length + " bytes");
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("a plaintext column which an INSERT could carry is rejected outright")
    @Test
    void rejectInsertablePlaintext() {
        // _LifecycleEntity maps its plaintext column with the default insertable = true
        assertThatThrownBy(() -> apply(em -> {
            final var entity = new _LifecycleEntity();
            entity.name = "never-stored";
            em.persist(entity);
            return entity;
        }))
                .as("the mapping which leaks on EclipseLink must not be accepted")
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("non-insertable");
    }

    @DisplayName("nothing the application assigns after persist() reaches the database in the clear")
    @Test
    void observeInsert() {
        installRecorder();
        _WriteRecorder.clear();
        final var guarded = apply(em -> {
            final var entity = new _GuardedEntity();
            entity.name = "before-persist";
            em.persist(entity);
            entity.name = "AFTER-PERSIST"; // the window between @PrePersist and the INSERT
            return entity;
        });
        log.info("[insert] row after a post-persist() mutation: {}", describe(row(guarded.id)));
        log.info("[insert] statements the database saw: {}", _WriteRecorder.WRITES);
        assertThat(_WriteRecorder.anyWriteContains("AFTER-PERSIST"))
                .as("a plaintext column declared insertable = false can never reach the database")
                .isFalse();

        // which of the two values survived is provider-dependent, and that divergence is the point of this assertion:
        // Hibernate issues INSERT then UPDATE, so @PreUpdate re-encrypts the late assignment; EclipseLink issues one
        // INSERT built at commit, so the late assignment is DROPPED and the row keeps the earlier value.
        final var stored = apply(em -> {
            final var reloaded = em.find(_GuardedEntity.class, guarded.id); // @PostLoad decrypts it
            return reloaded.name;
        });
        log.info("[insert] the value which survived: '{}'", stored);
        assertThat(stored)
                .as("an assignment made after persist() either survives or is dropped, never leaks")
                .isIn("AFTER-PERSIST", "before-persist");
        if ("before-persist".equals(stored)) {
            log.info("[insert] this provider DROPPED the assignment made after persist()");
        }
    }

    @DisplayName("whether a transaction which only reads still writes")
    @Test
    void observeReadOnlyTransaction() {
        final var id = apply(em -> {
            final var entity = new _GuardedEntity();
            entity.name = "read-me";
            em.persist(entity);
            return entity;
        }).id;
        final var before = row(id);
        log.info("[read] row before the read-only transaction: {}", describe(before));

        final var seen = apply(em -> {
            final var entity = em.find(_GuardedEntity.class, id);
            return entity.name; // nothing is modified
        });

        final var after = row(id);
        log.info("[read] row after the read-only transaction:  {}", describe(after));
        log.info("[read] the application read '{}'; version {} -> {}; ciphertext {}",
                 seen, before[0], after[0],
                 Arrays.equals((byte[]) before[2], (byte[]) after[2]) ? "unchanged" : "REWRITTEN");
        assertThat(seen).as("the application does read the plaintext back").isEqualTo("read-me");
        // holds on both providers: decrypting in @PostLoad dirties the instance, so reading writes
        assertThat(((Number) after[0]).longValue())
                .as("a transaction which only reads still issues an UPDATE; see the README")
                .isGreaterThan(((Number) before[0]).longValue());
        assertThat((byte[]) after[2])
                .as("and, encryption being randomized, it rewrites the ciphertext")
                .isNotEqualTo((byte[]) before[2]);
    }

    @DisplayName("what the application holds after a flush inside the transaction")
    @Test
    void observeStateAfterFlush() {
        apply(em -> {
            final var entity = new _GuardedEntity();
            entity.name = "flush-me";
            em.persist(entity);
            em.flush();
            log.info("[flush] after flush(): name={}, name_enc={}",
                     entity.name == null ? "NULL" : "'" + entity.name + "'",
                     entity.nameEnc__ == null ? "null" : entity.nameEnc__.length + " bytes");
            // holds on both providers: the instance is left encrypted, so the application cannot read it any more
            assertThat(entity.name)
                    .as("the application's own instance is emptied by the flush")
                    .isNull();
            return null;
        });
    }
}
