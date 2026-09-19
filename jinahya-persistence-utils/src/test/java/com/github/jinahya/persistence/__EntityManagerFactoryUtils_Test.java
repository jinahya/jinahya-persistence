package com.github.jinahya.persistence;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnitUtil;
import jakarta.persistence.metamodel.Metamodel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link __EntityManagerFactoryUtils}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class __EntityManagerFactoryUtils_Test {

    private EntityManagerFactory factory;

    private PersistenceUnitUtil persistenceUnitUtil;

    @BeforeEach
    void mockFactory() {
        factory = mock(EntityManagerFactory.class);
        persistenceUnitUtil = mock(PersistenceUnitUtil.class);
        when(factory.getPersistenceUnitUtil()).thenReturn(persistenceUnitUtil);
    }

    @DisplayName("getPersistenceUnitUtil(factory)")
    @Nested
    class GetPersistenceUnitUtil_Test {

        @DisplayName("the factory's own instance is returned, on every call")
        @Test
        void __notMemoized() {
            // the memoization this used to do could never expire, and bought nothing; asking twice has to
            // ask the factory twice
            assertThat(__EntityManagerFactoryUtils.getPersistenceUnitUtil(factory)).isSameAs(persistenceUnitUtil);
            assertThat(__EntityManagerFactoryUtils.getPersistenceUnitUtil(factory)).isSameAs(persistenceUnitUtil);
            org.mockito.Mockito.verify(factory, org.mockito.Mockito.times(2)).getPersistenceUnitUtil();
        }

        @DisplayName("a null factory is rejected by name")
        @Test
        void _NullPointerException_NullFactory() {
            assertThatThrownBy(() -> __EntityManagerFactoryUtils.getPersistenceUnitUtil(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("factory");
        }
    }

    @DisplayName("getIdentifier(factory, entity)")
    @Nested
    class GetIdentifier_Test {

        @DisplayName("the identifier comes back cast to the caller's type")
        @Test
        void __casts() {
            final var entity = new Object();
            when(persistenceUnitUtil.getIdentifier(entity)).thenReturn(42L);

            final Long identifier = __EntityManagerFactoryUtils.getIdentifier(factory, entity);

            assertThat(identifier).isEqualTo(42L);
        }

        @DisplayName("an entity with no identifier yet reads back as null")
        @Test
        void __nullWhenNotAssignedYet() {
            final var entity = new Object();
            when(persistenceUnitUtil.getIdentifier(entity)).thenReturn(null);

            assertThat(__EntityManagerFactoryUtils.<Long>getIdentifier(factory, entity)).isNull();
        }

        @DisplayName("null arguments are rejected by name")
        @Test
        void _NullPointerException_ForNullArguments() {
            assertThatThrownBy(() -> __EntityManagerFactoryUtils.getIdentifier(null, new Object()))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("factory");
            assertThatThrownBy(() -> __EntityManagerFactoryUtils.getIdentifier(factory, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("entity");
        }
    }

    @DisplayName("getVersion(factory, entity)")
    @Nested
    class GetVersion_Test {

        @DisplayName("the version comes back cast to the caller's type")
        @Test
        void __casts() {
            final var entity = new Object();
            when(persistenceUnitUtil.getVersion(entity)).thenReturn(7);

            final Integer version = __EntityManagerFactoryUtils.getVersion(factory, entity);

            assertThat(version).isEqualTo(7);
        }

        @DisplayName("an entity with no version attribute reads back as null")
        @Test
        void __nullWhenThereIsNoVersionAttribute() {
            final var entity = new Object();
            when(persistenceUnitUtil.getVersion(entity)).thenReturn(null);

            assertThat(__EntityManagerFactoryUtils.<Integer>getVersion(factory, entity)).isNull();
        }

        @DisplayName("null arguments are rejected by name")
        @Test
        void _NullPointerException_ForNullArguments() {
            assertThatThrownBy(() -> __EntityManagerFactoryUtils.getVersion(null, new Object()))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("factory");
            assertThatThrownBy(() -> __EntityManagerFactoryUtils.getVersion(factory, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("entity");
        }
    }

    @DisplayName("getMetamodel(factory)")
    @Nested
    class GetMetamodel_Test {

        @DisplayName("the factory's own metamodel is returned, on every call")
        @Test
        void __notMemoized() {
            final var metamodel = mock(Metamodel.class);
            when(factory.getMetamodel()).thenReturn(metamodel);

            assertThat(__EntityManagerFactoryUtils.getMetamodel(factory)).isSameAs(metamodel);
            assertThat(__EntityManagerFactoryUtils.getMetamodel(factory)).isSameAs(metamodel);
            org.mockito.Mockito.verify(factory, org.mockito.Mockito.times(2)).getMetamodel();
        }

        @DisplayName("a null factory is rejected by name")
        @Test
        void _NullPointerException_NullFactory() {
            assertThatThrownBy(() -> __EntityManagerFactoryUtils.getMetamodel(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("factory");
        }
    }
}
