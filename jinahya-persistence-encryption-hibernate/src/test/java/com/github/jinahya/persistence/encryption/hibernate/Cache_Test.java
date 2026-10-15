package com.github.jinahya.persistence.encryption.hibernate;

import com.github.jinahya.persistence.encryption.hibernate.examples.CachedEntity;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The second-level cache holds the ciphertext: a cache hit decrypts, and the entity read from the cache has the value.
 */
@SuppressWarnings({"java:S101"})
class Cache_Test {

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

    @DisplayName("a cache hit decrypts what the cache holds, and never reads the row")
    @Test
    void cacheHitDecrypts() {
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
        assertThat(EntityEncryptionServiceImpl.DECRYPTED.get()).as("the cached form was decrypted")
                .isGreaterThan(decrypted);
    }
}
