package com.github.jinahya.persistence.encryption;

import com.github.jinahya.persistence.encryption.examples.converter.ConvertedSecretEntity;
import com.github.jinahya.persistence.encryption.examples.converter.EncryptedStringConverter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceConfiguration;
import jakarta.persistence.SharedCacheMode;
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

/**
 * A sketch, not a feature: what a standard {@link jakarta.persistence.AttributeConverter} gives, measured on whichever
 * provider the build runs on. Nothing of the module's listener or engines is involved.
 */
@SuppressWarnings({"java:S101"})
class ConverterSketch_Test {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static EntityManagerFactory EMF;

    private static String provider() {
        for (final var name : new String[] {"org.hibernate.jpa.HibernatePersistenceProvider",
                                            "org.eclipse.persistence.jpa.PersistenceProvider"}) {
            try {
                Class.forName(name);
                return name;
            } catch (final ClassNotFoundException cnfe) {
                // next
            }
        }
        throw new IllegalStateException("no persistence provider on the class path");
    }

    @BeforeAll
    static void open() {
        final var provider = provider();
        log.info("provider: {}", provider);
        EMF = new PersistenceConfiguration("__converterSketchPU")
                .provider(provider)
                .managedClass(ConvertedSecretEntity.class)
                .managedClass(EncryptedStringConverter.class)
                .sharedCacheMode(SharedCacheMode.NONE)
                .property("jakarta.persistence.jdbc.driver", "org.h2.Driver")
                .property("jakarta.persistence.jdbc.url",
                          "jdbc:h2:mem:converter-sketch;DB_CLOSE_DELAY=-1;database_to_upper=false;MODE=LEGACY")
                .property("jakarta.persistence.jdbc.user", "sa")
                .property("jakarta.persistence.jdbc.password", "password")
                .property("eclipselink.target-database", "org.eclipse.persistence.platform.database.H2Platform")
                .property("eclipselink.ddl-generation", "create-tables")
                .property("eclipselink.logging.level.sql", "FINE")
                .property("eclipselink.logging.parameters", "true")
                .property("hibernate.hbm2ddl.auto", "create-drop")
                .property("hibernate.show_sql", "true")
                .createEntityManagerFactory();
    }

    @AfterAll
    static void close() {
        if (EMF != null) {
            EMF.close();
        }
    }

    private static <R> R apply(final Function<? super EntityManager, ? extends R> function) {
        try (final var em = EMF.createEntityManager()) {
            final var tx = em.getTransaction();
            tx.begin();
            try {
                final var result = function.apply(em);
                tx.commit();
                return result;
            } catch (final Exception e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                throw e;
            }
        }
    }

    /** version, name (legacy column), name_enc */
    private static Object[] row(final long id) {
        return apply(em -> (Object[]) em.createNativeQuery(
                        "SELECT version, name, name_enc FROM " + ConvertedSecretEntity.TABLE_NAME + " WHERE id = " + id)
                .getSingleResult());
    }

    private static long persisted(final String name) {
        return apply(em -> {
            final var e = new ConvertedSecretEntity();
            e.name = name;
            e.note = "note";
            em.persist(e);
            return e;
        }).id;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("persist stores the ciphertext only, the legacy column stays NULL, and the value reads back")
    @Test
    void persist() {
        final var id = persisted("alice");
        final var row = row(id);
        assertThat(row[1]).as("legacy plaintext column").isNull();
        assertThat((byte[]) row[2]).as("ciphertext").isNotNull().isNotEmpty();
        assertThat(new String((byte[]) row[2])).doesNotContain("alice");
        assertThat(apply(em -> em.find(ConvertedSecretEntity.class, id)).name).isEqualTo("alice");
    }

    @DisplayName("a read, flushed and committed, never writes: version and ciphertext unchanged, no encrypt call")
    @Test
    void readDoesNotWrite() {
        final var id = persisted("bob");
        final var before = row(id);
        final var encryptedBefore = EncryptedStringConverter.ENCRYPTED.get();
        apply(em -> {
            final var e = em.find(ConvertedSecretEntity.class, id);
            assertThat(e.name).isEqualTo("bob");
            em.flush();
            return e;
        });
        apply(em -> em.find(ConvertedSecretEntity.class, id)); // and once more
        final var after = row(id);
        assertThat(after[0]).as("version").isEqualTo(before[0]);
        assertThat((byte[]) after[2]).as("ciphertext bytes").isEqualTo((byte[]) before[2]);
        assertThat(EncryptedStringConverter.ENCRYPTED.get()).as("encrypt calls").isEqualTo(encryptedBefore);
    }

    @DisplayName("an unrelated change: measured whether the provider re-encrypts the unchanged plaintext")
    @Test
    void unrelatedChange() {
        final var id = persisted("carol");
        final var before = row(id);
        apply(em -> {
            final var e = em.find(ConvertedSecretEntity.class, id);
            e.note = "changed";
            return e;
        });
        final var after = row(id);
        assertThat(after[0]).as("version").isEqualTo((Integer) before[0] + 1);
        final var same = Arrays.equals((byte[]) before[2], (byte[]) after[2]);
        log.info("unrelated change: ciphertext {}", same ? "kept as stored" : "re-encrypted (every column written)");
        assertThat(apply(em -> em.find(ConvertedSecretEntity.class, id)).name).isEqualTo("carol");
    }

    @DisplayName("a changed plaintext is stored as a new ciphertext, and a null plaintext as a NULL ciphertext")
    @Test
    void changed() {
        final var id = persisted("dave");
        final var before = row(id);
        apply(em -> {
            em.find(ConvertedSecretEntity.class, id).name = "dave2";
            return null;
        });
        final var after = row(id);
        assertThat((byte[]) after[2]).isNotEqualTo((byte[]) before[2]);
        assertThat(apply(em -> em.find(ConvertedSecretEntity.class, id)).name).isEqualTo("dave2");
        apply(em -> {
            em.find(ConvertedSecretEntity.class, id).name = null;
            return null;
        });
        assertThat(row(id)[2]).isNull();
        assertThat(apply(em -> em.find(ConvertedSecretEntity.class, id)).name).isNull();
    }

    @DisplayName("a legacy row reads its plaintext, and the reading transaction's flush migrates it")
    @Test
    void legacyRow() {
        final var id = persisted("placeholder");
        // turn it into a legacy row: plaintext in the old column, no ciphertext
        apply(em -> em.createNativeQuery("UPDATE " + ConvertedSecretEntity.TABLE_NAME
                                         + " SET name = 'legacy', name_enc = NULL WHERE id = " + id).executeUpdate());
        assertThat(row(id)[1]).isEqualTo("legacy");
        assertThat(row(id)[2]).isNull();
        final var read = apply(em -> em.find(ConvertedSecretEntity.class, id));
        assertThat(read.name).isEqualTo("legacy");
        final var migrated = row(id);
        assertThat(migrated[1]).as("legacy column nulled by the migrating UPDATE").isNull();
        assertThat((byte[]) migrated[2]).as("ciphertext stored by the migrating UPDATE").isNotNull();
        assertThat(apply(em -> em.find(ConvertedSecretEntity.class, id)).name).isEqualTo("legacy");
        // and the migrated row no longer writes on read
        final var v = row(id)[0];
        apply(em -> em.find(ConvertedSecretEntity.class, id));
        assertThat(row(id)[0]).isEqualTo(v);
    }

    @DisplayName("the ciphertext attribute mirrors the column: loaded with the row, stale after a change until refresh")
    @Test
    void mirror() {
        final var id = persisted("gina");
        final var row = row(id);
        apply(em -> {
            final var e = em.find(ConvertedSecretEntity.class, id);
            assertThat(e.nameEnc).as("mirror after load").isEqualTo((byte[]) row[2]);
            e.name = "gina2";
            em.flush();
            final var stale = e.nameEnc;
            final var stored = (byte[]) em.createNativeQuery("SELECT name_enc FROM " + ConvertedSecretEntity.TABLE_NAME
                                                             + " WHERE id = " + id).getSingleResult();
            log.info("mirror after a change and flush: {}", Arrays.equals(stale, stored) ? "fresh" : "stale");
            em.refresh(e);
            assertThat(e.nameEnc).as("mirror after refresh").isEqualTo(stored);
            assertThat(e.name).isEqualTo("gina2");
            return e;
        });
        // the mirror is never written: assigning it changes nothing in the row
        final var before = row(id);
        apply(em -> {
            em.find(ConvertedSecretEntity.class, id).nameEnc = new byte[] {1, 2, 3};
            return null;
        });
        assertThat(row(id)).isEqualTo(before);
    }

    @DisplayName("a detached instance merged with a changed plaintext stores it")
    @Test
    void merged() {
        final var id = persisted("erin");
        final var detached = apply(em -> em.find(ConvertedSecretEntity.class, id));
        detached.name = "erin2";
        apply(em -> em.merge(detached));
        assertThat(apply(em -> em.find(ConvertedSecretEntity.class, id)).name).isEqualTo("erin2");
    }

    @DisplayName("a JPQL predicate on the plaintext: measured, not asserted")
    @Test
    void query() {
        persisted("frank");
        try {
            final var found = apply(em -> em.createQuery(
                            "SELECT e FROM ConvertedSecretEntity e WHERE e.name = :name", ConvertedSecretEntity.class)
                    .setParameter("name", "frank").getResultList());
            log.info("JPQL predicate on the converted attribute matched {} row(s)", found.size());
        } catch (final RuntimeException re) {
            log.info("JPQL predicate on the converted attribute failed: {}", re.toString());
        }
    }
}
