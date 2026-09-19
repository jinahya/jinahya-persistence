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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.HashSet;

/**
 * Utilities internal to this package.
 * <p>
 * What lives here is the mechanical part of reading a value out of a member which an annotation points at, with nothing
 * of the mark's own meaning: finding the member, asking what type it holds, and reading it. The rules which say whether
 * a mark is required, or which type it has to hold, belong to whoever owns the mark — see
 * {@link __SelfReferencingUtils}, the first caller.
 * <p>
 * Both of Jakarta Persistence's access types are accounted for throughout: a mark may sit on a field or on an accessor,
 * because the access type belongs to the entity and is not visible from here.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
final class ___Utils {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Finds, in the class tree of the specified type, the single field or accessor annotated with the specified
     * annotation.
     *
     * @param type            the type whose class tree is scanned.
     * @param annotationClass the class of the annotation to look for.
     * @return the annotated member, made accessible; {@code null} when the class tree of the {@code type} carries no
     *         such member.
     * @throws IllegalStateException when the class tree of the {@code type} carries more than one such field, more than
     *                               one such accessor, or an annotated method which is not an accessor.
     * @implNote Fields and methods are scanned alike, because the access type belongs to the annotated class
     *         and is not visible from here. An accessor found alongside a field wins: invoking it goes through whatever
     *         a persistence provider put in front of the property, where reading a field does not, so the accessor is
     *         the read which initializes a lazy proxy rather than answering out of an empty field.
     *         <p>
     *         The climb starts at the type itself — which may well be a provider's proxy subclass — and stops at, but
     *         does not include, {@link Object}.
     *         <p>
     *         Because it starts at the most derived type, an annotated method is seen before the method it overrides,
     *         and the ones above it are skipped by name; a nullary method is identified by its name alone. Without
     *         that, a class which annotates an accessor and a subclass which annotates its override would look like two
     *         separate marks. Bridge and synthetic members are skipped for the same reason: a covariant return
     *         generates them, and they carry the annotations of the method they stand for.
     */
    static @Nullable Member findMember(final Class<?> type, final Class<? extends Annotation> annotationClass) {
        assert type != null;
        assert annotationClass != null;
        Field field = null;
        Method method = null;
        final var names = new HashSet<String>();
        // the type itself may be a provider's proxy subclass, hence the climb starts there
        for (var c = type; c != null && c != Object.class; c = c.getSuperclass()) {
            for (final var f : c.getDeclaredFields()) {
                if (f.isSynthetic() || !f.isAnnotationPresent(annotationClass)) {
                    continue;
                }
                if (field != null) {
                    throw new IllegalStateException(
                            "more than one field annotated with @" + annotationClass.getSimpleName() +
                            " in the class tree of " + type + "; both " + field + " and " + f
                    );
                }
                field = f;
            }
            for (final var m : c.getDeclaredMethods()) {
                if (m.isBridge() || m.isSynthetic() || !m.isAnnotationPresent(annotationClass)) {
                    continue;
                }
                if (m.getParameterCount() > 0 || m.getReturnType() == void.class) {
                    throw new IllegalStateException(
                            "the method annotated with @" + annotationClass.getSimpleName() +
                            " is not an accessor; " + m + "; in the class tree of " + type
                    );
                }
                if (!names.add(m.getName())) {
                    continue; // an override, already seen on a more derived type
                }
                if (method != null) {
                    throw new IllegalStateException(
                            "more than one accessor annotated with @" + annotationClass.getSimpleName() +
                            " in the class tree of " + type + "; both " + method + " and " + m
                    );
                }
                method = m;
            }
        }
        // an accessor wins over a field annotated alongside it; see the implementation note
        final var found = method != null ? (Member) method : field;
        if (found instanceof Method m) {
            m.setAccessible(true);
        } else if (found instanceof Field f) {
            f.setAccessible(true);
        }
        return found;
    }

    /**
     * Returns the type of the value held by the specified member.
     *
     * @param member the member.
     * @return the type of the {@code member}'s value; the type of a field, the return type of a method.
     */
    static Class<?> valueTypeOf(final Member member) {
        assert member != null;
        if (member instanceof Method method) {
            return method.getReturnType();
        }
        return ((Field) member).getType();
    }

    /**
     * Reads the value held by the specified member of the specified instance.
     *
     * @param member   the member to read.
     * @param instance the instance to read from.
     * @return the value of the {@code member} of the {@code instance}.
     * @throws ReflectiveOperationException when the {@code member} can not be read.
     * @implNote An exception thrown by an accessor is rethrown as it is, rather than wrapped: an annotated
     *         accessor which derives the value it holds is running its caller's own code here, and burying its failure
     *         inside a reflection error would hide the only part of the stack which explains it.
     */
    static @Nullable Object valueOf(final Member member, final Object instance) throws ReflectiveOperationException {
        assert member != null;
        assert instance != null;
        if (member instanceof Method method) {
            try {
                return method.invoke(instance);
            } catch (final InvocationTargetException ite) {
                final var cause = ite.getCause();
                if (cause instanceof RuntimeException re) {
                    throw re;
                }
                if (cause instanceof Error e) {
                    throw e;
                }
                throw ite;
            }
        }
        return ((Field) member).get(instance);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance, which is not allowed.
     */
    private ___Utils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
