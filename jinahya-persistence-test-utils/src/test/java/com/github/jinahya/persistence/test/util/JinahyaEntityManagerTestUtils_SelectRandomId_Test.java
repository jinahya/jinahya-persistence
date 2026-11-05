package com.github.jinahya.persistence.test.util;

import com.github.jinahya.persistence.test.util.randomid._Basic;
import com.github.jinahya.persistence.test.util.randomid._Derived;
import com.github.jinahya.persistence.test.util.randomid._DerivedId;
import com.github.jinahya.persistence.test.util.randomid._Embeddable;
import com.github.jinahya.persistence.test.util.randomid._Embedded;
import com.github.jinahya.persistence.test.util.randomid._IdClass;
import com.github.jinahya.persistence.test.util.randomid._IdClassId;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests {@link JinahyaEntityManagerTestUtils#selectRandomEntityId(EntityManager, Class)} against each shape of
 * identifier, and {@link JinahyaEntityManagerTestUtils#selectRandomEntityReference(EntityManager, Class)}. Each test
 * runs in a transaction which is rolled back, so every table starts empty.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@EnableAutoWeld
@AddBeanClasses(__PersistenceProducer.class)
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S3577" // Test classes should comply with a naming convention
})
class JinahyaEntityManagerTestUtils_SelectRandomId_Test {

    @DisplayName("@Id of a primitive type")
    @Test
    void __id() {
        acceptEntityManagerInTransactionAndRollback(em -> {
            assertThat(JinahyaEntityManagerTestUtils.selectRandomEntityId(em, _Basic.class)).isEmpty();
            final var ids = List.of(1L, 2L, 3L);
            ids.forEach(id -> em.persist(_Basic.of(id)));
            em.flush();
            final Optional<Long> selected = JinahyaEntityManagerTestUtils.selectRandomEntityId(em, _Basic.class);
            assertThat(selected).hasValueSatisfying(id -> assertThat(id).isIn(ids));
        });
    }

    @DisplayName("@EmbeddedId")
    @Test
    void __embeddedId() {
        acceptEntityManagerInTransactionAndRollback(em -> {
            assertThat(JinahyaEntityManagerTestUtils.selectRandomEntityId(em, _Embedded.class))
                    .isEmpty();
            final var ids = List.of(_Embeddable.of(1, "a"), _Embeddable.of(1, "b"), _Embeddable.of(2, "a"));
            ids.forEach(id -> em.persist(_Embedded.of(id)));
            em.flush();
            final Optional<_Embeddable> selected
                    = JinahyaEntityManagerTestUtils.selectRandomEntityId(em, _Embedded.class);
            assertThat(selected).hasValueSatisfying(id -> assertThat(id).isIn(ids));
        });
    }

    @DisplayName("@IdClass")
    @Test
    void __idClass() {
        acceptEntityManagerInTransactionAndRollback(em -> {
            assertThat(JinahyaEntityManagerTestUtils.selectRandomEntityId(em, _IdClass.class))
                    .isEmpty();
            final var ids = List.of(_IdClassId.of(1, "a"), _IdClassId.of(1, "b"), _IdClassId.of(2, "a"));
            em.persist(_IdClass.of(1, "a"));
            em.persist(_IdClass.of(1, "b"));
            em.persist(_IdClass.of(2, "a"));
            em.flush();
            final Optional<_IdClassId> selected
                    = JinahyaEntityManagerTestUtils.selectRandomEntityId(em, _IdClass.class);
            assertThat(selected).hasValueSatisfying(id -> assertThat(id).isIn(ids));
        });
    }

    @DisplayName("@IdClass with an association; a derived identity")
    @Test
    void __derivedIdentity() {
        acceptEntityManagerInTransactionAndRollback(em -> {
            assertThat(JinahyaEntityManagerTestUtils.selectRandomEntityId(em, _Derived.class))
                    .isEmpty();
            final var basic1 = _Basic.of(1L);
            final var basic2 = _Basic.of(2L);
            em.persist(basic1);
            em.persist(basic2);
            em.persist(_Derived.of(basic1, 1));
            em.persist(_Derived.of(basic1, 2));
            em.persist(_Derived.of(basic2, 1));
            em.flush();
            final var ids = List.of(_DerivedId.of(1L, 1), _DerivedId.of(1L, 2), _DerivedId.of(2L, 1));
            final Optional<_DerivedId> selected
                    = JinahyaEntityManagerTestUtils.selectRandomEntityId(em, _Derived.class);
            assertThat(selected).hasValueSatisfying(id -> assertThat(id).isIn(ids));
        });
    }

    @DisplayName("selectRandomEntityReference(entityManager, entityClass)")
    @Test
    void __selectRandomEntityReference() {
        acceptEntityManagerInTransactionAndRollback(em -> {
            assertThat(JinahyaEntityManagerTestUtils.selectRandomEntityReference(em, _Basic.class)).isEmpty();
            assertThat(JinahyaEntityManagerTestUtils.selectRandomEntityReference(em, _IdClass.class)).isEmpty();
            final var basic1 = _Basic.of(1L);
            final var basic2 = _Basic.of(2L);
            em.persist(basic1);
            em.persist(basic2);
            em.persist(_IdClass.of(1, "a"));
            em.persist(_IdClass.of(2, "b"));
            em.flush();
            em.clear();
            final var util = em.getEntityManagerFactory().getPersistenceUnitUtil();
            assertThat(JinahyaEntityManagerTestUtils.selectRandomEntityReference(em, _Basic.class))
                    .hasValueSatisfying(r -> {
                        assertThat(r).isInstanceOf(_Basic.class);
                        assertThat(util.getIdentifier(r)).isIn(1L, 2L);
                    });
            assertThat(JinahyaEntityManagerTestUtils.selectRandomEntityReference(em, _IdClass.class))
                    .hasValueSatisfying(r -> {
                        assertThat(r).isInstanceOf(_IdClass.class);
                        assertThat(util.getIdentifier(r)).isIn(_IdClassId.of(1, "a"), _IdClassId.of(2, "b"));
                    });
        });
    }

    @DisplayName("selectRandomEntityId(entityManager, entityClass, idClass)")
    @Test
    void __selectRandomEntityId_idClass() {
        acceptEntityManagerInTransactionAndRollback(em -> {
            assertThat(JinahyaEntityManagerTestUtils.selectRandomEntityId(em, _IdClass.class, _IdClassId.class))
                    .isEmpty();
            final var basic = _Basic.of(1L);
            em.persist(basic);
            em.persist(_Embedded.of(_Embeddable.of(1, "a")));
            em.persist(_IdClass.of(1, "a"));
            em.persist(_Derived.of(basic, 1));
            em.flush();
            assertThat(JinahyaEntityManagerTestUtils.selectRandomEntityId(em, _Basic.class, Long.class))
                    .hasValue(1L);
            assertThat(JinahyaEntityManagerTestUtils.selectRandomEntityId(em, _Basic.class, Number.class))
                    .hasValue(1L);
            assertThat(JinahyaEntityManagerTestUtils.selectRandomEntityId(em, _Embedded.class, _Embeddable.class))
                    .hasValue(_Embeddable.of(1, "a"));
            assertThat(JinahyaEntityManagerTestUtils.selectRandomEntityId(em, _IdClass.class, _IdClassId.class))
                    .hasValue(_IdClassId.of(1, "a"));
            assertThat(JinahyaEntityManagerTestUtils.selectRandomEntityId(em, _Derived.class, _DerivedId.class))
                    .hasValue(_DerivedId.of(1L, 1));
            assertThatThrownBy(
                    () -> JinahyaEntityManagerTestUtils.selectRandomEntityId(em, _Basic.class, String.class))
                    .isInstanceOf(ClassCastException.class);
            assertThatThrownBy(
                    () -> JinahyaEntityManagerTestUtils.selectRandomEntityId(em, _IdClass.class, _DerivedId.class))
                    .isInstanceOf(ClassCastException.class);
        });
    }

    // -----------------------------------------------------------------------------------------------------------------
    private void acceptEntityManagerInTransactionAndRollback(final Consumer<? super EntityManager> consumer) {
        final var transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            consumer.accept(entityManager);
        } finally {
            transaction.rollback();
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Inject
    @__PersistenceProducer.__SpecPU
    private EntityManager entityManager;
}
