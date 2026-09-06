package com.github.jinahya.persistence.more;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that a {@link __StringAttributeConverter} nested converter can be registered on a real entity and
 * instantiated by the persistence provider itself.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class __StringAttributeConverter_PersistenceTest {

    private static EntityManagerFactory ENTITY_MANAGER_FACTORY;

    @BeforeAll
    static void openEntityManagerFactory() {
        ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("__colorPU");
    }

    @AfterAll
    static void closeEntityManagerFactory() {
        if (ENTITY_MANAGER_FACTORY != null) {
            ENTITY_MANAGER_FACTORY.close();
        }
    }

    @DisplayName("the provider instantiates the converter and round-trips through it")
    @Test
    void __roundTripThroughTheProvider() {
        final var amount = new BigDecimal("1234.5600");
        final Long id;
        try (var em = ENTITY_MANAGER_FACTORY.createEntityManager()) {
            final var tx = em.getTransaction();
            tx.begin();
            final var entity = new _ConvertedEntity();
            entity.amount = amount;
            em.persist(entity);
            tx.commit();
            id = entity.id;
        }
        assertThat(id).isNotNull();

        try (var em = ENTITY_MANAGER_FACTORY.createEntityManager()) {
            final var found = em.find(_ConvertedEntity.class, id);
            assertThat(found).isNotNull();
            // toPlainString keeps the scale for a non-negative scale, so this comparison is by value AND scale
            assertThat(found.amount).isEqualByComparingTo(amount);
        }
    }

    @DisplayName("the stored column holds the plain string the converter writes")
    @Test
    void __storedFormIsThePlainString() {
        final Long id;
        try (var em = ENTITY_MANAGER_FACTORY.createEntityManager()) {
            final var tx = em.getTransaction();
            tx.begin();
            final var entity = new _ConvertedEntity();
            entity.amount = new BigDecimal("1E+3");
            em.persist(entity);
            tx.commit();
            id = entity.id;
        }
        try (var em = ENTITY_MANAGER_FACTORY.createEntityManager()) {
            final var stored = em.createNativeQuery("SELECT amount FROM converted_entity WHERE id = " + id)
                    .getSingleResult();
            // free of an exponent, which is the documented reason toPlainString() is used
            assertThat(String.valueOf(stored)).isEqualTo("1000");
        }
    }
}
