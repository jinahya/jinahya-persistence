package com.github.jinahya.persistence.crypto;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
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
class __DirectListener_Test {

    /**
     * Produces the service. Application-scoped, so that the listener is injected a client proxy while the factory the
     * service needs is still being built; the service itself is created on the first callback.
     */
    static class ServiceProducer {

        @Produces
        @ApplicationScoped
        AbstractEntityEncryptionService produceService() {
            return new _EntityEncryptionService(ENTITY_MANAGER_FACTORY, new _EntityEncryptionManager());
        }
    }

    private static WeldContainer CONTAINER;

    private static EntityManagerFactory ENTITY_MANAGER_FACTORY;

    @BeforeAll
    static void setUp() {
        CONTAINER = new Weld()
                .disableDiscovery()
                .addBeanClasses(ServiceProducer.class)
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
            final var entity = new _DirectListenerEntity();
            entity.name = "direct";
            em.persist(entity);
            return entity;
        }).id;

        final var row = apply(em -> (Object[]) em.createNativeQuery(
                        "SELECT name, name_enc FROM " + _DirectListenerEntity.TABLE_NAME + " WHERE id = " + id)
                .getSingleResult());
        assertThat(row[0]).as("no plaintext reached the row").isNull();
        assertThat(row[1]).as("the ciphertext did").isNotNull();

        final var read = apply(em -> em.find(_DirectListenerEntity.class, id).name);
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
