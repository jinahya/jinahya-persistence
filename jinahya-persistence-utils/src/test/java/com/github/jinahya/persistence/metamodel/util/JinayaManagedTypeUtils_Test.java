package com.github.jinahya.persistence.metamodel.util;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.ManagedType;
import jakarta.persistence.metamodel.Metamodel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for the deprecated {@link JinayaManagedTypeUtils}, which only delegates to {@link JinahyaManagedTypeUtils}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S3577", // Test classes should comply with a naming convention
        "removal"
})
class JinayaManagedTypeUtils_Test {

    private static class Alpha {

    }

    private static EntityManagerFactory knowing(final Class<?> type, final ManagedType<?> managedType) {
        final var factory = mock(EntityManagerFactory.class);
        final var metamodel = mock(Metamodel.class);
        when(factory.getMetamodel()).thenReturn(metamodel);
        doReturn(managedType).when(metamodel).managedType(type);
        return factory;
    }

    @DisplayName("findManagedType/getManagedType still answer, by delegating to JinahyaManagedTypeUtils")
    @Test
    void __managedType() {
        final var expected = mock(ManagedType.class);
        final var factory = knowing(Alpha.class, expected);
        assertThat(JinayaManagedTypeUtils.findManagedType(Alpha.class, factory)).containsSame(expected);
        assertThat(JinayaManagedTypeUtils.findManagedType(Alpha.class, List.of(factory))).containsSame(expected);
        assertThat(JinayaManagedTypeUtils.getManagedType(Alpha.class, factory)).isSameAs(expected);
        assertThat(JinayaManagedTypeUtils.getManagedType(Alpha.class, List.of(factory))).isSameAs(expected);
    }

    @DisplayName("resolveAttributePath still answers, by delegating to JinahyaManagedTypeUtils")
    @Test
    void __resolveAttributePath() {
        final var managedType = mock(ManagedType.class);
        final var attribute = mock(Attribute.class);
        doReturn(attribute).when(managedType).getAttribute("name");
        assertThat(JinayaManagedTypeUtils.resolveAttributePath(managedType, "name")).isSameAs(attribute);
    }
}
