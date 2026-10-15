package com.github.jinahya.persistence.encryption.hibernate;

import com.github.jinahya.persistence.encryption.EntityEncryptionEngine;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.hibernate.Interceptor;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.type.Type;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Verifies the Hibernate ORM integration against an in-memory database: what reaches the row, what the entity keeps,
 * and which flushes write.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class EntityEncryptionIntegrator_Test {

    private static final CountingEncryptionService SERVICE = new CountingEncryptionService();

    private static EntityManagerFactory ENTITY_MANAGER_FACTORY;

    @BeforeAll
    static void setUp() {
        ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory(
                "__encryptionHibernatePU", Map.of(EntityEncryptionEngine.PROPERTY_ENCRYPTION_SERVICE, SERVICE));
    }

    @AfterAll
    static void tearDown() {
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

    private static Object column(final String table, final String column, final long id) {
        return apply(em -> em.createNativeQuery("SELECT " + column + " FROM " + table + " WHERE id = " + id)
                .getSingleResult());
    }

    private static long updates() {
        return ENTITY_MANAGER_FACTORY.unwrap(SessionFactory.class).getStatistics().getEntityUpdateCount();
    }

    private static long insertLegacy(final String table, final String name) {
        final var marker = "legacy-" + System.nanoTime();
        apply(em -> em.createNativeQuery("INSERT INTO " + table + " (note, name) VALUES ('" + marker + "', '" + name
                                         + "')").executeUpdate());
        return apply(em -> ((Number) em.createNativeQuery(
                "SELECT id FROM " + table + " WHERE note = '" + marker + "'").getSingleResult()).longValue());
    }

    private static long persist(final String name, final Integer code) {
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
            assertThat(column(SecretEntity.TABLE_NAME, "name", id)).isNull();
            assertThat(column(SecretEntity.TABLE_NAME, "code", id)).isNull();
            assertThat((byte[]) column(SecretEntity.TABLE_NAME, "name_enc", id)).isEqualTo(persisted.nameEnc);
            assertThat((byte[]) column(SecretEntity.TABLE_NAME, "code_enc", id)).isEqualTo(persisted.secret.codeEnc);
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
            final var before = updates();
            apply(em -> {
                final var entity = new SecretEntity();
                entity.name = "alice";
                entity.secret = new SecretEmbeddable();
                entity.secret.code = 42;
                em.persist(entity);
                em.flush();
                em.flush();
                return null;
            });
            assertThat(updates()).isEqualTo(before);
        }

        @DisplayName("a null plaintext is a null ciphertext")
        @Test
        void __null() {
            final long id = persist(null, null);
            assertThat(column(SecretEntity.TABLE_NAME, "name_enc", id)).isNull();
            assertThat(column(SecretEntity.TABLE_NAME, "code_enc", id)).isNull();
            apply(em -> {
                final var found = em.find(SecretEntity.class, id);
                assertThat(found.name).isNull();
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
            final var before = updates();
            final var encrypted = SERVICE.encrypted.get();
            final var initialized = SERVICE.initialized.get();
            apply(em -> {
                final var found = em.find(SecretEntity.class, id);
                assertThat(SERVICE.initialized.get()).as("one init, for the load").isEqualTo(initialized + 1);
                assertThat(found.name).isEqualTo("alice");
                em.flush();
                em.flush();
                return null;
            });
            assertThat(updates()).isEqualTo(before);
            assertThat(SERVICE.encrypted.get()).isEqualTo(encrypted);
            assertThat(SERVICE.initialized.get()).as("no init for the flushes").isEqualTo(initialized + 1);
        }
    }

    @DisplayName("update")
    @Nested
    class UpdateTest {

        @DisplayName("a changed plaintext is encrypted again; the entity keeps it, and the next flush writes nothing")
        @Test
        void __changed() {
            final long id = persist("alice", 1);
            final var stored = (byte[]) column(SecretEntity.TABLE_NAME, "name_enc", id);
            apply(em -> {
                final var found = em.find(SecretEntity.class, id);
                found.name = "bob";
                found.secret.code = 2;
                em.flush();
                assertThat(found.name).isEqualTo("bob");
                assertThat(found.secret.code).isEqualTo(2);
                assertThat(found.updates).isEqualTo(1);
                final var before = updates();
                em.flush();
                assertThat(updates()).isEqualTo(before);
                return null;
            });
            assertThat(column(SecretEntity.TABLE_NAME, "name", id)).isNull();
            assertThat(column(SecretEntity.TABLE_NAME, "code", id)).isNull();
            assertThat((byte[]) column(SecretEntity.TABLE_NAME, "name_enc", id)).isNotEqualTo(stored);
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
            final var name = (byte[]) column(SecretEntity.TABLE_NAME, "name_enc", id);
            final var code = (byte[]) column(SecretEntity.TABLE_NAME, "code_enc", id);
            final var encrypted = SERVICE.encrypted.get();
            final var before = updates();
            apply(em -> {
                final var found = em.find(SecretEntity.class, id);
                found.note = "changed";
                em.flush();
                assertThat(found.name).isEqualTo("alice");
                return null;
            });
            assertThat(updates()).isEqualTo(before + 1);
            assertThat(SERVICE.encrypted.get()).isEqualTo(encrypted);
            assertThat((byte[]) column(SecretEntity.TABLE_NAME, "name_enc", id)).isEqualTo(name);
            assertThat((byte[]) column(SecretEntity.TABLE_NAME, "code_enc", id)).isEqualTo(code);
            assertThat(column(SecretEntity.TABLE_NAME, "note", id)).isEqualTo("changed");
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
            assertThat(column(SecretEntity.TABLE_NAME, "name", id)).isNull();
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
            assertThat(column(SecretEntity.TABLE_NAME, "name_enc", id)).isNull();
            assertThat(column(SecretEntity.TABLE_NAME, "code_enc", id)).isNotNull();
        }
    }

    @DisplayName("legacy rows")
    @Nested
    class LegacyTest {

        @DisplayName("a legacy row reads its plaintext, and a read does not migrate it")
        @Test
        void __read() {
            final long id = insertLegacy(SecretEntity.TABLE_NAME, "legacy-value");
            final var before = updates();
            apply(em -> {
                assertThat(em.find(SecretEntity.class, id).name).isEqualTo("legacy-value");
                return null;
            });
            assertThat(updates()).isEqualTo(before);
            assertThat(column(SecretEntity.TABLE_NAME, "name", id)).isEqualTo("legacy-value");
        }

        @DisplayName("a legacy row is migrated by an unrelated write")
        @Test
        void __migrated() {
            final long id = insertLegacy(SecretEntity.TABLE_NAME, "legacy-value");
            apply(em -> {
                final var found = em.find(SecretEntity.class, id);
                found.note = "written";
                em.flush();
                assertThat(found.name).isEqualTo("legacy-value");
                return null;
            });
            assertThat(column(SecretEntity.TABLE_NAME, "name", id)).isNull();
            assertThat((byte[]) column(SecretEntity.TABLE_NAME, "name_enc", id)).isNotEmpty();
            apply(em -> {
                assertThat(em.find(SecretEntity.class, id).name).isEqualTo("legacy-value");
                return null;
            });
        }
    }

    @DisplayName("callbacks")
    @Nested
    class CallbackTest {

        // https://github.com/jinahya/jinahya-persistence/issues/100
        @DisplayName("a plaintext assigned by @PreUpdate is kept by the entity, and stored as ciphertext only")
        @Test
        void __preUpdate() {
            final long id = apply(em -> {
                final var entity = new CallbackSecretEntity();
                entity.name = "original";
                em.persist(entity);
                return entity;
            }).id;
            apply(em -> {
                final var found = em.find(CallbackSecretEntity.class, id);
                found.note = "changed";
                found.nameOnPreUpdate = "callback-value";
                em.flush();
                assertThat(found.name).isEqualTo("callback-value");
                return null;
            });
            assertThat(column(CallbackSecretEntity.TABLE_NAME, "name", id)).isNull();
            apply(em -> {
                assertThat(em.find(CallbackSecretEntity.class, id).name).isEqualTo("callback-value");
                return null;
            });
        }

        private long persistCallback(final String name) {
            return apply(em -> {
                final var entity = new CallbackSecretEntity();
                entity.name = name;
                em.persist(entity);
                return entity;
            }).id;
        }

        // https://github.com/jinahya/jinahya-persistence/issues/106
        @DisplayName("@PostLoad sees the decrypted plaintext, and a read still writes nothing")
        @Test
        void __postLoad() {
            final long id = persistCallback("original");
            final var before = updates();
            apply(em -> {
                final var found = em.find(CallbackSecretEntity.class, id);
                assertThat(found.nameSeenOnPostLoad).isEqualTo("original");
                em.flush();
                return null;
            });
            assertThat(updates()).isEqualTo(before);
        }

        // https://github.com/jinahya/jinahya-persistence/issues/106
        @DisplayName("@PostLoad sees a legacy row's plaintext, and the read doesn't migrate it")
        @Test
        void __postLoadLegacy() {
            final long id = insertLegacy(CallbackSecretEntity.TABLE_NAME, "legacy");
            apply(em -> {
                assertThat(em.find(CallbackSecretEntity.class, id).nameSeenOnPostLoad).isEqualTo("legacy");
                return null;
            });
            assertThat(column(CallbackSecretEntity.TABLE_NAME, "name", id)).isEqualTo("legacy");
            assertThat(column(CallbackSecretEntity.TABLE_NAME, "name_enc", id)).isNull();
        }

        // https://github.com/jinahya/jinahya-persistence/issues/106
        @DisplayName("a plaintext @PostLoad assigns is kept, not overwritten by decryption, and stored encrypted")
        @Test
        void __postLoadAssigns() {
            final long id = persistCallback("original");
            CallbackSecretEntity.nameAssignedOnPostLoad = "assigned";
            try {
                apply(em -> {
                    final var found = em.find(CallbackSecretEntity.class, id);
                    assertThat(found.name).isEqualTo("assigned");
                    return null;
                });
            } finally {
                CallbackSecretEntity.nameAssignedOnPostLoad = null;
            }
            assertThat(column(CallbackSecretEntity.TABLE_NAME, "name", id)).isNull();
            apply(em -> {
                assertThat(em.find(CallbackSecretEntity.class, id).name).isEqualTo("assigned");
                return null;
            });
        }

        // https://github.com/jinahya/jinahya-persistence/issues/106
        @DisplayName("@PostLoad sees the decrypted plaintext on refresh")
        @Test
        void __postLoadRefresh() {
            final long id = persistCallback("original");
            apply(em -> {
                final var found = em.find(CallbackSecretEntity.class, id);
                found.nameSeenOnPostLoad = null;
                em.refresh(found);
                assertThat(found.nameSeenOnPostLoad).isEqualTo("original");
                return null;
            });
        }

        // https://github.com/jinahya/jinahya-persistence/issues/102
        @DisplayName("a plaintext an interceptor assigns in onFlushDirty is kept by the entity, and stored as ciphertext"
                     + " only")
        @Test
        void __onFlushDirty() {
            final long id = persist("original", 1);
            final var interceptor = new Interceptor() {
                @Override
                public boolean onFlushDirty(final Object entity, final Object id, final Object[] currentState,
                                            final Object[] previousState, final String[] propertyNames,
                                            final Type[] types) {
                    currentState[Arrays.asList(propertyNames).indexOf("name")] = "interceptor-value";
                    return true;
                }
            };
            try (var session = ENTITY_MANAGER_FACTORY.unwrap(SessionFactory.class).withOptions()
                    .interceptor(interceptor).openSession()) {
                final var tx = session.beginTransaction();
                final var found = session.find(SecretEntity.class, id);
                found.note = "changed";
                session.flush();
                assertThat(found.name).isEqualTo("interceptor-value");
                tx.commit();
            }
            assertThat(column(SecretEntity.TABLE_NAME, "name", id)).isNull();
            apply(em -> {
                assertThat(em.find(SecretEntity.class, id).name).isEqualTo("interceptor-value");
                return null;
            });
        }
    }

    // https://github.com/jinahya/jinahya-persistence/issues/105
    @DisplayName("versionless optimistic locking is refused")
    @Nested
    class OptimisticLockingTest {

        @DisplayName("ALL")
        @Test
        void __all() {
            assertThatThrownBy(() -> apply(em -> {
                final var entity = new LockAllSecretEntity();
                entity.name = "alice";
                em.persist(entity);
                em.flush();
                return null;
            })).hasStackTraceContaining("versionless optimistic locking");
        }

        @DisplayName("DIRTY")
        @Test
        void __dirty() {
            assertThatThrownBy(() -> apply(em -> {
                final var entity = new LockDirtySecretEntity();
                entity.name = "alice";
                em.persist(entity);
                em.flush();
                return null;
            })).hasStackTraceContaining("versionless optimistic locking");
        }
    }

    // https://github.com/jinahya/jinahya-persistence/issues/104
    @DisplayName("StatelessSession")
    @Nested
    class StatelessTest {

        private static <R> R stateless(final Function<? super StatelessSession, ? extends R> function) {
            try (var session = ENTITY_MANAGER_FACTORY.unwrap(SessionFactory.class).openStatelessSession()) {
                final var tx = session.beginTransaction();
                try {
                    return function.apply(session);
                } finally {
                    tx.rollback();
                }
            }
        }

        @DisplayName("insert is refused, and nothing is written")
        @Test
        void __insert() {
            final var count = (Number) apply(em -> em.createNativeQuery(
                    "SELECT COUNT(*) FROM " + SecretEntity.TABLE_NAME).getSingleResult());
            final var entity = new SecretEntity();
            entity.name = "alice";
            assertThatThrownBy(() -> stateless(session -> {
                session.insert(entity);
                return null;
            })).isInstanceOf(IllegalStateException.class).hasMessageContaining("StatelessSession");
            assertThat((Number) apply(em -> em.createNativeQuery(
                    "SELECT COUNT(*) FROM " + SecretEntity.TABLE_NAME).getSingleResult()))
                    .isEqualTo(count);
        }

        @DisplayName("update is refused, so the plaintext is never written in the clear")
        @Test
        void __update() {
            final long id = persist("original", 1);
            final var stored = (byte[]) column(SecretEntity.TABLE_NAME, "name_enc", id);
            assertThatThrownBy(() -> stateless(session -> {
                final var found = session.get(SecretEntity.class, id);
                found.name = "changed";
                session.update(found);
                return null;
            })).isInstanceOf(IllegalStateException.class).hasMessageContaining("StatelessSession");
            assertThat(column(SecretEntity.TABLE_NAME, "name", id)).isNull();
            assertThat((byte[]) column(SecretEntity.TABLE_NAME, "name_enc", id)).isEqualTo(stored);
        }

        @DisplayName("upsert is refused, so the plaintext is never written in the clear")
        @Test
        void __upsert() {
            final long id = persist("original", 1);
            final var stored = (byte[]) column(SecretEntity.TABLE_NAME, "name_enc", id);
            assertThatThrownBy(() -> stateless(session -> {
                final var found = session.get(SecretEntity.class, id);
                found.name = "changed";
                session.upsert(found);
                return null;
            })).isInstanceOf(IllegalStateException.class).hasMessageContaining("StatelessSession");
            assertThat(column(SecretEntity.TABLE_NAME, "name", id)).isNull();
            assertThat((byte[]) column(SecretEntity.TABLE_NAME, "name_enc", id)).isEqualTo(stored);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("@DynamicInsert / @DynamicUpdate")
    @Nested
    class DynamicTest {

        @DisplayName("a value persisted is stored as ciphertext only, and reads back")
        @Test
        void __persist() {
            final long id = apply(em -> {
                final var entity = new DynamicSecretEntity();
                entity.name = "first";
                em.persist(entity);
                return entity;
            }).id;
            assertThat(column(DynamicSecretEntity.TABLE_NAME, "name", id)).isNull();
            assertThat((byte[]) column(DynamicSecretEntity.TABLE_NAME, "name_enc", id)).isNotEmpty();
            apply(em -> {
                assertThat(em.find(DynamicSecretEntity.class, id).name).isEqualTo("first");
                return null;
            });
        }

        @DisplayName("a changed plaintext reaches the row")
        @Test
        void __changed() {
            final long id = apply(em -> {
                final var entity = new DynamicSecretEntity();
                entity.name = "first";
                em.persist(entity);
                return entity;
            }).id;
            apply(em -> {
                em.find(DynamicSecretEntity.class, id).name = "second";
                return null;
            });
            apply(em -> {
                assertThat(em.find(DynamicSecretEntity.class, id).name).isEqualTo("second");
                return null;
            });
        }

        @DisplayName("a legacy row is migrated by an unrelated write")
        @Test
        void __legacyRowMigrated() {
            final long id = insertLegacy(DynamicSecretEntity.TABLE_NAME, "legacy-value");
            apply(em -> {
                em.find(DynamicSecretEntity.class, id).note = "written";
                return null;
            });
            assertThat(column(DynamicSecretEntity.TABLE_NAME, "name", id)).isNull();
            assertThat((byte[]) column(DynamicSecretEntity.TABLE_NAME, "name_enc", id)).isNotEmpty();
            apply(em -> {
                assertThat(em.find(DynamicSecretEntity.class, id).name).isEqualTo("legacy-value");
                return null;
            });
        }
    }
}
