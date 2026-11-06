package com.github.jinahya.persistence.test.util;

import jakarta.persistence.spi.PersistenceProviderResolverHolder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A class for testing {@link JinahyaPersistenceProviderTestUtils}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class JinahyaPersistenceProviderTestUtils_Test {

    @DisplayName("isEclipseLink() -> whether EclipseLink is among the resolved persistence providers")
    @Test
    void isEclipseLink__() {
        final var resolved = PersistenceProviderResolverHolder.getPersistenceProviderResolver()
                .getPersistenceProviders().stream()
                .anyMatch(p -> p.getClass().getName()
                        .equals(JinahyaPersistenceProviderTestUtils.ECLIPSELINK_PERSISTENCE_PROVIDER));
        assertThat(JinahyaPersistenceProviderTestUtils.isEclipseLink()).isEqualTo(resolved);
    }

    @DisplayName("isHibernateOrm() -> whether Hibernate ORM is among the resolved persistence providers")
    @Test
    void isHibernateOrm__() {
        final var resolved = PersistenceProviderResolverHolder.getPersistenceProviderResolver()
                .getPersistenceProviders().stream()
                .anyMatch(p -> p.getClass().getName()
                        .equals(JinahyaPersistenceProviderTestUtils.HIBERNATE_ORM_PERSISTENCE_PROVIDER));
        assertThat(JinahyaPersistenceProviderTestUtils.isHibernateOrm()).isEqualTo(resolved);
    }

    @DisplayName("isWovenByEclipseLink(a class no provider wove) -> false")
    @Test
    void isWovenByEclipseLink_False_NotWoven() {
        assertThat(JinahyaPersistenceProviderTestUtils.isWovenByEclipseLink(String.class)).isFalse();
        assertThat(JinahyaPersistenceProviderTestUtils.isWovenByEclipseLink(Object.class)).isFalse();
    }

    @DisplayName("isWovenByEclipseLink(null) -> false")
    @Test
    void isWovenByEclipseLink_False_Null() {
        assertThat(JinahyaPersistenceProviderTestUtils.isWovenByEclipseLink(null)).isFalse();
    }
}
