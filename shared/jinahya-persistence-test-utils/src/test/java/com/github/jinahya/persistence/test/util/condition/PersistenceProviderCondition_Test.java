package com.github.jinahya.persistence.test.util.condition;

import com.github.jinahya.persistence.test.util.JinahyaPersistenceProviderTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the persistence provider conditions; each test asserts what its own condition implies, so a condition
 * which let it run when it should not have fails it.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class PersistenceProviderCondition_Test {

    @DisabledOnEclipseLink(disabledReason = "verifying @DisabledOnEclipseLink")
    @DisplayName("@DisabledOnEclipseLink -> runs only without EclipseLink")
    @Test
    void disabledOnEclipseLink__() {
        assertThat(JinahyaPersistenceProviderTestUtils.isEclipseLink()).isFalse();
    }

    @EnabledOnEclipseLink(disabledReason = "verifying @EnabledOnEclipseLink")
    @DisplayName("@EnabledOnEclipseLink -> runs only with EclipseLink")
    @Test
    void enabledOnEclipseLink__() {
        assertThat(JinahyaPersistenceProviderTestUtils.isEclipseLink()).isTrue();
    }

    @DisabledOnHibernateOrm(disabledReason = "verifying @DisabledOnHibernateOrm")
    @DisplayName("@DisabledOnHibernateOrm -> runs only without Hibernate ORM")
    @Test
    void disabledOnHibernateOrm__() {
        assertThat(JinahyaPersistenceProviderTestUtils.isHibernateOrm()).isFalse();
    }

    @EnabledOnHibernateOrm(disabledReason = "verifying @EnabledOnHibernateOrm")
    @DisplayName("@EnabledOnHibernateOrm -> runs only with Hibernate ORM")
    @Test
    void enabledOnHibernateOrm__() {
        assertThat(JinahyaPersistenceProviderTestUtils.isHibernateOrm()).isTrue();
    }

    @DisabledOnEclipseLink
    @DisplayName("@DisabledOnEclipseLink on a class -> its tests run only without EclipseLink")
    @Nested
    class DisabledOnEclipseLinkClassTest {

        @Test
        void __() {
            assertThat(JinahyaPersistenceProviderTestUtils.isEclipseLink()).isFalse();
        }
    }

    @DisabledOnHibernateOrm
    @DisplayName("@DisabledOnHibernateOrm on a class -> its tests run only without Hibernate ORM")
    @Nested
    class DisabledOnHibernateOrmClassTest {

        @Test
        void __() {
            assertThat(JinahyaPersistenceProviderTestUtils.isHibernateOrm()).isFalse();
        }
    }
}
