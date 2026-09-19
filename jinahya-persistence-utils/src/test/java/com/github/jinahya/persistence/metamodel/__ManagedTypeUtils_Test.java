package com.github.jinahya.persistence.metamodel;

import jakarta.persistence.EntityManagerFactory;
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
 * Tests for {@link __ManagedTypeUtils}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class __ManagedTypeUtils_Test {

    // a class per test. The lookups no longer memoize anything, so this is no longer load-bearing, but it
    // keeps each case reading against a model of its own
    private static class Alpha {

    }

    private static class Bravo {

    }

    private static class Charlie {

    }

    private static class Delta {

    }

    /**
     * Returns a factory whose metamodel does <em>not</em> manage anything, exactly as the specification requires:
     * {@link Metamodel#managedType(Class)} throws, it does not return {@code null}.
     */
    private static EntityManagerFactory knowingNothing() {
        final var factory = mock(EntityManagerFactory.class);
        final var metamodel = mock(Metamodel.class);
        when(factory.getMetamodel()).thenReturn(metamodel);
        doThrow(new IllegalArgumentException("not a managed type")).when(metamodel).managedType(any());
        return factory;
    }

    private static EntityManagerFactory knowing(final Class<?> type, final ManagedType<?> managedType) {
        final var factory = mock(EntityManagerFactory.class);
        final var metamodel = mock(Metamodel.class);
        when(factory.getMetamodel()).thenReturn(metamodel);
        doReturn(managedType).when(metamodel).managedType(type);
        return factory;
    }

    @DisplayName("getManagedType(typeClass, entityManagerFactories)")
    @Nested
    class GetManagedType_Test {

        @DisplayName("a factory which does not manage the class does not abort the lookup")
        @Test
        void __fallsThroughToTheNextFactory() {
            final var expected = mock(ManagedType.class);

            // Metamodel.managedType throws for a class it does not manage -- it never returns null --
            // so a lookup which does not catch that stops at the first factory instead of trying the rest
            final var actual = __ManagedTypeUtils.getManagedType(
                    Alpha.class,
                    List.of(knowingNothing(), knowing(Alpha.class, expected))
            );

            assertThat(actual).isSameAs(expected);
        }

        @DisplayName("the first factory which knows the class wins")
        @Test
        void __firstMatchWins() {
            final var first = mock(ManagedType.class);
            final var second = mock(ManagedType.class);

            final var actual = __ManagedTypeUtils.getManagedType(
                    Bravo.class,
                    List.of(knowing(Bravo.class, first), knowing(Bravo.class, second))
            );

            assertThat(actual).isSameAs(first);
        }

        @DisplayName("no factory knowing the class -> IllegalArgumentException")
        @Test
        void _IllegalArgumentException_WhenNobodyKnowsIt() {
            assertThatThrownBy(() -> __ManagedTypeUtils.getManagedType(
                    Charlie.class,
                    List.of(knowingNothing(), knowingNothing())
            )).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("an empty iterable is not a match")
        @Test
        void _IllegalArgumentException_WhenThereIsNoFactoryAtAll() {
            assertThatThrownBy(() -> __ManagedTypeUtils.getManagedType(Charlie.class, List.of()))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("null arguments are rejected by name")
        @Test
        void _NullPointerException_ForNullArguments() {
            assertThatThrownBy(() -> __ManagedTypeUtils.getManagedType(null, List.of()))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("typeClass");
            assertThatThrownBy(() -> __ManagedTypeUtils.getManagedType(
                    Charlie.class,
                    (Iterable<EntityManagerFactory>) null
            )).isInstanceOf(NullPointerException.class);
        }
    }

    @DisplayName("getManagedType(typeClass, entityManagerFactory)")
    @Nested
    class GetManagedTypeOfOneFactory_Test {

        @DisplayName("the single-factory overload resolves what that factory knows")
        @Test
        void __resolves() {
            final var expected = mock(ManagedType.class);

            final var actual = __ManagedTypeUtils.getManagedType(Delta.class, knowing(Delta.class, expected));

            assertThat(actual).isSameAs(expected);
        }

        @DisplayName("a factory which does not know the class -> IllegalArgumentException")
        @Test
        void _IllegalArgumentException_WhenItDoesNotKnowIt() {
            assertThatThrownBy(() -> __ManagedTypeUtils.getManagedType(Delta.class, knowingNothing()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("no managed type found");
        }
    }

    @DisplayName("findManagedType(typeClass, ...)")
    @Nested
    class FindManagedType_Test {

        @DisplayName("a miss is an empty optional, not an exception")
        @Test
        void __missIsEmpty() {
            assertThat(__ManagedTypeUtils.findManagedType(Alpha.class, knowingNothing())).isEmpty();
            assertThat(__ManagedTypeUtils.findManagedType(Alpha.class, List.of(knowingNothing()))).isEmpty();
        }

        @DisplayName("a hit carries the managed type")
        @Test
        void __hitCarriesTheType() {
            final var expected = mock(ManagedType.class);

            assertThat(__ManagedTypeUtils.findManagedType(Alpha.class, knowing(Alpha.class, expected)))
                    .containsSame(expected);
            assertThat(__ManagedTypeUtils.findManagedType(
                    Alpha.class,
                    List.of(knowingNothing(), knowing(Alpha.class, expected))
            )).containsSame(expected);
        }
    }
}
