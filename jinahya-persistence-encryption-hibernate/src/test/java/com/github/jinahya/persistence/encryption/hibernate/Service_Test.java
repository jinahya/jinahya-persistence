package com.github.jinahya.persistence.encryption.hibernate;

import com.github.jinahya.persistence.encryption.EntityEncryptionService;
import com.github.jinahya.persistence.encryption.converter.EncryptedConverters;
import com.github.jinahya.persistence.encryption.hibernate.examples.SecretEntity;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Where the service comes from: the persistence unit property, as an instance or a class name, and otherwise the one
 * {@code META-INF/services/} names.
 */
@SuppressWarnings({"java:S101"})
class Service_Test {

    static class CountingService implements EntityEncryptionService {

        final AtomicInteger calls = new AtomicInteger();

        @Override
        public byte[] encrypt(final byte[] decryptedBytes) {
            calls.incrementAndGet();
            return EntityEncryptionServiceImpl.encryptBytes(decryptedBytes);
        }

        @Override
        public byte[] decrypt(final byte[] encryptedBytes) {
            calls.incrementAndGet();
            return EntityEncryptionServiceImpl.decryptBytes(encryptedBytes);
        }
    }

    public static class NamedService extends CountingService {

        static final AtomicInteger INSTANCES = new AtomicInteger();

        public NamedService() {
            INSTANCES.incrementAndGet();
        }
    }

    @DisplayName("an instance in the properties is the service used, and the loaded one is not")
    @Test
    void instance() {
        final var service = new CountingService();
        final var loaded = EntityEncryptionServiceImpl.ENCRYPTED.get() + EntityEncryptionServiceImpl.DECRYPTED.get();
        try (var emf = Persistence.createEntityManagerFactory(
                "__encryptionHibernatePU", Map.of(EncryptedConverters.PROPERTY_SERVICE, service))) {
            final var id = PersistenceTests.apply(emf, em -> {
                final var e = new SecretEntity();
                e.name = "instance";
                em.persist(e);
                return e;
            }).id;
            assertThat(PersistenceTests.apply(emf, em -> em.find(SecretEntity.class, id)).name).isEqualTo("instance");
        }
        assertThat(service.calls.get()).isEqualTo(2);
        assertThat(EntityEncryptionServiceImpl.ENCRYPTED.get() + EntityEncryptionServiceImpl.DECRYPTED.get())
                .isEqualTo(loaded);
    }

    @DisplayName("a class name in the properties is instantiated")
    @Test
    void className() {
        final var instances = NamedService.INSTANCES.get();
        try (var emf = Persistence.createEntityManagerFactory(
                "__encryptionHibernatePU",
                Map.of(EncryptedConverters.PROPERTY_SERVICE, NamedService.class.getName()))) {
            final var id = PersistenceTests.apply(emf, em -> {
                final var e = new SecretEntity();
                e.name = "named";
                em.persist(e);
                return e;
            }).id;
            assertThat(PersistenceTests.apply(emf, em -> em.find(SecretEntity.class, id)).name).isEqualTo("named");
        }
        assertThat(NamedService.INSTANCES.get()).isGreaterThan(instances);
    }

    @DisplayName("a property which names no service fails the factory, not its first write")
    @Test
    void invalid() {
        assertThatThrownBy(() -> Persistence.createEntityManagerFactory(
                "__encryptionHibernatePU", Map.of(EncryptedConverters.PROPERTY_SERVICE, "no.such.Service")).close())
                .hasStackTraceContaining("no.such.Service");
    }
}
