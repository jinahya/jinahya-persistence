package com.github.jinahya.persistence.crypto;

import com.github.jinahya.persistence.crypto.examples.DirectListenerEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Verifies that {@link EntityEncryptionListener}, registered directly, encrypts and decrypts with no subclass, taking
 * its service from CDI injection (#5). A Weld SE container's {@code BeanManager} is handed to the persistence provider
 * as {@code jakarta.persistence.bean.manager}, so that the provider creates the listener through CDI.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class DirectListener_Test {

    /**
     * Produces the entity manager factory, as an application would.
     */
    static class FactoryProducer {

        @Produces
        EntityManagerFactory produceEntityManagerFactory() {
            return ENTITY_MANAGER_FACTORY;
        }
    }

    /**
     * The encryption service, as an application would write it: a bean which resolves its entity manager factory
     * lazily, through a {@link Provider}, because the listener it is injected into is created while that factory is
     * still being built.
     */
    @ApplicationScoped
    static class Service implements EntityEncryptionService {

        @Override
        public EntityManagerFactory getEntityManagerFactory() {
            return entityManagerFactory.get();
        }

        @Override
        public String getEncryptionIdentifier(final Object entityInstance) {
            return cryptography.getEncryptionIdentifier(entityInstance);
        }

        @Override
        public byte[] encrypt(final String encryptionIdentifier, final byte[] decryptedBytes) {
            return cryptography.encrypt(encryptionIdentifier, decryptedBytes);
        }

        @Override
        public byte[] decrypt(final String encryptionIdentifier, final byte[] encryptedBytes) {
            return cryptography.decrypt(encryptionIdentifier, encryptedBytes);
        }

        @Inject
        Provider<EntityManagerFactory> entityManagerFactory;

        private final EntityEncryptionService cryptography = new EntityEncryptionServiceImpl();
    }

    private static WeldContainer CONTAINER;

    private static EntityManagerFactory ENTITY_MANAGER_FACTORY;

    @BeforeAll
    static void setUp() {
        CONTAINER = new Weld()
                .disableDiscovery()
                .addBeanClasses(FactoryProducer.class, Service.class)
                .initialize();
        ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory(
                "__cryptoPU", Map.of("jakarta.persistence.bean.manager", CONTAINER.getBeanManager()));
    }

    @AfterAll
    static void tearDown() {
        if (ENTITY_MANAGER_FACTORY != null) {
            ENTITY_MANAGER_FACTORY.close();
        }
        if (CONTAINER != null) {
            CONTAINER.shutdown();
        }
    }

    private static <R> R apply(final Function<? super EntityManager, ? extends R> function) {
        try (var entityManager = ENTITY_MANAGER_FACTORY.createEntityManager()) {
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

    @DisplayName("registered directly, with no subclass, the listener encrypts before writing and decrypts after reading")
    @Test
    void __safeByDefault() {
        final long id = apply(em -> {
            final var entity = new DirectListenerEntity();
            entity.name = "direct";
            em.persist(entity);
            return entity;
        }).id;

        final var row = apply(em -> (Object[]) em.createNativeQuery(
                        "SELECT name, name_enc FROM " + DirectListenerEntity.TABLE_NAME + " WHERE id = " + id)
                .getSingleResult());
        assertThat(row[0]).as("no plaintext reached the row").isNull();
        assertThat(row[1]).as("the ciphertext did").isNotNull();

        final var read = apply(em -> em.find(DirectListenerEntity.class, id).name);
        assertThat(read).as("and it reads back decrypted").isEqualTo("direct");
    }

    @DisplayName("a listener which was not injected fails, rather than looking a service up")
    @Test
    void __notInjected() {
        assertThatThrownBy(() -> new EntityEncryptionListener().getEncryptionService())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("jakarta.persistence.bean.manager");
    }
}
