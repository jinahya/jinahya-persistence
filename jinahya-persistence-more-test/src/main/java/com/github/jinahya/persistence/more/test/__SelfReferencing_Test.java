package com.github.jinahya.persistence.more.test;

/*-
 * #%L
 * jinahya-persistence-more-test
 * %%
 * Copyright (C) 2024 - 2025 Jinahya, Inc.
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

import com.github.jinahya.persistence.more.__SelfReferencing;
import jakarta.persistence.Transient;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * An abstract base class for testing {@link __SelfReferencing} implementations.
 * <p>
 * Everything checked here is checked through the interface, never through the mechanism behind it. That is deliberate:
 * an entity may answer {@link __SelfReferencing#getHierarchyParent() getHierarchyParent()} from its own accessor, or
 * delegate to {@link com.github.jinahya.persistence.more.__SelfReferencingUtils#parentOf(__SelfReferencing)
 * __SelfReferencingUtils.parentOf(instance)} and let the
 * {@link com.github.jinahya.persistence.more.__SelfReferencingParent @__SelfReferencingParent} mark find the member.
 * Both are supported, only one of them can fail for a reason the compiler cannot see, and calling the interface method
 * covers both — a missing mark, a duplicated one, or a member the scan cannot read surfaces here as the
 * {@link IllegalStateException} it is.
 *
 * @param <T> self-referencing type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __SelfReferencingOrdered_Test
 */
@SuppressWarnings({
        "java:S100", // Method names should comply with a naming convention
        "java:S101"  // Class names should comply with a naming convention
})
public abstract class __SelfReferencing_Test<T extends __SelfReferencing<T>> {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance for testing the specified entity class.
     *
     * @param entityClass the entity class to test.
     */
    protected __SelfReferencing_Test(final Class<T> entityClass) {
        super();
        this.entityClass = Objects.requireNonNull(entityClass, "entityClass is null");
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Verifies that the parent of a fresh instance can be read at all.
     *
     * @implNote This is the assertion which pays for this class. An entity which implements
     *         {@link __SelfReferencing#getHierarchyParent() getHierarchyParent()} by delegating to
     *         {@link com.github.jinahya.persistence.more.__SelfReferencingUtils#parentOf(__SelfReferencing)
     *         parentOf(instance)} carries the parent association on a member marked
     *         {@link com.github.jinahya.persistence.more.__SelfReferencingParent @__SelfReferencingParent}, and
     *         nothing checks that the mark is there, is there once, and sits on something readable until the first
     *         call — which, without this test, is a call made by the application.
     */
    @DisplayName("the parent of a new instance can be read")
    @Test
    protected void _DoesNotThrow_GetHierarchyParent() {
        final var instance = newEntityInstance();
        assertDoesNotThrow(
                instance::getHierarchyParent,
                () -> "failed to read the hierarchy parent of an instance of " + entityClass
        );
    }

    /**
     * Verifies that the depth of a fresh instance is not negative, as {@code @PositiveOrZero} on the interface says.
     */
    @DisplayName("the depth of a new instance is zero or positive")
    @Test
    protected void _PositiveOrZero_GetHierarchyDepth() {
        final var instance = newEntityInstance();
        final var depth = assertDoesNotThrow(
                instance::getHierarchyDepth,
                () -> "failed to read the hierarchy depth of an instance of " + entityClass
        );
        assertTrue(depth >= 0, () -> "negative hierarchy depth of an instance of " + entityClass + ": " + depth);
    }

    /**
     * Verifies that an instance which has no parent reports itself at the root.
     *
     * @implNote The interface documents the two answers as one fact said twice — a {@code null} parent
     *         <em>is</em> a root, and a root is at depth {@code 0}. An entity which computes the two separately can
     *         let them drift, and this is where that shows.
     */
    @DisplayName("an instance with no parent is at depth zero")
    @Test
    protected void _Zero_HierarchyDepthOfRoot() {
        final var instance = newEntityInstance();
        if (instance.getHierarchyParent() != null) {
            return;
        }
        assertEquals(
                0,
                instance.getHierarchyDepth(),
                () -> "non-zero hierarchy depth of a parentless instance of " + entityClass
        );
    }

    /**
     * Verifies that the accessors this entity declares itself are marked {@link Transient @Transient}, for an entity
     * which {@link #mapsByPropertyAccess() maps by property access}.
     *
     * @implNote Jakarta Persistence reads mapping annotations from the entity class and its
     *         {@code @MappedSuperclass}es; an implemented <em>interface</em> is not part of the mapping, so the
     *         {@link Transient @Transient} which {@link __SelfReferencing} carries does not reach an implementation's
     *         own accessor. Under property access that omission makes the provider treat the accessor as a persistent
     *         property and look for a column which does not exist.
     */
    @DisplayName("an entity mapping by property access marks its own accessors @Transient")
    @Test
    protected void _Transient_OwnAccessors() {
        if (!mapsByPropertyAccess()) {
            return;
        }
        assertTransientWhenDeclared("getHierarchyParent");
        assertTransientWhenDeclared("getHierarchyDepth");
    }

    /**
     * Asserts that the named no-argument accessor, where the entity class tree declares one, carries
     * {@link Transient @Transient}.
     *
     * @param name the name of the accessor.
     */
    protected final void assertTransientWhenDeclared(final String name) {
        final var declared = findDeclaredAccessor(name);
        if (declared == null) {
            return;
        }
        assertNotNull(
                declared.getAnnotation(Transient.class),
                () -> "the accessor " + declared + " carries no @Transient, and the one on the interface does not"
                      + " reach it; an entity mapping by property access has to declare it here"
        );
    }

    /**
     * Returns the most derived no-argument method of the specified name, declared in the class tree of the entity
     * class.
     *
     * @param name the name of the method.
     * @return the method; {@code null} when the entity class tree declares none, which is an entity inheriting the
     *         accessor from elsewhere.
     */
    private @Nullable Method findDeclaredAccessor(final String name) {
        for (Class<?> c = entityClass; c != null && c != Object.class; c = c.getSuperclass()) {
            try {
                return c.getDeclaredMethod(name);
            } catch (final NoSuchMethodException nsme) {
                // keep walking up; an entity may inherit the accessor from a mapped superclass
            }
        }
        return null;
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns whether the entity under test maps its attributes by property access, that is, whether the provider
     * reads its accessors rather than its fields.
     *
     * @return {@code true} when the entity maps by property access; {@code false} otherwise.
     * @implSpec The default implementation returns {@code false}, which is field access — the more common mapping,
     *           and the one for which the {@link Transient @Transient} question does not arise. An entity which puts
     *           its {@code @Id} on an accessor overrides this method.
     */
    protected boolean mapsByPropertyAccess() {
        return false;
    }

    /**
     * Creates a new instance of {@link #entityClass}.
     *
     * @return a new instance of {@link #entityClass}.
     * @implSpec The default implementation invokes the no-argument constructor which Jakarta Persistence requires
     *           every entity to declare. An entity whose instances are not usable until something is set overrides
     *           this method and returns one which is.
     */
    protected T newEntityInstance() {
        return ___Utils.newInstance(entityClass);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The entity class to test.
     */
    protected final Class<T> entityClass;
}
