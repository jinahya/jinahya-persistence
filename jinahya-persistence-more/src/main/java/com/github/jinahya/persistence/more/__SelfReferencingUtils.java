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
 * Utilities for {@link __SelfReferencing}, reading the values which the interface itself can not hold.
 * <p>
 * An implementing entity declares the parent association, and — when it implements {@link __SelfReferencingOrdered} —
 * an ordinal among siblings, as its own members, and marks them
 * {@link __SelfReferencingParent @__SelfReferencingParent} and
 * {@link __SelfReferencingOrdinal @__SelfReferencingOrdinal}. The methods here find those members and read them, so
 * that an entity whose members are named on its own terms can still answer
 * {@link __SelfReferencing#getHierarchyParent() getHierarchyParent()}:
 * {@snippet lang = "java":
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @Nullable
 * @Transient
 * @Override public Category getHierarchyParent() { return __SelfReferencingUtils.parentOf(this); }}
 *         <p>
 *         This is the back door, not the front one. An entity which names its member so that its own accessor already
 *         implements {@code getHierarchyParent()} never comes here, and is better off for it.
 * @implNote The members are found by {@link ___Utils#findMember(Class, Class) ___Utils.findMember(type, mark)}
 *         and the results cached in {@link ClassValue}s, computed once per type and safe to share between threads, so
 *         that walking a hierarchy — which calls back into these methods for every step — does not rescan a class tree
 *         on every read. A {@code ClassValue} also ties each entry to the class it was computed from, so that neither
 *         outlives the other and a redeployed application does not leak its class loader.
 *         <p>
 *         That these caches are here, rather than in {@link __SelfReferencing} itself, is what this class is for: a
 *         field of an interface is {@code public static final} with no way to say otherwise, so a cache declared there
 *         would be inherited by every implementing type, reachable as {@code Category.PARENT_MEMBERS}, and would stand
 *         in the published contract next to the constants which are meant to be read. An interface can hide a method;
 *         it can not hide a field.
 * @see __SelfReferencing
 * @see __SelfReferencingOrdered
 * @see __SelfReferencingParent
 * @see __SelfReferencingOrdinal
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class __SelfReferencingUtils {

    /**
     * The member annotated with {@link __SelfReferencingParent @__SelfReferencingParent}, of each type.
     *
     * @implNote The lookup itself is {@link ___Utils#findMember(Class, Class) ___Utils.findMember(type, mark)},
     *         which accepts the mark on a field or on an accessor and knows nothing of what it means. What is decided
     *         here is the rule: carrying the mark nowhere fails, because a missing mark is a broken implementation, not
     *         an entity without a parent.
     */
    private static final ClassValue<Member> PARENT_MEMBERS = new ClassValue<>() {
        @Override
        protected Member computeValue(final Class<?> type) {
            assert type != null;
            final var member = ___Utils.findMember(type, __SelfReferencingParent.class);
            if (member == null) {
                throw new IllegalStateException(
                        "no member annotated with @" + __SelfReferencingParent.class.getSimpleName() +
                        " in the class tree of " + type
                );
            }
            return member;
        }
    };

    /**
     * The member annotated with {@link __SelfReferencingOrdinal @__SelfReferencingOrdinal}, of each type.
     *
     * @implNote The rule decided here is the parent's rule: carrying the mark nowhere fails. Only a type
     *         implementing {@link __SelfReferencingOrdered} ever reaches this cache, and within that interface an
     *         ordinal always exists, so a missing mark is a broken implementation rather than an entity which does not
     *         order its siblings. A type which does not order them implements {@link __SelfReferencing} alone and is
     *         not accepted by {@link #ordinalOf(__SelfReferencingOrdered) ordinalOf(instance)} in the first place.
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
     * Returns the parent of the specified instance, read from the member annotated with
     * {@link __SelfReferencingParent @__SelfReferencingParent}.
     *
     * @param instance the instance whose parent is read.
     * @param <T>      self-referencing type parameter
     * @return the parent of the {@code instance}; {@code null} when the {@code instance} is a root.
     * @throws IllegalStateException when the class tree of the {@code instance} carries no member annotated with
     *                               {@link __SelfReferencingParent @__SelfReferencingParent}, or carries more than
     *                               one.
     * @apiNote The member is read directly, and the name it is declared under is never looked at.
     *         <p>
     *         Which member carries the mark decides one thing beyond where it sits. A marked <em>field</em> is read as
     *         a field, which does not go through the accessor a provider generates, hence does not trigger the
     *         initialization which would fetch the parent: an <em>uninitialized proxy</em> answers {@code null} here,
     *         as a value rather than as an exception, indistinguishable from a root. A marked <em>accessor</em> is
     *         invoked, and does not have that problem. Neither does an entity whose own accessor implements
     *         {@link __SelfReferencing#getHierarchyParent() getHierarchyParent()} directly, which remains the path to
     *         prefer.
     * @see __SelfReferencingParent
     */
    public static <T extends __SelfReferencing<T>> @Nullable T parentOf(final T instance) {
        Objects.requireNonNull(instance, "instance is null");
        final var member = PARENT_MEMBERS.get(instance.getClass());
        try {
            @SuppressWarnings({"unchecked"})
            final var parent = (T) ___Utils.valueOf(member, instance);
            return parent;
        } catch (final ReflectiveOperationException roe) {
            throw new RuntimeException(
                    """
                            failed to read the parent
                            ; instance: %1$s
                            ; member: %2$s"""
                            .formatted(instance.getClass().getName(), member),
                    roe
            );
        }
    }

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
     * @apiNote The parameter is an {@link __SelfReferencingOrdered}, not a {@link __SelfReferencing}, and that is
     *         the whole point of the distinction. The two absences which used to answer alike are now separated, and
     *         each is reported by whatever can actually see it: a type which does not order its siblings is turned
     *         away here by the compiler, and an instance which orders them but has not been given its ordinal answers
     *         {@code null}, for {@link NotNull @NotNull} to report.
     *         <p>
     *         That second one deliberately does <em>not</em> fail here. Jakarta Validation evaluates the constraints
     *         on {@link __SelfReferencingOrdered#getSiblingOrdinal() getSiblingOrdinal()} by calling it, so a read
     *         which threw on an unassigned ordinal would abort the very validation pass which exists to name the
     *         problem — Hibernate Validator reports {@code HV000090: Unable to access getSiblingOrdinal} and no
     *         violation at all. Reading the member and answering with what is there leaves the judgment to the
     *         constraint.
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
    private __SelfReferencingUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
