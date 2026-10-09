package com.github.jinahya.persistence.crypto;

import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.enterprise.util.TypeLiteral;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that {@link EntityEncryptionListener}, registered directly, encrypts and decrypts with no subclass, taking
 * its service from CDI (#5). A stub {@link CDI} stands in for a container.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class __DirectListener_Test {

    /**
     * A container which knows exactly one bean: the service.
     */
    private static final class StubCDI extends CDI<Object> {

        @Override
        public BeanManager getBeanManager() {
            throw new IllegalStateException("no bean manager in this stub");
        }

        @Override
        @SuppressWarnings({"unchecked"})
        public <U> Instance<U> select(final Class<U> subtype, final Annotation... qualifiers) {
            assertThat(subtype).isSameAs(AbstractEntityEncryptionService.class);
            return new Single<>((U) SERVICE);
        }

        @Override
        public Instance<Object> select(final Annotation... qualifiers) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <U> Instance<U> select(final TypeLiteral<U> subtype, final Annotation... qualifiers) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isUnsatisfied() {
            return false;
        }

        @Override
        public boolean isAmbiguous() {
            return false;
        }

        @Override
        public void destroy(final Object instance) {
        }

        @Override
        public Handle<Object> getHandle() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Iterable<? extends Handle<Object>> handles() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Iterator<Object> iterator() {
            return List.<Object>of(SERVICE).iterator();
        }

        @Override
        public Object get() {
            throw new UnsupportedOperationException();
        }
    }

    private record Single<U>(U value) implements Instance<U> {

        @Override
        public U get() {
            return value;
        }

        @Override
        public Instance<U> select(final Annotation... qualifiers) {
            return this;
        }

        @Override
        public <V extends U> Instance<V> select(final Class<V> subtype, final Annotation... qualifiers) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <V extends U> Instance<V> select(final TypeLiteral<V> subtype, final Annotation... qualifiers) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isUnsatisfied() {
            return false;
        }

        @Override
        public boolean isAmbiguous() {
            return false;
        }

        @Override
        public void destroy(final U instance) {
        }

        @Override
        public Handle<U> getHandle() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Iterable<? extends Handle<U>> handles() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Iterator<U> iterator() {
            return List.of(value).iterator();
        }
    }

    private static EntityManagerFactory ENTITY_MANAGER_FACTORY;

    private static volatile AbstractEntityEncryptionService SERVICE;

    @BeforeAll
    static void setUp() {
        ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("__cryptoPU");
        SERVICE = new EntityEncryptionService(ENTITY_MANAGER_FACTORY, new _Entity_EncryptionManager());
        final var cdi = new StubCDI();
        CDI.setCDIProvider(() -> cdi);
    }

    @AfterAll
    static void tearDown() {
        if (ENTITY_MANAGER_FACTORY != null) {
            ENTITY_MANAGER_FACTORY.close();
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
}
