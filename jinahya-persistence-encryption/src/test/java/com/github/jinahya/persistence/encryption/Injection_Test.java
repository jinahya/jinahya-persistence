package com.github.jinahya.persistence.encryption;

import com.github.jinahya.persistence.encryption.examples.SecretEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Hands a CDI container's {@code BeanManager} to the persistence provider, as {@code jakarta.persistence.bean.manager},
 * and measures whether the provider then creates the converters through it, so that the service bean is injected; when
 * it does not, the {@link java.util.ServiceLoader} fallback serves.
 */
@SuppressWarnings({"java:S101"})
class Injection_Test {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * The service as a bean: counts its calls, so that it can be told from the one {@code ServiceLoader} names.
     */
    @ApplicationScoped
    static class Service implements EntityEncryptionService {

        static final AtomicInteger CALLS = new AtomicInteger();

        @Override
        public byte[] encrypt(final byte[] decryptedBytes) {
            CALLS.incrementAndGet();
            return EntityEncryptionServiceImpl.encryptBytes(decryptedBytes);
        }

        @Override
        public byte[] decrypt(final byte[] encryptedBytes) {
            CALLS.incrementAndGet();
            return EntityEncryptionServiceImpl.decryptBytes(encryptedBytes);
        }
    }

    private static WeldContainer CONTAINER;

    private static EntityManagerFactory EMF;

    @BeforeAll
    static void open() {
        CONTAINER = new Weld().disableDiscovery().addBeanClasses(Service.class).initialize();
        EMF = Persistence.createEntityManagerFactory(
                "__encryptionPU", Map.of("jakarta.persistence.bean.manager", CONTAINER.getBeanManager()));
    }

    @AfterAll
    static void close() {
        if (EMF != null) {
            EMF.close();
        }
        if (CONTAINER != null) {
            CONTAINER.shutdown();
        }
    }

    @DisplayName("the service is found, injected or loaded, and the value round-trips")
    @Test
    void __() {
        final var loaded = EntityEncryptionServiceImpl.ENCRYPTED.get() + EntityEncryptionServiceImpl.DECRYPTED.get();
        final var injected = Service.CALLS.get();
        final var id = PersistenceTests.apply(EMF, em -> {
            final var e = new SecretEntity();
            e.name = "injected";
            em.persist(e);
            return e;
        }).id;
        assertThat(PersistenceTests.apply(EMF, em -> em.find(SecretEntity.class, id)).name).isEqualTo("injected");
        final var byInjection = Service.CALLS.get() - injected;
        final var byLoader = EntityEncryptionServiceImpl.ENCRYPTED.get() + EntityEncryptionServiceImpl.DECRYPTED.get()
                             - loaded;
        log.info("service calls: {} through the injected bean, {} through the ServiceLoader one",
                 byInjection, byLoader);
        assertThat(byInjection + byLoader).isEqualTo(2);
    }
}
