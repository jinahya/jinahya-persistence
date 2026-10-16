package com.github.jinahya.persistence.encryption;

import com.github.jinahya.persistence.encryption.examples.hibernate.CachedEntity;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Measures what Hibernate ORM's second-level cache holds for a converted attribute: the plaintext. A cache hit
 * issues no statement, and decrypts nothing, because the cached form is the domain value.
 * <p>
 * This is the counterpart of the {@code -hibernate} module's cache test, which measures the opposite for its own
 * mapping: the ciphertext is cached, and a hit decrypts it. Compiled, and run, under the
 * {@code jakarta-ee-11-hibernate-orm} profile only.
 */
@SuppressWarnings({"java:S101"})
class HibernateCache_Test {

    private static EntityManagerFactory EMF;

    @BeforeAll
    static void open() {
        EMF = Persistence.createEntityManagerFactory("__encryptionHibernateCachedPU");
    }

    @AfterAll
    static void close() {
        if (EMF != null) {
            EMF.close();
        }
    }

    @DisplayName("a cache hit reads the row never, and decrypts nothing: the cache holds the plaintext")
    @Test
    void cacheHitDecryptsNothing() {
        final var factory = EMF.unwrap(SessionFactory.class);
        final var statistics = factory.getStatistics();
        statistics.clear();
        final var id = PersistenceTests.apply(EMF, em -> {
            final var e = new CachedEntity();
            e.name = "cached";
            em.persist(e);
            return e;
        }).id;
        // warm the cache from a load, in case the insert did not populate it
        PersistenceTests.apply(EMF, em -> em.find(CachedEntity.class, id));
        assertThat(factory.getCache().containsEntity(CachedEntity.class, id)).isTrue();
        final var hits = statistics.getSecondLevelCacheHitCount();
        final var statements = statistics.getPrepareStatementCount();
        final var decrypted = EntityEncryptionServiceImpl.DECRYPTED.get();
        final var read = PersistenceTests.apply(EMF, em -> em.find(CachedEntity.class, id));
        assertThat(read.name).isEqualTo("cached");
        assertThat(statistics.getSecondLevelCacheHitCount()).as("a cache hit").isGreaterThan(hits);
        assertThat(statistics.getPrepareStatementCount()).as("no statement").isEqualTo(statements);
        assertThat(EntityEncryptionServiceImpl.DECRYPTED.get()).as("nothing decrypted: the plaintext was cached")
                .isEqualTo(decrypted);
    }
}
