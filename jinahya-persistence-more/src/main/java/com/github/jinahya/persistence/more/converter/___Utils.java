package com.github.jinahya.persistence.more.converter;

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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Utilities internal to this package.
 * <p>
 * What lives here is the mechanical part of reading a column through a type's own static factory: finding the method,
 * calling it, and letting what it throws through. Which factory to look for, and what to make of its absence, belongs
 * to the caller — see {@link __TemporalAmountStringAttributeConverter} and
 * {@link __TemporalAccessorStringAttributeConverter}, the two callers, which look for {@code parse(CharSequence)} and
 * {@code from(TemporalAccessor)} respectively.
 * <p>
 * Neither factory is anything the Jakarta Persistence or {@code java.time} interfaces declare; both are a naming
 * convention {@code java.time} keeps, which is why this is reflection rather than a method reference.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
final class ___Utils {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Finds, on the specified class, the {@code public static} factory method of the specified name which takes a
     * single argument of the specified type and returns the class itself.
     *
     * @param clazz         the class to look at.
     * @param name          the name of the method, e.g. {@code "parse"} or {@code "from"}.
     * @param parameterType the type of the method's single parameter.
     * @return the method; {@code null} when the {@code clazz} declares none which qualifies.
     * @implNote The return type has to be the {@code clazz} itself or a subtype of it, so that
     *         {@link #invokeStaticFactory(Method, Class, Object) invoking} it can not fail the cast; a method which
     *         merely shares the name and parameter is not accepted.
     */
    @Nullable
    static Method findStaticFactory(final Class<?> clazz, final String name, final Class<?> parameterType) {
        assert clazz != null;
        assert name != null;
        assert parameterType != null;
        final Method method;
        try {
            method = clazz.getMethod(name, parameterType);
        } catch (final NoSuchMethodException nsme) {
            return null;
        }
        if (!Modifier.isStatic(method.getModifiers()) || !clazz.isAssignableFrom(method.getReturnType())) {
            return null;
        }
        return method;
    }

    /**
     * Invokes the specified static factory with the specified argument, and returns what it produced.
     *
     * @param method      the method, from {@link #findStaticFactory(Class, String, Class)}.
     * @param resultClass the class to cast the result to.
     * @param argument    the single argument to invoke the {@code method} with.
     * @param <X>         result type parameter
     * @return what the {@code method} returned; {@code null} when it returned {@code null}.
     * @implSpec Whatever the factory itself throws is thrown from here unchanged: a
     *         {@link java.time.format.DateTimeParseException DateTimeParseException} from a column which does not hold
     *         the expected form is the useful exception, and burying it in an {@link InvocationTargetException} would
     *         hide it behind the fact that this package used reflection.
     */
    @Nullable
    static <X> X invokeStaticFactory(final Method method, final Class<X> resultClass,
                                     final @Nullable Object argument) {
        assert method != null;
        assert resultClass != null;
        try {
            return resultClass.cast(method.invoke(null, argument));
        } catch (final IllegalAccessException iae) {
            throw new IllegalStateException("failed to invoke " + method, iae);
        } catch (final InvocationTargetException ite) {
            final var cause = ite.getCause();
            if (cause instanceof RuntimeException re) {
                throw re;
            }
            if (cause instanceof Error error) {
                throw error;
            }
            throw new IllegalStateException("failed to invoke " + method, cause);
        }
    }

    /**
     * Returns an exception for a converter whose attribute class declares no static factory to read a column with.
     *
     * @param attributeClass the class of the entity attribute which declares no such factory.
     * @param signature      the signature which was looked for, e.g. {@code "parse(CharSequence)"}.
     * @param converter      the converter which can not read a column without it.
     * @return a new exception, for the caller to throw.
     * @apiNote Returned rather than thrown, so that the {@code throw} stays at the point it applies to and the
     *         compiler can see that the method ends there.
     */
    static UnsupportedOperationException noStaticFactory(final Class<?> attributeClass, final String signature,
                                                         final Object converter) {
        assert attributeClass != null;
        assert signature != null;
        assert converter != null;
        return new UnsupportedOperationException(
                attributeClass + " declares no public static " + signature + "; " +
                "override convertToEntityAttribute(String) in " + converter.getClass()
        );
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance, which is not allowed.
     */
    private ___Utils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
