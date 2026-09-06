package com.github.jinahya.persistence.metamodel;

import jakarta.persistence.metamodel.Attribute;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link __AttributeUtils}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class __AttributeUtils_Test {

    // a mapped-superclass-shaped root: one attribute, inherited by two subclasses
    public static class Root {

        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(final String value) {
            this.value = value;
        }
    }

    /**
     * A subclass which <em>overrides</em> the setter, so its write method is declared on the subclass rather than on
     * {@link Root}.
     */
    public static class Overriding extends Root {

        @Override
        public void setValue(final String value) {
            super.setValue("overridden:" + value);
        }
    }

    /**
     * A sibling subclass which does not override anything.
     */
    public static class Plain extends Root {

    }

    /**
     * A class whose property has a getter but no setter.
     */
    public static class ReadOnly {

        public String getValue() {
            return "immutable";
        }
    }

    private static Attribute<?, ?> attributeNamed(final String name, final Class<?> javaType, final Method javaMember) {
        final var attribute = mock(Attribute.class);
        when(attribute.getName()).thenReturn(name);
        doReturn(javaType).when(attribute).getJavaType();
        doReturn(javaMember).when(attribute).getJavaMember();
        return attribute;
    }

    @DisplayName("setAttributeValue(entity, attribute, value)")
    @Nested
    class SetAttributeValue_Test {

        @DisplayName("one attribute shared by two subclasses writes to each correctly")
        @Test
        void __sharedAttributeAcrossSiblingSubclasses() throws Exception {
            // exactly the shape of an attribute declared on a @MappedSuperclass: ONE Attribute instance,
            // used for instances of two different subclasses
            final var attribute = attributeNamed("value", String.class, Root.class.getMethod("getValue"));

            // the overriding subclass first, so its own setValue is what gets resolved (and cached)
            final var overriding = new Overriding();
            __AttributeUtils.setAttributeValue(overriding, attribute, "a");
            assertThat(overriding.getValue()).isEqualTo("overridden:a");

            // then a sibling which does NOT override: a write method cached from the other subclass
            // cannot be invoked on this one
            final var plain = new Plain();
            __AttributeUtils.setAttributeValue(plain, attribute, "b");
            assertThat(plain.getValue()).isEqualTo("b");
        }

        @DisplayName("a property with no setter is reported, not an NPE")
        @Test
        void __readOnlyPropertyIsReported() throws Exception {
            final var attribute = attributeNamed("value", String.class, ReadOnly.class.getMethod("getValue"));

            assertThatThrownBy(() -> __AttributeUtils.setAttributeValue(new ReadOnly(), attribute, "x"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("no setter")
                    .isNotInstanceOf(NullPointerException.class);
        }

        @DisplayName("an ordinary property round-trips")
        @Test
        void __ordinaryProperty() throws Exception {
            final var attribute = attributeNamed("value", String.class, Root.class.getMethod("getValue"));
            final var root = new Root();

            __AttributeUtils.setAttributeValue(root, attribute, "v");

            assertThat(root.getValue()).isEqualTo("v");
            assertThat(__AttributeUtils.getAttributeValue(root, attribute)).isEqualTo("v");
        }
    }
}
