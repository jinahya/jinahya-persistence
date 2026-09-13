package com.github.jinahya.persistence.test.util;

import com.github.jinahya.persistence.test.util.__PersistenceProducer.__SpecPU;
import com.github.jinahya.persistence.test.util.spec._Department;
import com.github.jinahya.persistence.test.util.spec._Employee;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that {@link __PersistenceProducer} really produces, from {@code META-INF/persistence.xml}, an entity manager
 * which works, and that {@link __SpecPU} is what names it.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://github.com/weld/weld-testing">weld-testing</a>
 */
@AddBeanClasses(__PersistenceProducer.class)
@EnableAutoWeld
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class __PersistenceProducer_Test {

    /**
     * A new entity manager, per test instance; {@link jakarta.enterprise.context.Dependent Dependent}.
     */
    @Inject
    @__SpecPU
    EntityManager entityManager;

    /**
     * The one factory of the container; {@link jakarta.enterprise.context.ApplicationScoped ApplicationScoped}.
     */
    @Inject
    @__SpecPU
    EntityManagerFactory entityManagerFactory;

    /**
     * For obtaining entity managers by hand, rather than at an injection point.
     */
    @Inject
    @__SpecPU
    Instance<EntityManager> qualified;

    /**
     * The same type, asked for without the qualifier; unsatisfied, and meant to be.
     */
    @Inject
    Instance<EntityManager> unqualified;

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("an entity manager is injected, and it is open")
    @Test
    void produceEntityManager_Open_() {
        assertThat(entityManager).isNotNull();
        assertThat(entityManager.isOpen()).isTrue();
    }

    @DisplayName("a bare EntityManager resolves to nothing; the qualifier removed @Default")
    @Test
    void select_Unsatisfied_Unqualified() {
        assertThat(unqualified.isUnsatisfied())
                .as("a unit must be asked for by name, never picked by accident")
                .isTrue();
        assertThat(qualified.isUnsatisfied()).isFalse();
    }

    @DisplayName("the unit really is the one declared in META-INF/persistence.xml")
    @Test
    void produceEntityManagerFactory_UnitFromTheDescriptor_() {
        assertThat(entityManagerFactory.isOpen()).isTrue();
        assertThat(entityManagerFactory.getMetamodel().getEntities())
                .as("the <class> elements of " + __PersistenceProducer.PERSISTENCE_UNIT_NAME)
                .extracting(t -> t.getJavaType().getName())
                .contains(_Employee.class.getName(), _Department.class.getName());
    }

    @DisplayName("@Dependent: each injection point gets its own entity manager,"
                 + " @ApplicationScoped: all of them share one factory")
    @Test
    void produce_OneFactoryManyEntityManagers_() {
        final var first = qualified.get();
        final var second = qualified.get();
        assertThat(first).isNotSameAs(second).isNotSameAs(entityManager);
        assertThat(first.getEntityManagerFactory()).isSameAs(second.getEntityManagerFactory());
    }

    @DisplayName("a produced entity manager persists, through the three roles of this package")
    @Test
    void produceEntityManager_Usable_Persist() {
        final var transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            final var employee = __PersisterUtils.newPersistedInstanceOf(entityManager, _Employee.class);
            entityManager.flush();
            assertThat(employee.getId()).isNotNull();
            assertThat(employee.getDepartment()).isNotNull();
            transaction.commit();
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }
}
