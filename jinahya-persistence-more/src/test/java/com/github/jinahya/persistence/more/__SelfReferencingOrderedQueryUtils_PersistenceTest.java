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
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that {@link __SelfReferencingOrderedQueryUtils} selects the roots in the order the entity orders its
 * siblings in, with nothing passed to say so.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class __SelfReferencingOrderedQueryUtils_PersistenceTest {

    private static EntityManagerFactory ENTITY_MANAGER_FACTORY;

    @BeforeAll
    static void openEntityManagerFactory() {
        ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("__morePU");
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

    @BeforeEach
    void deleteAll() {
        acceptEntityManager(em -> {
            // the parent reference has to go before the rows do, or the delete hits the self-referencing key
            em.createQuery("update _CategoryEntity c set c.parent = null").executeUpdate();
            em.createQuery("delete from _CategoryEntity").executeUpdate();
        });
    }

    /**
     * Persists three roots whose ordinals disagree with the order they are written in, and one child, so that an
     * ordering by identifier can not pass for an ordering by ordinal.
     */
    private static void persistRoots() {
        acceptEntityManager(em -> {
            final var third = new _CategoryEntity("third", null, 2);
            em.persist(third);
            final var first = new _CategoryEntity("first", null, 0);
            em.persist(first);
            em.persist(new _CategoryEntity("second", null, 1));
            // a child, which is no root and must not turn up
            em.persist(new _CategoryEntity("child", first, 0));
            em.flush();
        });
    }

    // -----------------------------------------------------------------------------------------------------------------

    @DisplayName("the roots come back in sibling-ordinal order, with no ordering passed")
    @Test
    void selectRoots__() {
        persistRoots();
        acceptEntityManager(em -> {
            final List<_CategoryEntity> roots =
                    __SelfReferencingOrderedQueryUtils.selectRoots(em, _CategoryEntity.class, Stream::toList);
            assertThat(roots).extracting(_CategoryEntity::getName).containsExactly("first", "second", "third");
        });
    }

    @DisplayName("a child is no root, whatever its own ordinal")
    @Test
    void childrenAreNotRoots__() {
        persistRoots();
        acceptEntityManager(em -> {
            final List<_CategoryEntity> roots =
                    __SelfReferencingOrderedQueryUtils.selectRoots(em, _CategoryEntity.class, Stream::toList);
            assertThat(roots).extracting(_CategoryEntity::getName).doesNotContain("child");
        });
    }

    @DisplayName("the stream-mapping overload hands the roots over in the same order")
    @Test
    void mapping__() {
        persistRoots();
        acceptEntityManager(em -> {
            final String joined = __SelfReferencingOrderedQueryUtils.selectRoots(
                    em, _CategoryEntity.class,
                    stream -> stream.map(_CategoryEntity::getName).collect(Collectors.joining(","))
            );
            assertThat(joined).isEqualTo("first,second,third");
        });
    }

    @DisplayName("an empty table answers with an empty list")
    @Test
    void empty__() {
        acceptEntityManager(em -> {
            final long count =
                    __SelfReferencingOrderedQueryUtils.selectRoots(em, _CategoryEntity.class, Stream::count);
            assertThat(count).isZero();
        });
    }
}
