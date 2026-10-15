package com.github.jinahya.persistence.encryption;

import com.github.jinahya.persistence.encryption.examples.CallbackSecretEntity;
import com.github.jinahya.persistence.encryption.examples.XmlCallbackSecretEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Verifies, against the persistence provider, that callbacks which run after the encryption listener can't store a
 * plaintext (#101).
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://github.com/jinahya/jinahya-persistence/issues/101">#101</a>
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class LateWriteCallback_Persistence_Test {

    private static EntityManagerFactory ENTITY_MANAGER_FACTORY;

    @BeforeAll
    static void openEntityManagerFactory() {
        ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("__encryptionCallbacksPU");
        LifecycleListenerEntity.SERVICE = new EntityEncryptionServiceImpl(ENTITY_MANAGER_FACTORY);
    }

    @AfterAll
    static void closeEntityManagerFactory() {
        LifecycleListenerEntity.SERVICE = null;
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
            } catch (final RuntimeException re) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                throw re;
            }
        }
    }

    private static long count(final String table) {
        return apply(em -> ((Number) em.createNativeQuery("SELECT COUNT(*) FROM " + table).getSingleResult()))
                .longValue();
    }

    private static Object name(final String table, final long id) {
        return apply(em -> em.createNativeQuery("SELECT name FROM " + table + " WHERE id = " + id)
                .getSingleResult());
    }

    @DisplayName("an entity with a callback of its own is rejected before anything is encrypted or written")
    @Test
    void __rejected() {
        final var before = count(CallbackSecretEntity.TABLE_NAME);
        final var entity = new CallbackSecretEntity();
        entity.name = "original";
        assertThatThrownBy(() -> apply(em -> {
            em.persist(entity);
            em.flush();
            return null;
        })).hasStackTraceContaining("callbacks which run after")
                .hasStackTraceContaining("CallbackSecretEntity.assignOnPreUpdate()");
        assertThat(entity.nameEnc).isNull();
        assertThat(count(CallbackSecretEntity.TABLE_NAME)).isEqualTo(before);
    }

    @DisplayName("a plaintext assigned by a callback the validation can't see fails the UPDATE, which is rolled back")
    @Test
    void __guarded() {
        final long id = apply(em -> {
            final var entity = new XmlCallbackSecretEntity();
            entity.name = "original";
            em.persist(entity);
            return entity;
        }).id;
        assertThatThrownBy(() -> apply(em -> {
            final var found = em.find(XmlCallbackSecretEntity.class, id);
            found.note = "changed";
            found.nameOnPreUpdate = "late-value";
            em.flush();
            return null;
        })).hasStackTraceContaining("was assigned after it was encrypted");
        assertThat(name(XmlCallbackSecretEntity.TABLE_NAME, id)).isNull();
        apply(em -> {
            assertThat(em.find(XmlCallbackSecretEntity.class, id).name).isEqualTo("original");
            return null;
        });
    }
}
