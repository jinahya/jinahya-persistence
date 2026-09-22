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

import com.github.jinahya.persistence.more.__SelfReferencingOrdered;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * An abstract base class for testing {@link __SelfReferencingOrdered} implementations.
 * <p>
 * Beyond what {@link __SelfReferencing_Test} checks of the hierarchy itself, this class reads the sibling ordinal —
 * which is where the reflective back door is load-bearing, because
 * {@link __SelfReferencingOrdered#getSiblingOrdinal() getSiblingOrdinal()} has a <em>default</em> implementation. An
 * entity which does not override it carries the value on a member marked
 * {@link com.github.jinahya.persistence.more.__SelfReferencingOrdinal @__SelfReferencingOrdinal}, and the mark being
 * absent, duplicated, or on a member typed neither {@code int} nor {@link Integer} is not a compile error.
 *
 * @param <T> ordered self-referencing type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __SelfReferencing_Test
 */
@SuppressWarnings({
        "java:S100", // Method names should comply with a naming convention
        "java:S101"  // Class names should comply with a naming convention
})
public abstract class __SelfReferencingOrdered_Test<T extends __SelfReferencingOrdered<T>>
        extends __SelfReferencing_Test<T> {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance for testing the specified entity class.
     *
     * @param entityClass the entity class to test.
     */
    protected __SelfReferencingOrdered_Test(final Class<T> entityClass) {
        super(entityClass);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Verifies that the sibling ordinal of a fresh instance is answered rather than thrown for.
     *
     * @implNote An instance whose ordinal has not been assigned has to answer {@code null}, and the interface says
     *         at length why: Jakarta Validation evaluates the {@code @NotNull} on
     *         {@link __SelfReferencingOrdered#getSiblingOrdinal() getSiblingOrdinal()} by <em>calling</em> it, so an
     *         override which throws for an instance it considers incomplete does not produce a violation — it aborts
     *         the validation pass, and Hibernate Validator reports {@code HV000090: Unable to access
     *         getSiblingOrdinal} and nothing else. This is that mistake, caught in a unit test rather than in the
     *         log of a failed insert. The same call is what verifies the
     *         {@link com.github.jinahya.persistence.more.__SelfReferencingOrdinal @__SelfReferencingOrdinal} mark for
     *         an entity which keeps the default implementation.
     */
    @DisplayName("the sibling ordinal of a new instance is answered, not thrown for")
    @Test
    protected void _DoesNotThrow_GetSiblingOrdinal() {
        final var instance = newEntityInstance();
        assertDoesNotThrow(
                instance::getSiblingOrdinal,
                () -> "failed to read the sibling ordinal of an instance of " + entityClass
                      + "; an unassigned ordinal is answered as null, for @NotNull to report"
        );
    }

    /**
     * Verifies that an assigned sibling ordinal is not negative, as {@code @PositiveOrZero} on the interface says.
     */
    @DisplayName("an assigned sibling ordinal is zero or positive")
    @Test
    protected void _PositiveOrZero_GetSiblingOrdinal() {
        final var instance = newEntityInstance();
        final var ordinal = instance.getSiblingOrdinal();
        if (ordinal == null) {
            return;
        }
        assertTrue(
                ordinal >= 0,
                () -> "negative sibling ordinal of an instance of " + entityClass + ": " + ordinal
        );
    }

    /**
     * {@inheritDoc}
     *
     * @implNote Adds {@code getSiblingOrdinal} to the accessors checked, for the same reason the two in the
     *         superclass are checked.
     */
    @DisplayName("an entity mapping by property access marks its own accessors @Transient")
    @Test
    @Override
    protected void _Transient_OwnAccessors() {
        super._Transient_OwnAccessors();
        if (!mapsByPropertyAccess()) {
            return;
        }
        assertTransientWhenDeclared("getSiblingOrdinal");
    }
}
