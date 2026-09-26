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

import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Member;
import java.util.Objects;

/**
 * Utilities for {@link __SelfReferencingOrdered}, reading the ordinal which the interface itself can not hold.
 * <p>
 * This is {@link __SelfReferencingUtils} one layer up. That class reads the parent, which every
 * {@link __SelfReferencing} has; this one reads the ordinal among siblings, which only an entity ordering its siblings
 * has — so its methods are bounded by {@link __SelfReferencingOrdered} and a hierarchy without an order is turned away
 * by the compiler rather than by a {@code null}.
 * <p>
 * Splitting the two apart is the same split the interfaces make, carried through: an order among siblings is laid on
 * top of a hierarchy rather than being part of one, and the code which reads it says so by living somewhere else.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @implNote The member is found by {@link ___Utils#findMember(Class, Class) ___Utils.findMember(type, mark)}
 *         and the result cached in a {@link ClassValue}, for the reasons {@link __SelfReferencingUtils} gives for its
 *         own.
 * @see __SelfReferencingOrdered
 * @see __SelfReferencingOrdinal
 * @see __SelfReferencingUtils
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class __SelfReferencingOrderedUtils {

    /**
     * The member annotated with {@link __SelfReferencingOrdinal @__SelfReferencingOrdinal}, of each type.
     *
     * @implNote The rule decided here is the one {@link __SelfReferencingUtils} decides for the parent:
     *         carrying the mark nowhere fails. Only a type implementing {@link __SelfReferencingOrdered} ever reaches
     *         this cache, and within that interface an ordinal always exists, so a missing mark is a broken
     *         implementation rather than an entity which does not order its siblings. A type which does not order them
     *         implements {@link __SelfReferencing} alone and is not accepted by
     *         {@link #ordinalOf(__SelfReferencingOrdered) ordinalOf(instance)} in the first place.
     *         <p>
     *         Both {@code int} and {@link Integer} are accepted, and which one an entity declares is its own decision.
     *         It is not an empty one: an {@link Integer} member constrained {@code @NotNull} turns a forgotten
     *         assignment into a validation failure before the insert, where an {@code int} member reads {@code 0} and
     *         quietly puts the instance at the head of its siblings.
     */
    private static final ClassValue<Member> ORDINAL_MEMBERS = new ClassValue<>() {
        @Override
        protected Member computeValue(final Class<?> type) {
            assert type != null;
            final var member = ___Utils.findMember(type, __SelfReferencingOrdinal.class);
            if (member == null) {
                throw new IllegalStateException(
                        "no member annotated with @" + __SelfReferencingOrdinal.class.getSimpleName() +
                        " in the class tree of " + type
                );
            }
            final var valueType = ___Utils.valueTypeOf(member);
            if (valueType != int.class && valueType != Integer.class) {
                throw new IllegalStateException(
                        "the member annotated with @" + __SelfReferencingOrdinal.class.getSimpleName() +
                        " is typed neither int nor " + Integer.class.getSimpleName() + "; " + member +
                        "; in the class tree of " + type
                );
            }
            return member;
        }
    };

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns the member annotated with {@link __SelfReferencingOrdinal @__SelfReferencingOrdinal}, in the class tree
     * of the specified type.
     *
     * @param type the type whose class tree is scanned.
     * @return the marked member.
     * @throws IllegalStateException when the class tree of the {@code type} carries no such member, carries more than
     *                               one, or carries one which is typed neither {@code int} nor {@link Integer}.
     * @implNote Package-private, and the cache is what is being shared.
     *         {@link __SelfReferencingOrderedQueryUtils}, which orders a query by the ordinal, takes the member from
     *         here and asks
     *         {@link ___Utils#attributeNameOf(jakarta.persistence.metamodel.EntityType, Member, Class) attributeNameOf}
     *         to map it onto an attribute, rather than rescanning a class tree which has already been scanned.
     */
    static Member ordinalMemberOf(final Class<?> type) {
        assert type != null;
        return ORDINAL_MEMBERS.get(type);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns the ordinal of the specified instance among its siblings, that is, among the instances sharing the same
     * {@link __SelfReferencing#getHierarchyParent() parent}, read from the member annotated with
     * {@link __SelfReferencingOrdinal @__SelfReferencingOrdinal}.
     *
     * @param instance the instance whose ordinal is read.
     * @param <T>      ordered self-referencing type parameter
     * @return the ordinal of the {@code instance}, {@code 0} for the first sibling; {@code null} when the marked member
     *         holds no value, which is an instance whose ordinal has not been assigned.
     * @throws IllegalStateException when the class tree of the {@code instance} carries no member annotated with
     *                               {@link __SelfReferencingOrdinal @__SelfReferencingOrdinal}, carries more than one,
     *                               or carries one which is typed neither {@code int} nor {@link Integer}.
     * @apiNote The parameter is an {@link __SelfReferencingOrdered}, not a {@link __SelfReferencing}, and that
     *         is the whole point of the distinction — the reason this method is here rather than next to
     *         {@link __SelfReferencingUtils#parentOf(__SelfReferencing) parentOf(instance)}. The two absences which
     *         used to answer alike are now separated, and each is reported by whatever can actually see it: a type
     *         which does not order its siblings is turned away here by the compiler, and an instance which orders them
     *         but has not been given its ordinal answers {@code null}, for {@link NotNull @NotNull} to report.
     *         <p>
     *         That second one deliberately does <em>not</em> fail here. Jakarta Validation evaluates the constraints on
     *         {@link __SelfReferencingOrdered#getSiblingOrdinal() getSiblingOrdinal()} by calling it, so a read which
     *         threw on an unassigned ordinal would abort the very validation pass which exists to name the problem —
     *         Hibernate Validator reports {@code HV000090: Unable to access getSiblingOrdinal} and no violation at all.
     *         Reading the member and answering with what is there leaves the judgment to the constraint.
     * @see __SelfReferencingOrdinal
     */
    public static <T extends __SelfReferencingOrdered<T>> @Nullable Integer ordinalOf(final T instance) {
        Objects.requireNonNull(instance, "instance is null");
        final var member = ORDINAL_MEMBERS.get(instance.getClass());
        try {
            return (Integer) ___Utils.valueOf(member, instance);
        } catch (final ReflectiveOperationException roe) {
            throw new RuntimeException(
                    """
                            failed to read the sibling ordinal
                            ; instance: %1$s
                            ; member: %2$s"""
                            .formatted(instance.getClass().getName(), member),
                    roe
            );
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance, which is not allowed.
     */
    private __SelfReferencingOrderedUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
