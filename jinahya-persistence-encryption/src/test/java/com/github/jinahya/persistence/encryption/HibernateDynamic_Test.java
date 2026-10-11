package com.github.jinahya.persistence.encryption;

import com.github.jinahya.persistence.encryption.examples.hibernate.DynamicEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies, under Hibernate ORM's {@code @DynamicInsert} and {@code @DynamicUpdate}, that what the listener writes into
 * an entity reaches the row: those annotations write only the non-{@code null}, and only the changed, columns, so a
 * column the listener changes has to be seen as changed. Compiled, and run, under the jakarta-ee-11-hibernate-orm
 * profile only.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class HibernateDynamic_Test {

    private static EntityManagerFactory ENTITY_MANAGER_FACTORY;

    @BeforeAll
    static void setUp() {
        ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("__encryptionHibernatePU");
        LifecycleListenerEntity.SERVICE = new EntityEncryptionServiceImpl(ENTITY_MANAGER_FACTORY);
    }

    @AfterAll
    static void tearDown() {
        LifecycleListenerEntity.SERVICE = null;
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

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("@DynamicInsert / @DynamicUpdate")
    @Nested
    class DynamicTest {

        @DisplayName("a value persisted is stored as ciphertext only, and reads back")
        @Test
        void __persist() {
            final long id = apply(em -> {
                final var entity = new DynamicEntity();
                entity.note = "note";
                entity.name = "first";
                em.persist(entity);
                return entity;
            }).id;
            assertThat(column(DynamicEntity.TABLE_NAME, "name", id)).isNull();
            assertThat((byte[]) column(DynamicEntity.TABLE_NAME, "name_enc", id)).isNotNull().isNotEmpty();
            // read inside the transaction: the listener empties the instance on every flush, the commit included
            final String read = apply(em -> em.find(DynamicEntity.class, id).name);
            assertThat(read).isEqualTo("first");
        }

        @DisplayName("a legacy row is migrated by an unrelated write: the plaintext nulled, the ciphertext stored")
        @Test
        void __legacyRowMigrated() {
            final var marker = "legacy-" + System.nanoTime();
            apply(em -> em.createNativeQuery("INSERT INTO " + DynamicEntity.TABLE_NAME
                                             + " (note, name) VALUES ('" + marker + "', 'legacy-value')")
                    .executeUpdate());
            final long id = apply(em -> ((Number) em.createNativeQuery(
                            "SELECT id FROM " + DynamicEntity.TABLE_NAME + " WHERE note = '" + marker + "'")
                    .getSingleResult()).longValue());

            apply(em -> {
                em.find(DynamicEntity.class, id).note = marker + "-written";
                return null;
            });

            assertThat(column(DynamicEntity.TABLE_NAME, "name", id)).as("the plaintext column").isNull();
            assertThat((byte[]) column(DynamicEntity.TABLE_NAME, "name_enc", id)).as("the ciphertext")
                    .isNotNull().isNotEmpty();
            final String read = apply(em -> em.find(DynamicEntity.class, id).name);
            assertThat(read).isEqualTo("legacy-value");
        }
    }
}
