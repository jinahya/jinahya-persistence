package com.github.jinahya.persistence.metamodel;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.ManagedType;
import jakarta.persistence.metamodel.Metamodel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link __EntityTypeUtils}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class __EntityTypeUtils_Test {

    private static class Alpha {

    }

    private static class Bravo {

    }

    private static class Charlie {

    }

    /**
     * Returns a factory whose metamodel maps nothing, exactly as the specification requires:
     * {@link Metamodel#entity(Class)} throws, it does not return {@code null}.
     */
    private static EntityManagerFactory knowingNothing() {
        final var factory = mock(EntityManagerFactory.class);
        final var metamodel = mock(Metamodel.class);
        when(factory.getMetamodel()).thenReturn(metamodel);
        doThrow(new IllegalArgumentException("not an entity")).when(metamodel).entity(any(Class.class));
        doThrow(new IllegalArgumentException("not a managed type")).when(metamodel).managedType(any());
        return factory;
    }

    private static EntityManagerFactory knowing(final Class<?> type, final EntityType<?> entityType) {
        final var factory = mock(EntityManagerFactory.class);
        final var metamodel = mock(Metamodel.class);
        when(factory.getMetamodel()).thenReturn(metamodel);
        doReturn(entityType).when(metamodel).entity(type);
        doReturn(entityType).when(metamodel).managedType(type);
        return factory;
    }

    @DisplayName("getEntityType(entityClass, entityManagerFactories)")
    @Nested
    class GetEntityType_Test {

        @DisplayName("a factory which does not map the class does not abort the lookup")
        @Test
        void __fallsThroughToTheNextFactory() {
            final var expected = mock(EntityType.class);

            final var actual = __EntityTypeUtils.getEntityType(
                    Alpha.class,
                    List.of(knowingNothing(), knowing(Alpha.class, expected))
            );

            assertThat(actual).isSameAs(expected);
        }

        @DisplayName("the first factory which maps the class wins")
        @Test
        void __firstMatchWins() {
            final var first = mock(EntityType.class);
            final var second = mock(EntityType.class);

            final var actual = __EntityTypeUtils.getEntityType(
                    Bravo.class,
                    List.of(knowing(Bravo.class, first), knowing(Bravo.class, second))
            );

            assertThat(actual).isSameAs(first);
        }

        @DisplayName("no factory mapping the class -> IllegalArgumentException")
        @Test
        void _IllegalArgumentException_WhenNobodyMapsIt() {
            assertThatThrownBy(() -> __EntityTypeUtils.getEntityType(
                    Charlie.class,
                    List.of(knowingNothing(), knowingNothing())
            ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("no entity type found");
        }

        @DisplayName("the single-factory overload resolves what that factory maps")
        @Test
        void __singleFactoryOverload() {
            final var expected = mock(EntityType.class);

            assertThat(__EntityTypeUtils.getEntityType(Alpha.class, knowing(Alpha.class, expected)))
                    .isSameAs(expected);
            assertThatThrownBy(() -> __EntityTypeUtils.getEntityType(Alpha.class, knowingNothing()))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("findEntityType(entityClass, ...)")
    @Nested
    class FindEntityType_Test {

        @DisplayName("a miss is an empty optional, not an exception")
        @Test
        void __missIsEmpty() {
            assertThat(__EntityTypeUtils.findEntityType(Alpha.class, knowingNothing())).isEmpty();
            assertThat(__EntityTypeUtils.findEntityType(Alpha.class, List.of(knowingNothing()))).isEmpty();
        }

        @DisplayName("a hit carries the entity type")
        @Test
        void __hitCarriesTheType() {
            final var expected = mock(EntityType.class);

            assertThat(__EntityTypeUtils.findEntityType(Alpha.class, knowing(Alpha.class, expected)))
                    .containsSame(expected);
            assertThat(__EntityTypeUtils.findEntityType(
                    Alpha.class,
                    List.of(knowingNothing(), knowing(Alpha.class, expected))
            )).containsSame(expected);
        }
    }

    @DisplayName("the deprecated getManagedType(entityClass, entityManagerFactories)")
    @Nested
    class DeprecatedGetManagedType_Test {

        @DisplayName("still answers, by delegating to __ManagedTypeUtils")
        @Test
        @SuppressWarnings({"removal"})
        void __delegates() {
            final ManagedType<?> expected = mock(EntityType.class);

            final var actual = __EntityTypeUtils.getManagedType(
                    Alpha.class,
                    List.of(knowingNothing(), knowing(Alpha.class, (EntityType<?>) expected))
            );

            assertThat(actual).isSameAs(expected);
        }
    }
}
