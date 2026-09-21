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
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that an entity implementing {@link __SelfReferencingOrdered} maps and behaves as the marks promise, against
 * a real persistence provider.
 * <p>
 * The unit tests next door read the marks out of plain objects. This is the only place where a provider creates the
 * tables, writes the parent reference and the ordinal, reads them back, and where the constraints inherited from the
 * interface are actually evaluated on the way in.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class __SelfReferencingOrdered_PersistenceTest {

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
     * Runs specified action, and returns the {@link ConstraintViolationException} it failed with.
     * <p>
     * The exception is searched for down the cause chain, and the violations are read from the exception itself rather
     * than from its message: EclipseLink names only the entity in the message, where Hibernate names the property too.
     */
    private static ConstraintViolationException constraintViolationOf(final Runnable runnable) {
        try {
            runnable.run();
        } catch (final Exception e) {
            for (Throwable c = e; c != null; c = c.getCause()) {
                if (c instanceof ConstraintViolationException cve) {
                    return cve;
                }
            }
            throw new AssertionError("not a constraint violation", e);
        }
        throw new AssertionError("no constraint violation was raised");
    }

    private static List<String> propertyPathsOf(final ConstraintViolationException cve) {
        return cve.getConstraintViolations().stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(String::valueOf)
                .toList();
    }

    private static List<String> columnNamesOf(final String tableName) {
        // untyped: EclipseLink reads a result class as an entity class, and has no descriptor for String
        final List<?> rows = applyEntityManager(em -> em
                .createNativeQuery(
                        // uppercase: database_to_upper=false keeps identifiers verbatim, and H2's own
                        // schema is upper case
                        "select lower(COLUMN_NAME) from INFORMATION_SCHEMA.COLUMNS"
                        + " where lower(TABLE_NAME) = lower(?1)")
                .setParameter(1, tableName)
                .getResultList()
        );
        return rows.stream().map(String::valueOf).toList();
    }

    /**
     * Persists a root and the specified number of children, each ordered by its index, and returns the root's
     * identifier.
     */
    private static Long persistFamily(final String rootName, final int childCount) {
        return applyEntityManager(em -> {
            final var root = new _CategoryEntity(rootName, null, 0);
            em.persist(root);
            for (var i = 0; i < childCount; i++) {
                em.persist(new _CategoryEntity(rootName + "-child-" + i, root, i));
            }
            em.flush();
            return root.getId();
        });
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("mapping")
    @Nested
    class ColumnsTest {

        @DisplayName("a marked field maps the parent reference and the ordinal, under field access")
        @Test
        void fieldAccessColumns__() {
            assertThat(columnNamesOf(_CategoryEntity.TABLE_NAME))
                    .contains(_CategoryEntity.COLUMN_NAME_PARENT, _CategoryEntity.COLUMN_NAME_SIBLING_ORDINAL);
        }

        @DisplayName("a marked accessor maps them under property access, named on the entity's own terms")
        @Test
        void propertyAccessColumns__() {
            assertThat(columnNamesOf(_PropertyAccessNodeEntity.TABLE_NAME))
                    .contains(_PropertyAccessNodeEntity.COLUMN_NAME_PARENT,
                              _PropertyAccessNodeEntity.COLUMN_NAME_DISPLAY_ORDER);
        }

        @DisplayName("the inherited default accessor does not become a column of its own")
        @Test
        void defaultMethodIsNotMapped__() {
            // getSiblingOrdinal() is a default method of an implemented interface, and an implemented
            // interface is not part of the mapping -- under property access no less than under field access
            assertThat(columnNamesOf(_PropertyAccessNodeEntity.TABLE_NAME))
                    .doesNotContain("siblingordinal", "sibling_ordinal");
            assertThat(columnNamesOf(_CategoryEntity.TABLE_NAME))
                    .doesNotContain("siblingordinal");
        }

        @DisplayName("the overridden interface accessors, marked @Transient, do not become columns")
        @Test
        void transientAccessorsAreNotMapped__() {
            assertThat(columnNamesOf(_PropertyAccessNodeEntity.TABLE_NAME))
                    .doesNotContain("hierarchyparent", "hierarchyparent_id", "hierarchydepth");
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("round trip")
    @Nested
    class RoundTripTest {

        @DisplayName("a parent and an ordinal survive a write and a read, read through the marks")
        @Test
        void hierarchy__() {
            final var rootId = persistFamily("round-trip", 3);
            acceptEntityManager(em -> {
                final var root = em.find(_CategoryEntity.class, rootId);
                assertThat(root).isNotNull();
                assertThat(root.getHierarchyParent()).isNull();
                assertThat(root.getHierarchyDepth()).isZero();
                assertThat(root.getSiblingOrdinal()).isZero();
            });
        }

        @DisplayName("a child reaches its parent, and its depth, through the marked member")
        @Test
        void childReachesItsParent__() {
            final var rootId = persistFamily("depth", 1);
            acceptEntityManager(em -> {
                final var child = em
                        .createQuery("select c from _CategoryEntity c where c.name = :name", _CategoryEntity.class)
                        .setParameter("name", "depth-child-0")
                        .getSingleResult();
                assertThat(child.getHierarchyParent()).isNotNull();
                assertThat(child.getHierarchyParent().getId()).isEqualTo(rootId);
                assertThat(child.getHierarchyDepth()).isOne();
                assertThat(child.getSiblingOrdinal()).isZero();
            });
        }

        @DisplayName("siblings come back in ordinal order, and answer their own ordinals")
        @Test
        void siblingsAreOrdered__() {
            final var rootId = persistFamily("ordered", 4);
            acceptEntityManager(em -> {
                final var children = em
                        .createQuery(
                                "select c from _CategoryEntity c where c.parent.id = :id"
                                + " order by c.siblingOrdinal asc, c.id asc",
                                _CategoryEntity.class)
                        .setParameter("id", rootId)
                        .getResultList();
                assertThat(children).hasSize(4);
                assertThat(children).extracting(__SelfReferencingOrdered::getSiblingOrdinal)
                        .containsExactly(0, 1, 2, 3);
                assertThat(children).extracting(_CategoryEntity::getName)
                        .containsExactly("ordered-child-0", "ordered-child-1", "ordered-child-2", "ordered-child-3");
            });
        }

        @DisplayName("an entity which marks its accessors answers the same way")
        @Test
        void propertyAccess__() {
            final var id = applyEntityManager(em -> {
                final var root = new _PropertyAccessNodeEntity(null, 0);
                em.persist(root);
                final var child = new _PropertyAccessNodeEntity(root, 5);
                em.persist(child);
                em.flush();
                return child.getId();
            });
            acceptEntityManager(em -> {
                final var child = em.find(_PropertyAccessNodeEntity.class, id);
                assertThat(child.getSiblingOrdinal()).isEqualTo(5);
                assertThat(child.getDisplayOrder()).isEqualTo(5);
                assertThat(child.getHierarchyParent()).isNotNull();
                assertThat(child.getHierarchyDepth()).isOne();
            });
        }

        @DisplayName("two siblings may share an ordinal; the type promises comparability, not distinctness")
        @Test
        void tiesArePossible__() {
            final var rootId = applyEntityManager(em -> {
                final var root = new _CategoryEntity("tied", null, 0);
                em.persist(root);
                em.persist(new _CategoryEntity("tied-a", root, 1));
                em.persist(new _CategoryEntity("tied-b", root, 1));
                em.flush();
                return root.getId();
            });
            acceptEntityManager(em -> {
                final var children = em
                        .createQuery("select c from _CategoryEntity c where c.parent.id = :id", _CategoryEntity.class)
                        .setParameter("id", rootId)
                        .getResultList();
                assertThat(children).extracting(__SelfReferencingOrdered::getSiblingOrdinal)
                        .containsExactly(1, 1);
            });
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("constraints inherited from the interface")
    @Nested
    class ValidationTest {

        @DisplayName("an ordinal which was never assigned is reported, by name, rather than written")
        @Test
        void unassignedOrdinalIsReported__() {
            // the read answers null instead of failing, precisely so that this violation can be raised:
            // Jakarta Validation evaluates the constraint by calling getSiblingOrdinal()
            final var cve = constraintViolationOf(
                    () -> acceptEntityManager(em -> em.persist(new _CategoryEntity("unassigned", null, null)))
            );
            assertThat(propertyPathsOf(cve)).contains("siblingOrdinal");
        }

        @DisplayName("a negative ordinal violates the @PositiveOrZero declared on the interface")
        @Test
        void negativeOrdinalIsReported__() {
            final var cve = constraintViolationOf(
                    () -> acceptEntityManager(em -> em.persist(new _CategoryEntity("negative", null, -1)))
            );
            assertThat(propertyPathsOf(cve)).contains("siblingOrdinal");
        }

        @DisplayName("the constraints reach an entity which names its own member differently")
        @Test
        void propertyAccessOrdinalIsReported__() {
            // getSiblingOrdinal() reads getDisplayOrder(), so the interface's own property is violated too
            final var cve = constraintViolationOf(
                    () -> acceptEntityManager(em -> em.persist(new _PropertyAccessNodeEntity(null, -1)))
            );
            assertThat(propertyPathsOf(cve)).contains("siblingOrdinal", "displayOrder");
        }

        @DisplayName("a zero ordinal is valid; it is the first sibling, not a missing one")
        @Test
        void zeroIsValid__() {
            acceptEntityManager(em -> em.persist(new _CategoryEntity("zero", null, 0)));
        }
    }
}
