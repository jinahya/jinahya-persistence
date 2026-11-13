package com.github.jinahya.persistence.encryption;

import com.github.jinahya.persistence.encryption.examples.hibernate.DynamicEntity;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Under Hibernate ORM's {@code @DynamicUpdate}, an unrelated change keeps the stored ciphertext; without it, Hibernate
 * writes every column, and so encrypts the unchanged plaintext again. Compiled and run under the Hibernate ORM profile
 * only.
 */
@SuppressWarnings({"java:S101"})
class HibernateDynamic_Test {

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
                "SELECT name_enc FROM " + DynamicEntity.TABLE_NAME + " WHERE id = " + id).getSingleResult());
    }

    @DisplayName("an unrelated change keeps the stored ciphertext, and the value reads back")
    @Test
    void unrelatedChangeKeepsCiphertext() {
        final var id = PersistenceTests.apply(EMF, em -> {
            final var e = new DynamicEntity();
            e.name = "dynamic";
            em.persist(e);
            return e;
        }).id;
        final var before = ciphertext(id);
        final var encrypted = EntityEncryptionServiceImpl.ENCRYPTED.get();
        PersistenceTests.apply(EMF, em -> em.find(DynamicEntity.class, id).note = "changed");
        assertThat(ciphertext(id)).isEqualTo(before);
        assertThat(EntityEncryptionServiceImpl.ENCRYPTED.get()).isEqualTo(encrypted);
        assertThat(PersistenceTests.apply(EMF, em -> em.find(DynamicEntity.class, id)).name).isEqualTo("dynamic");
    }
}
