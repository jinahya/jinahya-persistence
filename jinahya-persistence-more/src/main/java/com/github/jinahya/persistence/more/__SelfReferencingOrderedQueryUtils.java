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

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Selects of an {@link __SelfReferencingOrdered} hierarchy what {@link __SelfReferencingQueryUtils} selects of a
 * {@link __SelfReferencing} one, with the ordering already decided.
 * <p>
 * The methods there take an ordering because a plain hierarchy has none: the children of an instance are a set, and
 * nothing in {@link __SelfReferencing} says which of them comes first. An {@link __SelfReferencingOrdered} hierarchy
 * does say, and says it in a member the {@link __SelfReferencingOrdinal @__SelfReferencingOrdinal} mark locates — so
 * there is nothing for a caller to pass, and the parameter which would carry it is gone:
 * {@snippet lang = "java":
 * // the roots, in the order the entity orders its siblings in
 * __SelfReferencingOrderedQueryUtils.selectRoots(entityManager, Category.class, Stream::toList);}
 * <p>
 * The roots of a hierarchy are siblings of one another — each of them has no parent, so they share one — which is why
 * the sibling ordinal is what orders them, rather than being a thing that only applies further down.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __SelfReferencingOrdered
 * @see __SelfReferencingOrdinal
 * @see __SelfReferencingQueryUtils
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class __SelfReferencingOrderedQueryUtils {

    /**
     * Returns the ordering of the specified entity class: its sibling ordinal, then its identifier.
     *
     * @implNote The identifier is a tiebreak rather than a second key anybody asked for. Nothing makes the
     *         ordinals of a sibling group distinct — {@link __SelfReferencingOrdered} says as much, and says to order
     *         by a tiebreak anyway — so without one, two roots sharing an ordinal come back in whichever order the
     *         database felt like, and not necessarily the same one twice running. A composite identifier has no single
     *         term to order by and simply goes without.
     */
    private static <T extends __SelfReferencingOrdered<T>> BiFunction<CriteriaBuilder, Root<T>, List<Order>> orderingOf(
            final EntityManager entityManager, final Class<T> entityClass) {
        final var entityType = entityManager.getMetamodel().entity(entityClass);
        final var ordinalAttributeName = ___Utils.attributeNameOf(
                entityType, __SelfReferencingOrderedUtils.ordinalMemberOf(entityClass), __SelfReferencingOrdinal.class
        );
        final var idAttributeName = ___Utils.idAttributeNameOf(entityType);
        return (builder, from) -> {
            final var orders = new ArrayList<Order>(2);
            orders.add(builder.asc(from.get(ordinalAttributeName)));
            if (idAttributeName != null) {
                orders.add(builder.asc(from.get(idAttributeName)));
            }
            return orders;
        };
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Applies the specified function to a stream of the roots of the specified entity class, ordered by their sibling
     * ordinals, and returns the result.
     *
     * @param entityManager the entity manager to select with.
     * @param entityClass   the entity class to select.
     * @param mapping       the function applied to the stream of selected roots.
     * @param <T>           ordered self-referencing type parameter
     * @param <R>           result type parameter
     * @return the result of applying the {@code mapping} to the stream of roots.
     * @throws IllegalArgumentException when the {@code entityClass} is not an entity of the {@code entityManager}'s
     *                                  persistence unit.
     * @throws IllegalStateException    when the class tree of the {@code entityClass} carries no member annotated with
     *                                  {@link __SelfReferencingParent @__SelfReferencingParent} or none annotated with
     *                                  {@link __SelfReferencingOrdinal @__SelfReferencingOrdinal}, carries more than
     *                                  one of either, carries an ordinal typed neither {@code int} nor {@link Integer},
     *                                  or carries one which no persistent attribute maps.
     * @apiNote The stream lives no longer than the call, and whatever shape the roots are wanted in is said in
     *         the {@code mapping}; see
     *         {@link __SelfReferencingQueryUtils#selectRoots(EntityManager, Class, BiFunction, Function) the method
     *         this one hands over to}.
     *         {@snippet lang = "java":
     *                         // as a list, in sibling order
     *                         final List<Category> roots =
     *                                 __SelfReferencingOrderedQueryUtils.selectRoots(entityManager, Category.class, Stream::toList);
     *
     *                         // as the first one, if there is one
     *                         final Optional<Category> first =
     *         __SelfReferencingOrderedQueryUtils.selectRoots(entityManager, Category.class, Stream::findFirst);}
     */
    public static <T extends __SelfReferencingOrdered<T>, R> R selectRoots(
            final EntityManager entityManager, final Class<T> entityClass,
            final Function<? super Stream<T>, ? extends R> mapping) {
        Objects.requireNonNull(entityManager, "entityManager is null");
        Objects.requireNonNull(entityClass, "entityClass is null");
        Objects.requireNonNull(mapping, "mapping is null");
        return __SelfReferencingQueryUtils.selectRoots(
                entityManager,
                entityClass,
                orderingOf(entityManager, entityClass),
                mapping
        );
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance, which is not allowed.
     */
    private __SelfReferencingOrderedQueryUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
