package com.github.jinahya.persistence.encryption;

import com.github.jinahya.persistence.encryption.examples.SecretEntity;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Measures a converted attribute through Hibernate ORM's {@code StatelessSession}, which has no persistence context
 * and no dirty checking: the converter runs on insert, on get and on update all the same.
 * <p>
 * Compiled, and run, under the {@code jakarta-ee-11-hibernate-orm} profile only.
 */
@SuppressWarnings({"java:S101"})
class HibernateStateless_Test {

    private static EntityManagerFactory EMF;

    @BeforeAll
    static void open() {
        EMF = Persistence.createEntityManagerFactory("__encryptionHibernatePU");
    }

    @AfterAll
    static void close() {
        if (EMF != null) {
            EMF.close();
        }
    }

    private static byte[] ciphertext(final long id) {
        return PersistenceTests.apply(EMF, em -> (byte[]) em.createNativeQuery(
                "SELECT name_enc FROM " + SecretEntity.TABLE_NAME + " WHERE id = " + id).getSingleResult());
    }

    @DisplayName("a StatelessSession stores the ciphertext, reads it decrypted, and updates")
    @Test
    void stateless() {
        final var factory = EMF.unwrap(SessionFactory.class);
        final var e = new SecretEntity();
        e.name = "stateless";
        try (var session = factory.openStatelessSession()) {
            final var tx = session.beginTransaction();
            session.insert(e);
            tx.commit();
        }
        assertThat(new String(ciphertext(e.id))).doesNotContain("stateless");
        try (var session = factory.openStatelessSession()) {
            final var got = session.get(SecretEntity.class, e.id);
            assertThat(got.name).isEqualTo("stateless");
            got.name = "stateless2";
            final var tx = session.beginTransaction();
            session.update(got);
            tx.commit();
        }
        assertThat(PersistenceTests.apply(EMF, em -> em.find(SecretEntity.class, e.id)).name)
                .isEqualTo("stateless2");
    }
}
