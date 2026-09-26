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

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Utilities for selecting instances of a {@link __SelfReferencing} hierarchy, with portable queries.
 * <p>
 * What makes them worth having is not the SQL, which is ordinary, but where the column names come from. A
 * self-referencing entity stores its parent in a member the {@link __SelfReferencingParent @__SelfReferencingParent}
 * mark locates, named on the entity's own terms, so a query written by hand has to agree on that name and breaks,
 * silently, the day it changes. The methods here read the mark and ask the metamodel which attribute maps it.
 * <p>
 * Nothing here is specific to a database: each method builds one criteria query and runs it.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __SelfReferencing
 * @see __SelfReferencingParent
 * @see __SelfReferencingOrderedQueryUtils
 * @see __SelfReferencingUtils
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class __SelfReferencingQueryUtils {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Applies the specified function to a stream of the roots of the specified entity class, ordered by what the
     * specified function builds, and returns the result.
     *
     * @param entityManager the entity manager to select with.
     * @param entityClass   the entity class to select.
     * @param ordering      a function invoked with the builder and the root of the query, answering the orders to order
     *                      it by; an empty list for no ordering.
     * @param mapping       the function applied to the stream of selected roots.
     * @param <T>           self-referencing type parameter
     * @param <R>           result type parameter
     * @return the result of applying the {@code mapping} to the stream of roots.
     * @throws IllegalArgumentException when the {@code entityClass} is not an entity of the {@code entityManager}'s
     *                                  persistence unit.
     * @throws IllegalStateException    when the class tree of the {@code entityClass} carries no member annotated with
     *                                  {@link __SelfReferencingParent @__SelfReferencingParent}, carries more than one,
     *                                  or carries one which no persistent attribute maps.
     * @apiNote A root is an instance whose parent reference holds no value, so this is one query and no walk.
     *         Which attribute that reference is stored in is read from the mark rather than agreed by name, which is
     *         the whole of what this method adds over writing the query by hand.
     *         <p>
     *         The stream lives no longer than the call: it is handed to the {@code mapping} and is spent when that
     *         returns, which is what lets a provider hold a cursor open for it and close it afterwards. Answering with
     *         the stream itself would leave that to the caller and to no-one in particular, which is why there is no
     *         overload which does — whatever shape the roots are wanted in is said in the {@code mapping}:
     *         {@snippet lang = "java":
     *         final var byName = (BiFunction<CriteriaBuilder, Root<Category>, List<Order>>)
     *                 (b, r) -> List.of(b.asc(r.get("name")));
     *
     *         // as a list
     *         final List<Category> roots = __SelfReferencingQueryUtils.selectRoots(
     *                 entityManager, Category.class, byName, Stream::toList
     *         );
     *
     *         // as a count, without holding any of them
     *         final long count = __SelfReferencingQueryUtils.selectRoots(
     *                 entityManager, Category.class, byName, Stream::count
     *         );
     *
     *         // as whatever else the caller is really after
     *         final Map<String, Category> indexed = __SelfReferencingQueryUtils.selectRoots(
     *                 entityManager, Category.class, byName,
     *                 s -> s.collect(Collectors.toMap(Category::getName, Function.identity()))
     *         );}
     *         <p>
     *         Note that the result type is inferred from what the call is assigned to, and a method which is overloaded
     *         on its parameter — AssertJ's {@code assertThat} being the one to trip over — gives the compiler nothing
     *         to infer from. A local variable, as above, settles it.
     *         <p>
     *         The ordering is a function rather than a list of {@link Order}s because an order belongs to the root it
     *         was built from, and the root of the query is made here.
     */
    public static <T extends __SelfReferencing<T>, R> R selectRoots(
            final EntityManager entityManager, final Class<T> entityClass,
            final BiFunction<CriteriaBuilder, Root<T>, List<Order>> ordering,
            final Function<? super Stream<T>, ? extends R> mapping) {
        Objects.requireNonNull(entityManager, "entityManager is null");
        Objects.requireNonNull(entityClass, "entityClass is null");
        Objects.requireNonNull(ordering, "ordering is null");
        Objects.requireNonNull(mapping, "mapping is null");
        final var entityType = entityManager.getMetamodel().entity(entityClass);
        final var parentAttributeName = ___Utils.attributeNameOf(
                entityType, __SelfReferencingUtils.parentMemberOf(entityClass), __SelfReferencingParent.class
        );
        final var builder = entityManager.getCriteriaBuilder();
        final var criteriaQuery = builder.createQuery(entityClass);
        final var from = criteriaQuery.from(entityClass);
        criteriaQuery.select(from).where(from.get(parentAttributeName).isNull());
        final var orders = ordering.apply(builder, from);
        if (!orders.isEmpty()) {
            criteriaQuery.orderBy(orders);
        }
        return mapping.apply(entityManager.createQuery(criteriaQuery).getResultStream());
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance, which is not allowed.
     */
    private __SelfReferencingQueryUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
