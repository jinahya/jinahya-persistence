package com.github.jinahya.persistence.encryption.eclipselink;

import com.github.jinahya.persistence.encryption.eclipselink.examples.SecretEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * What the named converter does across the life cycle, on EclipseLink: the same claims as the generic module's
 * converters, plus what EclipseLink's own paths give.
 */
@SuppressWarnings({"java:S101"})
class EncryptedConverter_Test {

    private static EntityManagerFactory EMF;

    @BeforeAll
    static void open() {
        EMF = Persistence.createEntityManagerFactory("__encryptionEclipseLinkPU");
    }

    @AfterAll
    static void close() {
        if (EMF != null) {
            EMF.close();
        }
    }

    private static <R> R apply(final Function<? super EntityManager, ? extends R> function) {
        return PersistenceTests.apply(EMF, function);
    }

    /** version, name_enc, bytes_enc, when_enc */
    private static Object[] row(final long id) {
        return apply(em -> (Object[]) em.createNativeQuery(
                        "SELECT version, name_enc, bytes_enc, when_enc FROM " + SecretEntity.TABLE_NAME
                        + " WHERE id = " + id)
                .getSingleResult());
    }

    private static long persisted(final String name) {
        return apply(em -> {
            final var e = new SecretEntity();
            e.name = name;
            e.note = "note";
            e.bytes = new byte[] {1, 2, 3};
            e.when = new java.util.Date(1_000_000L);
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

        @DisplayName("stores the ciphertext only, in a binary column the converter typed, and the value reads back")
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
            final var id = apply(em -> {
                final var e = new SecretEntity();
                em.persist(e);
                return e;
            }).id;
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

        @DisplayName("a read-only query is decrypted like any other")
        @Test
        void __readOnly() {
            final var id = persisted("readonly");
            final var found = apply(em -> em.createQuery(
                            "SELECT e FROM SecretEntity e WHERE e.id = :id", SecretEntity.class)
                    .setParameter("id", id).setHint("eclipselink.read-only", "true").getSingleResult());
            assertThat(found.name).isEqualTo("readonly");
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
            apply(em -> em.find(SecretEntity.class, id).name = "dave2");
            assertThat((byte[]) row(id)[1]).isNotEqualTo((byte[]) before[1]);
            assertThat(name(id)).isEqualTo("dave2");
            apply(em -> em.find(SecretEntity.class, id).name = null);
            assertThat(row(id)[1]).isNull();
        }

        @DisplayName("an unrelated change keeps the stored ciphertext")
        @Test
        void __unrelated() {
            final var id = persisted("carol");
            final var before = row(id);
            final var encrypted = EntityEncryptionServiceImpl.ENCRYPTED.get();
            apply(em -> em.find(SecretEntity.class, id).note = "changed");
            final var after = row(id);
            assertThat(after[0]).isEqualTo((Integer) before[0] + 1);
            assertThat((byte[]) after[1]).isEqualTo((byte[]) before[1]);
            assertThat(EntityEncryptionServiceImpl.ENCRYPTED.get()).isEqualTo(encrypted);
        }

        @DisplayName("an array changed in place is not detected; one assigned anew is stored")
        @Test
        void __arrayInPlace() {
            final var id = persisted("mutable");
            final var before = row(id);
            apply(em -> em.find(SecretEntity.class, id).bytes[0] = 99);
            assertThat((byte[]) row(id)[2]).as("not written").isEqualTo((byte[]) before[2]);
            apply(em -> em.find(SecretEntity.class, id).bytes = new byte[] {99, 2, 3});
            assertThat((byte[]) row(id)[2]).isNotEqualTo((byte[]) before[2]);
            assertThat(apply(em -> em.find(SecretEntity.class, id)).bytes).isEqualTo(new byte[] {99, 2, 3});
        }

        @DisplayName("a @Mutable Date changed in place is detected and stored, and a read of it still never writes")
        @Test
        void __mutableDateInPlace() {
            final var id = persisted("mutable-date");
            final var before = row(id);
            apply(em -> em.find(SecretEntity.class, id));
            assertThat(row(id)[0]).as("a read never writes").isEqualTo(before[0]);
            apply(em -> {
                em.find(SecretEntity.class, id).when.setTime(2_000_000L);
                return null;
            });
            assertThat((byte[]) row(id)[3]).isNotEqualTo((byte[]) before[3]);
            assertThat(apply(em -> em.find(SecretEntity.class, id)).when.getTime()).isEqualTo(2_000_000L);
        }

        @DisplayName("a detached instance merged with a changed plaintext stores it")
        @Test
        void __merged() {
            final var id = persisted("gina");
            final var detached = apply(em -> em.find(SecretEntity.class, id));
            detached.name = "gina2";
            apply(em -> em.merge(detached));
            assertThat(name(id)).isEqualTo("gina2");
        }

        @DisplayName("a bulk JPQL UPDATE with a parameter stores the ciphertext")
        @Test
        void __bulk() {
            final var id = persisted("bulk");
            apply(em -> em.createQuery("UPDATE SecretEntity e SET e.name = :name WHERE e.id = :id")
                    .setParameter("name", "bulk2").setParameter("id", id).executeUpdate());
            assertThat(new String((byte[]) row(id)[1])).doesNotContain("bulk2");
            assertThat(name(id)).isEqualTo("bulk2");
        }
    }
}
