package com.github.jinahya.persistence.encryption.hibernate;

import com.github.jinahya.persistence.encryption.converter.EncryptedStringConverter;
import com.github.jinahya.persistence.encryption.hibernate.examples.DynamicEntity;
import com.github.jinahya.persistence.encryption.hibernate.examples.MixedEntity;
import com.github.jinahya.persistence.encryption.hibernate.examples.SecretEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * What {@code @Encrypted} does across the life cycle, on Hibernate ORM: the same claims as the generic module's
 * converters, plus what Hibernate ORM's own paths give.
 */
@SuppressWarnings({"java:S101"})
class EncryptedUserType_Test {

    private static EntityManagerFactory EMF;

    /**
     * Every plaintext this class stored; none may appear in any write the database saw.
     */
    private static final Set<String> PLAINTEXTS = ConcurrentHashMap.newKeySet();

    @BeforeAll
    static void open() {
        EMF = Persistence.createEntityManagerFactory("__encryptionHibernatePU");
        // records every row the database is asked to write, so that a plaintext written and then overwritten is seen
        apply(em -> em.createNativeQuery(
                        "CREATE TRIGGER IF NOT EXISTS trg_" + SecretEntity.TABLE_NAME
                        + " BEFORE INSERT, UPDATE ON " + SecretEntity.TABLE_NAME
                        + " FOR EACH ROW CALL \"" + WriteRecorder.class.getName() + "\"")
                .executeUpdate());
    }

    @AfterAll
    static void close() {
        assertThat(WriteRecorder.count()).as("writes recorded").isPositive();
        for (final var plaintext : PLAINTEXTS) {
            assertThat(WriteRecorder.anyWriteContains(plaintext)).as("no plaintext ever reached the database: %s", plaintext)
                    .isFalse();
        }
        if (EMF != null) {
            EMF.close();
        }
    }

    private static <R> R apply(final Function<? super EntityManager, ? extends R> function) {
        return PersistenceTests.apply(EMF, function);
    }

    /** version, name_enc */
    private static Object[] row(final long id) {
        return apply(em -> (Object[]) em.createNativeQuery(
                "SELECT version, name_enc FROM " + SecretEntity.TABLE_NAME + " WHERE id = " + id).getSingleResult());
    }

    private static long persisted(final String name) {
        if (name != null) {
            PLAINTEXTS.add(name);
        }
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

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("persist")
    @Nested
    class Persist {

        @DisplayName("stores the ciphertext only, in a binary column, and the value reads back")
        @Test
        void __() {
            final var id = persisted("alice");
            final var row = row(id);
            assertThat((byte[]) row[1]).isNotNull().isNotEmpty();
            assertThat(new String((byte[]) row[1])).doesNotContain("alice");
            assertThat(PersistenceTests.columnType(EMF, SecretEntity.TABLE_NAME, "name_enc")).contains("BINARY");
            assertThat(name(id)).isEqualTo("alice");
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

        @DisplayName("Bean Validation sees the plaintext")
        @Test
        void __validated() {
            assertThatThrownBy(() -> persisted("x".repeat(SecretEntity.NAME_MAX + 1)))
                    .satisfies(t -> {
                        var cause = (Throwable) t;
                        while (cause != null && !(cause instanceof ConstraintViolationException)) {
                            cause = cause.getCause();
                        }
                        assertThat(cause).as("a constraint violation").isNotNull();
                    });
            final var within = "x".repeat(SecretEntity.NAME_MAX);
            assertThat(name(persisted(within))).isEqualTo(within);
        }
    }

    @DisplayName("read")
    @Nested
    class Read {
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


        @DisplayName("a read, flushed and committed, never writes")
        @Test
        void __() {
            final var id = persisted("bob");
            final var before = row(id);
            final var encrypted = EntityEncryptionServiceImpl.ENCRYPTED.get();
            apply(em -> {
                final var e = em.find(SecretEntity.class, id);
                assertThat(e.name).isEqualTo("bob");
                em.flush();
                return e;
            });
            final var after = row(id);
            assertThat(after[0]).isEqualTo(before[0]);
            assertThat((byte[]) after[1]).isEqualTo((byte[]) before[1]);
            assertThat(EntityEncryptionServiceImpl.ENCRYPTED.get()).isEqualTo(encrypted);
        }

        @DisplayName("a refresh after an external change of the column reads the new plaintext")
        @Test
        void __refresh() {
            final var id = persisted("henry");
            PLAINTEXTS.add("henry2");
            final var external = new EncryptedStringConverter().convertToDatabaseColumn("henry2");
            final var refreshed = apply(em -> {
                final var e = em.find(SecretEntity.class, id);
                em.createNativeQuery("UPDATE " + SecretEntity.TABLE_NAME + " SET name_enc = ?1 WHERE id = ?2")
                        .setParameter(1, external).setParameter(2, id).executeUpdate();
                em.refresh(e);
                return e;
            });
            assertThat(refreshed.name).isEqualTo("henry2");
        }

        @DisplayName("a JPQL predicate on the plaintext matches nothing")
        @Test
        void __query() {
            persisted("frank");
            final List<SecretEntity> found = apply(em -> em.createQuery(
                            "SELECT e FROM SecretEntity e WHERE e.name = :name", SecretEntity.class)
                    .setParameter("name", "frank").getResultList());
            assertThat(found).isEmpty();
        }
    }

    @DisplayName("update")
    @Nested
    class Update {

        @DisplayName("a changed plaintext is stored as a new ciphertext; null as NULL")
        @Test
        void __changed() {
            final var id = persisted("dave");
            final var before = row(id);
            PLAINTEXTS.add("dave2");
            apply(em -> em.find(SecretEntity.class, id).name = "dave2");
            assertThat((byte[]) row(id)[1]).isNotEqualTo((byte[]) before[1]);
            assertThat(name(id)).isEqualTo("dave2");
            apply(em -> em.find(SecretEntity.class, id).name = null);
            assertThat(row(id)[1]).isNull();
        }

        @DisplayName("an unrelated change bumps the version and keeps the plaintext readable; every column is rewritten")
        @Test
        void __unrelated() {
            final var id = persisted("carol");
            final var before = row(id);
            final var encrypted = EntityEncryptionServiceImpl.ENCRYPTED.get();
            apply(em -> em.find(SecretEntity.class, id).note = "changed");
            final var after = row(id);
            assertThat(after[0]).as("version").isEqualTo((Integer) before[0] + 1);
            // without @DynamicUpdate the UPDATE names every column: the unchanged plaintext is encrypted again
            assertThat((byte[]) after[1]).as("ciphertext rewritten").isNotEqualTo((byte[]) before[1]);
            assertThat(EntityEncryptionServiceImpl.ENCRYPTED.get()).as("encrypted again").isEqualTo(encrypted + 1);
            assertThat(name(id)).isEqualTo("carol");
        }

        @DisplayName("a detached instance merged with a changed plaintext stores it")
        @Test
        void __merged() {
            final var id = persisted("gina");
            final var detached = apply(em -> em.find(SecretEntity.class, id));
            PLAINTEXTS.add("gina2");
            detached.name = "gina2";
            apply(em -> em.merge(detached));
            assertThat(name(id)).isEqualTo("gina2");
        }

        @DisplayName("under @DynamicUpdate, an unrelated change keeps the stored ciphertext")
        @Test
        void __dynamic() {
            final var id = apply(em -> {
                final var e = new DynamicEntity();
                e.name = "dynamic";
                em.persist(e);
                return e;
            }).id;
            final var before = (byte[]) apply(em -> em.createNativeQuery(
                    "SELECT name_enc FROM " + DynamicEntity.TABLE_NAME + " WHERE id = " + id).getSingleResult());
            final var encrypted = EntityEncryptionServiceImpl.ENCRYPTED.get();
            apply(em -> em.find(DynamicEntity.class, id).note = "changed");
            assertThat((byte[]) apply(em -> em.createNativeQuery(
                    "SELECT name_enc FROM " + DynamicEntity.TABLE_NAME + " WHERE id = " + id).getSingleResult()))
                    .isEqualTo(before);
            assertThat(EntityEncryptionServiceImpl.ENCRYPTED.get()).isEqualTo(encrypted);
        }

        @DisplayName("a bulk JPQL UPDATE with a parameter stores the ciphertext")
        @Test
        void __bulk() {
            final var id = persisted("bulk");
            PLAINTEXTS.add("bulk2");
            apply(em -> em.createQuery("UPDATE SecretEntity e SET e.name = :name WHERE e.id = :id")
                    .setParameter("name", "bulk2").setParameter("id", id).executeUpdate());
            assertThat(new String((byte[]) row(id)[1])).doesNotContain("bulk2");
            assertThat(name(id)).isEqualTo("bulk2");
        }
    }

    @DisplayName("a StatelessSession stores the ciphertext, and reads it decrypted")
    @Test
    void stateless() {
        final var factory = EMF.unwrap(SessionFactory.class);
        final var e = new SecretEntity();
        e.name = "stateless";
        PLAINTEXTS.add("stateless");
        PLAINTEXTS.add("stateless2");
        try (var session = factory.openStatelessSession()) {
            final var tx = session.beginTransaction();
            session.insert(e);
            tx.commit();
        }
        assertThat(new String((byte[]) row(e.id)[1])).doesNotContain("stateless");
        try (var session = factory.openStatelessSession()) {
            final var got = session.get(SecretEntity.class, e.id);
            assertThat(got.name).isEqualTo("stateless");
            got.name = "stateless2";
            final var tx = session.beginTransaction();
            session.update(got);
            tx.commit();
        }
        assertThat(name(e.id)).isEqualTo("stateless2");
    }

    @DisplayName("the native and the standard mapping write the same rows: each reads what the other wrote")
    @Test
    void mixed() {
        final var id = apply(em -> {
            final var e = new MixedEntity();
            e.nativeName = "native";
            e.standardName = "standard";
            em.persist(e);
            return e;
        }).id;
        // swap the columns underneath, and read through the other mapping
        apply(em -> em.createNativeQuery("UPDATE " + MixedEntity.TABLE_NAME
                                         + " SET native_enc = standard_enc, standard_enc = native_enc WHERE id = " + id)
                .executeUpdate());
        final var swapped = apply(em -> em.find(MixedEntity.class, id));
        assertThat(swapped.nativeName).isEqualTo("standard");
        assertThat(swapped.standardName).isEqualTo("native");
    }
}
