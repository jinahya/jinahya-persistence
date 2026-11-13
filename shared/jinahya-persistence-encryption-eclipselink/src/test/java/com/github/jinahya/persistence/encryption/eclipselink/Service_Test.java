package com.github.jinahya.persistence.encryption.eclipselink;

import com.github.jinahya.persistence.encryption.EntityEncryptionService;
import com.github.jinahya.persistence.encryption.converter.EncryptedConverters;
import com.github.jinahya.persistence.encryption.eclipselink.examples.SecretEntity;
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
                "__encryptionEclipseLinkPU", Map.of(EncryptedConverters.PROPERTY_SERVICE, service))) {
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

    @DisplayName("a class name in the properties is instantiated once, and shared by every attribute and factory")
    @Test
    void className() {
        assertThat(NamedService.INSTANCES.get()).as("none yet").isZero();
        try (var emf = Persistence.createEntityManagerFactory(
                "__encryptionEclipseLinkPU",
                Map.of(EncryptedConverters.PROPERTY_SERVICE, NamedService.class.getName()))) {
            final var id = PersistenceTests.apply(emf, em -> {
                final var e = new SecretEntity();
                e.name = "named";
                em.persist(e);
                return e;
            }).id;
            assertThat(PersistenceTests.apply(emf, em -> em.find(SecretEntity.class, id)).name).isEqualTo("named");
        }
        assertThat(NamedService.INSTANCES.get()).as("one instance for the whole factory").isEqualTo(1);
        // a second factory naming the same class shares the instance
        try (var emf = Persistence.createEntityManagerFactory(
                "__encryptionEclipseLinkPU",
                Map.of(EncryptedConverters.PROPERTY_SERVICE, NamedService.class.getName()))) {
            final var id = PersistenceTests.apply(emf, em -> {
                final var e = new SecretEntity();
                e.name = "named2";
                em.persist(e);
                return e;
            }).id;
            assertThat(PersistenceTests.apply(emf, em -> em.find(SecretEntity.class, id)).name).isEqualTo("named2");
        }
        assertThat(NamedService.INSTANCES.get()).as("still one").isEqualTo(1);
    }

    @DisplayName("a property which names no service fails on the first use of the unit, before any write")
    @Test
    void invalid() {
        // EclipseLink initializes its mappings when the unit is first used, not when the factory is created
        assertThatThrownBy(() -> {
            try (var emf = Persistence.createEntityManagerFactory(
                    "__encryptionEclipseLinkPU", Map.of(EncryptedConverters.PROPERTY_SERVICE, "no.such.Service"))) {
                PersistenceTests.apply(emf, em -> em.find(SecretEntity.class, 1L));
            }
        }).hasStackTraceContaining("no.such.Service");
    }
}
