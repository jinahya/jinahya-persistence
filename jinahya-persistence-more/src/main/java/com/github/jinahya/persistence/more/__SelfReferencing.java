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
import jakarta.validation.constraints.PositiveOrZero;
import org.jspecify.annotations.Nullable;

/**
 * An interface for entities which reference other instances of their own type, forming a hierarchy.
 * <p>
 * An instance implementing this interface exposes its position within that hierarchy: the instance it directly
 * references as its parent, and how far it sits from the root. A root instance has no parent and a depth of {@code 0};
 * every other instance has a parent whose depth is exactly one less than its own.
 * <p>
 * These methods describe a <em>view</em> of the hierarchy, and are not meant to be mapped, on their own, to persistent
 * attributes. An implementing entity decides how the relationship is actually stored — typically a self-referencing
 * many-to-one association for the parent — and how the depth is derived.
 * <p>
 * <strong>That is a convention here, not something this interface can enforce.</strong> Jakarta Persistence reads
 * mapping annotations from the entity class and its {@code @MappedSuperclass}es; an implemented <em>interface</em> is
 * not part of the mapping. So an implementing entity which uses property access, and declares its own
 * {@code getHierarchyDepth()}, has to annotate that accessor {@link Transient @Transient} itself. Annotating the
 * methods here would do nothing for it, which is why they are not annotated.
 * <p>
 * Jakarta Validation behaves differently: it <em>does</em> inherit constraint declarations from implemented interfaces,
 * so the {@link PositiveOrZero @PositiveOrZero} below genuinely constrains every implementation.
 * <p>
 * An entity which names its parent member on its own terms, rather than so that its accessor lines up with
 * {@link #getHierarchyParent()}, can mark it {@link __SelfReferencingParent @__SelfReferencingParent} — the field under
 * field access, the accessor under property access — and implement the interface method by delegating to
 * {@link __SelfReferencingUtils#parentOf(__SelfReferencing) parentOf(this)}, which finds the member by that mark.
 * <p>
 * Nothing here says anything about the <em>order</em> of the instances sharing a parent, because a hierarchy does not
 * have one: the children of an instance are a set. An entity whose children are a sequence implements
 * {@link __SelfReferencingOrdered} instead, which adds the ordinal and the mark that locates it.
 *
 * @param <T> self type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __SelfReferencingParent
 * @see __SelfReferencingOrdered
 * @see __SelfReferencingUtils
 */
@SuppressWarnings({
        "java:S114" // Interface names should comply with a naming convention
})
public interface __SelfReferencing<T extends __SelfReferencing<T>> {

    /**
     * Returns the instance which this instance directly references as its parent in the hierarchy.
     *
     * @return the parent of this instance; {@code null} when this instance is a root.
     * @apiNote An implementing entity which maps by property access has to mark its own accessor
     *         {@link Transient @Transient}; an annotation here would not reach it.
     * @see __SelfReferencingUtils#parentOf(__SelfReferencing)
     */
    @Nullable
    T getHierarchyParent();

    /**
     * Returns the distance between this instance and the root of its hierarchy.
     *
     * @return the number of parents between this instance and the root; {@code 0} when this instance is a root.
     * @apiNote An implementing entity which maps by property access has to mark its own accessor
     *         {@link Transient @Transient}; an annotation here would not reach it. The
     *         {@link PositiveOrZero @PositiveOrZero} constraint, by contrast, is inherited and does apply.
     * @see #getHierarchyParent()
     */
    @PositiveOrZero
    int getHierarchyDepth();
}
