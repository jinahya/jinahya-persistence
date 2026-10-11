package com.github.jinahya.persistence.crypto;

import com.github.jinahya.persistence.crypto.examples.hibernate.DynamicModeAEntity;
import com.github.jinahya.persistence.crypto.examples.hibernate.DynamicModeBEntity;
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
        ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("__cryptoHibernatePU");
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
    @DisplayName("Mode B")
    @Nested
    class ModeBTest {

        private static long persist(final String name) {
            return apply(em -> {
                final var entity = new DynamicModeBEntity();
                entity.note = "note";
                entity.setName(name);
                em.persist(entity);
                return entity;
            }).id;
        }

        private static byte[] ciphertext(final long id) {
            return (byte[]) column(DynamicModeBEntity.TABLE_NAME, "name_enc", id);
        }

        private static String read(final long id) {
            return apply(em -> em.find(DynamicModeBEntity.class, id).name);
        }

        @DisplayName("a value persisted is stored, and reads back")
        @Test
        void __persist() {
            final var id = persist("first");
            assertThat(ciphertext(id)).isNotNull().isNotEmpty();
            assertThat(read(id)).isEqualTo("first");
        }

        @DisplayName("persisted with no value, then given one: stored, and the empty marker never reaches the row")
        @Test
        void __noValueThenValue() {
            final var id = persist(null);
            assertThat(ciphertext(id)).isNull();
            apply(em -> {
                em.find(DynamicModeBEntity.class, id).setName("later");
                return null;
            });
            assertThat(ciphertext(id)).isNotNull().isNotEmpty();
            assertThat(read(id)).isEqualTo("later");
        }

        @DisplayName("a changed value is stored")
        @Test
        void __change() {
            final var id = persist("before");
            apply(em -> {
                em.find(DynamicModeBEntity.class, id).setName("after");
                return null;
            });
            assertThat(read(id)).isEqualTo("after");
        }

        @DisplayName("a cleared value is stored as null")
        @Test
        void __clear() {
            final var id = persist("clear-me");
            apply(em -> {
                em.find(DynamicModeBEntity.class, id).setName(null);
                return null;
            });
            assertThat(ciphertext(id)).isNull();
            assertThat(read(id)).isNull();
        }

        @DisplayName("an unrelated change leaves the ciphertext as it was")
        @Test
        void __unrelatedChange() {
            final var id = persist("kept");
            final var before = ciphertext(id);
            apply(em -> {
                em.find(DynamicModeBEntity.class, id).note = "changed";
                return null;
            });
            assertThat(ciphertext(id)).isEqualTo(before);
            assertThat(read(id)).isEqualTo("kept");
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("Mode A")
    @Nested
    class ModeATest {

        @DisplayName("a value persisted is stored as ciphertext only, and reads back")
        @Test
        void __persist() {
            final long id = apply(em -> {
                final var entity = new DynamicModeAEntity();
                entity.note = "note";
                entity.name = "first";
                em.persist(entity);
                return entity;
            }).id;
            assertThat(column(DynamicModeAEntity.TABLE_NAME, "name", id)).isNull();
            assertThat((byte[]) column(DynamicModeAEntity.TABLE_NAME, "name_enc", id)).isNotNull().isNotEmpty();
            // read inside the transaction: Mode A empties the instance on every flush, the commit included
            final String read = apply(em -> em.find(DynamicModeAEntity.class, id).name);
            assertThat(read).isEqualTo("first");
        }

        @DisplayName("a legacy row is migrated by an unrelated write: the plaintext nulled, the ciphertext stored")
        @Test
        void __legacyRowMigrated() {
            final var marker = "legacy-" + System.nanoTime();
            apply(em -> em.createNativeQuery("INSERT INTO " + DynamicModeAEntity.TABLE_NAME
                                             + " (note, name) VALUES ('" + marker + "', 'legacy-value')")
                    .executeUpdate());
            final long id = apply(em -> ((Number) em.createNativeQuery(
                            "SELECT id FROM " + DynamicModeAEntity.TABLE_NAME + " WHERE note = '" + marker + "'")
                    .getSingleResult()).longValue());

            apply(em -> {
                em.find(DynamicModeAEntity.class, id).note = marker + "-written";
                return null;
            });

            assertThat(column(DynamicModeAEntity.TABLE_NAME, "name", id)).as("the plaintext column").isNull();
            assertThat((byte[]) column(DynamicModeAEntity.TABLE_NAME, "name_enc", id)).as("the ciphertext")
                    .isNotNull().isNotEmpty();
            final String read = apply(em -> em.find(DynamicModeAEntity.class, id).name);
            assertThat(read).isEqualTo("legacy-value");
        }
    }
}
