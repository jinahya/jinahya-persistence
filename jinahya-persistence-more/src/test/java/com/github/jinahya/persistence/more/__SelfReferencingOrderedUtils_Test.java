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
 * A class for testing {@link __SelfReferencingOrderedUtils}, pinning which member the ordinal is read from.
 * <p>
 * Every implementation here is an {@link __SelfReferencingOrdered}, because nothing else reaches the method under test,
 * and every one of them marks the ordinal alone: which member the <em>parent</em> is read from is
 * {@link __SelfReferencingUtils_Test}'s subject, and these fixtures answer
 * {@link __SelfReferencing#getHierarchyParent() getHierarchyParent()} directly so that the two never overlap.
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
class __SelfReferencingOrderedUtils_Test {

    /**
     * An implementation which marks its field, as an entity using field access declares it.
     */
    static class FieldMarked implements __SelfReferencingOrdered<FieldMarked> {

        FieldMarked parent;

        @__SelfReferencingOrdinal
        Integer ordinal;

        @Override
        public FieldMarked getHierarchyParent() {
            return parent;
        }

        @Override
        public int getHierarchyDepth() {
            return 0;
        }
    }

    /**
     * An implementation which marks its accessor, as an entity using property access declares it, and whose ordinal is
     * <em>derived</em> — there is no field to mark for it.
     */
    static class AccessorMarked implements __SelfReferencingOrdered<AccessorMarked> {

        AccessorMarked parent;

        @__SelfReferencingOrdinal
        @Transient
        public Integer getDisplayOrder() {
            return parent == null ? null : 7;
        }

        @Override
        public AccessorMarked getHierarchyParent() {
            return parent;
        }

        @Override
        public int getHierarchyDepth() {
            return 0;
        }
    }

    /**
     * An implementation whose marked accessor returns a primitive, which an ordered type may: within
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
     * An implementation whose marked accessor holds neither an {@code int} nor an {@link Integer}.
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

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * A nested class for testing {@link __SelfReferencingOrderedUtils#ordinalOf(__SelfReferencingOrdered)}.
     */
    @DisplayName("ordinalOf(instance)")
    @Nested
    class OrdinalOfTest {

        @DisplayName("rejects a null instance")
        @Test
        void __null() {
            assertThatThrownBy(() -> __SelfReferencingOrderedUtils.ordinalOf(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @DisplayName("reads a marked field")
        @Test
        void __field() {
            final var instance = new FieldMarked();
            instance.ordinal = 3;
            assertThat(__SelfReferencingOrderedUtils.ordinalOf(instance)).isEqualTo(3);
            assertThat(instance.getSiblingOrdinal()).isEqualTo(3);
        }

        @DisplayName("reads a marked accessor which derives its value")
        @Test
        void __derivedAccessor() {
            final var child = new AccessorMarked();
            child.parent = new AccessorMarked();
            assertThat(__SelfReferencingOrderedUtils.ordinalOf(child)).isEqualTo(7);
            assertThat(child.getSiblingOrdinal()).isEqualTo(7);
        }

        @DisplayName("reads a marked accessor which returns a primitive")
        @Test
        void __primitive() {
            final var instance = new PrimitiveOrdinalMarked();
            assertThat(__SelfReferencingOrderedUtils.ordinalOf(instance)).isEqualTo(1);
            assertThat(instance.getSiblingOrdinal()).isEqualTo(1);
        }

        @DisplayName("answers null when the marked member holds no value")
        @Test
        void __empty() {
            // an ordinal which was never assigned is not read as 0, and does not fail the read either:
            // @NotNull on getSiblingOrdinal() is what reports it, and a throw here would abort the very
            // validation pass which does the reporting
            assertThat(__SelfReferencingOrderedUtils.ordinalOf(new FieldMarked())).isNull();
            assertThat(__SelfReferencingOrderedUtils.ordinalOf(new AccessorMarked())).isNull();
            assertThat(new FieldMarked().getSiblingOrdinal()).isNull();
        }

        @DisplayName("fails for an ordered type which carries no mark")
        @Test
        void __unmarked() {
            final var instance = new OrderedUnmarked();
            assertThatThrownBy(() -> __SelfReferencingOrderedUtils.ordinalOf(instance))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("no member annotated");
        }

        @DisplayName("fails on a marked method which is not an accessor")
        @Test
        void __nonAccessor() {
            final var instance = new NonAccessorMarked();
            assertThatThrownBy(() -> __SelfReferencingOrderedUtils.ordinalOf(instance))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("is not an accessor");
        }

        @DisplayName("fails on a marked member typed neither int nor Integer")
        @Test
        void __wrongType() {
            final var instance = new WrongTypeOrdinalMarked();
            assertThatThrownBy(() -> __SelfReferencingOrderedUtils.ordinalOf(instance))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("typed neither int nor " + Integer.class.getSimpleName());
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * A nested class for pinning where the ordinal mark may be written, which is what lets an entity keep it next to
     * the mapping its own access type decides.
     */
    @DisplayName("@Target / @Retention of the mark")
    @Nested
    class MarkTest {

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
