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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.jspecify.annotations.Nullable;

/**
 * An interface for {@link __SelfReferencing} entities whose siblings are <em>ordered</em>.
 * <p>
 * {@link __SelfReferencing} describes a rooted tree: every instance has a parent and a depth, and the instances sharing
 * a parent form a <em>set</em>. This interface describes an <em>ordered</em> tree, where those same instances form a
 * <em>sequence</em>. That is strictly more structure, and nothing in the base interface implies it — an order among
 * siblings is not a detail of a hierarchy, it is something laid on top of one.
 * <p>
 * Which is why it is a type rather than a value. Whether a hierarchy orders its children is a property of the
 * <em>implementation type</em>, not of any one instance, so it is answered by {@code instanceof} rather than by a
 * {@code null} which every caller has to remember to check. An entity which does not order its siblings simply
 * implements {@link __SelfReferencing} and stops there.
 * <p>
 * Splitting the layer off splits the two absences which a single nullable ordinal used to run together, and hands each
 * to whatever can actually see it. That a hierarchy has no order is a fact about the <em>type</em>, and is now settled
 * by which interface the entity implements. That an instance has not been <em>given</em> its ordinal yet is a fact
 * about the entity's state, like any unset column, and is reported by {@link NotNull @NotNull} on
 * {@link #getSiblingOrdinal()}. Neither is a {@code null} the caller has to interpret.
 * <p>
 * Which is also why the ordinal stays an {@link Integer} rather than becoming a primitive. An {@code int} can not be
 * unassigned: it reads {@code 0}, the head of the sibling group, and an entity which simply forgot to place an instance
 * jumps it in front of every sibling which was placed on purpose. An {@link Integer} reads {@code null}, and the
 * constraint names the property before the insert.
 *
 * <h2>What this promises, and what it does not</h2>
 * It promises that every instance carries a sort key, and that the keys of two <em>siblings</em> may be compared. It
 * does not promise those keys are distinct: two siblings can both answer {@code 3}, and no type can stop them. Where
 * ties must not happen, say so in the schema — a unique constraint over the parent reference and the ordinal — and
 * order by a tiebreak such as the identifier anyway.
 * <p>
 * Nor does it promise anything about <em>how</em> the numbers are maintained. Dense numbering which renumbers a group
 * on insert, sparse numbering which leaves gaps to insert into, who assigns the value of a newly persisted instance —
 * all of that is the entity's policy, and this interface neither asks nor answers.
 *
 * <h2>Two traps worth knowing</h2>
 * The ordinal belongs to the <em>relationship</em>, not to the instance. It says where an instance sits among the
 * children of one particular parent, which is why it lives in the same row as the reference to that parent — and why
 * moving an instance to another parent leaves it meaningless. Reparenting and reassigning the ordinal are one
 * operation, not two.
 * <p>
 * And the value is not globally comparable. Sorting a flat query result by {@link #getSiblingOrdinal()} compiles, looks
 * right, and interleaves instances from unrelated parents. Comparisons are valid within a sibling group; ordering a
 * whole tree means comparing the sequence of ordinals from the root down, not the single value found here.
 *
 * @param <T> self type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __SelfReferencing
 * @see __SelfReferencingOrdinal
 * @see __SelfReferencingOrderedUtils#ordinalOf(__SelfReferencingOrdered)
 */
@SuppressWarnings({
        "java:S114" // Interface names should comply with a naming convention
})
public interface __SelfReferencingOrdered<T extends __SelfReferencingOrdered<T>> extends __SelfReferencing<T> {

    /**
     * Returns the ordinal of this instance among its siblings, that is, among the instances sharing the same
     * {@link #getHierarchyParent() parent}.
     *
     * @return the ordinal of this instance, {@code 0} for the first sibling; {@code null} when this instance has not
     *         been given one, which {@link NotNull @NotNull} reports as a violation rather than this method as a
     *         failure.
     * @throws IllegalStateException when the class tree of this instance carries no member annotated with
     *                               {@link __SelfReferencingOrdinal @__SelfReferencingOrdinal}, carries more than one,
     *                               or carries one which is typed neither {@code int} nor {@link Integer}.
     * @apiNote An implementing entity which maps by property access has to mark its own accessor
     *         {@link Transient @Transient}; an annotation here would not reach it. The {@link NotNull @NotNull} and
     *         {@link PositiveOrZero @PositiveOrZero} constraints, by contrast, are inherited and do apply — which is
     *         how an instance whose ordinal was never assigned is caught, by name, on the next validation pass.
     *         <p>
     *         Note that Jakarta Validation evaluates those constraints by <em>calling</em> this method. An override
     *         which throws for an instance it considers incomplete does not produce a violation; it aborts the
     *         validation pass, and Hibernate Validator reports {@code HV000090: Unable to access getSiblingOrdinal}
     *         with nothing else said. Answer with what is there and let the constraints judge it.
     *         <p>
     *         Note that {@link PositiveOrZero @PositiveOrZero} says only that no ordinal is negative. It does not say
     *         that some sibling holds {@code 0}: a group numbered {@code 5, 9, 12} satisfies it. "{@code 0} is the
     *         first sibling" describes numbering anchored at zero, which is the reading intended here but is the
     *         entity's to keep.
     * @implSpec The default implementation reads, with reflection, the field or accessor annotated with
     *         {@link __SelfReferencingOrdinal} in the class tree of the implementation type. An entity whose own
     *         accessor already answers this question overrides it and never comes here.
     * @see __SelfReferencingOrderedUtils#ordinalOf(__SelfReferencingOrdered)
     */
    @Nullable
    @NotNull
    @PositiveOrZero
    @Transient
    @SuppressWarnings("unchecked")
    default Integer getSiblingOrdinal() {
        return __SelfReferencingOrderedUtils.ordinalOf((T) this);
    }
}
