package com.github.jinahya.persistence.test.util.spec;

import com.github.jinahya.persistence.test.util.__PersistenceProducer;
import com.github.jinahya.persistence.test.util.__PersisterUtils;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.Getter;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

@EnableAutoWeld
@AddBeanClasses(__PersistenceProducer.class)
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
abstract class __Spec_PersistenceTest<T> {

    __Spec_PersistenceTest(final Class<T> entityClass) {
        super();
        this.entityClass = Objects.requireNonNull(entityClass, "entityClass is null");
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Persists a new instance of the {@link #entityClass}, through the whole chain, and checks that the provider
     * assigned it an identifier.
     * <p>
     * This is the one test every entity in this package gets for free, and it is the one a downstream project actually
     * runs: it names no attribute, and asserts nothing about the entity beyond its being writable. An instantiator, a
     * randomizer and a persister located by the convention, and a row in the database -- if that chain holds for an
     * entity, this library did its job for it.
     *
     * @implNote The identifier is read through
     *         {@link jakarta.persistence.PersistenceUnitUtil#getIdentifier(Object)} rather than through a getter, so
     *         that this works for any entity, whatever it calls its identifier and wherever it declares it.
     */
    @DisplayName("newPersistedInstanceOf(entityManager, entityClass) -> persisted, and given an identifier")
    @Test
    void __persist() {
        final var instance = applyEntityManagerInTransaction(em -> {
            final var persisted = __PersisterUtils.newPersistedInstanceOf(em, entityClass);
            // flush, rather than wait for the commit: the insert has to happen while this assertion can still see it
            em.flush();
            return persisted;
        });
        assertThat(instance).isNotNull().isInstanceOf(entityClass);
        assertThat(getEntityManager().getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(instance))
                .as("the provider generated an identifier on insert")
                .isNotNull();
    }

    // --------------------------------------------------------------------------------------------------- entityManager
    <R> R applyEntityManager(final Function<? super EntityManager, ? extends R> function) {
        return function.apply(entityManager);
    }

    <R> R applyEntityManagerInTransaction(final Function<? super EntityManager, ? extends R> function) {
        return applyEntityManager(em -> {
            final var transaction = em.getTransaction();
            transaction.begin();
            try {
                final var result = function.apply(em);
                transaction.commit();
                return result;
            } catch (final Exception e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                throw e;
            }
        });
    }

    // -----------------------------------------------------------------------------------------------------------------
    final Class<T> entityClass;

    @Inject
    @__PersistenceProducer.__SpecPU
    @Getter(AccessLevel.PACKAGE)
    private EntityManager entityManager;
}
