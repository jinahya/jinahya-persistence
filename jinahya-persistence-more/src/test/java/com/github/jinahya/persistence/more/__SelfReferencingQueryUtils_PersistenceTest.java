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
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that {@link __SelfReferencingQueryUtils} reads a whole hierarchy out of a real database, with nothing but
 * the marks to go on.
 * <p>
 * The point of the walk is that it is portable, so it is worth running against whichever provider the build selects,
 * against real tables, rather than being reasoned about.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class __SelfReferencingQueryUtils_PersistenceTest {

    private static EntityManagerFactory ENTITY_MANAGER_FACTORY;

    @BeforeAll
    static void openEntityManagerFactory() {
        ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("__colorPU");
    }

    @AfterAll
    static void closeEntityManagerFactory() {
        if (ENTITY_MANAGER_FACTORY != null) {
            ENTITY_MANAGER_FACTORY.close();
        }
    }

    private static <R> R applyEntityManager(final Function<? super EntityManager, ? extends R> function) {
        try (final var entityManager = ENTITY_MANAGER_FACTORY.createEntityManager()) {
            final var transaction = entityManager.getTransaction();
            transaction.begin();
            try {
                final var result = function.apply(entityManager);
                transaction.commit();
                return result;
            } catch (final Exception e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                throw e;
            }
        }
    }

    private static void acceptEntityManager(final Consumer<? super EntityManager> consumer) {
        applyEntityManager(em -> {
            consumer.accept(em);
            return null;
        });
    }

    /**
     * Empties both hierarchy tables, so that a whole-table select is a select of what the test itself wrote.
     */
    @BeforeEach
    void deleteAll() {
        acceptEntityManager(em -> {
            // the parent reference has to go before the rows do, or the delete hits the self-referencing key
            em.createQuery("update _CategoryEntity c set c.parent = null").executeUpdate();
            em.createQuery("delete from _CategoryEntity").executeUpdate();
            em.createQuery("update _PropertyAccessNodeEntity n set n.parent = null").executeUpdate();
            em.createQuery("delete from _PropertyAccessNodeEntity").executeUpdate();
        });
    }

    /**
     * Persists a two-root forest, and returns the identifier of {@code b}, the only instance with grandchildren.
     * <pre>
     * root1(0)          root2(1)
     * ├── a(0)
     * └── b(1)
     *     ├── b1(0)
     *     └── b2(1)
     * </pre>
     */
    private static Long persistForest() {
        return applyEntityManager(em -> {
            final var root1 = new _CategoryEntity("root1", null, 0);
            em.persist(root1);
            final var root2 = new _CategoryEntity("root2", null, 1);
            em.persist(root2);
            final var a = new _CategoryEntity("a", root1, 0);
            em.persist(a);
            final var b = new _CategoryEntity("b", root1, 1);
            em.persist(b);
            em.persist(new _CategoryEntity("b1", b, 0));
            em.persist(new _CategoryEntity("b2", b, 1));
            em.flush();
            return b.getId();
        });
    }

    /**
     * Persists two instances each of which is the parent of the other, and returns the identifier of the first.
     * <p>
     * No root, hence nothing a whole-table select can reach.
     */
    private static Long persistCycle() {
        return applyEntityManager(em -> {
            final var first = new _CategoryEntity("first", null, 0);
            em.persist(first);
            final var second = new _CategoryEntity("second", first, 0);
            em.persist(second);
            em.flush();
            // the parent reference is updatable, which is what lets a hierarchy close on itself; done with a bulk
            // update rather than a setter, which the entity deliberately does not declare
            em.createQuery("update _CategoryEntity c set c.parent = :parent where c.id = :id")
                    .setParameter("parent", second)
                    .setParameter("id", first.getId())
                    .executeUpdate();
            return first.getId();
        });
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("selectRoots(entityManager, entityClass, ordering, mapping)")
    @Nested
    class SelectRootsTest {

        @DisplayName("only the instances with no parent come back")
        @Test
        void rootsOnly__() {
            persistForest();
            acceptEntityManager(em -> {
                final List<_CategoryEntity> roots = __SelfReferencingQueryUtils.selectRoots(
                        em, _CategoryEntity.class, (b, r) -> List.of(b.asc(r.get("name"))), Stream::toList
                );
                assertThat(roots).extracting(_CategoryEntity::getName).containsExactly("root1", "root2");
            });
        }

        @DisplayName("the ordering is the one the function builds")
        @Test
        void ordered__() {
            persistForest();
            acceptEntityManager(em -> {
                final List<_CategoryEntity> roots = __SelfReferencingQueryUtils.selectRoots(
                        em, _CategoryEntity.class, (b, r) -> List.of(b.desc(r.get("name"))), Stream::toList
                );
                assertThat(roots).extracting(_CategoryEntity::getName).containsExactly("root2", "root1");
            });
        }

        @DisplayName("an ordering which answers nothing leaves the query unordered")
        @Test
        void unordered__() {
            persistForest();
            acceptEntityManager(em -> {
                final List<_CategoryEntity> roots = __SelfReferencingQueryUtils.selectRoots(
                        em, _CategoryEntity.class, (b, r) -> List.of(), Stream::toList
                );
                assertThat(roots).extracting(_CategoryEntity::getName)
                        .containsExactlyInAnyOrder("root1", "root2");
            });
        }

        @DisplayName("the stream-mapping overload hands the roots to the mapping")
        @Test
        void mapping__() {
            persistForest();
            acceptEntityManager(em -> {
                // a local: R can not be inferred through AssertJ's overloads
                final String joined = __SelfReferencingQueryUtils.selectRoots(
                        em, _CategoryEntity.class,
                        (b, r) -> List.of(b.asc(r.get("name"))),
                        stream -> stream.map(_CategoryEntity::getName).collect(Collectors.joining(","))
                );
                assertThat(joined).isEqualTo("root1,root2");
            });
        }

        @DisplayName("an empty table answers with an empty list")
        @Test
        void empty__() {
            acceptEntityManager(em -> {
                final long count = __SelfReferencingQueryUtils.selectRoots(
                        em, _CategoryEntity.class, (b, r) -> List.of(), Stream::count
                );
                assertThat(count).isZero();
            });
        }

        @DisplayName("a hierarchy with no root answers with an empty list")
        @Test
        void noRoot__() {
            persistCycle();
            acceptEntityManager(em -> {
                final long count = __SelfReferencingQueryUtils.selectRoots(
                        em, _CategoryEntity.class, (b, r) -> List.of(), Stream::count
                );
                assertThat(count).isZero();
            });
        }
    }
}
