package com.github.jinahya.persistence.encryption;

import com.github.jinahya.persistence.encryption.examples.secondary.SecondaryEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies, on both providers, an entity whose ciphertext lives in a {@link jakarta.persistence.SecondaryTable}. The
 * module finds attributes in the metamodel, never in tables, so a ciphertext column of a secondary table is an ordinary
 * attribute of the same entity. Whether every row has its secondary row is the schema's, and the persistence
 * provider's, matter, not the module's; it is not tested here.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class SecondaryTable_Test {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static EntityManagerFactory ENTITY_MANAGER_FACTORY;

    @BeforeAll
    static void setUp() {
        ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("__encryptionPU");
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

    private static long count(final String table, final long id) {
        return apply(em -> ((Number) em.createNativeQuery(
                "SELECT COUNT(*) FROM " + table + " WHERE id = " + id).getSingleResult()).longValue());
    }

    private static Object single(final String sql) {
        return apply(em -> em.createNativeQuery(sql).getSingleResult());
    }

    private static String unique(final String prefix) {
        return prefix + "-" + System.nanoTime();
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("the plaintext in the primary table, the ciphertext in the secondary table")
    @Nested
    class PrimaryAndSecondaryTest {

        @DisplayName("a value round-trips; the primary table keeps no plaintext, the secondary table the ciphertext")
        @Test
        void __roundTrip() {
            final var plaintext = unique("a-secret");
            final long id = apply(em -> {
                final var entity = new SecondaryEntity();
                entity.note = unique("a-note");
                entity.name = plaintext;
                em.persist(entity);
                return entity;
            }).id;

            assertThat(single("SELECT name FROM " + SecondaryEntity.TABLE_NAME + " WHERE id = " + id))
                    .as("no plaintext in the primary table").isNull();
            final var stored = (byte[]) single("SELECT name_enc FROM " + SecondaryEntity.SECONDARY_TABLE_NAME
                                               + " WHERE id = " + id);
            assertThat(stored).as("the ciphertext is in the secondary table").isNotNull().isNotEmpty();

            // read inside the transaction: the listener empties the instance on every flush, the commit included
            final var read = apply(em -> em.find(SecondaryEntity.class, id).name);
            assertThat(read).isEqualTo(plaintext);
        }
    }
}
