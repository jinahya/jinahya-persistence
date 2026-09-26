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

import org.jspecify.annotations.Nullable;

import java.lang.reflect.Member;
import java.util.Objects;

/**
 * Utilities for {@link __SelfReferencing}, reading the parent which the interface itself can not hold.
 * <p>
 * An implementing entity declares the parent association as its own member and marks it
 * {@link __SelfReferencingParent @__SelfReferencingParent}. The method here finds that member and reads it, so that an
 * entity whose member is named on its own terms can still answer
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
 *         <p>
 *         The ordinal among siblings is read by {@link __SelfReferencingOrderedUtils} instead, and is there rather than
 *         here for the reason the interfaces are two rather than one: every {@link __SelfReferencing} has a parent,
 *         where only an {@link __SelfReferencingOrdered} has an ordinal.
 * @implNote The member is found by {@link ___Utils#findMember(Class, Class) ___Utils.findMember(type, mark)}
 *         and the result cached in a {@link ClassValue}, computed once per type and safe to share between threads, so
 *         that walking a hierarchy — which calls back into this method for every step — does not rescan a class tree on
 *         every read. A {@code ClassValue} also ties each entry to the class it was computed from, so that neither
 *         outlives the other and a redeployed application does not leak its class loader.
 *         <p>
 *         That the cache is here, rather than in {@link __SelfReferencing} itself, is what this class is for: a field
 *         of an interface is {@code public static final} with no way to say otherwise, so a cache declared there would
 *         be inherited by every implementing type, reachable as {@code Category.PARENT_MEMBERS}, and would stand in the
 *         published contract next to the constants which are meant to be read. An interface can hide a method; it can
 *         not hide a field.
 * @see __SelfReferencing
 * @see __SelfReferencingParent
 * @see __SelfReferencingOrderedUtils
 * @see __SelfReferencingQueryUtils
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

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns the member annotated with {@link __SelfReferencingParent @__SelfReferencingParent}, in the class tree of
     * the specified type.
     *
     * @param type the type whose class tree is scanned.
     * @return the marked member.
     * @throws IllegalStateException when the class tree of the {@code type} carries no such member, or carries more
     *                               than one.
     * @implNote Package-private, and the cache is what is being shared: {@link __SelfReferencingQueryUtils}
     *         maps the marked member onto a persistent attribute, and has no business rescanning a class tree which has
     *         already been scanned here.
     */
    static Member parentMemberOf(final Class<?> type) {
        assert type != null;
        return PARENT_MEMBERS.get(type);
    }

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

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance, which is not allowed.
     */
    private __SelfReferencingUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
