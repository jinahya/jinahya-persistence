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
 * A class for testing {@link __SelfReferencingUtils}, pinning which member the parent is read from.
 * <p>
 * Which member the <em>ordinal</em> is read from is {@link __SelfReferencingOrderedUtils_Test}'s subject, as the
 * ordinal itself belongs to {@link __SelfReferencingOrdered} rather than to {@link __SelfReferencing}.
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
     * An implementation which marks its field, as an entity using field access declares it.
     */
    static class FieldMarked implements __SelfReferencing<FieldMarked> {

        @__SelfReferencingParent
        FieldMarked parent;

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
     * An implementation which marks its accessor, as an entity using property access declares it.
     */
    static class AccessorMarked implements __SelfReferencing<AccessorMarked> {

        AccessorMarked parent;

        @__SelfReferencingParent
        public AccessorMarked getParent() {
            return parent;
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
     * A nested class for pinning where the parent mark may be written, which is what lets an entity keep it next to the
     * mapping its own access type decides.
     */
    @DisplayName("@Target / @Retention of the mark")
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
    }
}
