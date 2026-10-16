package com.github.jinahya.persistence.encryption;

import com.github.jinahya.persistence.encryption.examples.LegacySecretEntity;
import com.github.jinahya.persistence.encryption.examples.PropertyEntity;
import com.github.jinahya.persistence.encryption.examples.Secret;
import com.github.jinahya.persistence.encryption.examples.SecretEntity;
import com.github.jinahya.persistence.encryption.converter.EncryptedStringConverter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Measures, on whichever provider the build runs on, what a converted attribute does across the entity life cycle:
 * the row holds ciphertext only, a read never writes, a change is stored, and a legacy row is migrated.
 */
@SuppressWarnings({"java:S101"})
class EncryptionLifecycle_Test {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static EntityManagerFactory EMF;

    @BeforeAll
    static void open() {
        EMF = Persistence.createEntityManagerFactory("__encryptionPU");
        // records every row the database is asked to write, so that a plaintext written and then overwritten is seen
        apply(em -> em.createNativeQuery(
                        "CREATE TRIGGER IF NOT EXISTS trg_" + SecretEntity.TABLE_NAME
                        + " BEFORE INSERT, UPDATE ON " + SecretEntity.TABLE_NAME
                        + " FOR EACH ROW CALL \"" + WriteRecorder.class.getName() + "\"")
                .executeUpdate());
    }

    @AfterAll
    static void close() {
        try {
            assertThat(WriteRecorder.count()).as("writes recorded").isPositive();
            assertThat(WriteRecorder.anyWriteContains("secret-")).as("no plaintext ever reached the database")
                    .isFalse();
        } finally {
            if (EMF != null) {
                EMF.close();
            }
        }
    }

    private static <R> R apply(final Function<? super EntityManager, ? extends R> function) {
        return PersistenceTests.apply(EMF, function);
    }

    /** version, name_enc */
    private static Object[] row(final long id) {
        return apply(em -> (Object[]) em.createNativeQuery(
                        "SELECT version, name_enc FROM " + SecretEntity.TABLE_NAME + " WHERE id = " + id)
                .getSingleResult());
    }

    private static long persisted(final String name) {
        return apply(em -> {
            final var e = new SecretEntity();
            e.name = name;
            e.note = "note";
            em.persist(e);
            return e;
        }).id;
    }

    private static String name(final long id) {
        return apply(em -> em.find(SecretEntity.class, id)).name;
    }

    private static long rows() {
        return ((Number) apply(em -> em.createNativeQuery("SELECT COUNT(*) FROM " + SecretEntity.TABLE_NAME)
                .getSingleResult())).longValue();
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("persist")
    @Nested
    class Persist {

        @DisplayName("an empty plaintext is a ciphertext, not an empty column, and reads back empty")
        @Test
        void __empty() {
            final var id = persisted("");
            assertThat((byte[]) row(id)[1]).as("the ciphertext of an empty value").isNotNull().isNotEmpty();
            assertThat(name(id)).isEmpty();
        }

        @DisplayName("a service which fails on encrypt fails the transaction, and no row is written")
        @Test
        void __serviceFails() {
            final var rows = rows();
            EntityEncryptionServiceImpl.FAIL_ENCRYPT = true;
            try {
                assertThatThrownBy(() -> persisted("secret-refused")).hasStackTraceContaining("refused, for a test");
            } finally {
                EntityEncryptionServiceImpl.FAIL_ENCRYPT = false;
            }
            assertThat(rows()).as("no row written").isEqualTo(rows);
        }

        @DisplayName("an attribute of an embeddable is encrypted like one of the entity")
        @Test
        void __embedded() {
            final var id = apply(em -> {
                final var e = new SecretEntity();
                e.secret = new Secret();
                e.secret.code = "secret-embedded";
                em.persist(e);
                return e;
            }).id;
            final var stored = (byte[]) apply(em -> em.createNativeQuery(
                    "SELECT code_enc FROM " + SecretEntity.TABLE_NAME + " WHERE id = " + id).getSingleResult());
            assertThat(stored).isNotNull().isNotEmpty();
            assertThat(apply(em -> em.find(SecretEntity.class, id)).secret.code).isEqualTo("secret-embedded");
        }

        @DisplayName("an attribute mapped on its getter, property access, is encrypted like a field")
        @Test
        void __property() {
            final var id = apply(em -> {
                final var e = new PropertyEntity();
                e.setName("secret-property");
                em.persist(e);
                return e;
            }).getId();
            final var stored = (byte[]) apply(em -> em.createNativeQuery(
                    "SELECT name_enc FROM " + PropertyEntity.TABLE_NAME + " WHERE id = " + id).getSingleResult());
            assertThat(stored).isNotNull().isNotEmpty();
            assertThat(new String(stored, StandardCharsets.ISO_8859_1)).doesNotContain("secret-property");
            assertThat(apply(em -> em.find(PropertyEntity.class, id)).getName()).isEqualTo("secret-property");
        }

        @DisplayName("stores the ciphertext only, in a binary column, and the value reads back")
        @Test
        void __() {
            final var id = persisted("secret-alice");
            final var row = row(id);
            assertThat((byte[]) row[1]).as("ciphertext").isNotNull().isNotEmpty();
            assertThat(PersistenceTests.columnType(EMF, SecretEntity.TABLE_NAME, "name_enc")).contains("BINARY");
            assertThat(name(id)).isEqualTo("secret-alice");
        }

        @DisplayName("a null plaintext is a NULL column, and the service is not called")
        @Test
        void __null() {
            final var encrypted = EntityEncryptionServiceImpl.ENCRYPTED.get();
            final var id = persisted(null);
            assertThat(row(id)[1]).isNull();
            assertThat(EntityEncryptionServiceImpl.ENCRYPTED.get()).isEqualTo(encrypted);
            assertThat(name(id)).isNull();
        }

        @DisplayName("Bean Validation sees the plaintext: a value over its @Size fails, one within it is stored")
        @Test
        void __validated() {
            assertThatThrownBy(() -> persisted("secret-" + "x".repeat(SecretEntity.NAME_MAX)))
                    .satisfies(t -> {
                        // the violation itself, or wrapped by the provider's commit
                        var cause = (Throwable) t;
                        while (cause != null && !(cause instanceof ConstraintViolationException)) {
                            cause = cause.getCause();
                        }
                        assertThat(cause).as("a constraint violation").isNotNull();
                    });
            final var within = "secret-" + "x".repeat(SecretEntity.NAME_MAX - "secret-".length());
            // the ciphertext is far longer than NAME_MAX; it is the plaintext which was validated
            assertThat(name(persisted(within))).isEqualTo(within);
        }
    }

    @DisplayName("read")
    @Nested
    class Read {

        @DisplayName("a projection of the attribute reads the plaintext")
        @Test
        void __projection() {
            final var id = persisted("secret-projected");
            final var projected = apply(em -> em.createQuery(
                            "SELECT e.name FROM SecretEntity e WHERE e.id = :id", String.class)
                    .setParameter("id", id).getSingleResult());
            assertThat(projected).isEqualTo("secret-projected");
        }

        @DisplayName("a null check is the one predicate which works: NULL in the column is null in the attribute")
        @Test
        void __nullPredicate() {
            final var absent = persisted(null);
            final var present = persisted("secret-present");
            final var ids = List.of(absent, present);
            final var nulls = apply(em -> em.createQuery(
                            "SELECT e.id FROM SecretEntity e WHERE e.name IS NULL AND e.id IN :ids", Long.class)
                    .setParameter("ids", ids).getResultList());
            assertThat(nulls).containsExactly(absent);
            final var notNulls = apply(em -> em.createQuery(
                            "SELECT e.id FROM SecretEntity e WHERE e.name IS NOT NULL AND e.id IN :ids", Long.class)
                    .setParameter("ids", ids).getResultList());
            assertThat(notNulls).containsExactly(present);
        }

        @DisplayName("tampered bytes fail the read, naming no plaintext, instead of yielding a value")
        @Test
        void __tampered() {
            final var id = persisted("secret-tampered");
            final var tampered = ((byte[]) row(id)[1]).clone();
            tampered[tampered.length - 1] ^= 0x01;
            apply(em -> em.createNativeQuery("UPDATE " + SecretEntity.TABLE_NAME + " SET name_enc = ?1 WHERE id = ?2")
                    .setParameter(1, tampered).setParameter(2, id).executeUpdate());
            assertThatThrownBy(() -> name(id)).satisfies(t -> {
                for (var cause = (Throwable) t; cause != null; cause = cause.getCause()) {
                    assertThat(String.valueOf(cause.getMessage())).doesNotContain("secret-tampered");
                }
            });
        }

        @DisplayName("concurrent persistence contexts share the one converter, and each reads its own value")
        @Test
        void __concurrent() throws Exception {
            final var names = IntStream.range(0, 32).mapToObj(i -> "secret-c" + i).toList();
            final var ids = new ArrayList<Long>();
            final var pool = Executors.newFixedThreadPool(8);
            try {
                final var persisted = names.stream().map(n -> pool.submit(() -> persisted(n))).toList();
                for (final var future : persisted) {
                    ids.add(future.get());
                }
                final var read = ids.stream().map(i -> pool.submit(() -> name(i))).toList();
                for (int i = 0; i < read.size(); i++) {
                    assertThat(read.get(i).get()).isEqualTo(names.get(i));
                }
            } finally {
                pool.shutdown();
                assertThat(pool.awaitTermination(1L, TimeUnit.MINUTES)).isTrue();
            }
        }
        @DisplayName("the recorder is live: a plaintext handed to the binary column on purpose is seen")
        @Test
        void __recorderControl() {
            final var id = persisted(null);
            final var writes = WriteRecorder.count();
            apply(em -> em.createNativeQuery("UPDATE " + SecretEntity.TABLE_NAME + " SET name_enc = ?1 WHERE id = ?2")
                    .setParameter(1, "control-plaintext".getBytes(StandardCharsets.UTF_8)).setParameter(2, id)
                    .executeUpdate());
            assertThat(WriteRecorder.count()).isGreaterThan(writes);
            assertThat(WriteRecorder.anyWriteContains("control-plaintext")).isTrue();
        }


        @DisplayName("a read, flushed and committed, never writes: version, bytes and encrypt calls unchanged")
        @Test
        void __() {
            final var id = persisted("secret-bob");
            final var before = row(id);
            final var encrypted = EntityEncryptionServiceImpl.ENCRYPTED.get();
            apply(em -> {
                final var e = em.find(SecretEntity.class, id);
                assertThat(e.name).isEqualTo("secret-bob");
                em.flush();
                return e;
            });
            apply(em -> em.find(SecretEntity.class, id));
            final var after = row(id);
            assertThat(after[0]).as("version").isEqualTo(before[0]);
            assertThat((byte[]) after[1]).as("ciphertext").isEqualTo((byte[]) before[1]);
            assertThat(EntityEncryptionServiceImpl.ENCRYPTED.get()).as("encrypt calls").isEqualTo(encrypted);
        }

        @DisplayName("a refresh after an external change of the column reads the new plaintext")
        @Test
        void __refresh() {
            final var id = persisted("secret-henry");
            final var external = new EncryptedStringConverter().convertToDatabaseColumn("secret-henry2");
            final var refreshed = apply(em -> {
                final var e = em.find(SecretEntity.class, id);
                em.createNativeQuery("UPDATE " + SecretEntity.TABLE_NAME + " SET name_enc = ?1 WHERE id = ?2")
                        .setParameter(1, external).setParameter(2, id).executeUpdate();
                em.refresh(e);
                return e;
            });
            assertThat(refreshed.name).isEqualTo("secret-henry2");
        }

        @DisplayName("a query with EclipseLink's read-only hint is decrypted like any other; a plain query elsewhere")
        @Test
        void __readOnly() {
            final var id = persisted("secret-readonly");
            final var found = apply(em -> em.createQuery(
                            "SELECT e FROM SecretEntity e WHERE e.id = :id", SecretEntity.class)
                    .setParameter("id", id).setHint("eclipselink.read-only", "true").getSingleResult());
            assertThat(found.name).isEqualTo("secret-readonly");
        }

        @DisplayName("a JPQL predicate on the plaintext matches what is stored, which is ciphertext: nothing")
        @Test
        void __query() {
            persisted("secret-frank");
            final var found = apply(em -> em.createQuery(
                            "SELECT e FROM SecretEntity e WHERE e.name = :name", SecretEntity.class)
                    .setParameter("name", "secret-frank")
                    .getResultList());
            assertThat(found).isEmpty();
        }
    }

    @DisplayName("update")
    @Nested
    class Update {

        @DisplayName("a bulk JPQL UPDATE with a literal stores the ciphertext, not the literal")
        @Test
        void __bulkLiteral() {
            final var id = persisted("secret-literal");
            apply(em -> em.createQuery("UPDATE SecretEntity e SET e.name = 'secret-literal2' WHERE e.id = :id")
                    .setParameter("id", id).executeUpdate());
            assertThat(new String((byte[]) row(id)[1], StandardCharsets.ISO_8859_1)).doesNotContain("secret-literal2");
            assertThat(name(id)).isEqualTo("secret-literal2");
        }

        @DisplayName("a changed plaintext is stored as a new ciphertext")
        @Test
        void __changed() {
            final var id = persisted("secret-dave");
            final var before = row(id);
            apply(em -> em.find(SecretEntity.class, id).name = "secret-dave2");
            assertThat((byte[]) row(id)[1]).isNotEqualTo((byte[]) before[1]);
            assertThat(name(id)).isEqualTo("secret-dave2");
        }

        @DisplayName("a plaintext set to null is stored as NULL")
        @Test
        void __nulled() {
            final var id = persisted("secret-erin");
            apply(em -> em.find(SecretEntity.class, id).name = null);
            assertThat(row(id)[1]).isNull();
            assertThat(name(id)).isNull();
        }

        @DisplayName("an unrelated change: whether the unchanged plaintext is encrypted again is the provider's")
        @Test
        void __unrelated() {
            final var id = persisted("secret-carol");
            final var before = row(id);
            apply(em -> em.find(SecretEntity.class, id).note = "changed");
            final var after = row(id);
            assertThat(after[0]).as("version").isEqualTo((Integer) before[0] + 1);
            log.info("unrelated change: ciphertext {}", Arrays.equals((byte[]) before[1], (byte[]) after[1])
                                                        ? "kept as stored" : "encrypted again, every column written");
            assertThat(name(id)).isEqualTo("secret-carol");
        }

        @DisplayName("a detached instance merged with a changed plaintext stores it")
        @Test
        void __merged() {
            final var id = persisted("secret-gina");
            final var detached = apply(em -> em.find(SecretEntity.class, id));
            detached.name = "secret-gina2";
            apply(em -> em.merge(detached));
            assertThat(name(id)).isEqualTo("secret-gina2");
        }

        @DisplayName("a bulk JPQL UPDATE with a parameter stores the ciphertext, not the parameter")
        @Test
        void __bulk() {
            final var id = persisted("secret-bulk");
            apply(em -> em.createQuery("UPDATE SecretEntity e SET e.name = :name WHERE e.id = :id")
                    .setParameter("name", "secret-bulk2").setParameter("id", id).executeUpdate());
            assertThat(new String((byte[]) row(id)[1])).doesNotContain("secret-bulk2");
            assertThat(name(id)).isEqualTo("secret-bulk2");
        }
    }

    @DisplayName("legacy rows")
    @Nested
    class Legacy {

        /** version, name (the old column), name_enc */
        private Object[] legacyRow(final long id) {
            return apply(em -> (Object[]) em.createNativeQuery(
                            "SELECT version, name, name_enc FROM " + LegacySecretEntity.TABLE_NAME
                            + " WHERE id = " + id)
                    .getSingleResult());
        }

        @DisplayName("a row holding plaintext in the old column reads it, and the reading transaction migrates it")
        @Test
        void __() {
            final var id = apply(em -> {
                final var e = new LegacySecretEntity();
                e.name = "placeholder";
                em.persist(e);
                return e;
            }).id;
            // make it a legacy row: plaintext in the old column, no ciphertext
            apply(em -> em.createNativeQuery("UPDATE " + LegacySecretEntity.TABLE_NAME
                                             + " SET name = 'legacy', name_enc = NULL WHERE id = " + id)
                    .executeUpdate());
            assertThat(legacyRow(id)[1]).isEqualTo("legacy");
            assertThat(legacyRow(id)[2]).isNull();
            final var read = apply(em -> em.find(LegacySecretEntity.class, id));
            assertThat(read.name).isEqualTo("legacy");
            final var migrated = legacyRow(id);
            assertThat(migrated[1]).as("old column nulled").isNull();
            assertThat((byte[]) migrated[2]).as("ciphertext stored").isNotNull();
            assertThat(apply(em -> em.find(LegacySecretEntity.class, id)).name).isEqualTo("legacy");
            // migrated, the row no longer writes on read
            final var version = legacyRow(id)[0];
            apply(em -> em.find(LegacySecretEntity.class, id));
            assertThat(legacyRow(id)[0]).isEqualTo(version);
        }
    }
}
