package com.github.jinahya.persistence.more;

/*-
 * #%L
 * jinahya-persistence-more
 * %%
 * Copyright (C) 2025 - 2026 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import jakarta.persistence.Transient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * A class for testing {@link __SelfReferencingUtils}, pinning which member the marks are read from.
 * <p>
 * The access type belongs to the implementing entity, so both placements have to work: a <em>field</em>, as an entity
 * using field access declares it, and an <em>accessor</em>, as an entity using property access does — including an
 * accessor which derives its value and has no field behind it at all.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class __SelfReferencingUtils_Test {

    /**
     * An implementation which marks its fields, as an entity using field access declares them.
     */
    static class FieldMarked implements __SelfReferencingOrdered<FieldMarked> {

        @__SelfReferencingParent
        FieldMarked parent;

        @__SelfReferencingOrdinal
        Integer ordinal;

        @Override
        public FieldMarked getHierarchyParent() {
            return __SelfReferencingUtils.parentOf(this);
        }

        @Override
        public int getHierarchyDepth() {
            return 0;
        }
    }

    /**
     * An implementation which marks its accessors, as an entity using property access declares them, and whose ordinal
     * is <em>derived</em> — there is no field to mark for it.
     */
    static class AccessorMarked implements __SelfReferencingOrdered<AccessorMarked> {

        AccessorMarked parent;

        @__SelfReferencingParent
        public AccessorMarked getParent() {
            return parent;
        }

        @__SelfReferencingOrdinal
        @Transient
        public Integer getDisplayOrder() {
            return parent == null ? null : 7;
        }

        @Override
        public AccessorMarked getHierarchyParent() {
            return __SelfReferencingUtils.parentOf(this);
        }

        @Override
        public int getHierarchyDepth() {
            return 0;
        }
    }

    /**
     * An implementation which marks a field <em>and</em> an accessor, holding a different instance in each so that the
     * one which won can be told.
     */
    static class BothMarked implements __SelfReferencing<BothMarked> {

        @__SelfReferencingParent
        BothMarked viaField;

        BothMarked viaAccessor;

        @__SelfReferencingParent
        public BothMarked getParent() {
            return viaAccessor;
        }

        @Override
        public BothMarked getHierarchyParent() {
            return __SelfReferencingUtils.parentOf(this);
        }

        @Override
        public int getHierarchyDepth() {
            return 0;
        }
    }

    /**
     * A base which marks an accessor.
     */
    static class OverriddenBase implements __SelfReferencing<OverriddenBase> {

        @__SelfReferencingParent
        public OverriddenBase getParent() {
            return null;
        }

        @Override
        public OverriddenBase getHierarchyParent() {
            return __SelfReferencingUtils.parentOf(this);
        }

        @Override
        public int getHierarchyDepth() {
            return 0;
        }
    }

    /**
     * A subclass which overrides the marked accessor of {@link OverriddenBase} and marks its override as well, which is
     * one mark seen twice rather than two marks.
     */
    static class OverridingSub extends OverriddenBase {

        @__SelfReferencingParent
        @Override
        public OverriddenBase getParent() {
            return this;
        }
    }

    /**
     * An implementation which carries no mark at all.
     */
    static class Unmarked implements __SelfReferencing<Unmarked> {

        @Override
        public Unmarked getHierarchyParent() {
            return null;
        }

        @Override
        public int getHierarchyDepth() {
            return 0;
        }
    }

    /**
     * An implementation which marks a method which is not an accessor.
     */
    static class NonAccessorMarked implements __SelfReferencingOrdered<NonAccessorMarked> {

        @__SelfReferencingOrdinal
        public void setOrdinal(final Integer ordinal) {
            // no-op; marked only to be rejected
        }

        @Override
        public NonAccessorMarked getHierarchyParent() {
            return null;
        }

        @Override
        public int getHierarchyDepth() {
            return 0;
        }
    }

    /**
     * An implementation whose marked ordinal accessor returns a primitive, which an ordered type may: within
     * {@link __SelfReferencingOrdered} there is no absence for {@code 0} to also stand for.
     */
    static class PrimitiveOrdinalMarked implements __SelfReferencingOrdered<PrimitiveOrdinalMarked> {

        @__SelfReferencingOrdinal
        public int getOrdinal() {
            return 1;
        }

        @Override
        public PrimitiveOrdinalMarked getHierarchyParent() {
            return null;
        }

        @Override
        public int getHierarchyDepth() {
            return 0;
        }
    }

    /**
     * An implementation whose marked ordinal accessor holds neither an {@code int} nor an {@link Integer}.
     */
    static class WrongTypeOrdinalMarked implements __SelfReferencingOrdered<WrongTypeOrdinalMarked> {

        @__SelfReferencingOrdinal
        public long getOrdinal() {
            return 1L;
        }

        @Override
        public WrongTypeOrdinalMarked getHierarchyParent() {
            return null;
        }

        @Override
        public int getHierarchyDepth() {
            return 0;
        }
    }

    /**
     * An implementation which declares that its siblings are ordered and then carries no mark saying where the ordinal
     * is, which is a broken implementation rather than a hierarchy without an order.
     */
    static class OrderedUnmarked implements __SelfReferencingOrdered<OrderedUnmarked> {

        @Override
        public OrderedUnmarked getHierarchyParent() {
            return null;
        }

        @Override
        public int getHierarchyDepth() {
            return 0;
        }
    }

    /**
     * An implementation whose marked accessor throws, as a derived accessor may.
     */
    static class ThrowingAccessorMarked implements __SelfReferencing<ThrowingAccessorMarked> {

        @__SelfReferencingParent
        public ThrowingAccessorMarked getParent() {
            throw new IllegalArgumentException("boom");
        }

        @Override
        public ThrowingAccessorMarked getHierarchyParent() {
            return __SelfReferencingUtils.parentOf(this);
        }

        @Override
        public int getHierarchyDepth() {
            return 0;
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * A nested class for testing {@link __SelfReferencingUtils#parentOf(__SelfReferencing)}.
     */
    @DisplayName("parentOf(instance)")
    @Nested
    class ParentOfTest {

        @DisplayName("rejects a null instance")
        @Test
        void __null() {
            assertThatThrownBy(() -> __SelfReferencingUtils.parentOf(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @DisplayName("reads a marked field")
        @Test
        void __field() {
            final var root = new FieldMarked();
            final var child = new FieldMarked();
            child.parent = root;
            assertThat(__SelfReferencingUtils.parentOf(child)).isSameAs(root);
        }

        @DisplayName("reads a marked accessor")
        @Test
        void __accessor() {
            final var root = new AccessorMarked();
            final var child = new AccessorMarked();
            child.parent = root;
            assertThat(__SelfReferencingUtils.parentOf(child)).isSameAs(root);
        }

        @DisplayName("answers null for a root, either way")
        @Test
        void __root() {
            assertThat(__SelfReferencingUtils.parentOf(new FieldMarked())).isNull();
            assertThat(__SelfReferencingUtils.parentOf(new AccessorMarked())).isNull();
        }

        @DisplayName("prefers a marked accessor over a field marked alongside it")
        @Test
        void __accessorWins() {
            final var instance = new BothMarked();
            instance.viaField = new BothMarked();
            instance.viaAccessor = new BothMarked();
            assertThat(__SelfReferencingUtils.parentOf(instance)).isSameAs(instance.viaAccessor);
        }

        @DisplayName("takes a marked override for one mark, not for two")
        @Test
        void __override() {
            final var instance = new OverridingSub();
            assertThat(__SelfReferencingUtils.parentOf(instance)).isSameAs(instance);
        }

        @DisplayName("fails when nothing is marked")
        @Test
        void __unmarked() {
            final var instance = new Unmarked();
            assertThatThrownBy(() -> __SelfReferencingUtils.parentOf(instance))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("no member annotated");
        }

        @DisplayName("lets an exception thrown by a marked accessor through, unwrapped")
        @Test
        void __throwing() {
            final var instance = new ThrowingAccessorMarked();
            assertThatThrownBy(() -> __SelfReferencingUtils.parentOf(instance))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("boom");
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * A nested class for testing {@link __SelfReferencingUtils#ordinalOf(__SelfReferencingOrdered)}.
     */
    @DisplayName("ordinalOf(instance)")
    @Nested
    class OrdinalOfTest {

        @DisplayName("rejects a null instance")
        @Test
        void __null() {
            assertThatThrownBy(() -> __SelfReferencingUtils.ordinalOf(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @DisplayName("reads a marked field")
        @Test
        void __field() {
            final var instance = new FieldMarked();
            instance.ordinal = 3;
            assertThat(__SelfReferencingUtils.ordinalOf(instance)).isEqualTo(3);
            assertThat(instance.getSiblingOrdinal()).isEqualTo(3);
        }

        @DisplayName("reads a marked accessor which derives its value")
        @Test
        void __derivedAccessor() {
            final var child = new AccessorMarked();
            child.parent = new AccessorMarked();
            assertThat(__SelfReferencingUtils.ordinalOf(child)).isEqualTo(7);
            assertThat(child.getSiblingOrdinal()).isEqualTo(7);
        }

        @DisplayName("reads a marked accessor which returns a primitive")
        @Test
        void __primitive() {
            final var instance = new PrimitiveOrdinalMarked();
            assertThat(__SelfReferencingUtils.ordinalOf(instance)).isEqualTo(1);
            assertThat(instance.getSiblingOrdinal()).isEqualTo(1);
        }

        @DisplayName("answers null when the marked member holds no value")
        @Test
        void __empty() {
            // an ordinal which was never assigned is not read as 0, and does not fail the read either:
            // @NotNull on getSiblingOrdinal() is what reports it, and a throw here would abort the very
            // validation pass which does the reporting
            assertThat(__SelfReferencingUtils.ordinalOf(new FieldMarked())).isNull();
            assertThat(__SelfReferencingUtils.ordinalOf(new AccessorMarked())).isNull();
            assertThat(new FieldMarked().getSiblingOrdinal()).isNull();
        }

        @DisplayName("fails for an ordered type which carries no mark")
        @Test
        void __unmarked() {
            final var instance = new OrderedUnmarked();
            assertThatThrownBy(() -> __SelfReferencingUtils.ordinalOf(instance))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("no member annotated");
        }

        @DisplayName("fails on a marked method which is not an accessor")
        @Test
        void __nonAccessor() {
            final var instance = new NonAccessorMarked();
            assertThatThrownBy(() -> __SelfReferencingUtils.ordinalOf(instance))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("is not an accessor");
        }

        @DisplayName("fails on a marked member typed neither int nor Integer")
        @Test
        void __wrongType() {
            final var instance = new WrongTypeOrdinalMarked();
            assertThatThrownBy(() -> __SelfReferencingUtils.ordinalOf(instance))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("typed neither int nor " + Integer.class.getSimpleName());
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * A nested class for pinning where the marks may be written, which is what lets an entity keep the mark next to the
     * mapping its own access type decides.
     */
    @DisplayName("@Target / @Retention of the marks")
    @Nested
    class MarkTest {

        @DisplayName("@__SelfReferencingParent goes on a field or on a method, at runtime")
        @Test
        void __parent() {
            assertThat(__SelfReferencingParent.class.getAnnotation(Target.class).value())
                    .containsExactlyInAnyOrder(ElementType.FIELD, ElementType.METHOD);
            assertThat(__SelfReferencingParent.class.getAnnotation(Retention.class).value())
                    .isSameAs(RetentionPolicy.RUNTIME);
        }

        @DisplayName("@__SelfReferencingOrdinal goes on a field or on a method, at runtime")
        @Test
        void __ordinal() {
            assertThat(__SelfReferencingOrdinal.class.getAnnotation(Target.class).value())
                    .containsExactlyInAnyOrder(ElementType.FIELD, ElementType.METHOD);
            assertThat(__SelfReferencingOrdinal.class.getAnnotation(Retention.class).value())
                    .isSameAs(RetentionPolicy.RUNTIME);
        }
    }
}
