package com.github.jinahya.persistence.encryption.eclipselink;

import com.github.jinahya.persistence.encryption.EntityEncryptionEngine;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the EclipseLink integration against an in-memory database: what reaches the row, what the entity keeps, and
 * which flushes write. Run once per persistence unit: one builds objects straight into the unit of work, the other
 * clones them from the shared cache.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
abstract class EntityEncryptionSessionCustomizer_Test {

    EntityEncryptionSessionCustomizer_Test(final String unitName) {
        super();
        this.unitName = unitName;
    }

    @BeforeEach
    void setUp() {
        service = new CountingEncryptionService();
        entityManagerFactory = Persistence.createEntityManagerFactory(
                unitName, Map.of(EntityEncryptionEngine.PROPERTY_ENCRYPTION_SERVICE, service));
        service.entityManagerFactory = entityManagerFactory;
    }

    @AfterEach
    void tearDown() {
        if (entityManagerFactory != null) {
            entityManagerFactory.close();
        }
    }

    private <R> R apply(final Function<? super EntityManager, ? extends R> function) {
        try (var em = entityManagerFactory.createEntityManager()) {
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

    private Object column(final String column, final long id) {
        return apply(em -> em.createNativeQuery(
                "SELECT " + column + " FROM " + SecretEntity.TABLE_NAME + " WHERE id = " + id).getSingleResult());
    }

    private long version(final long id) {
        return ((Number) column("version", id)).longValue();
    }

    private long insertLegacy(final String name) {
        final var marker = "legacy-" + System.nanoTime();
        apply(em -> em.createNativeQuery("INSERT INTO " + SecretEntity.TABLE_NAME + " (version, note, name) VALUES (1, '"
                                         + marker + "', '" + name + "')").executeUpdate());
        return apply(em -> ((Number) em.createNativeQuery(
                "SELECT id FROM " + SecretEntity.TABLE_NAME + " WHERE note = '" + marker + "'")
                .getSingleResult()).longValue());
    }

    private long persist(final String name, final Integer code) {
        return apply(em -> {
            final var entity = new SecretEntity();
            entity.note = "note";
            entity.name = name;
            entity.secret = new SecretEmbeddable();
            entity.secret.code = code;
            em.persist(entity);
            return entity;
        }).id;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("persist")
    @Nested
    class PersistTest {

        @DisplayName("stores ciphertexts only, keeps the plaintexts in the entity, and reads back")
        @Test
        void __() {
            final var persisted = apply(em -> {
                final var entity = new SecretEntity();
                entity.name = "alice";
                entity.secret = new SecretEmbeddable();
                entity.secret.code = 42;
                em.persist(entity);
                em.flush();
                assertThat(entity.name).isEqualTo("alice");
                assertThat(entity.secret.code).isEqualTo(42);
                assertThat(entity.nameEnc).isNotEmpty();
                assertThat(entity.secret.codeEnc).isNotEmpty();
                return entity;
            });
            final long id = persisted.id;
            assertThat(column("name", id)).isNull();
            assertThat(column("code", id)).isNull();
            assertThat((byte[]) column("name_enc", id)).isEqualTo(persisted.nameEnc);
            assertThat((byte[]) column("code_enc", id)).isEqualTo(persisted.secret.codeEnc);
            apply(em -> {
                final var found = em.find(SecretEntity.class, id);
                assertThat(found.name).isEqualTo("alice");
                assertThat(found.secret.code).isEqualTo(42);
                return null;
            });
        }

        @DisplayName("an INSERT is not followed by an UPDATE")
        @Test
        void __noUpdate() {
            final long id = apply(em -> {
                final var entity = new SecretEntity();
                entity.name = "alice";
                entity.secret = new SecretEmbeddable();
                entity.secret.code = 42;
                em.persist(entity);
                em.flush();
                em.flush();
                return entity;
            }).id;
            assertThat(version(id)).isEqualTo(1L);
        }

        @DisplayName("a null plaintext is a null ciphertext")
        @Test
        void __null() {
            final long id = persist(null, null);
            assertThat(column("name_enc", id)).isNull();
            assertThat(column("code_enc", id)).isNull();
            apply(em -> {
                assertThat(em.find(SecretEntity.class, id).name).isNull();
                return null;
            });
        }
    }

    @DisplayName("read")
    @Nested
    class ReadTest {

        @DisplayName("a read, flushed, never writes")
        @Test
        void __() {
            final long id = persist("alice", 1);
            final var version = version(id);
            final var encrypted = service.encrypted.get();
            final var initialized = service.initialized.get();
            apply(em -> {
                final var found = em.find(SecretEntity.class, id);
                assertThat(service.initialized.get()).as("one init, for the load").isEqualTo(initialized + 1);
                assertThat(found.name).isEqualTo("alice");
                assertThat(found.secret.code).isEqualTo(1);
                em.flush();
                em.flush();
                return null;
            });
            assertThat(version(id)).isEqualTo(version);
            assertThat(service.encrypted.get()).isEqualTo(encrypted);
            assertThat(service.initialized.get()).as("no init for the flushes").isEqualTo(initialized + 1);
        }

        @DisplayName("a refreshed entity, flushed, does not write")
        @Test
        void __refresh() {
            final long id = persist("alice", 1);
            final var version = version(id);
            apply(em -> {
                final var found = em.find(SecretEntity.class, id);
                em.refresh(found);
                assertThat(found.name).isEqualTo("alice");
                em.flush();
                return null;
            });
            assertThat(version(id)).isEqualTo(version);
        }
    }

    @DisplayName("update")
    @Nested
    class UpdateTest {

        @DisplayName("a changed plaintext is encrypted again; the entity keeps it, and the next flush writes nothing")
        @Test
        void __changed() {
            final long id = persist("alice", 1);
            final var version = version(id);
            final var stored = (byte[]) column("name_enc", id);
            apply(em -> {
                final var found = em.find(SecretEntity.class, id);
                found.name = "bob";
                found.secret.code = 2;
                em.flush();
                assertThat(found.name).isEqualTo("bob");
                assertThat(found.secret.code).isEqualTo(2);
                em.flush();
                return null;
            });
            assertThat(version(id)).isEqualTo(version + 1);
            assertThat(column("name", id)).isNull();
            assertThat(column("code", id)).isNull();
            assertThat((byte[]) column("name_enc", id)).isNotEqualTo(stored);
            apply(em -> {
                final var found = em.find(SecretEntity.class, id);
                assertThat(found.name).isEqualTo("bob");
                assertThat(found.secret.code).isEqualTo(2);
                return null;
            });
        }

        @DisplayName("an unrelated change keeps the stored ciphertexts")
        @Test
        void __unrelated() {
            final long id = persist("alice", 1);
            final var version = version(id);
            final var name = (byte[]) column("name_enc", id);
            final var code = (byte[]) column("code_enc", id);
            final var encrypted = service.encrypted.get();
            apply(em -> {
                final var found = em.find(SecretEntity.class, id);
                found.note = "changed";
                em.flush();
                assertThat(found.name).isEqualTo("alice");
                return null;
            });
            assertThat(version(id)).isEqualTo(version + 1);
            assertThat(service.encrypted.get()).isEqualTo(encrypted);
            assertThat((byte[]) column("name_enc", id)).isEqualTo(name);
            assertThat((byte[]) column("code_enc", id)).isEqualTo(code);
            assertThat(column("note", id)).isEqualTo("changed");
        }

        @DisplayName("a detached instance merged with a changed plaintext stores it")
        @Test
        void __merged() {
            final long id = persist("alice", 1);
            final var detached = apply(em -> em.find(SecretEntity.class, id));
            detached.name = "carol";
            apply(em -> {
                final var merged = em.merge(detached);
                assertThat(merged.name).isEqualTo("carol");
                em.flush();
                assertThat(merged.name).isEqualTo("carol");
                return null;
            });
            assertThat(column("name", id)).isNull();
            apply(em -> {
                assertThat(em.find(SecretEntity.class, id).name).isEqualTo("carol");
                return null;
            });
        }

        @DisplayName("a plaintext set to null nulls the ciphertext")
        @Test
        void __nulled() {
            final long id = persist("alice", 1);
            apply(em -> {
                em.find(SecretEntity.class, id).name = null;
                return null;
            });
            assertThat(column("name_enc", id)).isNull();
            assertThat(column("code_enc", id)).isNotNull();
            apply(em -> {
                assertThat(em.find(SecretEntity.class, id).name).isNull();
                return null;
            });
        }
    }

    @DisplayName("legacy rows")
    @Nested
    class LegacyTest {

        @DisplayName("a legacy row reads its plaintext, and a read does not migrate it")
        @Test
        void __read() {
            final long id = insertLegacy("legacy-value");
            apply(em -> {
                assertThat(em.find(SecretEntity.class, id).name).isEqualTo("legacy-value");
                em.flush();
                return null;
            });
            assertThat(version(id)).isEqualTo(1L);
            assertThat(column("name", id)).isEqualTo("legacy-value");
        }

        @DisplayName("a legacy row is migrated by an unrelated write")
        @Test
        void __migrated() {
            final long id = insertLegacy("legacy-value");
            apply(em -> {
                final var found = em.find(SecretEntity.class, id);
                found.note = "written";
                em.flush();
                assertThat(found.name).isEqualTo("legacy-value");
                return null;
            });
            assertThat(column("name", id)).isNull();
            assertThat((byte[]) column("name_enc", id)).isNotEmpty();
            apply(em -> {
                assertThat(em.find(SecretEntity.class, id).name).isEqualTo("legacy-value");
                return null;
            });
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final String unitName;

    private CountingEncryptionService service;

    private EntityManagerFactory entityManagerFactory;
}
