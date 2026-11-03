package com.github.jinahya.persistence.test.util;

import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link JinahyaPersistenceTestUtils}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class JinahyaPersistenceTestUtils_Test {

    private static final String PERSISTENCE_UNIT_NAME = __PersistenceProducer.PERSISTENCE_UNIT_NAME;

    @DisplayName("applyEntityManagerFactory(persistenceUnitName, function)")
    @Nested
    class ApplyEntityManagerFactory_Test {

        @DisplayName("the same open factory is applied to each call for the same unit")
        @Test
        void __cached() {
            final var first = JinahyaPersistenceTestUtils.applyEntityManagerFactory(PERSISTENCE_UNIT_NAME, f -> f);
            final var second = JinahyaPersistenceTestUtils.applyEntityManagerFactory(PERSISTENCE_UNIT_NAME, f -> f);
            assertThat(second).isSameAs(first);
            assertThat(second.isOpen()).isTrue();
        }

        @DisplayName("a factory found closed is opened again, as a new one")
        @Test
        void __reopenedWhenClosed() {
            final var closed = JinahyaPersistenceTestUtils.applyEntityManagerFactory(PERSISTENCE_UNIT_NAME, f -> {
                f.close();
                return f;
            });
            final var reopened = JinahyaPersistenceTestUtils.applyEntityManagerFactory(PERSISTENCE_UNIT_NAME, f -> f);
            assertThat(reopened).isNotSameAs(closed);
            assertThat(reopened.isOpen()).isTrue();
        }

        @Test
        void __null() {
            assertThatThrownBy(() -> JinahyaPersistenceTestUtils.applyEntityManagerFactory(null, f -> f))
                    .isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> JinahyaPersistenceTestUtils.applyEntityManagerFactory(
                    PERSISTENCE_UNIT_NAME, (Function<EntityManagerFactory, Object>) null))
                    .isInstanceOf(NullPointerException.class);
        }
    }
}
