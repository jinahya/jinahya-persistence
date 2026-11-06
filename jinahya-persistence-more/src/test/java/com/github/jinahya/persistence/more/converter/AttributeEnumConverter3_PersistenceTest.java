package com.github.jinahya.persistence.more.converter;

import com.github.jinahya.persistence.shared.__PersistenceProviders;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/**
 * Verifies that {@link AttributeEnumConverter3} converts, through a real persistence provider, an attribute of an
 * enum, and another of a shared interface which more than one enum implements.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class AttributeEnumConverter3_PersistenceTest {

    private static EntityManagerFactory ENTITY_MANAGER_FACTORY;

    @BeforeAll
    static void openEntityManagerFactory() {
        // EclipseLink takes a converter's column type from the last type argument of the converter's own superclass
        // signature, rather than from AttributeConverter<X, Y>; that is the enum for OfString<_Size>, and a
        // wildcard, which it tries to load as a class named "*", for OfString<_ISize<?>>
        assumeFalse(__PersistenceProviders.isEclipseLink(), "EclipseLink misreads the converters' generic types");
        ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("__attributeEnumConverterPU");
    }

    @AfterAll
    static void closeEntityManagerFactory() {
        if (ENTITY_MANAGER_FACTORY != null) {
            ENTITY_MANAGER_FACTORY.close();
        }
    }

    private static Long persist(final _Size size, final _ISize<?> anySize) {
        try (var em = ENTITY_MANAGER_FACTORY.createEntityManager()) {
            final var tx = em.getTransaction();
            tx.begin();
            final var entity = new _SizedEntity();
            entity.size = size;
            entity.anySize = anySize;
            em.persist(entity);
            tx.commit();
            return entity.id;
        }
    }

    private static Object[] selectStored(final Long id) {
        try (var em = ENTITY_MANAGER_FACTORY.createEntityManager()) {
            return (Object[]) em.createNativeQuery("SELECT shirt_size, any_size FROM sized_entity WHERE id = " + id)
                    .getSingleResult();
        }
    }

    private static _SizedEntity find(final Long id) {
        try (var em = ENTITY_MANAGER_FACTORY.createEntityManager()) {
            return em.find(_SizedEntity.class, id);
        }
    }

    @DisplayName("each constant, of either enum class, is stored as its attribute value and read back as itself")
    @Test
    void _RoundTrip_Each() {
        for (final var anySize : new _ISize<?>[] {_Size.SMALL, _Size.MEDIUM, _Size.LARGE, _ExtendedSize.EXTRA_LARGE}) {
            final var id = persist(_Size.MEDIUM, anySize);
            assertThat(selectStored(id)).containsExactly("Medium", anySize.attributeValue());
            final var found = find(id);
            assertThat(found.size).isSameAs(_Size.MEDIUM);
            assertThat(found.anySize).isSameAs(anySize);
        }
    }

    @DisplayName("null is stored as null and read back as null")
    @Test
    void _Null_Null() {
        final var id = persist(null, null);
        assertThat(selectStored(id)).containsExactly(null, null);
        final var found = find(id);
        assertThat(found.size).isNull();
        assertThat(found.anySize).isNull();
    }

    @DisplayName("a query parameter of the shared interface is converted, too")
    @Test
    void _Query_Parameter() {
        final var id = persist(_Size.SMALL, _ExtendedSize.EXTRA_LARGE);
        try (var em = ENTITY_MANAGER_FACTORY.createEntityManager()) {
            final var ids = em.createQuery(
                            "SELECT e.id FROM _SizedEntity e WHERE e.anySize = :anySize", Long.class)
                    .setParameter("anySize", _ExtendedSize.EXTRA_LARGE)
                    .getResultList();
            assertThat(ids).contains(id);
            final var others = em.createQuery(
                            "SELECT e.anySize FROM _SizedEntity e WHERE e.id = :id", Object.class)
                    .setParameter("id", id)
                    .getResultList();
            assertThat(others).containsExactly(_ExtendedSize.EXTRA_LARGE);
        }
    }
}
