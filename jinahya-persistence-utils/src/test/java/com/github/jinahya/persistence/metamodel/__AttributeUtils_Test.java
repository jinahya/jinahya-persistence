package com.github.jinahya.persistence.metamodel;

import jakarta.persistence.Column;
import jakarta.persistence.metamodel.Attribute;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Member;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

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

    /**
     * A field-access class: the attribute maps to the field itself, and nothing here is public.
     */
    public static class FieldAccess {

        @Column(name = "VALUE", nullable = false)
        private String value;
    }

    /**
     * A class whose property accessor is {@code protected}, which Jakarta Persistence 3.2 &sect;2.2 permits and
     * {@link java.beans.Introspector} does not report.
     */
    public static class ProtectedSetter {

        private String value;

        @Column(name = "VALUE")
        public String getValue() {
            return value;
        }

        protected void setValue(final String value) {
            this.value = value;
        }
    }

    /**
     * A property whose getter and setter disagree across the primitive/wrapper boundary, so no type-matching write
     * method exists and only a by-name match can resolve it.
     */
    public static class Unboxing {

        private Integer count;

        public Integer getCount() {
            return count;
        }

        protected void setCount(final int count) {
            this.count = count;
        }
    }

    /**
     * A property with an overloaded setter: one taking the type the java member declares, one taking a subtype. Only
     * the first can be handed every value the member holds.
     */
    public static class Overloaded {

        private Date when;

        private String taken;

        public Date getWhen() {
            return when;
        }

        public void setWhen(final Date when) {
            this.when = when;
            this.taken = "Date";
        }

        public void setWhen(final java.sql.Timestamp when) {
            this.when = when;
            this.taken = "Timestamp";
        }

        public String taken() {
            return taken;
        }
    }

    private static Attribute<?, ?> attributeNamed(final String name, final Class<?> javaType, final Member javaMember) {
        final var attribute = mock(Attribute.class);
        when(attribute.getName()).thenReturn(name);
        doReturn(javaType).when(attribute).getJavaType();
        doReturn(javaMember).when(attribute).getJavaMember();
        return attribute;
    }

    @DisplayName("applyJavaMember(attribute, function)")
    @Nested
    class ApplyJavaMember_Test {

        @DisplayName("a method member arrives as the method, with a null field")
        @Test
        void __methodMember() throws Exception {
            final var method = Root.class.getMethod("getValue");
            final var attribute = attributeNamed("value", String.class, method);
            final var seen = new AtomicReference<Object[]>();

            __AttributeUtils.applyJavaMember(attribute, m -> f -> seen.getAndSet(new Object[]{m, f}));

            assertThat(seen.get()).containsExactly(method, null);
        }

        @DisplayName("a field member arrives as the field, with a null method")
        @Test
        void __fieldMember() throws Exception {
            final var field = FieldAccess.class.getDeclaredField("value");
            final var attribute = attributeNamed("value", String.class, field);
            final var seen = new AtomicReference<Object[]>();

            __AttributeUtils.applyJavaMember(attribute, m -> f -> seen.getAndSet(new Object[]{m, f}));

            assertThat(seen.get()).containsExactly(null, field);
        }

        @DisplayName("a member which is neither is reported, with both the attribute and the member named")
        @Test
        void _IllegalArgumentException_WhenTheMemberIsNeither() throws Exception {
            // a Member which is neither a Method nor a Field; a provider should never produce one
            final var attribute = attributeNamed("value", String.class, Root.class.getConstructor());

            assertThatThrownBy(() -> __AttributeUtils.applyJavaMember(attribute, m -> f -> null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("unknown java member type");
        }

        @DisplayName("null arguments are rejected by name")
        @Test
        void _NullPointerException_ForNullArguments() throws Exception {
            final var attribute = attributeNamed("value", String.class, Root.class.getMethod("getValue"));
            assertThatThrownBy(() -> __AttributeUtils.applyJavaMember(null, m -> f -> null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("attribute");
            assertThatThrownBy(() -> __AttributeUtils.applyJavaMember(attribute, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("function");
        }
    }

    @DisplayName("getJavaMemberType(attribute)")
    @Nested
    class GetJavaMemberType_Test {

        @DisplayName("a property reports its getter's return type, not the provider's view")
        @Test
        void __methodReturnTypeBeatsGetJavaType() throws Exception {
            // exactly the Hibernate ORM 7.2 shape: the provider calls a java.util.Date property a
            // java.sql.Timestamp, a type no value the member holds ever had
            final var attribute = attributeNamed(
                    "when", java.sql.Timestamp.class, Overloaded.class.getMethod("getWhen"));

            assertThat(__AttributeUtils.getJavaMemberType(attribute)).isEqualTo(Date.class);
        }

        @DisplayName("a field reports its own type")
        @Test
        void __fieldType() throws Exception {
            final var attribute = attributeNamed(
                    "value", Object.class, FieldAccess.class.getDeclaredField("value"));

            assertThat(__AttributeUtils.getJavaMemberType(attribute)).isEqualTo(String.class);
        }
    }

    @DisplayName("getJavaMemberAnnotation(attribute, annotationClass)")
    @Nested
    class GetJavaMemberAnnotation_Test {

        @DisplayName("an annotation on the getter is found")
        @Test
        void __onAMethod() throws Exception {
            final var attribute = attributeNamed(
                    "value", String.class, ProtectedSetter.class.getMethod("getValue"));

            assertThat(__AttributeUtils.getJavaMemberAnnotation(attribute, Column.class))
                    .isNotNull()
                    .extracting(Column::name).isEqualTo("VALUE");
        }

        @DisplayName("an annotation on the field is found")
        @Test
        void __onAField() throws Exception {
            final var attribute = attributeNamed(
                    "value", String.class, FieldAccess.class.getDeclaredField("value"));

            assertThat(__AttributeUtils.getJavaMemberAnnotation(attribute, Column.class))
                    .isNotNull()
                    .extracting(Column::nullable).isEqualTo(false);
        }

        @DisplayName("an absent annotation is null, not an exception")
        @Test
        void __absentIsNull() throws Exception {
            final var attribute = attributeNamed("value", String.class, Root.class.getMethod("getValue"));

            assertThat(__AttributeUtils.getJavaMemberAnnotation(attribute, Column.class)).isNull();
        }
    }

    @DisplayName("getAttributeValue(entity, attribute)")
    @Nested
    class GetAttributeValue_Test {

        @DisplayName("an unset value reads back as null")
        @Test
        void __nullIsAValue() throws Exception {
            final var attribute = attributeNamed("value", String.class, Root.class.getMethod("getValue"));

            // the declared return type has to admit this: an optional column, an unset association and a
            // not-yet-generated id all read back as null
            assertThat(__AttributeUtils.getAttributeValue(new Root(), attribute)).isNull();
        }

        @DisplayName("a private field is read without a getter")
        @Test
        void __readsAPrivateField() throws Exception {
            final var attribute = attributeNamed(
                    "value", String.class, FieldAccess.class.getDeclaredField("value"));
            final var entity = new FieldAccess();
            entity.value = "v";

            assertThat(__AttributeUtils.getAttributeValue(entity, attribute)).isEqualTo("v");
        }

        @DisplayName("a null entity is rejected by name")
        @Test
        void _NullPointerException_NullEntity() throws Exception {
            final var attribute = attributeNamed("value", String.class, Root.class.getMethod("getValue"));

            assertThatThrownBy(() -> __AttributeUtils.getAttributeValue(null, attribute))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("entity");
        }
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

        @DisplayName("a private field is written without a setter")
        @Test
        void __writesAPrivateField() throws Exception {
            final var attribute = attributeNamed(
                    "value", String.class, FieldAccess.class.getDeclaredField("value"));
            final var entity = new FieldAccess();

            assertThat(__AttributeUtils.setAttributeValue(entity, attribute, "v")).isNull();

            assertThat(entity.value).isEqualTo("v");
        }

        @DisplayName("null clears a field-mapped attribute")
        @Test
        void __clearsAFieldWithNull() throws Exception {
            final var attribute = attributeNamed(
                    "value", String.class, FieldAccess.class.getDeclaredField("value"));
            final var entity = new FieldAccess();
            entity.value = "v";

            __AttributeUtils.setAttributeValue(entity, attribute, null);

            assertThat(entity.value).isNull();
        }

        @DisplayName("a protected accessor, which the Introspector does not report, is still found")
        @Test
        void __protectedSetter() throws Exception {
            final var attribute = attributeNamed(
                    "value", String.class, ProtectedSetter.class.getMethod("getValue"));
            final var entity = new ProtectedSetter();

            __AttributeUtils.setAttributeValue(entity, attribute, "v");

            assertThat(entity.getValue()).isEqualTo("v");
        }

        @DisplayName("a setter which only matches by name, across the primitive boundary, is still found")
        @Test
        void __unboxingSetter() throws Exception {
            // getCount() returns Integer while setCount(int) takes a primitive, so neither is assignable to
            // the other and only a by-name match resolves it
            final var attribute = attributeNamed("count", Integer.class, Unboxing.class.getMethod("getCount"));
            final var entity = new Unboxing();

            __AttributeUtils.setAttributeValue(entity, attribute, 3);

            assertThat(entity.getCount()).isEqualTo(3);
        }

        @DisplayName("an overloaded setter does not shadow the one matching the java member")
        @Test
        void __overloadedSetterPicksTheMemberType() throws Exception {
            // the provider calls this a java.sql.Timestamp, and the class happens to declare a setter taking
            // one; the member is a java.util.Date, so setWhen(Date) is the only setter every value fits
            final var attribute = attributeNamed(
                    "when", java.sql.Timestamp.class, Overloaded.class.getMethod("getWhen"));
            final var entity = new Overloaded();
            final var when = new Date(0L);

            __AttributeUtils.setAttributeValue(entity, attribute, when);

            assertThat(entity.taken()).isEqualTo("Date");
            assertThat(entity.getWhen()).isEqualTo(when);
        }

        @DisplayName("a null entity is rejected by name")
        @Test
        void _NullPointerException_NullEntity() throws Exception {
            final var attribute = attributeNamed("value", String.class, Root.class.getMethod("getValue"));

            assertThatThrownBy(() -> __AttributeUtils.setAttributeValue(null, attribute, "v"))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("entity");
        }
    }

    @DisplayName("the write-method cache")
    @Nested
    class SetterCache_Test {

        @DisplayName("two attributes of one name, from two metamodels, resolve the same write method")
        @Test
        void __keyedByNameNotByAttributeInstance() throws Exception {
            final var method = Root.class.getMethod("getValue");
            // two distinct Attribute instances, as two entity manager factories over one class would give
            final var first = attributeNamed("value", String.class, method);
            final var second = attributeNamed("value", String.class, method);
            final var entity = new Root();

            __AttributeUtils.setAttributeValue(entity, first, "a");
            assertThat(entity.getValue()).isEqualTo("a");

            // the cache is keyed by the attribute NAME, so the second one hits the same entry rather than
            // adding one which reaches a second, now unreachable, persistence unit
            __AttributeUtils.setAttributeValue(entity, second, "b");
            assertThat(entity.getValue()).isEqualTo("b");
        }
    }
}
